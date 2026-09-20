package com.janboerman.cauldrondispensers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Map;

public class DispenserListener implements Listener {

    private final CauldronDispensers plugin;

    DispenserListener(CauldronDispensers plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        if (!(event.getBlock().getState() instanceof org.bukkit.block.Dispenser dispenser)) return;
        // Note: when this event is called, the item is already removed from the Dispenser's Inventory.

        Block dispenserBlock = event.getBlock();
        org.bukkit.block.data.type.Dispenser dispenserBlockData = (org.bukkit.block.data.type.Dispenser) dispenserBlock.getBlockData();
        BlockFace directionFacing = dispenserBlockData.getFacing();

        Block adjacentBlock = dispenserBlock.getRelative(directionFacing);
        ItemStack dispensedItem = event.getItem();

        if (isEmptyCauldron(adjacentBlock)) {
            // check whether we can dispense the bucket contents:
            if (isWaterBucket(dispensedItem)) {
                addEmptyBucketToDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.WATER_CAULDRON);
                setFullLevel(adjacentBlock);
                adjacentBlock.getWorld().playSound(adjacentBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY, 1F, 1F);
                markDispensedItemForRemoval(event, dispensedItem);
            } else if (isLavaBucket(dispensedItem)) {
                addEmptyBucketToDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.LAVA_CAULDRON);
                setFullLevel(adjacentBlock);
                adjacentBlock.getWorld().playSound(adjacentBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY_LAVA, 1F, 1F);
                markDispensedItemForRemoval(event, dispensedItem);
            } else if (isPowderSnowBucket(dispensedItem)) {
                addEmptyBucketToDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.POWDER_SNOW_CAULDRON);
                setFullLevel(adjacentBlock);
                adjacentBlock.getWorld().playSound(adjacentBlock.getLocation(), Sound.ITEM_BUCKET_EMPTY_POWDER_SNOW, 1F, 1F);
                markDispensedItemForRemoval(event, dispensedItem);
            }
        } else if (isEmptyBucket(dispensedItem)) {
            // check whether we can fill the bucket:
            if (isWaterCauldron(adjacentBlock)) {
                addWaterBucketToDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.CAULDRON);
                adjacentBlock.getWorld().playSound(adjacentBlock.getLocation(), Sound.ITEM_BUCKET_FILL, 1F, 1F);
                markDispensedItemForRemoval(event, dispensedItem);
            } else if (isLavaCauldron(adjacentBlock)) {
                addLavaBucketToDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.CAULDRON);
                adjacentBlock.getWorld().playSound(adjacentBlock.getLocation(), Sound.ITEM_BUCKET_FILL_LAVA, 1F, 1F);
                markDispensedItemForRemoval(event, dispensedItem);
            } else if (isPowderSnowCauldron(adjacentBlock)) {
                addPowderSnowBucketToDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.CAULDRON);
                adjacentBlock.getWorld().playSound(adjacentBlock.getLocation(), Sound.ITEM_BUCKET_FILL_POWDER_SNOW, 1F, 1F);
                markDispensedItemForRemoval(event, dispensedItem);
            }
        }
    }

    //

    private void addEmptyBucketToDispenser(org.bukkit.block.Dispenser dispenser, ItemStack filledBucket) {
        // Ensure the empty bucket retains metadata (such as custom name, lore, etc) by making a clone of the original
        ItemStack emptyBucketStack = filledBucket.clone();
        emptyBucketStack.setType(Material.BUCKET);

        addItemToDispenser(dispenser, emptyBucketStack);
    }

    private static void setFullLevel(Block block) {
        Levelled levelled = (Levelled) block.getBlockData();
        levelled.setLevel(levelled.getMaximumLevel());
        block.setBlockData(levelled);
    }

    //

    private void addWaterBucketToDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket) {
        addFilledBucketToDispenser(dispenser, emptyBucket, Material.WATER_BUCKET);
    }

    private void addLavaBucketToDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket) {
        addFilledBucketToDispenser(dispenser, emptyBucket, Material.LAVA_BUCKET);
    }

    private void addPowderSnowBucketToDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket) {
        addFilledBucketToDispenser(dispenser, emptyBucket, Material.POWDER_SNOW_BUCKET);
    }

    private void addFilledBucketToDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket, Material filledBucketMaterial) {
        // Ensure the empty bucket retains metadata (such as custom name, lore, etc) by making a clone of the original
        ItemStack filledBucketStack = emptyBucket.clone();
        filledBucketStack.setType(filledBucketMaterial);

        addItemToDispenser(dispenser, filledBucketStack);
    }

    //

    private void addItemToDispenser(org.bukkit.block.Dispenser dispenser, ItemStack itemStack) {
        // TODO adding items to the dispenser doesn't seem to work yet..
        IO.println("DEBUG: adding item to dispenser: " + itemStack);

        Map<Integer, ItemStack> remainder = dispenser.getInventory().addItem(itemStack);

        IO.println(("DEBUG: dispenser contents is now: " + Arrays.toString(dispenser.getInventory().getContents())));

        // couldn't add, or partial add - drop remainders on the ground
        for (ItemStack remainderItem : remainder.values()) {
            dropItemInsideBlockLocation(dispenser.getBlock(), remainderItem);
        }
    }

    private static void dropItemInsideBlockLocation(Block block, ItemStack itemStack) {
        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().dropItemNaturally(dropLocation, itemStack);
    }

    //

    private void markDispensedItemForRemoval(BlockDispenseEvent event, ItemStack dispensedItem) {
        ItemMeta itemMeta = dispensedItem.getItemMeta();
        PersistentDataContainer pdc = itemMeta.getPersistentDataContainer();
        pdc.set(plugin.taggedForRemovalKey, PersistentDataType.BOOLEAN, true);
        dispensedItem.setItemMeta(itemMeta);
        event.setItem(dispensedItem);
    }

    // utils

    private static boolean isWaterBucket(ItemStack itemStack) {
        return itemStack.getType() == Material.WATER_BUCKET;
    }

    private static boolean isLavaBucket(ItemStack itemStack) {
        return itemStack.getType() == Material.LAVA_BUCKET;
    }

    private static boolean isPowderSnowBucket(ItemStack itemStack) {
        return itemStack.getType() == Material.POWDER_SNOW_BUCKET;
    }

    private static boolean isEmptyBucket(ItemStack itemStack) {
        return itemStack.getType() == Material.BUCKET;
    }

    private static boolean isEmptyCauldron(Block block) {
        return block.getType() == Material.CAULDRON;
    }

    private static boolean isWaterCauldron(Block block) {
        return block.getType() == Material.WATER_CAULDRON && isFull((Levelled) block.getBlockData());
    }

    private static boolean isLavaCauldron(Block block) {
        return block.getType() == Material.LAVA_CAULDRON && isFull((Levelled) block.getBlockData());
    }

    private static boolean isPowderSnowCauldron(Block block) {
        return block.getType() == Material.POWDER_SNOW_CAULDRON && isFull((Levelled) block.getBlockData());
    }

    private static boolean isFull(Levelled levelled) {
        return levelled.getLevel() == levelled.getMaximumLevel();
        // The minecraft wiki suggests level 3 means full: https://minecraft.wiki/w/Cauldron#Block_states
        // (Note that this has nothing to do with levels 1-7 and 8-15 for normal flowing liquids, per Levelled javadocs)
    }

}
