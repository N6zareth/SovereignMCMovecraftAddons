package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Transformation;

import java.util.HashSet;

public class CraftDisplayManager {
    private final CraftDisplayStorage store;
    private final SovereignMCMovecraftAddons plugin;
    private final CraftHullIntegrityTracker hullTracker;
    private BukkitTask updateTask;

    public CraftDisplayManager(CraftDisplayStorage store, SovereignMCMovecraftAddons plugin) {
        this.store = store;
        this.plugin = plugin;
        this.hullTracker = plugin.getHullTracker();
        startUpdateTask();
    }

    private void startUpdateTask() {
        updateTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    for (Craft craft: store.craftTextDisplays.keySet()) {
                        updateTextDisplay(craft);
                    }
                } catch (Exception e) {
                    plugin.getLogger().severe("Error in CraftDisplayManager updateTask: " + e.getMessage());
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    public void attemptSpawnDisplay(Player player) {
        long mark = store.pendingPilot.remove(player.getUniqueId());
        if (System.currentTimeMillis() - mark > 5000) return;

        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
        if (craft == null) return;

        hullTracker.initializeData(craft);
        Location top = getCraftTopCenterLocation(craft);
        store.craftPilots.computeIfAbsent(craft, k -> new HashSet<>()).add(player);
        spawnTextDisplay(craft, player, top);
    }

    public void spawnTextDisplay(Craft craft, Player pilot, Location location) {
        World world = location.getWorld();
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);

        String pilotName = pilot != null ? pilot.getName() : "Unknown";
        int size = craft.getOrigBlockCount();
        String craftType = craft.getType().getStringProperty(CraftType.NAME);
        String craftName = craft.getName();

        Component displayText = MiniMessage.miniMessage().deserialize(
                "<bold><italic>" + pilotName + "'s " + craftType + " (" + size + ") " + hullTracker.getCurrentIntegrityString(craft) + " " + craftName + "</bold>");

        textDisplay.text(displayText);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setShadowed(true);
        textDisplay.setDefaultBackground(false);
        textDisplay.setViewRange(64);

        Transformation transformation = textDisplay.getTransformation();
        transformation.getScale().set(3.0f, 3.0f, 3.0f);
        textDisplay.setTransformation(transformation);

        store.craftTextDisplays.put(craft, textDisplay);
    }

    public void onCraftMove(Craft craft) {
        TextDisplay display = store.craftTextDisplays.get(craft);
        if (display != null) {
            Location newDisplayLocation = getCraftTopCenterLocation(craft);
            display.teleport(newDisplayLocation);
        }
    }

    public void cleanUpCraft(Craft craft) {
        TextDisplay display = store.craftTextDisplays.remove(craft);
        if (display != null) {
            display.remove();
        }
        store.craftPilots.remove(craft);
    }

    private Location getCraftTopCenterLocation(Craft craft) {
        double x = (craft.getHitBox().getMinX() + craft.getHitBox().getMaxX()) / 2D;
        double z = (craft.getHitBox().getMinZ() + craft.getHitBox().getMaxZ()) / 2D;
        double y = craft.getHitBox().getMaxY() + 5D;
        return new Location(craft.getWorld(), x, y, z);
    }

    public void updateTextDisplay(Craft craft) {
        TextDisplay display = store.craftTextDisplays.get(craft);
        if (display == null) return;

        Player pilot = store.craftPilots.getOrDefault(craft, new HashSet<>())
                .stream().findFirst().orElse(null);

        String pilotName = pilot != null ? pilot.getName() : "Unknown";
        int size = craft.getOrigBlockCount();
        String craftType = craft.getType().getStringProperty(CraftType.NAME);
        String craftName = craft.getName();

        Component displayText = MiniMessage.miniMessage().deserialize(
                "<bold><italic>" + pilotName + "'s " + craftType + " (" + size + ") " + hullTracker.getCurrentIntegrityString(craft) + " " + craftName + "</bold>");

        display.text(displayText);
    }

}