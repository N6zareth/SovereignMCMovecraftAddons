package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class CraftDisplayStorage {
    public final Map<Craft, TextDisplay> craftTextDisplays = new HashMap<>();
    public final Map<UUID, Long> pendingPilot = new HashMap<>();
    public final Map<Craft, Set<Player>> craftPilots = new HashMap<>();
}