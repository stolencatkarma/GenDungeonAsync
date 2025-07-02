package com.example.gendungeonasync.generator;

import com.example.gendungeonasync.GenDungeonAsync;
import com.example.gendungeonasync.model.Dungeon;
import com.example.gendungeonasync.model.Dungeon.DungeonDifficulty;
import com.example.gendungeonasync.model.Dungeon.DungeonSize;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

public class DungeonGenerator {
    
    private final GenDungeonAsync plugin;
    private final Random random;
    
    public DungeonGenerator(GenDungeonAsync plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }
    
    public CompletableFuture<Dungeon> generateDungeonAsync(Player player, DungeonSize size, DungeonDifficulty difficulty) {
        CompletableFuture<Dungeon> future = new CompletableFuture<>();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String dungeonName = generateDungeonName();
                String worldName = "dungeon_" + dungeonName.toLowerCase().replaceAll("[^a-z0-9]", "") + "_" + System.currentTimeMillis();

                // Create new world for this dungeon
                Bukkit.getScheduler().runTask(plugin, () -> {
                    WorldCreator creator = new WorldCreator(worldName);
                    creator.environment(World.Environment.NORMAL);
                    creator.type(WorldType.NORMAL);
                    creator.generatorSettings("");
                    World dungeonWorld = creator.createWorld();

                    // Center the dungeon around (0, 64, 0)
                    int centerX = 0;
                    int centerY = 64;
                    int centerZ = 0;
                    int startX = centerX - size.getWidth() / 2;
                    int startY = centerY;
                    int startZ = centerZ - size.getLength() / 2;
                    Location dungeonLocation = new Location(dungeonWorld, startX, startY, startZ);

                    // Set world spawn to the center of the dungeon
                    int spawnX = centerX;
                    int spawnY = centerY + 2;
                    int spawnZ = centerZ;
                    dungeonWorld.setSpawnLocation(spawnX, spawnY, spawnZ);

                    try {
                        Dungeon dungeon = buildDungeon(dungeonName, worldName, dungeonLocation, size, difficulty);
                        future.complete(dungeon);
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                });
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }
    
    private Location findSuitableLocation(Player player) {
        World world = player.getWorld();
        Location playerLoc = player.getLocation();
        
        // Search for a suitable location within 1000 blocks
        for (int attempts = 0; attempts < 50; attempts++) {
            int x = playerLoc.getBlockX() + random.nextInt(2000) - 1000;
            int z = playerLoc.getBlockZ() + random.nextInt(2000) - 1000;
            int y = world.getHighestBlockYAt(x, z);
            
            Location testLoc = new Location(world, x, y, z);
            
            // Check if the area is suitable (not too close to spawn, not in water, etc.)
            if (isSuitableLocation(testLoc)) {
                return testLoc;
            }
        }
        
        // Fallback to player's location offset
        return playerLoc.add(100, 0, 100);
    }
    
    private boolean isSuitableLocation(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        // Check if the area is mostly solid ground
        int solidBlocks = 0;
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                Block block = world.getBlockAt(x + dx, y - 1, z + dz);
                if (block.getType().isSolid()) {
                    solidBlocks++;
                }
            }
        }
        
