package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftStorage;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CraftStorageCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;

        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] Pilot a craft!");
            return true;
        }

        new CraftStorageGUI(player, craft).open();
        return true;
    }
}
