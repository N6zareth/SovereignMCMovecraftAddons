package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.events.CraftPilotEvent;
import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class CraftDisplaysPilotRelease implements Listener {

    private final Map<Craft, TextDisplay> craftTextDisplays = new HashMap<>();

    @EventHandler
    public void onCraftPilot(CraftPilotEvent event) {
        Craft craft = event.getCraft();
        Location location = getCraftTopCenterLocation(craft);
        spawnTextDisplay(craft, location);
    }

    // creation of the hologram should be here
    private void spawnTextDisplay(Craft craft, Location location) {
        World world = location.getWorld();
        TextDisplay textDisplay = (TextDisplay) world.spawnEntity(location, EntityType.TEXT_DISPLAY);

        MiniMessage mm = MiniMessage.miniMessage();
        Component testingText = mm.deserialize("<#555555>Testing</#555555><#FFFFFF>Testing2</#FFFFFF>");

        textDisplay.text(testingText);
        textDisplay.setBillboard(Display.Billboard.CENTER);
        textDisplay.setShadowed(true);
        textDisplay.setDefaultBackground(false);
        textDisplay.setViewRange(64);

        craftTextDisplays.put(craft, textDisplay);
    }

    // finds the craft's centre (stolen directly from the base /manoverboard lol)
    private Location getCraftTopCenterLocation(Craft craft) {
        double x = ((craft.getHitBox().getMinX() + craft.getHitBox().getMaxX()) / 2D);
        double z = ((craft.getHitBox().getMinZ() + craft.getHitBox().getMaxZ()) / 2D);
        double y = craft.getHitBox().getMaxY() + 5D;
        return new Location(craft.getWorld(), x, y, z);
    }


    // deletes it
    public static class CraftReleaseListener implements Listener {
        private final Map<Craft, TextDisplay> craftTextDisplays;

        public CraftReleaseListener(Map<Craft, TextDisplay> craftTextDisplays) {
            this.craftTextDisplays = craftTextDisplays;
        }

        @EventHandler
        public void onCraftRelease(CraftReleaseEvent event) {
            Bukkit.broadcastMessage("Testing!");
            Craft craft = event.getCraft();

            TextDisplay textDisplay = craftTextDisplays.get(craft);

            if (textDisplay != null) {
                textDisplay.remove();
                craftTextDisplays.remove(craft);
            }
        }
    }

    public void registerListeners(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new CraftReleaseListener(craftTextDisplays), plugin);
    }
}