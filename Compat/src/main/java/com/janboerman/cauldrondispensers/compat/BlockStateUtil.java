package com.janboerman.cauldrondispensers.compat;

public interface BlockStateUtil {

    void setHandle(org.bukkit.craftbukkit.block.CraftBlockState craftBlockState, net.minecraft.world.level.block.state.BlockState nmsBlockState);

}