        return solidBlocks > 80; // At least 80% solid ground
    }
    
    private Dungeon buildDungeon(String name, String worldName, Location startLocation, DungeonSize size, DungeonDifficulty difficulty) {
        World world = startLocation.getWorld();
        int startX = startLocation.getBlockX();
        int startY = startLocation.getBlockY();
        int startZ = startLocation.getBlockZ();

        Location endLocation = new Location(world,
            startX + size.getWidth(),
            startY + size.getHeight(),
            startZ + size.getLength());

        // Build the dungeon structure asynchronously in chunks
        new BukkitRunnable() {
            private int currentX = 0;
            private int currentZ = 0;

            @Override
            public void run() {
                // Process a small chunk each tick to avoid lag
                int processed = 0;
                while (processed < 100 && currentX < size.getWidth()) {
                    if (currentZ >= size.getLength()) {
                        currentZ = 0;
                        currentX++;
                        continue;
                    }

                    buildDungeonBlock(world, startX + currentX, startY, startZ + currentZ, size);
                    currentZ++;
                    processed++;
                }

                if (currentX >= size.getWidth()) {
                    // Dungeon structure complete, add details
                    addDungeonDetails(startLocation, size, difficulty);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);

        Dungeon dungeon = new Dungeon(name, worldName, startLocation, endLocation, size, difficulty);
        plugin.getDungeonManager().addDungeon(dungeon);

        return dungeon;
    }
    
    private void buildDungeonBlock(World world, int x, int y, int z, DungeonSize size) {
        // Build walls and floors
        for (int dy = 0; dy < size.getHeight(); dy++) {
            Block block = world.getBlockAt(x, y + dy, z);

            // Determine block type based on position
            if (dy == 0) {
                // Floor
                block.setType(Material.STONE_BRICKS);
            } else if (dy == size.getHeight() - 1) {
                // Ceiling
                block.setType(Material.STONE_BRICKS);
            } else if (x == 0 || z == 0 || x == size.getWidth() - 1 || z == size.getLength() - 1) {
                // Walls
                block.setType(Material.STONE_BRICK_WALL);
            } else {
                // Interior - clear space
                block.setType(Material.AIR);
            }
        }
    }
    
    private void addDungeonDetails(Location startLocation, DungeonSize size, DungeonDifficulty difficulty) {
        World world = startLocation.getWorld();
        int startX = startLocation.getBlockX();
        int startY = startLocation.getBlockY();
        int startZ = startLocation.getBlockZ();
        
        // Add entrance
        Block entrance = world.getBlockAt(startX + size.getWidth() / 2, startY + 1, startZ);
        entrance.setType(Material.AIR);
        entrance.getRelative(BlockFace.UP).setType(Material.AIR);
        
        // Add torches for lighting
        addTorches(world, startX, startY, startZ, size);
        
        // Add treasure chests
        addTreasureChests(world, startX, startY, startZ, size);
        
        // Spawn mobs based on difficulty
        spawnDungeonMobs(world, startX, startY, startZ, size, difficulty);
    }
    
    private void addTorches(World world, int startX, int startY, int startZ, DungeonSize size) {
        int torchSpacing = 8;
        
        for (int x = startX + torchSpacing; x < startX + size.getWidth(); x += torchSpacing) {
            for (int z = startZ + torchSpacing; z < startZ + size.getLength(); z += torchSpacing) {
                Block torchBlock = world.getBlockAt(x, startY + 1, z);
                if (torchBlock.getType() == Material.AIR) {
                    Block below = torchBlock.getRelative(BlockFace.DOWN);
                    if (below.getType().isSolid()) {
                        torchBlock.setType(Material.TORCH);
                    }
                }
            }
        }
    }
    
    private void addTreasureChests(World world, int startX, int startY, int startZ, DungeonSize size) {
        int numChests = size.ordinal() + 1; // 1-4 chests based on size
        
        for (int i = 0; i < numChests; i++) {
            int x = startX + 2 + random.nextInt(size.getWidth() - 4);
            int z = startZ + 2 + random.nextInt(size.getLength() - 4);
            
            Block chestBlock = world.getBlockAt(x, startY + 1, z);
            if (chestBlock.getType() == Material.AIR) {
                chestBlock.setType(Material.CHEST);
                // TODO: Add loot to chest
            }
        }
    }
    
    private void spawnDungeonMobs(World world, int startX, int startY, int startZ, DungeonSize size, DungeonDifficulty difficulty) {
        int numMobs = difficulty.getMinMobs() + random.nextInt(difficulty.getMaxMobs() - difficulty.getMinMobs() + 1);
        
        EntityType[] mobTypes = {
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, 
            EntityType.CREEPER, EntityType.WITCH
        };
        
        for (int i = 0; i < numMobs; i++) {
            int x = startX + 2 + random.nextInt(size.getWidth() - 4);
            int z = startZ + 2 + random.nextInt(size.getLength() - 4);
            
            Location spawnLoc = new Location(world, x + 0.5, startY + 1, z + 0.5);
            EntityType mobType = mobTypes[random.nextInt(mobTypes.length)];
            
            // Spawn mob asynchronously
            Bukkit.getScheduler().runTask(plugin, () -> {
                world.spawnEntity(spawnLoc, mobType);
            });
        }
    }
    
    private String generateDungeonName() {
        String[] prefixes = {"Ancient", "Dark", "Forgotten", "Mystic", "Shadow", "Crystal", "Iron", "Golden"};
        String[] suffixes = {"Catacombs", "Ruins", "Chambers", "Depths", "Halls", "Sanctum", "Vault", "Labyrinth"};
        
        String prefix = prefixes[random.nextInt(prefixes.length)];
        String suffix = suffixes[random.nextInt(suffixes.length)];
        
        return prefix + " " + suffix;
    }
}
