package com.janboerman.cauldrondispensers;

import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;

public record AddItemToDispenserAction(Dispenser dispenser, ItemStack item) {
}
