package net.sovereignmc.sovereignmcmovecraftaddons.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class RemoveTextDisplaysCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public RemoveTextDisplaysCommand(JavaPlugin plugin) {
        this.plugin = plugin;
        plugin.getCommand("cleardisplays").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can run this command.");
            return true;
        }

        int removedCount = 0;

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity.getType() == EntityType.TEXT_DISPLAY) {
                entity.remove();
                removedCount++;
            }
        }

        player.sendMessage(ChatColor.GREEN + "Removed " + removedCount + " TextDisplay entities.");
        return true;
    }
}