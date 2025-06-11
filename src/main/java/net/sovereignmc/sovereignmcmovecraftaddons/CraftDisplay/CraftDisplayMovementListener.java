package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class CraftDisplayMovementListener implements Listener {
    private final JavaPlugin plugin;
    private final CraftDisplayManager manager;

    public CraftDisplayMovementListener(JavaPlugin plugin, CraftDisplayManager manager) {
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