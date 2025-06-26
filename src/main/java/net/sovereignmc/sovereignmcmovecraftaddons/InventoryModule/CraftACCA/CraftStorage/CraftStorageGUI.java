package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftStorage;

import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.PaginatedGUI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.stream.Collectors;

public class CraftStorageGUI extends PaginatedGUI {
    private final List<Block> containers;

    public CraftStorageGUI(Player player, Craft craft) {
        super(player);
        this.containers = new ArrayList<>();

        for (MovecraftLocation loc : craft.getHitBox()) {
            Block block = craft.getWorld().getBlockAt(loc.getX(), loc.getY(), loc.getZ());
            if (block != null && isValidStorage(block.getType())) {
                containers.add(block);
            }
        }
    }

    private boolean isValidStorage(Material type) {
        return switch (type) {
            case CHEST, BARREL, FURNACE -> true;
            default -> false;
        };
    }

    @Override
    protected List<ItemStack> getPageItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Block block : containers) {
            if (!(block.getState() instanceof InventoryHolder holder)) continue;

            Map<String, Integer> itemToAmount = new LinkedHashMap<>();

            for (ItemStack stack : holder.getInventory().getContents()) {
                if (stack == null || stack.getType() == Material.AIR) continue;

                String itemName;
                if (stack.hasItemMeta() && stack.getItemMeta().hasDisplayName()) {
                    itemName = PlainTextComponentSerializer.plainText().serialize(stack.getItemMeta().displayName());
                } else {
                    itemName = formatMaterialName(stack.getType());
                }

                itemToAmount.merge(itemName, stack.getAmount(), Integer::sum);
            }

            ItemStack icon = new ItemStack(block.getType());
            ItemMeta meta = icon.getItemMeta();
            if (meta != null) {
                meta.displayName(MiniMessage.miniMessage().deserialize("<white><!i>" + formatMaterialName(block.getType())));

                List<Component> lore = new ArrayList<>();
                if (itemToAmount.isEmpty()) {
                    lore.add(MiniMessage.miniMessage().deserialize("<dark_gray><!i>Empty"));
                } else {
                    for (Map.Entry<String, Integer> entry : itemToAmount.entrySet()) {
                        String name = entry.getKey();
                        int amount = entry.getValue();

                        String mmLine = "<yellow><!i>" + amount + " <gray>" + name + "s";
                        lore.add(MiniMessage.miniMessage().deserialize(mmLine));
                    }
                }

                meta.lore(lore);
                icon.setItemMeta(meta);
            }

            items.add(icon);
        }

        return items;
    }

    @Override
    protected void onItemClick(int slot, ItemStack item) {
        if (slot >= containers.size()) return;

        Block block = containers.get(slot);
        BlockState state = block.getState();

        if (state instanceof InventoryHolder holder) {
            CraftStorageReturnManager.setReturnGui(player.getUniqueId(), this);
            CraftStorageReturnManager.setOpenedInventory(player.getUniqueId(), holder.getInventory());

            player.openInventory(holder.getInventory());
        } else {
            player.sendRichMessage("<red>Not a valid container.");
        }
    }

    @Override
    protected Component getTitle() {
        return MiniMessage.miniMessage().deserialize("<#6E97C8>" + player.getName() + "'s Storage");
    }

    private String formatMaterialName(Material mat) {
        return Arrays.stream(mat.name().toLowerCase().split("_"))
                .map(s -> Character.toUpperCase(s.charAt(0)) + s.substring(1))
                .collect(Collectors.joining(" "));
    }
}