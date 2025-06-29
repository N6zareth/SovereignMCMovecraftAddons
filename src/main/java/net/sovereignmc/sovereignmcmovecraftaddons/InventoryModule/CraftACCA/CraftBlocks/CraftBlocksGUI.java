package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftBlocks;

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

public class CraftBlocksGUI extends PaginatedGUI {
    private final String craftName;
    private final Set<Material> allowed;

    public CraftBlocksGUI(Player player, String craftName, Set<Material> allowed) {
        super(player);
        this.craftName = craftName;
        this.allowed = allowed;
    }

    @Override
    protected List<ItemStack> getPageItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Material mat : allowed) {
            if (!mat.isBlock()) continue;

            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                String prettyName = formatMaterialName(mat);
                meta.setDisplayName(NazyDeserializer("<white><!i>" + prettyName));
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
        return MiniMessage.miniMessage().deserialize("<dark_green>Allowed Blocks <dark_gray>- <dark_green>" + craftName);
    }

    private String formatMaterialName(Material mat) {
        return Arrays.stream(mat.name().toLowerCase().split("_"))
                .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1))
                .collect(Collectors.joining(" "));
    }
}