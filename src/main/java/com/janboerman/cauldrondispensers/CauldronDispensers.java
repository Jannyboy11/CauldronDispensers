package com.janboerman.cauldrondispensers;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;


/** The plugin's main class */
public class CauldronDispensers extends JavaPlugin {

    @Deprecated(forRemoval = true) // TODO remove this.
    final NamespacedKey taggedForRemovalKey = new NamespacedKey(this, "tagged-for-removal");

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DispenserListener(this), this);
        getServer().getPluginManager().registerEvents(new ItemSpawnListener(this), this);
    }

}
