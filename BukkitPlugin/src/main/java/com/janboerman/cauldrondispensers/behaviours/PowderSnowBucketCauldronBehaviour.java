package com.janboerman.cauldrondispensers.behaviours;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;

public class PowderSnowBucketCauldronBehaviour extends FilledBucketCauldronBehaviour {

    public PowderSnowBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        super(delegate);
    }

    @Override
    protected BlockState getFullCauldronState() {
        return Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL);
    }
}
