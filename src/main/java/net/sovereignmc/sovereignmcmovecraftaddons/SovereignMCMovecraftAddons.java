package net.sovereignmc.sovereignmcmovecraftaddons;

import net.sovereignmc.sovereignmcmovecraftaddons.commands.RemoveTextDisplaysCommand;
import net.sovereignmc.sovereignmcmovecraftaddons.commands.RotateCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay.CraftDisplaysPilotRelease;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.ManOverboardListener;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.ReleaseMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.RotateMessage;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class SovereignMCMovecraftAddons extends JavaPlugin {
    @Override
    public void onEnable() {

        // manoverboard!
        getServer().getPluginManager().registerEvents(new ManOverboardListener(), this);

        // rtato :3
        RotateCommands rotateCommands = new RotateCommands();
        this.getCommand("ror").setExecutor(rotateCommands);
        this.getCommand("rol").setExecutor(rotateCommands);

        // custom message
        getServer().getPluginManager().registerEvents(new RotateMessage(), this);
        getServer().getPluginManager().registerEvents(new ReleaseMessage(), this);

        // craft displays (the thingy that show pilot, craft health and size)
        CraftDisplaysPilotRelease craftDisplays = new CraftDisplaysPilotRelease(this);
        getServer().getPluginManager().registerEvents(craftDisplays, this);
        craftDisplays.registerListeners(this);

        // remove text displays
        new RemoveTextDisplaysCommand(this);
    }
}