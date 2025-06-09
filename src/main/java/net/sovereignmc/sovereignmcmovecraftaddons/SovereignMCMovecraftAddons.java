package net.sovereignmc.sovereignmcmovecraftaddons;

import net.sovereignmc.sovereignmcmovecraftaddons.commands.RotatePluginCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay.CraftDisplaysPilotRelease;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.ManOverboardListener;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.ReleaseMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.RotateMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class SovereignMCMovecraftAddons extends JavaPlugin {
    @Override
    public void onEnable() {
        // manoverboard!
        getServer().getPluginManager().registerEvents(new ManOverboardListener(), this);

        // rtato :3
        RotatePluginCommands rotateCommands = new RotatePluginCommands();
        this.getCommand("ror").setExecutor(rotateCommands);
        this.getCommand("rol").setExecutor(rotateCommands);

        // custom message
        getServer().getPluginManager().registerEvents(new RotateMessage(), this);
        getServer().getPluginManager().registerEvents(new ReleaseMessage(), this);

        // craft displays (the thingy that show pilot, craft health and size)
        CraftDisplaysPilotRelease craftDisplaysPilotRelease = new CraftDisplaysPilotRelease();
        getServer().getPluginManager().registerEvents(craftDisplaysPilotRelease, this);
        craftDisplaysPilotRelease.registerListeners(this);
    }
}