package net.sovereignmc.sovereignmcmovecraftaddons.Utilities;

import net.sovereignmc.sovereignmcmovecraftaddons.SovereignMCMovecraftAddons;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CombatTagManager implements Listener {
    private final Map<UUID, Long> taggedPlayers = new HashMap<>();
    private final long combatTagDurationMs;

    public CombatTagManager(SovereignMCMovecraftAddons plugin) {
        long seconds = plugin.getConfig().getLong("combat.tag-duration-seconds", 20);
        this.combatTagDurationMs = seconds * 1000;
    }

    public boolean isTagged(Player player) {
        Long tagTime = taggedPlayers.get(player.getUniqueId());
        if (tagTime == null) return false;
        return System.currentTimeMillis() - tagTime < combatTagDurationMs;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Entity damager = event.getDamager();
        if (damager instanceof Player attacker) {
            taggedPlayers.put(victim.getUniqueId(), System.currentTimeMillis());
            taggedPlayers.put(attacker.getUniqueId(), System.currentTimeMillis());
        }
    }
}
