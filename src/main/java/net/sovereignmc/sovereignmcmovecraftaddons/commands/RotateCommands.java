package net.sovereignmc.sovereignmcmovecraftaddons.commands;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RotateCommands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);

        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] Pilot a craft!");
            return true;
        }

        switch (label.toLowerCase()) {
            case "ror":
                player.performCommand("rotate right");
                break;
            case "rol":
                player.performCommand("rotate left");
                break;
            default:
                sender.sendMessage("Unknown command alias.");
                break;
        }

        return true;
    }
}