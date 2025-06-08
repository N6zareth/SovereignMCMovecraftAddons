package net.sovereignmc.sovereignmcmovecraftaddons;

import net.sovereignmc.sovereignmcmovecraftaddons.commands.HealthBarCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.commands.RotatePluginCommands;
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
        getCommand("ror").setExecutor(rotateCommands);
        getCommand("rol").setExecutor(rotateCommands);

        // custom message
        getServer().getPluginManager().registerEvents(new RotateMessage(), this);
        getServer().getPluginManager().registerEvents(new ReleaseMessage(), this);

        // testing
        this.getCommand("hptest").setExecutor(new HealthBarCommands());
    }
}