package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftComposition;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.PaginatedGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

import static net.sovereignmc.sovereignmcmovecraftaddons.Utilities.Deserializer.NazyDeserializer;

public class CraftCompGUI extends PaginatedGUI {
    private final Craft craft;
    private final List<Map.Entry<Material, Integer>> sortedEntries;
    private final Map<Material, Integer> blockCounts;

    public CraftCompGUI(Player player, Craft craft) {
        super(player);
        this.craft = craft;

        this.blockCounts = new HashMap<>();
        for (MovecraftLocation loc : craft.getHitBox()) {
            Material mat = BlockCountConsolidator.normalizeMaterial(
                    craft.getWorld().getBlockAt(loc.getX(), loc.getY(), loc.getZ()).getType()
            );
            if (mat != null && mat != Material.AIR && mat.isItem()) {
                blockCounts.merge(mat, 1, Integer::sum);
            }
        }

        this.sortedEntries = new ArrayList<>(blockCounts.entrySet());
        this.sortedEntries.sort((a, b) -> b.getValue().compareTo(a.getValue()));
    }

    @Override
    protected List<ItemStack> getPageItems() {
        List<ItemStack> items = new ArrayList<>();
        int total = blockCounts.values().stream().mapToInt(Integer::intValue).sum();

        for (Map.Entry<Material, Integer> entry : sortedEntries) {
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
            items.add(item);
        }

        return items;
    }

    @Override
    protected void onItemClick(int slot, ItemStack item) {
    }

    @Override
    protected Component getTitle() {
        String type = craft.getType().getStringProperty(CraftType.NAME);
        return MiniMessage.miniMessage().deserialize("<#6E97C8>" + player.getName() + "'s " + type);
    }

    private String formatMaterialName(Material mat) {
        return Arrays.stream(mat.name().toLowerCase().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}