package com.janboerman.cauldrondispensers;

import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated(forRemoval = true) // TODO remove this.
public class ItemSpawnListener implements Listener {

    private final CauldronDispensers plugin;

    ItemSpawnListener(CauldronDispensers plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
    public void onItemSpawn(ItemSpawnEvent event) {
        Item item = event.getEntity();
        ItemStack itemStack = item.getItemStack();

        if (itemStack.getItemMeta().getPersistentDataContainer().has(plugin.taggedForRemovalKey)) {
            event.setCancelled(true);
        }
    }
}
