package net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay;

import net.countercraft.movecraft.events.CraftReleaseEvent;
import net.countercraft.movecraft.events.CraftSinkEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CraftDisplayCleanupListener implements Listener {
    private final CraftDisplayManager manager;

    public CraftDisplayCleanupListener(CraftDisplayManager manager) {
        this.manager = manager;
    }

    @EventHandler
    public void onCraftRelease(CraftReleaseEvent event) {
        manager.cleanUpCraft(event.getCraft());
    }

    @EventHandler
    public void onCraftSink(CraftSinkEvent event) {
        manager.cleanUpCraft(event.getCraft());
    }
}