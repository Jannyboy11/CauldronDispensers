package com.janboerman.cauldrondispensers.behaviours;

import com.janboerman.cauldrondispensers.CauldronDispensers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Objects;

public abstract class FilledBucketCauldronBehaviour extends DefaultDispenseItemBehavior {

    private final DispenseItemBehavior delegate;

    public FilledBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        this.delegate = Objects.requireNonNull(delegate);
    }

    /**
     * @param blockSource the dispenser block
     * @param waterBucketItemStack the filled bucket item
     * @return the dispensed item
     */
    @Override
    public ItemStack execute(BlockSource blockSource, ItemStack waterBucketItemStack) {
        Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
        ServerLevel level = blockSource.level();

        BlockPos hopefullyCauldronBlockPos = blockSource.pos().relative(direction);
        BlockState adjacentBlockState = level.getBlockState(hopefullyCauldronBlockPos);

        if (adjacentBlockState.is(Blocks.CAULDRON) || isSpecificCauldron(adjacentBlockState)) {

            // TODO fire BlockDispenseItemEvent? probably yes.
            // TODO what about CauldronLevelChangeEvent (only if the level was already 1 or 2, and now changed to 3)

            level.setBlockAndUpdate(hopefullyCauldronBlockPos, getFullCauldronState());
            level.gameEvent(null, GameEvent.BLOCK_CHANGE, hopefullyCauldronBlockPos);

            ItemStack singleEmptyBucket = waterBucketItemStack.transmuteCopy(CauldronDispensers.EMPTY_BUCKET, 1);
            return consumeWithRemainder(blockSource, waterBucketItemStack, singleEmptyBucket);
        }

        else {
            return delegate.dispense(blockSource, waterBucketItemStack);
        }
    }

    protected abstract BlockState getFullCauldronState();

    protected abstract boolean isSpecificCauldron(BlockState blockState);
}
