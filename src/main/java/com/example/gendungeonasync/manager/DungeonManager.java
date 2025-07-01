package com.example.gendungeonasync.manager;

import com.example.gendungeonasync.GenDungeonAsync;
import com.example.gendungeonasync.model.Dungeon;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class DungeonManager {
    
    private final GenDungeonAsync plugin;
    private final Map<UUID, Dungeon> dungeons;
    private final Map<UUID, UUID> playerDungeons; // Player UUID -> Dungeon UUID
    private final Map<UUID, Location> playerPreviousLocations; // Player UUID -> Previous Location
    private final Map<UUID, org.bukkit.GameMode> playerPreviousGameModes; // Player UUID -> Previous GameMode
    
    public DungeonManager(GenDungeonAsync plugin) {
        this.plugin = plugin;
        this.dungeons = new ConcurrentHashMap<>();
        this.playerDungeons = new ConcurrentHashMap<>();
        this.playerPreviousLocations = new ConcurrentHashMap<>();
        this.playerPreviousGameModes = new ConcurrentHashMap<>();
    }
    
    public void addDungeon(Dungeon dungeon) {
        dungeons.put(dungeon.getId(), dungeon);
    }
    
    public Optional<Dungeon> getDungeon(UUID dungeonId) {
        return Optional.ofNullable(dungeons.get(dungeonId));
    }
    
    public Optional<Dungeon> getPlayerDungeon(Player player) {
        UUID dungeonId = playerDungeons.get(player.getUniqueId());
        if (dungeonId != null) {
            return getDungeon(dungeonId);
        }
        return Optional.empty();
    }
    
    public void assignPlayerToDungeon(Player player, Dungeon dungeon) {
        // Store player's current location and gamemode before teleporting
        playerPreviousLocations.put(player.getUniqueId(), player.getLocation());
        playerPreviousGameModes.put(player.getUniqueId(), player.getGameMode());
        playerDungeons.put(player.getUniqueId(), dungeon.getId());
        dungeon.addPlayer(player);
    }
    
    public void removePlayerFromDungeon(Player player) {
        UUID dungeonId = playerDungeons.remove(player.getUniqueId());
        playerPreviousLocations.remove(player.getUniqueId());
        playerPreviousGameModes.remove(player.getUniqueId());
        if (dungeonId != null) {
            getDungeon(dungeonId).ifPresent(dungeon -> dungeon.removePlayer(player));
        }
    }
    public Optional<org.bukkit.GameMode> getPlayerPreviousGameMode(Player player) {
        return Optional.ofNullable(playerPreviousGameModes.get(player.getUniqueId()));
    }
    
    public Optional<Location> getPlayerPreviousLocation(Player player) {
        return Optional.ofNullable(playerPreviousLocations.get(player.getUniqueId()));
    }
    
    public void removeDungeon(UUID dungeonId) {
        Dungeon dungeon = dungeons.remove(dungeonId);
        if (dungeon != null) {
            // Remove all player assignments to this dungeon
            playerDungeons.entrySet().removeIf(entry -> entry.getValue().equals(dungeonId));
        }
    }
    
    public Map<UUID, Dungeon> getAllDungeons() {
        return new HashMap<>(dungeons);
    }
    
    public void cleanup() {
        // Clean up any resources or save data if needed
        dungeons.clear();
        playerDungeons.clear();
        playerPreviousLocations.clear();
        playerPreviousGameModes.clear();
    }
    
    public boolean isDungeonLocation(Location location) {
        return dungeons.values().stream()
                .anyMatch(dungeon -> isLocationInDungeon(location, dungeon));
    }
    
    private boolean isLocationInDungeon(Location location, Dungeon dungeon) {
        Location start = dungeon.getStartLocation();
        Location end = dungeon.getEndLocation();
        
        if (!location.getWorld().equals(start.getWorld())) {
            return false;
        }
        
        double minX = Math.min(start.getX(), end.getX());
        double maxX = Math.max(start.getX(), end.getX());
        double minY = Math.min(start.getY(), end.getY());
        double maxY = Math.max(start.getY(), end.getY());
        double minZ = Math.min(start.getZ(), end.getZ());
        double maxZ = Math.max(start.getZ(), end.getZ());
        
        return location.getX() >= minX && location.getX() <= maxX &&
               location.getY() >= minY && location.getY() <= maxY &&
               location.getZ() >= minZ && location.getZ() <= maxZ;
    }
}
