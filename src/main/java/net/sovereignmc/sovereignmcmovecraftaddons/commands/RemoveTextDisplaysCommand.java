package net.sovereignmc.sovereignmcmovecraftaddons.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class RemoveTextDisplaysCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendRichMessage("<dark_red>Only players can run this command</dark_red>");
            return true;
        }

        if (!player.hasPermission("nazy.cleardisplays")) {
            player.sendRichMessage("<red>Permissions disabled!");
            return true;
        }

        int removedCount = 0;

        for (Entity entity : player.getNearbyEntities(3, 3, 3)) {
            if (entity.getType() == EntityType.TEXT_DISPLAY) {
                entity.remove();
                removedCount++;
            }
        }

        player.sendRichMessage("<dark_red>Removed</dark_red> <white>" + removedCount + "</white> <dark_red>TextDisplay entities.</dark_red>");
        return true;
    }
}