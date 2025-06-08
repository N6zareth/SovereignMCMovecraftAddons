package net.sovereignmc.sovereignmcmovecraftaddons.listeners;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class ReleaseMessage implements Listener {
    @EventHandler
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String message = event.getMessage();
        Player player = event.getPlayer();

        if (!message.toLowerCase().startsWith("/rotate ")) return;

        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] Pilot a craft!");
            event.setCancelled(true);
            return;
        }
    }
}