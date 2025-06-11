package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;

public class CraftDisplayMovementListener implements Listener {
    private final SovereignMCMovecraftAddons plugin;
    private final CraftDisplayManager manager;

    public CraftDisplayMovementListener(SovereignMCMovecraftAddons plugin, CraftDisplayManager manager) {
        this.plugin = plugin;
        this.manager = manager;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftMove(CraftTranslateEvent event) {
        Craft craft = event.getCraft();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            manager.onCraftMove(craft);
        }, 1L);
    }
}