package com.janboerman.cauldrondispensers;

import com.janboerman.cauldrondispensers.compat.BlockStateUtil;
import com.janboerman.cauldrondispensers.compat.ItemUtil;
import com.janboerman.cauldrondispensers.compat.craftbukkit.CraftBukkitBlockStateUtil;
import com.janboerman.cauldrondispensers.compat.craftbukkit.CraftBukkitItemUtil;
import com.janboerman.cauldrondispensers.compat.paper.PaperBlockStateUtil;
import com.janboerman.cauldrondispensers.compat.paper.PaperItemUtil;
import net.minecraft.world.level.block.state.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class Compat {

    public static final ItemUtil ITEM_UTIL = getItemUtil();
    public static final BlockStateUtil BLOCK_STATE_UTIL = getBlockStateUtil();

    private static ItemUtil getItemUtil() {
        try {
            CraftItemStack.class.getDeclaredMethod("asBukkitMirror", net.minecraft.world.item.ItemStack.class);
            return new PaperItemUtil();
        } catch (NoSuchMethodException e) {
            return new CraftBukkitItemUtil();
        }
    }

    private static BlockStateUtil getBlockStateUtil() {
        try {
            CraftBlockState.class.getDeclaredMethod("setData", BlockState.class);
            return new CraftBukkitBlockStateUtil();
        } catch (NoSuchMethodException e) {
            return new PaperBlockStateUtil();
        }
    }
}
