package com.janboerman.cauldrondispensers.compat.paper;

import com.janboerman.cauldrondispensers.compat.BlockStateUtil;

public class PaperBlockStateUtil implements BlockStateUtil {

    @Override
    public void setHandle(org.bukkit.craftbukkit.block.CraftBlockState craftBlockState, net.minecraft.world.level.block.state.BlockState nmsBlockState) {
        craftBlockState.setBlock(nmsBlockState);
    }
}
