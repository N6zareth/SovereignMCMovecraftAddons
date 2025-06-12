package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

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
        if (event.getPlayer() instanceof Player player) {
            InventoryManager.untrack(player);
        }
    }
}