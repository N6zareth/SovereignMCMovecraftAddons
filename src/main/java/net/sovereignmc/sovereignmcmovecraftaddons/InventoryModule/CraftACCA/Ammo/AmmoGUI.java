package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.Ammo;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.PaginatedGUI;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.Deserializer;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AmmoGUI extends PaginatedGUI {
    private final SovereignMCMovecraftAddons plugin;

    public AmmoGUI(Player player, SovereignMCMovecraftAddons plugin) {
        super(player);
        this.plugin = plugin;
    }

    @Override
    protected List<ItemStack> getPageItems() {
        List<ItemStack> items = new ArrayList<>();
        for (ShellsEnum shell : ShellsEnum.values()) {
            ItemStack item = new ItemStack(shell.getDisplayItem());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(Deserializer.NazyDeserializer("<white><!i>" + shell.getDisplayName()));
                meta.setLore(List.of(Deserializer.NazyDeserializer("<gray>Click to receive 64")));

                NamespacedKey key = new NamespacedKey(plugin, "shell_id");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, shell.getSfId());

                meta.setCustomModelData(shell.getCustomModelData());
                item.setItemMeta(meta);
            }
            items.add(item);
        }
        return items;
    }

    @Override
    protected void onItemClick(int slot, ItemStack item) {
        NamespacedKey key = new NamespacedKey(plugin, "shell_id");
        ItemMeta meta = item.getItemMeta();
        if (meta != null && meta.getPersistentDataContainer().has(key, PersistentDataType.STRING)) {
            String shellId = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);

            int amount = 64;
            String command = String.format("sf give %s %s %d", player.getName(), shellId, amount);
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

            player.sendRichMessage(("<green>Gave you 64 </green><yellow>" + shellId + "</yellow>"));
        }
    }

    @Override
    protected Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<#6E97C8>Ammo Selection");
    }
}