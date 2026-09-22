package com.janboerman.cauldrondispensers.behaviours;

import com.janboerman.cauldrondispensers.CauldronDispensers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractCauldronBlock;
import net.minecraft.world.level.block.Blocks;
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

public class EmptyBucketCauldronBehaviour extends DefaultDispenseItemBehavior {

    private final DispenseItemBehavior delegate;

    public EmptyBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /**
     * @param blockSource the dispenser block
     * @param emptyBucketItemStack the empty bucket item
     * @return the dispensed item
     */
    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack emptyBucketItemStack) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        ServerLevel level = blockSource.level();

        BlockPos adjacentBlockPos = blockSource.pos().relative(direction);
        BlockState adjacentBlockState = level.getBlockState(adjacentBlockPos);

        // Water
        if (adjacentBlockState.is(Blocks.WATER_CAULDRON) && isFull(adjacentBlockState)) {
            // Dispenser update:
            ItemStack result = switch (callDispenseEvent(level, blockSource, emptyBucketItemStack)) {
                case DispenseEventResult.Cancelled _, DispenseEventResult.AlreadyHandled _:
                    yield null;
                case DispenseEventResult.AllowedItemUnchanged _:
                    ItemStack singleWaterBucket = emptyBucketItemStack.transmuteCopy(CauldronDispensers.WATER_BUCKET, 1);
                    yield consumeWithRemainder(blockSource, emptyBucketItemStack, singleWaterBucket);
                case DispenseEventResult.AllowedItemChanged(ItemStack changedStack):
                    singleWaterBucket = changedStack.transmuteCopy(CauldronDispensers.WATER_BUCKET, 1);
                    yield consumeWithRemainder(blockSource, changedStack, singleWaterBucket);
            };
            if (result == null) {
                return emptyBucketItemStack;
            }

            // Cauldron update:
            updateCauldron(level, adjacentBlockPos);

            return result;
        }

        // Lava
        else if (adjacentBlockState.is(Blocks.LAVA_CAULDRON) && isFull(adjacentBlockState)) {
            // Dispenser update:
            ItemStack result = switch (callDispenseEvent(level, blockSource, emptyBucketItemStack)) {
                case DispenseEventResult.Cancelled _, DispenseEventResult.AlreadyHandled _:
                    yield null;
                case DispenseEventResult.AllowedItemUnchanged _:
                    ItemStack singleLavaBucket = emptyBucketItemStack.transmuteCopy(CauldronDispensers.LAVA_BUCKET, 1);
                    yield consumeWithRemainder(blockSource, emptyBucketItemStack, singleLavaBucket);
                case DispenseEventResult.AllowedItemChanged(ItemStack changedStack):
                    singleLavaBucket = changedStack.transmuteCopy(CauldronDispensers.LAVA_BUCKET, 1);
                    yield consumeWithRemainder(blockSource, changedStack, singleLavaBucket);
            };
            if (result == null) {
                return emptyBucketItemStack;
            }

            // Cauldron update:
            updateCauldron(level, adjacentBlockPos);

            return result;
        }

        // Powder Snow
        else if (adjacentBlockState.is(Blocks.POWDER_SNOW_CAULDRON) && isFull(adjacentBlockState)) {
            // Dispenser update:
            ItemStack result = switch (callDispenseEvent(level, blockSource, emptyBucketItemStack)) {
                case DispenseEventResult.Cancelled _, DispenseEventResult.AlreadyHandled _:
                    yield null;
                case DispenseEventResult.AllowedItemUnchanged _:
                    ItemStack singlePowderSnowBucket = emptyBucketItemStack.transmuteCopy(CauldronDispensers.POWDER_SNOW_BUCKET, 1);
                    yield consumeWithRemainder(blockSource, emptyBucketItemStack, singlePowderSnowBucket);
                case DispenseEventResult.AllowedItemChanged(ItemStack changedStack):
                    singlePowderSnowBucket = changedStack.transmuteCopy(CauldronDispensers.POWDER_SNOW_BUCKET, 1);
                    yield consumeWithRemainder(blockSource, changedStack, singlePowderSnowBucket);
            };
            if (result == null) {
                return emptyBucketItemStack;
            }

            // Cauldron update:
            updateCauldron(level, adjacentBlockPos);

            return result;
        }

        // Fallback
        else {
            return delegate.dispense(blockSource, emptyBucketItemStack);
        }
    }

    // Helpers

    private static boolean isFull(BlockState cauldronBlockState) {
        return ((AbstractCauldronBlock) cauldronBlockState.getBlock()).isFull(cauldronBlockState);
    }

    private static void updateCauldron(ServerLevel level, BlockPos cauldronBlockBos) {
        switch (callCauldronEvent(level, cauldronBlockBos)) {
            case CauldronEventResult.Allowed(BlockState newBlockState):
                level.setBlockAndUpdate(cauldronBlockBos, newBlockState);
                level.gameEvent(null, GameEvent.BLOCK_CHANGE, cauldronBlockBos);
                break;
            case CauldronEventResult.Cancelled _:
                break;
        }
    }

    // Event stuff

    private DispenseEventResult callDispenseEvent(ServerLevel serverLevel, BlockSource dispenserBlock, ItemStack filledBucketStack) {
        CraftBlock dispenserCraftBlock = CraftBlock.at(serverLevel, dispenserBlock.pos());
        CraftItemStack craftItemStack = ITEM_UTIL.asCraftMirror(filledBucketStack);
        Vector velocity = new Vector(0, 0, 0);

        BlockDispenseEvent event = new BlockDispenseEvent(dispenserCraftBlock, craftItemStack, velocity);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return DispenseEventResult.Cancelled.INSTANCE;
        }

        else if (!event.getItem().equals(craftItemStack)) {
            // Chain to handler for new item
            ItemStack eventStack = ITEM_UTIL.asNmsCopy(event.getItem());
            DispenseItemBehavior dispenseItemBehaviour = DispenserBlock.DISPENSER_REGISTRY.getOrDefault(eventStack.getItem(), DispenseItemBehavior.NOOP);
            if (dispenseItemBehaviour != this) {
                dispenseItemBehaviour.dispense(dispenserBlock, eventStack);
                return DispenseEventResult.AlreadyHandled.INSTANCE;
            } else {
                return new DispenseEventResult.AllowedItemChanged(eventStack);
            }
        }

        else {
            return DispenseEventResult.AllowedItemUnchanged.INSTANCE;
        }
    }

    private sealed interface DispenseEventResult {
        enum AllowedItemUnchanged implements DispenseEventResult { INSTANCE; }
        record AllowedItemChanged(ItemStack changedStack) implements DispenseEventResult { }
        enum Cancelled implements DispenseEventResult { INSTANCE; }
        enum AlreadyHandled implements DispenseEventResult { INSTANCE; }
    }

    private static CauldronEventResult callCauldronEvent(ServerLevel level, BlockPos cauldronBlockPos) {
        BlockState newState = Blocks.CAULDRON.defaultBlockState();

        CraftBlock cauldronCraftBlock = CraftBlock.at(level, cauldronBlockPos);
        CraftBlockState craftBlockState = CraftBlockStates.getBlockState(level, cauldronBlockPos);
        BLOCK_STATE_UTIL.setHandle(craftBlockState, newState);

        CauldronLevelChangeEvent event = new CauldronLevelChangeEvent(cauldronCraftBlock, null, CauldronLevelChangeEvent.ChangeReason.UNKNOWN, craftBlockState);
        Bukkit.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            return CauldronEventResult.Cancelled.INSTANCE;
        }

        newState = craftBlockState.getHandle();
        return new CauldronEventResult.Allowed(newState);
    }

    private sealed interface CauldronEventResult {
        enum Cancelled implements CauldronEventResult { INSTANCE; }
        record Allowed(BlockState newBlockState) implements CauldronEventResult { }
    }

}
