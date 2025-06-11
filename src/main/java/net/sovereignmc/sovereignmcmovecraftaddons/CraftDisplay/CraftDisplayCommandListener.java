package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class CraftDisplayCommandListener implements Listener {
    private final CraftDisplayStorage store;
    private final CraftDisplayManager manager;
    private final SovereignMCMovecraftAddons plugin;

    public CraftDisplayCommandListener(SovereignMCMovecraftAddons plugin, CraftDisplayStorage store, CraftDisplayManager manager) {
        this.plugin = plugin;
        this.store = store;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPilotCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        if (msg.startsWith("/pilot")) {
            Craft currentCraft = CraftManager.getInstance().getCraftByPlayer(player);
            if (currentCraft != null) return;
            store.pendingPilot.put(player.getUniqueId(), System.currentTimeMillis());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                manager.attemptSpawnDisplay(player);
            }, 1L);
        }
    }
}