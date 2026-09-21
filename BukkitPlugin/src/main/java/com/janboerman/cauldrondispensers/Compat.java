package com.janboerman.cauldrondispensers;

import com.janboerman.cauldrondispensers.compat.ItemUtil;
import com.janboerman.cauldrondispensers.compat.craftbukkit.CraftBukkitItemUtil;
import com.janboerman.cauldrondispensers.compat.paper.PaperItemUtil;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class Compat {

    public static final ItemUtil ITEM_UTIL = getItemUtil();

    private static ItemUtil getItemUtil() {
        try {
            CraftItemStack.class.getDeclaredMethod("asBukkitMirror", net.minecraft.world.item.ItemStack.class);
            return new PaperItemUtil();
        } catch (NoSuchMethodException e) {
            return new CraftBukkitItemUtil();
        }
    }
}
