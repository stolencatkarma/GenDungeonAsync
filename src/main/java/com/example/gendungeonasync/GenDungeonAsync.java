package com.example.gendungeonasync;

import com.example.gendungeonasync.commands.DungeonCommand;
import com.example.gendungeonasync.commands.DungeonTeleportCommand;
import com.example.gendungeonasync.generator.DungeonGenerator;
import com.example.gendungeonasync.listener.DungeonEventListener;
import com.example.gendungeonasync.manager.DungeonManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class GenDungeonAsync extends JavaPlugin {
    
    private DungeonManager dungeonManager;
    private DungeonGenerator dungeonGenerator;
    
    @Override
    public void onEnable() {
        // Initialize managers
        this.dungeonManager = new DungeonManager(this);
        this.dungeonGenerator = new DungeonGenerator(this);
        
        // Register commands
        getCommand("dungeon").setExecutor(new DungeonCommand(this));
        getCommand("dungeontp").setExecutor(new DungeonTeleportCommand(this));
        
        // Register event listeners
        getServer().getPluginManager().registerEvents(new DungeonEventListener(this), this);
        
        // Save default config
        saveDefaultConfig();
        
        getLogger().info("GenDungeonAsync plugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Clean up any running async tasks
        if (dungeonManager != null) {
            dungeonManager.cleanup();
        }
        
        getLogger().info("GenDungeonAsync plugin has been disabled!");
    }
    
    public DungeonManager getDungeonManager() {
        return dungeonManager;
    }
    
    public DungeonGenerator getDungeonGenerator() {
        return dungeonGenerator;
    }
}
