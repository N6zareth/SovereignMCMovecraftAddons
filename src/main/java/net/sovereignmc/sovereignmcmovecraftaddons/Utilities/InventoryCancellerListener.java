package net.sovereignmc.sovereignmcmovecraftaddons.Utilities;

import net.sovereignmc.sovereignmcmovecraftaddons.AmmoDisplay.AmmoCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.AmmoDisplay.GUIEnum;
import net.sovereignmc.sovereignmcmovecraftaddons.AmmoDisplay.ShellsEnum;
import net.sovereignmc.sovereignmcmovecraftaddons.CraftComposition.CraftCompCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class InventoryCancellerListener implements Listener {

    private final Set<Inventory> trackedInventories = new HashSet<>();
    private final Map<UUID, GUIEnum> playerGuiMap = new HashMap<>();
    private CraftCompCommands compCommandHandler;
    private AmmoCommands ammoCommandHandler;

    private final SovereignMCMovecraftAddons plugin;

    public InventoryCancellerListener(SovereignMCMovecraftAddons plugin) {
        this.plugin = plugin;
    }

    public void setCompCommandHandler(CraftCompCommands handler) {
        this.compCommandHandler = handler;
    }

    public void setAmmoCommandHandler(AmmoCommands handler) {
        this.ammoCommandHandler = handler;
    }

    public void track(Player player, Inventory inv, GUIEnum type) {
        trackedInventories.add(inv);
        playerGuiMap.put(player.getUniqueId(), type);
    }

    public void untrack(Player player, Inventory inv) {
        trackedInventories.remove(inv);
        playerGuiMap.remove(player.getUniqueId());
    }

    public boolean isTracked(Inventory inv) {
        return trackedInventories.contains(inv);
    }

    public GUIEnum getGuiType(UUID playerId) {
        return playerGuiMap.get(playerId);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory clickedInventory = event.getView().getTopInventory();
        if (!isTracked(clickedInventory)) return;

        event.setCancelled(true);

        UUID uuid = player.getUniqueId();
        GUIEnum type = getGuiType(uuid);
        if (type == null) return;

        switch (type) {
            case COMP -> {
                int slot = event.getRawSlot();
                if (slot < 0 || slot >= clickedInventory.getSize()) return;

                int currentPage = compCommandHandler.getPage(uuid);
                int size = clickedInventory.getSize();
                int lastRowStart = size - 9;

                int prevSlot = lastRowStart + 3;
                int closeSlot = lastRowStart + 4;
                int nextSlot = lastRowStart + 5;

                if (slot == prevSlot && currentPage > 0) {
                    compCommandHandler.setPage(uuid, currentPage - 1);
                    compCommandHandler.openCompInventory(player);
                } else if (slot == nextSlot) {
                    compCommandHandler.setPage(uuid, currentPage + 1);
                    compCommandHandler.openCompInventory(player);
                } else if (slot == closeSlot) {
                    player.closeInventory();
                }
            }
            case AMMO -> {
                int slot = event.getRawSlot();
                ItemStack clickedItem = event.getCurrentItem();
                if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

                ItemMeta meta = clickedItem.getItemMeta();
                if (meta == null) return;

                NamespacedKey key = new NamespacedKey(plugin, "shell_id");
                String sfId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
                if (sfId == null) return;

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "sf give " + player.getName() + " " + sfId + " 64");

                ShellsEnum match = Arrays.stream(ShellsEnum.values())
                        .filter(s -> s.getSfId().equals(sfId))
                        .findFirst()
                        .orElse(null);

                String display = (match != null) ? match.getDisplayName() : sfId.replace("_", " ");
                player.sendRichMessage("<green>You received 64 <white>" + display + "<green>!");
                player.closeInventory();
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            Inventory inv = event.getView().getTopInventory();
            untrack(player, inv);
        }
    }
}