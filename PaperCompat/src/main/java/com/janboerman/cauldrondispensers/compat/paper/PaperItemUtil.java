package com.janboerman.cauldrondispensers.compat.paper;

import com.janboerman.cauldrondispensers.compat.ItemUtil;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class PaperItemUtil implements ItemUtil {

    @Override
    public org.bukkit.craftbukkit.inventory.CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack nmsStack) {
        return (CraftItemStack) CraftItemStack.asBukkitMirror(nmsStack);
    }

    @Override
    public net.minecraft.world.item.ItemStack asNmsCopy(org.bukkit.inventory.ItemStack bukkitStack) {
        return CraftItemStack.asNMSCopy(bukkitStack);
    }
}
