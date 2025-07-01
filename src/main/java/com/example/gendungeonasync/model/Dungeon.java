package com.example.gendungeonasync.model;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dungeon {
    
    private final UUID id;
    private final String name;
    private final Location startLocation;
    private final Location endLocation;
    private final DungeonSize size;
    private final DungeonDifficulty difficulty;
    private final List<UUID> players;
    private final long createdTime;
    private boolean completed;
    
    public Dungeon(String name, Location startLocation, Location endLocation, 
                   DungeonSize size, DungeonDifficulty difficulty) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.size = size;
        this.difficulty = difficulty;
        this.players = new ArrayList<>();
        this.createdTime = System.currentTimeMillis();
        this.completed = false;
    }
    
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Location getStartLocation() { return startLocation; }
    public Location getEndLocation() { return endLocation; }
    public DungeonSize getSize() { return size; }
    public DungeonDifficulty getDifficulty() { return difficulty; }
    public List<UUID> getPlayers() { return new ArrayList<>(players); }
    public long getCreatedTime() { return createdTime; }
    public boolean isCompleted() { return completed; }
    
    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
    }
    
    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
    }
    
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
    
    public World getWorld() {
        return startLocation.getWorld();
    }
    
    public enum DungeonSize {
        SMALL(16, 16, 10),
        MEDIUM(32, 32, 15),
        LARGE(64, 64, 20),
        HUGE(128, 128, 30);
        
        private final int width;
        private final int length;
        private final int height;
        
        DungeonSize(int width, int length, int height) {
            this.width = width;
            this.length = length;
            this.height = height;
        }
        
        public int getWidth() { return width; }
        public int getLength() { return length; }
        public int getHeight() { return height; }
    }
    
    public enum DungeonDifficulty {
        EASY(1, 3),
        NORMAL(2, 5),
        HARD(3, 7),
        EXPERT(4, 10);
        
        private final int minMobs;
        private final int maxMobs;
        
        DungeonDifficulty(int minMobs, int maxMobs) {
            this.minMobs = minMobs;
            this.maxMobs = maxMobs;
        }
        
        public int getMinMobs() { return minMobs; }
        public int getMaxMobs() { return maxMobs; }
    }
}
