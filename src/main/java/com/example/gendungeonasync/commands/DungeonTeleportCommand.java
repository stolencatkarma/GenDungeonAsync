package com.example.gendungeonasync.commands;

import com.example.gendungeonasync.GenDungeonAsync;
import com.example.gendungeonasync.model.Dungeon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DungeonTeleportCommand implements CommandExecutor, TabCompleter {
    
    private final GenDungeonAsync plugin;
    
    public DungeonTeleportCommand(GenDungeonAsync plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("gendungeon.teleport")) {
            sender.sendMessage(Component.text("You don't have permission to teleport to dungeons!")
                    .color(NamedTextColor.RED));
            return true;
        }
        
        if (args.length < 1) {
            sender.sendMessage(Component.text("Usage: /dungeontp <player> [dungeon_id]")
                    .color(NamedTextColor.RED));
            return true;
        }
        
        Player targetPlayer = Bukkit.getPlayer(args[0]);
        if (targetPlayer == null) {
            sender.sendMessage(Component.text("Player not found!")
                    .color(NamedTextColor.RED));
            return true;
        }
        
        Dungeon targetDungeon = null;
        
        if (args.length > 1) {
            // Specific dungeon ID provided
            try {
                UUID dungeonId = UUID.fromString(args[1]);
                targetDungeon = plugin.getDungeonManager().getDungeon(dungeonId).orElse(null);
                
                if (targetDungeon == null) {
                    sender.sendMessage(Component.text("Dungeon not found!")
                            .color(NamedTextColor.RED));
                    return true;
                }
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.text("Invalid dungeon ID!")
                        .color(NamedTextColor.RED));
                return true;
            }
        } else {
            // Use player's current dungeon
            targetDungeon = plugin.getDungeonManager().getPlayerDungeon(targetPlayer).orElse(null);
            
            if (targetDungeon == null) {
                sender.sendMessage(Component.text("Player doesn't have an active dungeon!")
                        .color(NamedTextColor.RED));
                return true;
            }
        }
        
        // Teleport the player
        if (sender instanceof Player senderPlayer) {
            senderPlayer.teleport(targetDungeon.getStartLocation().add(
                    targetDungeon.getSize().getWidth() / 2.0, 2, 0));
            
            senderPlayer.sendMessage(Component.text("Teleported to ")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(targetPlayer.getName())
                            .color(NamedTextColor.YELLOW))
                    .append(Component.text("'s dungeon: ")
                            .color(NamedTextColor.GREEN))
                    .append(Component.text(targetDungeon.getName())
                            .color(NamedTextColor.GOLD)));
        } else {
            sender.sendMessage(Component.text("Console cannot teleport!")
                    .color(NamedTextColor.RED));
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // Player name completion
            String partial = args[0].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partial)) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 2) {
            // Dungeon ID completion (show available dungeons)
            String partial = args[1].toLowerCase();
            Map<UUID, Dungeon> dungeons = plugin.getDungeonManager().getAllDungeons();
            
            for (Map.Entry<UUID, Dungeon> entry : dungeons.entrySet()) {
                String dungeonId = entry.getKey().toString();
                String dungeonName = entry.getValue().getName();
                
                if (dungeonId.toLowerCase().startsWith(partial) || 
                    dungeonName.toLowerCase().startsWith(partial)) {
                    completions.add(dungeonId);
                }
            }
        }
        
        return completions;
    }
}
