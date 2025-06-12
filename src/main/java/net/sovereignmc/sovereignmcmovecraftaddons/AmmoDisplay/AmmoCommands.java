package net.sovereignmc.sovereignmcmovecraftaddons.AmmoDisplay;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.Deserializer;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.InventoryCancellerListener;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class AmmoCommands implements CommandExecutor {
    private final InventoryCancellerListener inventoryListener;
    private final SovereignMCMovecraftAddons plugin;

    public AmmoCommands(SovereignMCMovecraftAddons plugin, InventoryCancellerListener inventoryListener) {
        this.plugin = plugin;
        this.inventoryListener = inventoryListener;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return false;
        openAmmoInventory(player);
        return true;
    }

    public void openAmmoInventory(Player player) {
        Component title = MiniMessage.miniMessage().deserialize("<#6E97C8>Ammo Selection");
        Inventory inv = Bukkit.createInventory(null, 9 * 3, title);

        int slot = 0;
        for (ShellsEnum shell : ShellsEnum.values()) {
            ItemStack item = new ItemStack(shell.getDisplayItem());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(Deserializer.NazyDeserializer("<white><!i>" + shell.getDisplayName()));
                meta.setLore(List.of(Deserializer.NazyDeserializer("<gray>Click to receive 64")));

                NamespacedKey key = new NamespacedKey(plugin, "shell_id");
                meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, shell.getSfId());

                item.setItemMeta(meta);
            }
            inv.setItem(slot++, item);
        }

        for (int i = slot; i < inv.getSize(); i++) {
            inv.setItem(i, filler());
        }

        inventoryListener.track(player, inv, GUIEnum.AMMO);
        player.openInventory(inv);
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