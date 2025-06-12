package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

import static net.sovereignmc.sovereignmcmovecraftaddons.Utilities.Deserializer.NazyDeserializer;

public abstract class PaginatedGUI {
    protected final Player player;
    protected final UUID playerId;
    protected int currentPage = 0;
    protected final int itemsPerPage = 45;

    public PaginatedGUI(Player player) {
        this.player = player;
        this.playerId = player.getUniqueId();
    }

    public void open() {
        Inventory inventory = Bukkit.createInventory(player, 54, getTitle());

        List<ItemStack> allItems = getPageItems();
        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allItems.size());
        List<ItemStack> pageItems = allItems.subList(start, end);

        for (int i = 0; i < pageItems.size(); i++) {
            inventory.setItem(i, pageItems.get(i));
        }

        addNavigationControls(inventory, start, allItems.size());

        player.openInventory(inventory);

        InventoryManager.track(player, this);
    }

    private void addNavigationControls(Inventory inv, int start, int totalSize) {
        int navRowStart = 45;

        for (int i = navRowStart; i < 54; i++) {
            inv.setItem(i, filler());
        }

        if (currentPage > 0) {
            inv.setItem(navRowStart + 3, navButton("Previous Page", 555501));
        }
        if (start + itemsPerPage < totalSize) {
            inv.setItem(navRowStart + 5, navButton("Next Page", 555502));
        }

        inv.setItem(navRowStart + 4, closeButton());
    }

    public void handleClick(InventoryClickEvent event) {
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) return;

        if (clicked.getType() == Material.BARRIER) {
            player.closeInventory();
            return;
        }

        if (meta.hasCustomModelData()) {
            switch (meta.getCustomModelData()) {
                case 555501 -> {
                    currentPage = Math.max(0, currentPage - 1);
                    open();
                }
                case 555502 -> {
                    currentPage++;
                    open();
                }
                default -> onItemClick(event.getSlot(), clicked);
            }
        } else {
            onItemClick(event.getSlot(), clicked);
        }
    }

    protected ItemStack navButton(String name, int modelData) {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer("<gray><!i>" + name));
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack closeButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer("<red><!i>Close"));
            item.setItemMeta(meta);
        }
        return item;
    }

    protected ItemStack filler() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer(" "));
            meta.setCustomModelData(555599);
            item.setItemMeta(meta);
        }
        return item;
    }

    protected abstract List<ItemStack> getPageItems();
    protected abstract void onItemClick(int slot, ItemStack item);
    protected abstract Component getTitle();
}