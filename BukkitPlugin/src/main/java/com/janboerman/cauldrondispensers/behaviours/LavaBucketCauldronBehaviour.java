package com.janboerman.cauldrondispensers.behaviours;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class LavaBucketCauldronBehaviour extends FilledBucketCauldronBehaviour {

    public LavaBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        super(delegate);
    }

    @Override
    protected BlockState getFullCauldronState() {
        return Blocks.LAVA_CAULDRON.defaultBlockState();
    }
}
