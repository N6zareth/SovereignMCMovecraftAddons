package net.sovereignmc.sovereignmcmovecraftaddons.listeners;

import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.CombatTagManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.Vector;

public class ManOverboardListener implements Listener {
    private final CombatTagManager combatTagManager;

    public ManOverboardListener(SovereignMCMovecraftAddons plugin, CombatTagManager combatTagManager) {
        this.combatTagManager = combatTagManager;
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage();

        if (!msg.equalsIgnoreCase("/manoverboard")) return;
        event.setCancelled(true);

        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);

        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] You must pilot a craft to go manoverboard!");
            return;
        }

        if (combatTagManager.isTagged(player)) {
            player.sendRichMessage("<dark_red>[\uD83D\uDDE1] You cannot go manoverboard while in combat!");
            return;
        }

        World world = craft.getWorld();
        boolean foundSign = false;

        for (MovecraftLocation mLoc : craft.getHitBox()) {
            Location loc = new Location(world, mLoc.getX(), mLoc.getY(), mLoc.getZ());
            Block block = loc.getBlock();

            if (block.getState() instanceof Sign sign) {
                for (String line : sign.getLines()) {
                    if (line.equalsIgnoreCase("Manoverboard")) {
                        foundSign = true;

                        Location telPoint = block.getLocation().add(0.5, 1, 0.5);
                        telPoint.setYaw(0);
                        telPoint.setPitch(0);

                        player.setVelocity(new Vector(0, 0, 0));
                        player.setFallDistance(0);

                        Movecraft.getInstance().getSmoothTeleport().teleport(player, telPoint, 0f, 0f);
                        player.sendRichMessage("<#6E97C8>[\u2693] Manoverboard!");
                        return;
                    }
                }
            }
        }

        if (!foundSign) {
            Bukkit.dispatchCommand(player, "movecraft:manoverboard");
            player.sendRichMessage("<#6E97C8>[\u2693] Manoverboard!");
        }
    }
}