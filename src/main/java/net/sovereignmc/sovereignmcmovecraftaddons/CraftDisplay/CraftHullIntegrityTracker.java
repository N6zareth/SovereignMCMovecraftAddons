package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.craft.type.RequiredBlockEntry;
import net.countercraft.movecraft.MovecraftLocation;
import net.kyori.adventure.bossbar.BossBar;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.World;
import org.bukkit.Material;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class CraftHullIntegrityTracker {
    private final SovereignMCMovecraftAddons plugin;
    private BukkitTask updateTask;
    private final Map<UUID, Deque<Double>> craftHullIntegrityHistory = new HashMap<>();
    private final Map<UUID, Integer> craftOriginalBlockCount = new HashMap<>();
    private final int hullHistorySize = 20;
    private final long updateInterval = 2L; // Faster update rate (2 ticks)

    public CraftHullIntegrityTracker(SovereignMCMovecraftAddons plugin) {
        this.plugin = plugin;
        startUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    updateHullIntegrityData();
                } catch (Exception e) {
                    plugin.getLogger().severe("Error in CraftHullIntegrityTracker updateTask: " + e.getMessage());
                }
            }
        }.runTaskTimer(plugin, 0L, updateInterval);
    }

    private void updateHullIntegrityData() {
        for (UUID craftId : new HashSet<>(craftHullIntegrityHistory.keySet())) {
            Craft craft = null;
            for (Craft c : net.countercraft.movecraft.craft.CraftManager.getInstance().getCrafts()) {
                if (c.getUUID().equals(craftId)) {
                    craft = c;
                    break;
                }
            }
            if (craft == null) {
                craftHullIntegrityHistory.remove(craftId);
                craftOriginalBlockCount.remove(craftId);
                continue;
            }

            double currentIntegrity = calculateHullIntegrity(craft);

            Deque<Double> history = craftHullIntegrityHistory.get(craftId);
            history.addLast(currentIntegrity);

            if (history.size() > hullHistorySize) {
                history.removeFirst();
            }
        }
    }

    public void initializeData(Craft craft) {
        UUID craftId = craft.getUUID();
        Deque<Double> history = new ArrayDeque<>();
        history.addLast(1.0); // Start with 100% integrity
        craftHullIntegrityHistory.put(craftId, history);
        craftOriginalBlockCount.put(craftId, countRealBlocks(craft));
    }

    public void cleanupCraft(Craft craft) {
        UUID craftId = craft.getUUID();
        craftHullIntegrityHistory.remove(craftId);
        craftOriginalBlockCount.remove(craftId);
    }

    public String getDisplayText(Craft craft) {
        double integrity = getCurrentHullIntegrity(craft);
        String color;

        if (integrity >= 0.85) {
            color = "<green>";
        } else if (integrity >= 0.75) {
            color = "<yellow>";
        } else {
            color = "<red>";
        }

        return String.format("%s%.0f%%", color, integrity * 100);
    }

    public String getCurrentIntegrityString(Craft craft) {
        double integrity = getCurrentHullIntegrity(craft);

        return String.format("%.0f%%", integrity * 100);
    }

    public float getProgress(Craft craft) {
        return (float) getCurrentHullIntegrity(craft);
    }

    public BossBar.Color getColor(Craft craft) {
        double integrity = getCurrentHullIntegrity(craft);

        if (integrity >= 0.85) {
            return BossBar.Color.GREEN;
        } else if (integrity >= 0.75) {
            return BossBar.Color.YELLOW;
        } else {
            return BossBar.Color.RED;
        }
    }

    private double getCurrentHullIntegrity(Craft craft) {
        UUID craftId = craft.getUUID();
        if (!craftHullIntegrityHistory.containsKey(craftId)) {
            return 1.0;
        }

        Deque<Double> history = craftHullIntegrityHistory.get(craftId);
        return history.stream().max(Double::compareTo).orElse(1.0);
    }

    private double calculateHullIntegrity(Craft craft) {
        UUID craftId = craft.getUUID();
        int originalSize = craftOriginalBlockCount.getOrDefault(craftId, 0);
        int currentSize = countRealBlocks(craft);

        if (originalSize == 0) return 1.0;

        return Math.max(0.0, Math.min(1.0, currentSize / (double) originalSize));
    }

    private int countRealBlocks(Craft craft) {
        return Math.max(1, getFlyBlockCount(craft));
    }

    public static int getFlyBlockCount(@NotNull Craft craft) {
        int count = 0;
        Collection<RequiredBlockEntry> flyBlocks = craft.getType().getRequiredBlockProperty(CraftType.FLY_BLOCKS);

        if (flyBlocks == null) {
            return count;
        }

        World world = craft.getWorld();
        for (MovecraftLocation loc : craft.getHitBox()) {
            Material mat = world.getBlockAt(loc.getX(), loc.getY(), loc.getZ()).getType();
            for (RequiredBlockEntry entry : flyBlocks) {
                if (entry.contains(mat)) {
                    count++;
                    break;
                }
            }
        }
        return count;
    }

    public void disable() {
        craftHullIntegrityHistory.clear();
        craftOriginalBlockCount.clear();
        if (updateTask != null) {
            updateTask.cancel();
            updateTask = null;
        }
    }
}