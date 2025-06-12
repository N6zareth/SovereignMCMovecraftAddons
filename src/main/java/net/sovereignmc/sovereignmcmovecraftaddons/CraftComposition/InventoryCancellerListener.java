package net.sovereignmc.sovereignmcmovecraftaddons.CraftComposition;

import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class InventoryCancellerListener implements Listener {

    private final Set<Inventory> trackedCompInventories = new HashSet<>();
    private CraftCompCommands commandHandler;

    public InventoryCancellerListener() {}

    public void setCommandHandler(CraftCompCommands commandHandler) {
        this.commandHandler = commandHandler;
    }

    public boolean isTracked(Inventory inv) {
        return trackedCompInventories.contains(inv);
    }

    public void track(Inventory inv) {
        trackedCompInventories.add(inv);
    }

    public void untrack(Inventory inv) {
        trackedCompInventories.remove(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity clicker = event.getWhoClicked();
        if (!(clicker instanceof Player player)) return;

        Inventory clickedInventory = event.getView().getTopInventory();
        if (!isTracked(clickedInventory)) return;
        event.setCancelled(true);

        int slot = event.getRawSlot();
        if (slot >= clickedInventory.getSize()) return;

        UUID uuid = player.getUniqueId();
        int currentPage = commandHandler.getPage(uuid);

        int size = clickedInventory.getSize();
        int lastRowStart = size - 9;

        int prevSlot = lastRowStart + 3;
        int closeSlot = lastRowStart + 4;
        int nextSlot = lastRowStart + 5;

        if (slot == prevSlot && currentPage > 0) {
            commandHandler.setPage(uuid, currentPage - 1);
            commandHandler.openCompInventory(player);
            return;
        }

        if (slot == nextSlot) {
            commandHandler.setPage(uuid, currentPage + 1);
            commandHandler.openCompInventory(player);
            return;
        }

        if (slot == closeSlot) {
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Inventory closed = event.getView().getTopInventory();
        trackedCompInventories.remove(closed);
    }
}