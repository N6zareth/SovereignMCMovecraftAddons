package net.sovereignmc.sovereignmcmovecraftaddons.listeners;


import net.countercraft.movecraft.Movecraft;
import net.countercraft.movecraft.MovecraftLocation;
import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.util.Vector;


public class ManOverboardListener implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String msg = event.getMessage();

        // cancels the thing
        if (!msg.equalsIgnoreCase("/manoverboard")) return;
        event.setCancelled(true);

        Craft craft = CraftManager.getInstance().getCraftByPlayer(player);

        // in case not piloting
        if (craft == null) {
            player.sendRichMessage("<#6E97C8>[\u2693] Pilot a craft!");
            return;
        }

        World world = craft.getWorld();


        // the thingy it does

        for (MovecraftLocation mLoc : craft.getHitBox()) {
            Location loc = new Location(world, mLoc.getX(), mLoc.getY(), mLoc.getZ());
            Block block = loc.getBlock();

            if (block.getState() instanceof Sign sign) {
                for (String line : sign.getLines()) {
                    if (line.equalsIgnoreCase("Manoverboard")) {
                        // Found the sign
                        Location telPoint = block.getLocation().add(0.5, 1, 0.5);
                        float yaw = 0;
                        float pitch = 0;

                        telPoint.setYaw(yaw);
                        telPoint.setPitch(pitch);

                        player.setVelocity(new Vector(0, 0, 0));
                        player.setFallDistance(0);

                        Movecraft.getInstance().getSmoothTeleport().teleport(player, telPoint, yaw, pitch);

                        player.sendRichMessage("<#6E97C8>[\u2693] Manoverboard!");
                        return;
                    }
                }
            }
        }
    }
}