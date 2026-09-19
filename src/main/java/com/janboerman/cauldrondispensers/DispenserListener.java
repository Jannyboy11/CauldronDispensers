package com.janboerman.cauldrondispensers;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Levelled;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class DispenserListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onDispense(BlockDispenseEvent event) {
        if (!(event.getBlock().getState() instanceof org.bukkit.block.Dispenser dispenser)) return;

        Block dispenserBlock = event.getBlock();
        org.bukkit.block.data.type.Dispenser dispenserBlockData = (org.bukkit.block.data.type.Dispenser) dispenserBlock.getBlockData();
        BlockFace directionFacing = dispenserBlockData.getFacing();

        Block adjacentBlock = dispenserBlock.getRelative(directionFacing);
        ItemStack dispensedItem = event.getItem();

        if (isEmptyCauldron(adjacentBlock)) {
            // check whether we can dispense the bucket contents:
            if (isWaterBucket(dispensedItem)) {
                event.setCancelled(true);
                setEmptyBucketInDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.WATER_CAULDRON);
                setFullLevel(adjacentBlock);
                // TODO any sound?
                // TODO will the dispenser sound still play? does a water / lava sound play when dispensing water/lava normally? do we want an extra (bucket or water) sound here as well?
            } else if (isLavaBucket(dispensedItem)) {
                event.setCancelled(true);
                setEmptyBucketInDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.LAVA_CAULDRON);
                setFullLevel(adjacentBlock);
                // TODO any sound?
            } else if (isPowderSnowBucket(dispensedItem)) {
                event.setCancelled(true);
                setEmptyBucketInDispenser(dispenser, dispensedItem);
                setFullLevel(adjacentBlock);
                // TODO any sound?
            }
        } else if (isEmptyBucket(dispensedItem)) {
            // check whether we can fill the bucket:
            if (isWaterCauldron(adjacentBlock)) {
                event.setCancelled(true);
                setWaterBucketInDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.CAULDRON);
                // TODO any sound?
            } else if (isLavaCauldron(adjacentBlock)) {
                event.setCancelled(true);
                setLavaBucketInDispenser(dispenser, dispensedItem);
                adjacentBlock.setType(Material.CAULDRON);
                // TODO any sound?
            } else if (isPowderSnowCauldron(adjacentBlock)) {
                event.setCancelled(true);
                setPowderSnowBucketInDispenser(dispenser, dispensedItem);
                // TODO
            }
        }
    }

    // logic (generic)

    private static int findSlotOfItemIgnoringAmount(Inventory inventory, ItemStack item) {
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length; i += 1) {
            if (item.isSimilar(contents[i])) {
                return i;
            }
        }

        return -1;
    }

    // logic (dispensing)

    private static void setEmptyBucketInDispenser(org.bukkit.block.Dispenser dispenser, ItemStack filledBucket) {
        // Find the index of the filled bucket
        Inventory dispenserInventory = dispenser.getInventory();
        int bucketInventorySlot = findSlotOfItemIgnoringAmount(dispenserInventory, filledBucket);
        ItemStack dispenserBucket = dispenserInventory.getItem(bucketInventorySlot);

        // Ensure the empty bucket retains metadata (such as custom name, lore, etc) by making a clone of the original
        ItemStack emptyBucketStack = filledBucket.clone();
        emptyBucketStack.setType(Material.BUCKET);

        // Replace the filled bucket by an empty bucket in the dispenser
        if (dispenserBucket.getAmount() <= filledBucket.getAmount()) {
            // replace filled bucket
            dispenserInventory.setItem(bucketInventorySlot, emptyBucketStack);
        } else {
            // subtract filledBucket count from filled bucket (should naturally be 1, but may differ)
            dispenserBucket.setAmount(dispenserBucket.getAmount() - filledBucket.getAmount());
            dispenserInventory.setItem(bucketInventorySlot, dispenserBucket);

            // add empty bucket, falling back to dropping the empty bucket on the ground
            Map<Integer, ItemStack> remainder = dispenserInventory.addItem(emptyBucketStack);
            for (ItemStack remainderItem : remainder.values()) {
                dispenser.getWorld().dropItemNaturally(dispenser.getLocation().add(0.5, 0.5, 0.5), remainderItem);
            }
        }
    }

    private static void setFullLevel(Block block) {
        Levelled levelled = (Levelled) block.getBlockData();
        levelled.setLevel(levelled.getMaximumLevel());
        block.setBlockData(levelled);
    }

    // logic (retracting)

    private static void setWaterBucketInDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket) {
        setFilledBucketInDispenser(dispenser, emptyBucket, Material.WATER_BUCKET);
    }

    private static void setLavaBucketInDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket) {
        setFilledBucketInDispenser(dispenser, emptyBucket, Material.LAVA_BUCKET);
    }

    private static void setPowderSnowBucketInDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket) {
        setFilledBucketInDispenser(dispenser, emptyBucket, Material.POWDER_SNOW_BUCKET);
    }

    private static void setFilledBucketInDispenser(org.bukkit.block.Dispenser dispenser, ItemStack emptyBucket, Material filledBucketMaterial) {
        // Find the index of the empty bucket
        Inventory dispenserInventory = dispenser.getInventory();
        int bucketInventorySlot = findSlotOfItemIgnoringAmount(dispenserInventory, emptyBucket);
        ItemStack dispenserBucket = dispenserInventory.getItem(bucketInventorySlot);

        ItemStack filledBucketStack = emptyBucket.clone();
        filledBucketStack.setType(filledBucketMaterial);

        // Replace the empty bucket by a filled bucket in the dispenser
        if (dispenserBucket.getAmount() <= emptyBucket.getAmount()) {
            // replace the empty bucket
            dispenserInventory.setItem(bucketInventorySlot, filledBucketStack);
        } else {
            // subtract emptyBucket count from the empty bucket (should naturally be 1, but may differ)
            dispenserBucket.setAmount(dispenserBucket.getAmount() - emptyBucket.getAmount());
            dispenserInventory.setItem(bucketInventorySlot, dispenserBucket);

            // add filled bucket, falling back to dropping the filled bucket on the ground
            Map<Integer, ItemStack> remainder = dispenser.getInventory().addItem(filledBucketStack);
            for (ItemStack remainderItem : remainder.values()) {
                dispenser.getWorld().dropItemNaturally(dispenser.getLocation().add(0.5, 0.5, 0.5), remainderItem);
            }
        }
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
        // TODO or should we return levelled.getLevel() == 0?
        return levelled.getLevel() == levelled.getMaximumLevel();
        // The minecraft wiki suggests level 3 means full: https://minecraft.wiki/w/Cauldron#Block_states
    }

}
