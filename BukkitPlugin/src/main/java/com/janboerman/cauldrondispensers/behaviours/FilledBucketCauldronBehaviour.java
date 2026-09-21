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
import net.minecraft.world.level.block.DispenserBlock;
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

import static com.janboerman.cauldrondispensers.Compat.BLOCK_STATE_UTIL;
import static com.janboerman.cauldrondispensers.Compat.ITEM_UTIL;

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
            blockDispenseEvent:
            {
                CraftBlock dispenserCraftBlock = CraftBlock.at(level, blockSource.pos());
                CraftItemStack craftItemStack = ITEM_UTIL.asCraftMirror(filledBucketItemStack);
                Vector velocity = new Vector(0, 0, 0);

                BlockDispenseEvent dispenseEvent = new BlockDispenseEvent(dispenserCraftBlock, craftItemStack, velocity);
                Bukkit.getPluginManager().callEvent(dispenseEvent); // ignore DispenserBlock.eventFired because it doesn't exist on Paper

                if (dispenseEvent.isCancelled()) {
                    return filledBucketItemStack;
                }

                if (!dispenseEvent.getItem().equals(craftItemStack)) {
                    // Chain to handler for new item
                    ItemStack eventStack = ITEM_UTIL.asNmsCopy(dispenseEvent.getItem());
                    DispenseItemBehavior dispenseitembehavior = DispenserBlock.DISPENSER_REGISTRY.get(eventStack.getItem());
                    if (dispenseitembehavior != this) {
                        dispenseitembehavior.dispense(blockSource, eventStack);
                    }
                    return filledBucketItemStack;
                }
            }

            // Actual dispense logic:
            ItemStack singleEmptyBucket = filledBucketItemStack.transmuteCopy(CauldronDispensers.EMPTY_BUCKET, 1);
            ItemStack resultDispensedItem = consumeWithRemainder(blockSource, filledBucketItemStack, singleEmptyBucket);


            cauldronLevelChangeEvent:
            {
                BlockState newState = getFullCauldronState();
                CraftBlock cauldronCraftBlock = CraftBlock.at(level, hopefullyCauldronBlockPos);
                CraftBlockState craftBlockState = CraftBlockStates.getBlockState(level, hopefullyCauldronBlockPos);
                BLOCK_STATE_UTIL.setHandle(craftBlockState, newState);

                CauldronLevelChangeEvent cauldronEvent = new CauldronLevelChangeEvent(cauldronCraftBlock, null, CauldronLevelChangeEvent.ChangeReason.UNKNOWN, craftBlockState);
                Bukkit.getPluginManager().callEvent(cauldronEvent);

                if (cauldronEvent.isCancelled()) {
                    break cauldronLevelChangeEvent;
                }

                newState = craftBlockState.getHandle();

                // Actual cauldron logic:
                level.setBlockAndUpdate(hopefullyCauldronBlockPos, newState);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, hopefullyCauldronBlockPos);
            }


            // Finally, return.
            return resultDispensedItem;
        }

        else {
            // Dispenser is not adjacent to Cauldron - just delegate to standard water/lava/powdered_snow bucket behaviour
            return delegate.dispense(blockSource, filledBucketItemStack);
        }
    }

    protected abstract BlockState getFullCauldronState();
}
