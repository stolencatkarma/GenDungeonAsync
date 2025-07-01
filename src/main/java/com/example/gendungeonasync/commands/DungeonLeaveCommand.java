package com.example.gendungeonasync.commands;

import com.example.gendungeonasync.GenDungeonAsync;
import com.example.gendungeonasync.model.Dungeon;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DungeonLeaveCommand implements CommandExecutor {
    
    private final GenDungeonAsync plugin;
    
    public DungeonLeaveCommand(GenDungeonAsync plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can only be used by players!")
                    .color(NamedTextColor.RED));
            return true;
        }
        
        // Check if player has an active dungeon
        Dungeon playerDungeon = plugin.getDungeonManager().getPlayerDungeon(player).orElse(null);
        
        if (playerDungeon == null) {
            player.sendMessage(Component.text("You are not currently in a dungeon!")
                    .color(NamedTextColor.YELLOW));
            return true;
        }
        
        // Store the dungeon name for the message
        String dungeonName = playerDungeon.getName();

        // Get previous location if available
        var prevLocOpt = plugin.getDungeonManager().getPlayerPreviousLocation(player);

        // Remove player from dungeon
        plugin.getDungeonManager().removePlayerFromDungeon(player);

        // Teleport player back to previous location or world spawn
        if (prevLocOpt.isPresent()) {
            player.teleport(prevLocOpt.get());
            player.sendMessage(Component.text("You have left the dungeon '")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(dungeonName)
                            .color(NamedTextColor.GOLD))
                    .append(Component.text("' and returned to your previous location.")
                            .color(NamedTextColor.GREEN)));
        } else {
            player.teleport(player.getWorld().getSpawnLocation());
            player.sendMessage(Component.text("You have left the dungeon '")
                    .color(NamedTextColor.GREEN)
                    .append(Component.text(dungeonName)
                            .color(NamedTextColor.GOLD))
                    .append(Component.text("' and returned to spawn.")
                            .color(NamedTextColor.GREEN)));
        }

        // Optional: Clean up empty dungeons
        if (playerDungeon.getPlayers().isEmpty()) {
            plugin.getDungeonManager().removeDungeon(playerDungeon.getId());
            plugin.getLogger().info("Cleaned up empty dungeon: " + dungeonName + " (" + playerDungeon.getId() + ")");
        }

        return true;
    }
}
