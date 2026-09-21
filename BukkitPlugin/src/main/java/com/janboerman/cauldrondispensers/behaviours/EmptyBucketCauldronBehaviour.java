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

import java.util.Objects;

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

        if (adjacentBlockState.is(Blocks.WATER_CAULDRON) && isFull(adjacentBlockState)) {
            // TODO fire BlockDispenseEvent

            // TODO fire CauldronLevelChangeEvent

            setEmptyCauldron(level, adjacentBlockPos);

            ItemStack singleWaterBucket = emptyBucketItemStack.transmuteCopy(CauldronDispensers.WATER_BUCKET, 1);
            return consumeWithRemainder(blockSource, emptyBucketItemStack, singleWaterBucket);
        }

        else if (adjacentBlockState.is(Blocks.LAVA_CAULDRON) && isFull(adjacentBlockState)) {
            // TODO fire BlockDispenseEvent

            // TODO fire CauldronLevelChangeEvent

            setEmptyCauldron(level, adjacentBlockPos);

            ItemStack singleLavaBucket = emptyBucketItemStack.transmuteCopy(CauldronDispensers.LAVA_BUCKET, 1);
            return consumeWithRemainder(blockSource, emptyBucketItemStack, singleLavaBucket);
        }

        else if (adjacentBlockState.is(Blocks.POWDER_SNOW_CAULDRON) && isFull(adjacentBlockState)) {
            // TODO fire BlockDispenseEvent

            // TODO fire CauldronLevelChangeEvent

            setEmptyCauldron(level, adjacentBlockPos);

            ItemStack singlePowderSnowBucket = emptyBucketItemStack.transmuteCopy(CauldronDispensers.POWDER_SNOW_BUCKET, 1);
            return consumeWithRemainder(blockSource, emptyBucketItemStack, singlePowderSnowBucket);
        }

        else {
            return delegate.dispense(blockSource, emptyBucketItemStack);
        }
    }

    private static boolean isFull(BlockState cauldronBlockState) {
        return ((AbstractCauldronBlock) cauldronBlockState.getBlock()).isFull(cauldronBlockState);
    }

    private static void setEmptyCauldron(ServerLevel level, BlockPos cauldronBlockPos) {
        level.setBlockAndUpdate(cauldronBlockPos, Blocks.CAULDRON.defaultBlockState());
        level.gameEvent(null, GameEvent.BLOCK_CHANGE, cauldronBlockPos);
    }
}
