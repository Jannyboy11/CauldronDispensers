package com.janboerman.cauldrondispensers;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;


/** The plugin's main class */
public class CauldronDispensers extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new DispenserListener(this), this);
    }

}
