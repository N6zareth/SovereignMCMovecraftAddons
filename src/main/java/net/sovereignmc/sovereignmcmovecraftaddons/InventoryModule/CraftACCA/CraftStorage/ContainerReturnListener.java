package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftStorage;

import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContainerReturnListener implements Listener {
    private final Map<UUID, Long> openTimestamps = new HashMap<>();

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        if (event.getInventory().getHolder() instanceof BlockState) {
            openTimestamps.put(player.getUniqueId(), System.currentTimeMillis());
        }
    }

    @EventHandler
    public void onContainerClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;

        UUID uuid = player.getUniqueId();
        CraftStorageGUI gui = CraftStorageReturnManager.getReturnGui(uuid);
        Inventory expected = CraftStorageReturnManager.getOpenedInventory(uuid);

        if (gui == null || expected == null) return;

        if (!event.getInventory().equals(expected)) return;

        CraftStorageReturnManager.clear(uuid);

        Bukkit.getScheduler().runTask(SovereignMCMovecraftAddons.getInstance(), gui::open);
    }
}