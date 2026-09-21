package com.janboerman.cauldrondispensers.compat.craftbukkit;

import com.janboerman.cauldrondispensers.compat.ItemUtil;
import org.bukkit.craftbukkit.inventory.CraftItemStack;

public class ItemUtilImpl implements ItemUtil {

    @Override
    public org.bukkit.inventory.ItemStack asCraftMirror(net.minecraft.world.item.ItemStack nmsStack) {
        return CraftItemStack.asCraftMirror(nmsStack);
    }

    @Override
    public net.minecraft.world.item.ItemStack asNmsCopy(org.bukkit.inventory.ItemStack bukkitStack) {
        return CraftItemStack.asNMSCopy(bukkitStack);
    }
}
