package com.janboerman.cauldrondispensers.behaviours;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.block.CraftBlockStates;

public class WaterBucketCauldronBehaviour extends FilledBucketCauldronBehaviour {

    public WaterBucketCauldronBehaviour(DispenseItemBehavior delegate) {
        super(delegate);
    }

    @Override
    protected BlockState getFullCauldronState() {
        return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, LayeredCauldronBlock.MAX_FILL_LEVEL);
    }

    @Override
    protected boolean isSpecificCauldron(BlockState blockState) {
        return blockState.is(Blocks.WATER_CAULDRON);
    }

    @Override
    protected CraftBlockState toBukkitBlockState(BlockState fullCauldronState) {
        CraftBlockState newState = CraftBlockStates.getBlockState(level, blockpos);
        newState.setData(fullCauldronState);
        return null;
    }

    @Override
    protected BlockState getCauldronState(int level) {
        return Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, level);
    }
}
