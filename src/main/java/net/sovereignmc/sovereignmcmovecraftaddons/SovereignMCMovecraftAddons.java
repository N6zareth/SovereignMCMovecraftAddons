package net.sovereignmc.sovereignmcmovecraftaddons;

import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftBlocks.CraftBlocksCommand;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftBlocks.AllowedBlocksTabCompleter;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.Ammo.AmmoCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.CraftDisplay.*;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftStorage.ContainerReturnListener;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftStorage.CraftStorageCommand;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.CombatTagManager;
import net.sovereignmc.sovereignmcmovecraftaddons.commands.RemoveTextDisplaysCommand;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftComposition.CraftCompCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.commands.RotateCommands;
import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.InventoryListener;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.ManOverboardListener;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.ReleaseMessage;
import net.sovereignmc.sovereignmcmovecraftaddons.listeners.RotateMessage;
import org.bukkit.plugin.java.JavaPlugin;

public class SovereignMCMovecraftAddons extends JavaPlugin {
    private CraftHullIntegrityTracker hullTracker;

    public static SovereignMCMovecraftAddons getInstance() {
        return JavaPlugin.getPlugin(SovereignMCMovecraftAddons.class);
    }
    @Override
    public void onEnable() {

        saveDefaultConfig();

        // initialize hull integrity tracker
        hullTracker = new CraftHullIntegrityTracker(this);

        // manoverboard!
        CombatTagManager combatTagManager = new CombatTagManager(this);
        getServer().getPluginManager().registerEvents(combatTagManager, this);
        getServer().getPluginManager().registerEvents(new ManOverboardListener(this, combatTagManager), this);


        // rotate
        RotateCommands rotateCommands = new RotateCommands();
        this.getCommand("ror").setExecutor(rotateCommands);
        this.getCommand("rol").setExecutor(rotateCommands);

        // custom message
        getServer().getPluginManager().registerEvents(new RotateMessage(), this);
        getServer().getPluginManager().registerEvents(new ReleaseMessage(), this);

        // craft displays (the thingy that show pilot, craft health and size)
        CraftDisplayStorage store = new CraftDisplayStorage();
        CraftDisplayManager manager = new CraftDisplayManager(store, this);

        getServer().getPluginManager().registerEvents(new CraftDisplayCommandListener(this, store, manager), this);
        getServer().getPluginManager().registerEvents(new CraftDisplayMovementListener(this, manager), this);
        getServer().getPluginManager().registerEvents(new CraftDisplayCleanupListener(manager), this);

        // remove text displays
        this.getCommand("cleardisplays").setExecutor(new RemoveTextDisplaysCommand());

        // inv listenre handles PaginatedGUI clicks globally
        InventoryListener inventoryListener = new InventoryListener();
        getServer().getPluginManager().registerEvents(inventoryListener, this);

        //craftcomp
        getCommand("craftcomp").setExecutor(new CraftCompCommands());

        // ammo commands
        getCommand("craftammo").setExecutor(new AmmoCommands(this));

        //allowedblocks
        getCommand("craftblocks").setExecutor(new CraftBlocksCommand());
        getCommand("craftblocks").setTabCompleter(new AllowedBlocksTabCompleter());

        //craftstorage
        getCommand("craftstorage").setExecutor(new CraftStorageCommand());
        getServer().getPluginManager().registerEvents(new ContainerReturnListener(), this);
    }

    public CraftHullIntegrityTracker getHullTracker() {
        return this.hullTracker;
    }
}