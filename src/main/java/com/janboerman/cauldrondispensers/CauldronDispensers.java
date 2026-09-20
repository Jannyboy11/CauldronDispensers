package com.janboerman.cauldrondispensers;

import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.LinkedList;
import java.util.Map;

/** The plugin's main class */
public class CauldronDispensers extends JavaPlugin {

    final NamespacedKey taggedForRemovalKey = new NamespacedKey(this, "tagged-for-removal");

    private final LinkedList<AddItemToDispenserAction> actionQueue = new LinkedList<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DispenserListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemSpawnListener(this), this);
    }

    @Override
    public void onDisable() {
        processActionQueue();
    }

    void addItemToDispenser(Dispenser dispenser, ItemStack itemStack) {
        actionQueue.add(new AddItemToDispenserAction(dispenser, itemStack));
        // TODO doing this by one tick delay isn't helping...
        getServer().getScheduler().runTaskLater(this, this::processQueueItem, 1L);
    }

    private boolean processQueueItem() {
        AddItemToDispenserAction action = actionQueue.poll();
        if (action == null) return false;

        IO.println("DEBUG: action = " + action);

        Dispenser dispenser = action.dispenser();
        ItemStack itemStack = action.item();

        // Note: because this is running delayed, the Dispenser might actually be absent, so we have to refresh it!
        Block dispenserBlock = dispenser.getWorld().getBlockAt(dispenser.getLocation());
        if (dispenserBlock.getState() instanceof Dispenser newDispenser) {
            // dispenser still present - let's add the item!
            Map<Integer, ItemStack> remainder = newDispenser.getInventory().addItem(itemStack);
            // couldn't add, or partial add - drop remainders on the ground
            for (ItemStack remainderItem : remainder.values()) {
                dropItemInsideBlockLocation(dispenserBlock, remainderItem);
            }
            dispenser.update();
        } else {
            // oh noes! dispenser is gone!
            // just drop the items on the ground..
            dropItemInsideBlockLocation(dispenserBlock, itemStack);
        }

        return true;
    }

    private static void dropItemInsideBlockLocation(Block block, ItemStack itemStack) {
        Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);
        block.getWorld().dropItemNaturally(dropLocation, itemStack);
    }

    private void processActionQueue() {
        while (processQueueItem());
    }
}
