package com.janboerman.cauldrondispensers.behaviours;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public class LavaBucketCauldronBehaviour extends FilledBucketCauldronBehaviour {

    public LavaBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        super(delegate);
    }

    @Override
    protected BlockState getFullCauldronState() {
        return Blocks.LAVA_CAULDRON.defaultBlockState();
    }

    @Override
    protected boolean isSpecificCauldron(BlockState blockState) {
        return blockState.is(Blocks.LAVA_CAULDRON);
    }

    @Override
    protected BlockState getCauldronState(int level) {
        // should throw exception, but we put this here just to be future-proof.
        return Blocks.LAVA_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, level);
    }
}
