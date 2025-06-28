package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule;

import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class InventoryListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        PaginatedGUI gui = InventoryManager.get(player);
        if (gui == null) return;

        event.setCancelled(true);

        if (event.getView().getTopInventory().equals(event.getClickedInventory())) {
            gui.handleClick(event);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        PaginatedGUI gui = InventoryManager.get(player);
        if (gui != null && gui.searching) return;

        InventoryManager.untrack(player);

        Bukkit.getScheduler().runTaskLater(SovereignMCMovecraftAddons.getInstance(), () -> {
            player.updateInventory();
        }, 1L);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onPlayerChat(AsyncPlayerChatEvent event) {

        Player player = event.getPlayer();
        PaginatedGUI gui = InventoryManager.get(player);

        if (gui != null && gui.searching) {
            event.setCancelled(true);
            String query = event.getMessage();

            Bukkit.getScheduler().runTask(SovereignMCMovecraftAddons.getInstance(), () -> {
                gui.searching = false;
                gui.setSearch(query);
                InventoryManager.track(player, gui);
            });
        }
    }
}