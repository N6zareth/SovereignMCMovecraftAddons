package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.Ammo;

import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AmmoCommands implements CommandExecutor {
    private final SovereignMCMovecraftAddons plugin;

    public AmmoCommands(SovereignMCMovecraftAddons plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        new AmmoGUI(player, plugin).open();
        return true;
    }
}