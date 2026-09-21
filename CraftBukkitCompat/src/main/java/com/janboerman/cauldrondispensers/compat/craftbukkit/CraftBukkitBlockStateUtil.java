package com.janboerman.cauldrondispensers.compat.craftbukkit;

import com.janboerman.cauldrondispensers.compat.BlockStateUtil;

public class CraftBukkitBlockStateUtil implements BlockStateUtil {

    @Override
    public void setHandle(org.bukkit.craftbukkit.block.CraftBlockState craftBlockState, net.minecraft.world.level.block.state.BlockState nmsBlockState) {
        craftBlockState.setData(nmsBlockState);
    }
}
