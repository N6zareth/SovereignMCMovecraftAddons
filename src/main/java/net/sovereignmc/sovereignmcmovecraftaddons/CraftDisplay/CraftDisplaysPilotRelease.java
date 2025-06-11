package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.events.CraftSinkEvent;
import net.countercraft.movecraft.events.CraftTranslateEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;

import java.util.*;

public class CraftDisplaysPilotRelease implements Listener {

    private final Map<Craft, TextDisplay> craftTextDisplays = new HashMap<>();
    private final Map<UUID, Long> pendingPilot = new HashMap<>();
    private final Map<Craft, Set<Player>> craftPilots = new HashMap<>();
    private final JavaPlugin plugin;

    public CraftDisplaysPilotRelease(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        String msg = event.getMessage().toLowerCase();
        Player player = event.getPlayer();

        if (msg.startsWith("/pilot")){
            Craft currentCraft = CraftManager.getInstance().getCraftByPlayer(player);
            if (currentCraft != null) return;

            if ((isCraftTypeEqual(currentCraft, "submarine") || (isCraftTypeEqual(currentCraft, "shipyard")))) return;

            pendingPilot.put(event.getPlayer().getUniqueId(), System.currentTimeMillis());

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                attemptSpawnDisplay(event.getPlayer());
            }, 1L);
        }
    }

    public boolean isCraftTypeEqual(Craft craft, String type) {
        return craft.getType().getStringProperty(CraftType.NAME).equalsIgnoreCase(type);
    }

    private void attemptSpawnDisplay(Player player) {
        long mark = pendingPilot.remove(player.getUniqueId());
        if (System.currentTimeMillis() - mark > 5000) return;

        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);
        if (craft == null) return;

        Location top = getCraftTopCenterLocation(craft);
        craftPilots.computeIfAbsent(craft, k -> new HashSet<>()).add(player);
        spawnTextDisplay(craft, player, top);
    }

    private void spawnTextDisplay(Craft craft, Player pilot, Location location) {
        World world = location.getWorld();
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);

        String pilotName = pilot != null ? pilot.getName() : "Unknown";

        int size = craft.getOrigBlockCount();

        String craftType = craft.getType().getStringProperty(CraftType.NAME);
        String craftName = craft.getName();

        MiniMessage mm = MiniMessage.miniMessage();
        Component displayText = mm.deserialize("<bold><italic>" + pilotName + "'s " + craftType + " (" + size + ") " + craftName + "</bold>");
        textDisplay.text(displayText);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setShadowed(true);
        textDisplay.setDefaultBackground(false);
        textDisplay.setViewRange(64);

        // hopefully this makes it bigger
        Transformation transformation = textDisplay.getTransformation();
        transformation.getScale().set(3.0f, 3.0f, 3.0f);
        textDisplay.setTransformation(transformation);

        craftTextDisplays.put(craft, textDisplay);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraftMove(CraftTranslateEvent event) {
        Craft craft = event.getCraft();

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            TextDisplay display = craftTextDisplays.get(craft);
            if (display != null) {
                Location newDisplayLocation = getCraftTopCenterLocation(craft);
                display.teleport(newDisplayLocation);
            }
        }, 1L); // 1 tick delay
    }

    private Location getCraftTopCenterLocation(Craft craft) {
        double x = (craft.getHitBox().getMinX() + craft.getHitBox().getMaxX()) / 2D;
        double z = (craft.getHitBox().getMinZ() + craft.getHitBox().getMaxZ()) / 2D;
        double y = craft.getHitBox().getMaxY() + 5D;
        return new Location(craft.getWorld(), x, y, z);
    }

    public class CraftCleanup implements Listener {
        public void cleanUpCraft(Craft craft) {
            TextDisplay textDisplay = craftTextDisplays.get(craft);

            if (textDisplay != null) {
                textDisplay.remove();
                craftTextDisplays.remove(craft);
                craftPilots.remove(craft);
            }
        }

        @EventHandler
        public void onCraftRelease(CraftReleaseEvent event) {
            cleanUpCraft(event.getCraft());
        }

        @EventHandler
        public void onCraftSink(CraftSinkEvent event) {
            cleanUpCraft(event.getCraft());
        }
    }

    public void registerListeners(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this.new CraftCleanup(), plugin);
    }
}