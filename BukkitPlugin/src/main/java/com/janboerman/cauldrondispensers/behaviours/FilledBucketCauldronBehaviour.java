package com.janboerman.cauldrondispensers.behaviours;

import com.janboerman.cauldrondispensers.CauldronDispensers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.CauldronLevelChangeEvent;
import org.bukkit.util.Vector;

import java.util.Objects;

import static com.janboerman.cauldrondispensers.Compat.ITEM_UTIL;
import static com.janboerman.cauldrondispensers.behaviours.EmptyBucketCauldronBehaviour.isFull;

public abstract class FilledBucketCauldronBehaviour extends DefaultDispenseItemBehavior {

    private final DispenseItemBehavior delegate;

    public FilledBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /**
     * @param blockSource the dispenser block
     * @param filledBucketItemStack the filled bucket item
     * @return the dispensed item
     */
    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack filledBucketItemStack) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        ServerLevel level = blockSource.level();

        BlockPos hopefullyCauldronBlockPos = blockSource.pos().relative(direction);
        BlockState adjacentBlockState = level.getBlockState(hopefullyCauldronBlockPos);

        if (adjacentBlockState.is(BlockTags.CAULDRONS)) {
            // BlockDispenseEvent:
            CraftBlock dispenserCraftBlock = CraftBlock.at(level, blockSource.pos());
            CraftItemStack craftItemStack = ITEM_UTIL.asCraftMirror(filledBucketItemStack);
            // Weird velocity, but copied from standard water/lava bucket dispenser behaviour.
            Vector velocity = new Vector(hopefullyCauldronBlockPos.getX(), hopefullyCauldronBlockPos.getY(), hopefullyCauldronBlockPos.getZ());

            BlockDispenseEvent dispenseEvent = new BlockDispenseEvent(dispenserCraftBlock, craftItemStack, velocity);
            // Ignore CraftBukkit's DispenserBlock#eventFired, since it is never set to true.
            Bukkit.getPluginManager().callEvent(dispenseEvent);

            if (dispenseEvent.isCancelled()) {
                return filledBucketItemStack;
            }

            if (!dispenseEvent.getItem().equals(craftItemStack)) {
                // Chain to handler for new item
                ItemStack eventStack = ITEM_UTIL.asNmsCopy(dispenseEvent.getItem());
                DispenseItemBehavior dispenseitembehavior = DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                if (dispenseitembehavior != DispenseItemBehavior.NOOP && dispenseitembehavior != this) {
                    dispenseitembehavior.dispense(blockSource, eventStack);
                    return filledBucketItemStack;
                }
            }

            // CauldronLevelChangeEvent:
            BlockState newState = getFullCauldronState();
            if (!isFull(adjacentBlockState)) {
                CraftBlock cauldronCraftBlock = CraftBlock.at(level, hopefullyCauldronBlockPos);
                CraftBlockState craftBlockState = CraftBlockStates.getBlockState(level, hopefullyCauldronBlockPos);
                craftBlockState.setData(newState);

                CauldronLevelChangeEvent cauldronEvent = new CauldronLevelChangeEvent(cauldronCraftBlock, null, CauldronLevelChangeEvent.ChangeReason.BUCKET_EMPTY, craftBlockState);
                Bukkit.getPluginManager().callEvent(cauldronEvent);

                if (!cauldronEvent.isCancelled()) {
                    int newLevel = cauldronEvent.getNewLevel();
                    if (newState.getBlock() instanceof LayeredCauldronBlock && !Integer.valueOf(newLevel).equals(newState.getValue(LayeredCauldronBlock.LEVEL))) {
                        newState = getCauldronState(newLevel);
                    }

                    // Cauldron logic:
                    level.setBlockAndUpdate(hopefullyCauldronBlockPos, newState);
                    level.gameEvent(null, GameEvent.BLOCK_CHANGE, hopefullyCauldronBlockPos);
                }
            }

            // Dispense logic:
            ItemStack singleEmptyBucket = filledBucketItemStack.transmuteCopy(CauldronDispensers.EMPTY_BUCKET, 1);
            return consumeWithRemainder(blockSource, filledBucketItemStack, singleEmptyBucket);
        }

        else {
            // Dispenser is not adjacent to Cauldron - just delegate to standard water/lava/powdered_snow bucket behaviour
            return delegate.dispense(blockSource, filledBucketItemStack);
        }
    }

    protected abstract BlockState getFullCauldronState();

    protected abstract boolean isSpecificCauldron(BlockState blockState);

    protected abstract BlockState getCauldronState(int level);
}
