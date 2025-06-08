package net.sovereignmc.sovereignmcmovecraftaddons.commands;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class HealthBarCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player player = (Player) sender;
        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);

        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] Pilot a craft!");
            return true;
        }

        Location spawnLoc = player.getLocation().add(0, 2.5, 0); //
        MiniMessage mm = MiniMessage.miniMessage();
        String legacy = LegacyComponentSerializer.legacySection().serialize(
                mm.deserialize("<gold>testing")
        );

        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(spawnLoc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setCustomName(legacy);
        stand.setCustomNameVisible(true);
        stand.setGravity(false);
        stand.setMarker(true); // No hitbox
        stand.setSmall(true);  // Optional

        player.sendRichMessage("&4Spawned health bar above your head.");
        return true;
    }
}
