package com.example.gendungeonasync.listener;

import com.example.gendungeonasync.GenDungeonAsync;
import com.example.gendungeonasync.model.Dungeon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Optional;

public class DungeonEventListener implements Listener {
    
    private final GenDungeonAsync plugin;
    
    public DungeonEventListener(GenDungeonAsync plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Remove player from any active dungeon
        Optional<Dungeon> dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
        if (dungeon.isPresent()) {
            plugin.getDungeonManager().removePlayerFromDungeon(player);
            
            // If no players left in dungeon, consider removing it
            if (dungeon.get().getPlayers().isEmpty()) {
                plugin.getDungeonManager().removeDungeon(dungeon.get().getId());
            }
        }
    }
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        
        // Check if player died in a dungeon
        Optional<Dungeon> dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
        if (dungeon.isPresent()) {
            // Customize death message for dungeon deaths
            event.deathMessage(Component.text(player.getName())
                    .color(NamedTextColor.YELLOW)
                    .append(Component.text(" perished in the ")
                            .color(NamedTextColor.WHITE))
                    .append(Component.text(dungeon.get().getName())
                            .color(NamedTextColor.GOLD))
                    .append(Component.text("!")
                            .color(NamedTextColor.WHITE)));
        }
    }
    
    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        
        // Check if player was in a dungeon when they died
        Optional<Dungeon> dungeon = plugin.getDungeonManager().getPlayerDungeon(player);
        if (dungeon.isPresent()) {
            // Teleport them outside the dungeon on respawn
            plugin.getDungeonManager().removePlayerFromDungeon(player);
            
            player.sendMessage(Component.text("You have been removed from the dungeon due to death.")
                    .color(NamedTextColor.RED));
        }
    }
}
