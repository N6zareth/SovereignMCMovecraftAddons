package net.sovereignmc.sovereignmcmovecraftaddons.CraftComposition;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static net.sovereignmc.sovereignmcmovecraftaddons.Deserializer.NazyDeserializer;


public class CraftCompCommands implements CommandExecutor {
    private final InventoryCancellerListener inventoryListener;
    private final SovereignMCMovecraftAddons plugin;
    private final Map<UUID, Integer> playerPages = new HashMap<>();

    public int getPage(UUID uuid) {
        return playerPages.getOrDefault(uuid, 0);
    }

    public void setPage(UUID uuid, int page) {
        playerPages.put(uuid, page);
    }

    public CraftCompCommands(SovereignMCMovecraftAddons plugin, InventoryCancellerListener inventoryListener) {
        this.plugin = plugin;
        this.inventoryListener = inventoryListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        setPage(player.getUniqueId(), 0);
        openCompInventory(player);
        return true;
    }

    public void openCompInventory(Player player) {
        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] Pilot a craft!");
            return;
        }

        String craftType = craft.getType().getStringProperty(CraftType.NAME);
        String playerName = player.getName();
        Component ccm = MiniMessage.miniMessage().deserialize("<#6E97C8>" + playerName + "'s " + craftType);

        Map<Material, Integer> blockCounts = new HashMap<>();
        World world = craft.getWorld();

        for (MovecraftLocation loc : craft.getHitBox()) {
            Block block = world.getBlockAt(loc.getX(), loc.getY(), loc.getZ());
            Material rawType = block.getType();
            Material type = BlockCountConsolidator.normalizeMaterial(rawType);

            if (type == null || type == Material.AIR || !type.isItem()) {
                continue;
            }
            blockCounts.merge(type, 1, Integer::sum);
        }

        List<Map.Entry<Material, Integer>> sortedEntries = new ArrayList<>(blockCounts.entrySet());
        sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));

        int page = getPage(player.getUniqueId());
        plugin.getLogger().info("DEBUG: Opening comp inventory for " + player.getName() + " on page " + page);


        int itemsPerPage = 45;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, sortedEntries.size());

        plugin.getLogger().info("Start index: " + start + ", End index: " + end);
        plugin.getLogger().info("Start index: " + start + ", End index: " + end);

        if (start >= sortedEntries.size() && page > 0) {
            page--;
            setPage(player.getUniqueId(), page);
            start = page * itemsPerPage;
            end = Math.min(start + itemsPerPage, sortedEntries.size());
        }

        int visibleItemCount = end - start;
        int rows = (int) Math.ceil(visibleItemCount / 9.0);
        boolean needsNav = (end < sortedEntries.size()) || (page > 0);

        if (needsNav) {
            rows += 1; // Add extra row only if nav is needed
        }
        rows = Math.max(1, Math.min(rows, 6));
        int size = rows * 9;

        Inventory inventory = Bukkit.createInventory(null, size, ccm);


        for (int i = 0; i < (end - start); i++) {
            Map.Entry<Material, Integer> entry = sortedEntries.get(start + i);
            ItemStack item = new ItemStack(entry.getKey());
            ItemMeta meta = item.getItemMeta();

            if (meta != null) {
                String prettyName = formatMaterialName(entry.getKey());
                if (entry.getKey() == Material.WHITE_CONCRETE) {
                    prettyName = "Concrete";
                }
                if (entry.getKey() == Material.WHITE_WOOL) {
                    prettyName = "Wool";
                }

                int totalBlocks = blockCounts.values().stream().mapToInt(Integer::intValue).sum();
                double percent = (entry.getValue() * 100.0) / totalBlocks;

                meta.setDisplayName(NazyDeserializer("<white><!i>" + prettyName));
                meta.setLore(List.of(
                        NazyDeserializer("<green><!i>Amount: </green><yellow>" + entry.getValue() + "</yellow>"),
                        NazyDeserializer("<green><!i>Percentage: </green><yellow>" + String.format("%.1f", percent) + "%</yellow>")
                ));
                item.setItemMeta(meta);
            }

            if (item != null) {
                inventory.setItem(i, item);
            } else {
                plugin.getLogger().warning("Null item for material: " + entry.getKey());
            }
        }

        if (needsNav) {
            int lastRowStart = size - 9;

            for (int slot = lastRowStart; slot < size; slot++) {
                inventory.setItem(slot, filler());
            }

            if (page > 0) {
                inventory.setItem(lastRowStart + 3, navButton("Previous Page", 555501));
            }
            if (end < sortedEntries.size()) {
                inventory.setItem(lastRowStart + 5, navButton("Next Page", 555502));
            }

            inventory.setItem(lastRowStart + 4, closeButton());
        }

        inventoryListener.track(inventory);
        player.openInventory(inventory);
    }

    private String formatMaterialName(Material material) {
        return Arrays.stream(material.name().toLowerCase().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }

    private ItemStack navButton(String name, int modelData) {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer("<gray><!i>" + name));
            meta.setCustomModelData(modelData);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack closeButton() {
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(NazyDeserializer("<red><!i>Close"));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack filler() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }
}