package com.example.gendungeonasync.commands;

import com.example.gendungeonasync.GenDungeonAsync;
import com.example.gendungeonasync.model.Dungeon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class DungeonCommand implements CommandExecutor, TabCompleter {
    
    private final GenDungeonAsync plugin;
    
    public DungeonCommand(GenDungeonAsync plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!")
                    .color(NamedTextColor.RED));
            return true;
        }
        
        if (!player.hasPermission("gendungeon.create")) {
            player.sendMessage(Component.text("You don't have permission to create dungeons!")
                    .color(NamedTextColor.RED));
            return true;
        }
        
        // Parse arguments
        Dungeon.DungeonSize size = Dungeon.DungeonSize.MEDIUM;
        Dungeon.DungeonDifficulty difficulty = Dungeon.DungeonDifficulty.NORMAL;
        
        if (args.length > 0) {
            try {
                size = Dungeon.DungeonSize.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(Component.text("Invalid size! Valid sizes: SMALL, MEDIUM, LARGE, HUGE")
                        .color(NamedTextColor.RED));
                return true;
            }
        }
        
        if (args.length > 1) {
            try {
                difficulty = Dungeon.DungeonDifficulty.valueOf(args[1].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage(Component.text("Invalid difficulty! Valid difficulties: EASY, NORMAL, HARD, EXPERT")
                        .color(NamedTextColor.RED));
                return true;
            }
        }
        
        // Make final for lambda usage
        final Dungeon.DungeonSize finalSize = size;
        final Dungeon.DungeonDifficulty finalDifficulty = difficulty;
        
        // Check if player already has a dungeon
        if (plugin.getDungeonManager().getPlayerDungeon(player).isPresent()) {
            player.sendMessage(Component.text("You already have an active dungeon! Complete it first.")
                    .color(NamedTextColor.YELLOW));
            return true;
        }
        
        // Generate dungeon asynchronously
        player.sendMessage(Component.text("Generating your dungeon... Please wait!")
                .color(NamedTextColor.GREEN));
        
        CompletableFuture<Dungeon> dungeonFuture = plugin.getDungeonGenerator()
                .generateDungeonAsync(player, finalSize, finalDifficulty);
        
        dungeonFuture.thenAccept(dungeon -> {
            // This runs on the main thread
            plugin.getDungeonManager().assignPlayerToDungeon(player, dungeon);
            
            player.sendMessage(Component.text("Dungeon '")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(dungeon.getName())
                            .color(NamedTextColor.GOLD))
                    .append(Component.text("' has been generated!")
                            .color(NamedTextColor.GREEN)));
            
            player.sendMessage(Component.text("Size: ")
                    .color(NamedTextColor.GRAY)
                    .append(Component.text(finalSize.name())
                            .color(NamedTextColor.WHITE))
                    .append(Component.text(" | Difficulty: ")
                            .color(NamedTextColor.GRAY))
                    .append(Component.text(finalDifficulty.name())
                            .color(NamedTextColor.WHITE)));
            
            // Teleport player to dungeon entrance
            player.teleport(dungeon.getStartLocation().add(
                    dungeon.getSize().getWidth() / 2.0, 2, 0));
            
        }).exceptionally(throwable -> {
            player.sendMessage(Component.text("Failed to generate dungeon: " + throwable.getMessage())
                    .color(NamedTextColor.RED));
            plugin.getLogger().severe("Failed to generate dungeon for " + player.getName() + ": " + throwable.getMessage());
            return null;
        });
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Size completion
            for (Dungeon.DungeonSize size : Dungeon.DungeonSize.values()) {
                if (size.name().toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(size.name().toLowerCase());
                }
            }
        } else if (args.length == 2) {
            // Difficulty completion
            for (Dungeon.DungeonDifficulty difficulty : Dungeon.DungeonDifficulty.values()) {
                if (difficulty.name().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(difficulty.name().toLowerCase());
                }
            }
        }
        
        return completions;
    }
}
