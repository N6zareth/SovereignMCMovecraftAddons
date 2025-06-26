package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftStorage;

import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CraftStorageReturnManager {
    private static final Map<UUID, CraftStorageGUI> guiMap = new HashMap<>();
    private static final Map<UUID, Inventory> openedInventoryMap = new HashMap<>();

    public static void setReturnGui(UUID uuid, CraftStorageGUI gui) {
        guiMap.put(uuid, gui);
    }

    public static CraftStorageGUI getReturnGui(UUID uuid) {
        return guiMap.get(uuid);
    }

    public static void setOpenedInventory(UUID uuid, Inventory inv) {
        openedInventoryMap.put(uuid, inv);
    }

    public static Inventory getOpenedInventory(UUID uuid) {
        return openedInventoryMap.get(uuid);
    }

    public static void clear(UUID uuid) {
        guiMap.remove(uuid);
        openedInventoryMap.remove(uuid);
    }
}