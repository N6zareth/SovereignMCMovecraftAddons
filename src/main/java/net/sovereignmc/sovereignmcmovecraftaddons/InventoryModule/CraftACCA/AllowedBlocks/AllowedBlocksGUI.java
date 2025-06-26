package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.AllowedBlocks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftComposition.BlockCountConsolidator;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.PaginatedGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static net.sovereignmc.sovereignmcmovecraftaddons.Utilities.Deserializer.NazyDeserializer;

public class AllowedBlocksGUI extends PaginatedGUI {
    private final String craftName;
    private final Set<Material> allowed;

    public AllowedBlocksGUI(Player player, String craftName, Set<Material> allowed) {
        super(player);
        this.craftName = craftName;
        this.allowed = allowed;
    }

    @Override
    protected List<ItemStack> getPageItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Material original : allowed) {
            Material mat = BlockCountConsolidator.normalizeMaterial(original);
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String prettyName = formatMaterialName(mat);
                meta.setDisplayName(NazyDeserializer(prettyName));
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
        return MiniMessage.miniMessage().deserialize("<green>" + craftName + " Allowed Blocks");
    }

    private String formatMaterialName(Material material) {
        String name = material.name().toLowerCase().replace('_', ' ');

        if (name.startsWith("white ") && name.endsWith("wool")) return "Wool";
        if (name.startsWith("white ") && name.endsWith("concrete")) return "Concrete";
        if (name.endsWith("planks")) return name.replace("planks", "wood");

        return Arrays.stream(name.split(" "))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }
}