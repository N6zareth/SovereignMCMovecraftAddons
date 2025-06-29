package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
    protected String currentSearch = null;
    protected boolean searching = false;

    public PaginatedGUI(Player player) {
        this.player = player;
        this.playerId = player.getUniqueId();
    }

    public void open() {
        Inventory inventory = Bukkit.createInventory(player, 54, getTitle());

        List<ItemStack> allItems = getFilteredItems();

        int maxPages = Math.max(1, (int) Math.ceil(allItems.size() / (double) itemsPerPage));
        currentPage = Math.min(currentPage, maxPages - 1);

        int start = currentPage * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allItems.size());

        List<ItemStack> pageItems = allItems.subList(start, end);

        System.out.println(allItems.size());

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
            inv.setItem(navRowStart + 3, navButton("Previous Page", 2200007));
        }

        if (start + itemsPerPage < totalSize) {
            inv.setItem(navRowStart + 5, navButton("Next Page", 2200009));
        }

        if (isSearchEnabled()) {
            ItemStack searchItem = new ItemStack(Material.NAME_TAG);
            ItemMeta meta = searchItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(NazyDeserializer("<white><!i>Search"));
                meta.setCustomModelData(2200005);
                searchItem.setItemMeta(meta);
            }
            inv.setItem(navRowStart + 4, searchItem);
        }
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
                case 2200007 -> {
                    currentPage = Math.max(0, currentPage - 1);
                    open();
                }
                case 2200009 -> {
                    currentPage++;
                    open();
                }
                case 2200005 -> {
                    if (isSearchEnabled()) {
                        searching = true;
                        player.closeInventory();
                        player.sendRichMessage("<gray>Type your search term in chat...");
                    }
                }
                default -> onItemClick(event.getSlot(), clicked);
            }
        } else {
            onItemClick(event.getSlot(), clicked);
        }
    }

    protected ItemStack navButton(String name, int modelData) {
        ItemStack item = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer("<white><!i>" + name));
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
        }
        return item;
    }

    protected boolean isSearchEnabled() {
        return true;
    }

    protected ItemStack filler() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer(" "));
            meta.setCustomModelData(2200002);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void setSearch(String query) {
        this.currentSearch = query == null ? null : query.toLowerCase();
        this.currentPage = 0;
        open();
    }

    private static final MiniMessage MINI = MiniMessage.miniMessage();
    private List<ItemStack> getFilteredItems() {
        List<ItemStack> all = getPageItems();

        if (currentSearch == null) return all;

        return all.stream()
                .filter(item -> {
                    if (!item.hasItemMeta()) return false;
                    ItemMeta meta = item.getItemMeta();
                    return meta.hasDisplayName() &&
                            ChatColor.stripColor(meta.getDisplayName()).toLowerCase().contains(currentSearch);
                })
                .toList();
    }

    protected abstract List<ItemStack> getPageItems();
    protected abstract void onItemClick(int slot, ItemStack item);
    protected abstract Component getTitle();
}