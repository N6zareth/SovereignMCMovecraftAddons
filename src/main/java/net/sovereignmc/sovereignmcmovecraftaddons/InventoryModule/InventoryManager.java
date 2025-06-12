package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule;

import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InventoryManager {
    private static final Map<UUID, PaginatedGUI> openGuis = new HashMap<>();

    public static void track(Player player, PaginatedGUI gui) {
        openGuis.put(player.getUniqueId(), gui);
    }

    public static void untrack(Player player) {
        openGuis.remove(player.getUniqueId());
    }

    public static PaginatedGUI get(Player player) {
        return openGuis.get(player.getUniqueId());
    }
}
