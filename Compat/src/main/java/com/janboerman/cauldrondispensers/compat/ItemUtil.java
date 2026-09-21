package com.janboerman.cauldrondispensers.compat;

public interface ItemUtil {

    org.bukkit.craftbukkit.inventory.CraftItemStack asCraftMirror(net.minecraft.world.item.ItemStack nmsStack);

    net.minecraft.world.item.ItemStack asNmsCopy(org.bukkit.inventory.ItemStack bukkitStack);

}
