package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.AllowedBlocks;

import net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftComposition.BlockCountConsolidator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class AllowedBlocksCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage("Usage: /allowedblocks <craftName>");
            return true;
        }

        String craftName = args[0];

        try {
            YamlConfiguration config = getCraftConfig(craftName);
            if (config == null) {
                player.sendMessage("§cCraft type \"" + craftName + "\" not found.");
                return true;
            }

            Set<Material> allowed = getAllowedMaterialsFromConfig(config);
            if (allowed.isEmpty()) {
                player.sendRichMessage("<red>Not yet implemented.");
                return true;
            }

            new AllowedBlocksGUI(player, craftName, allowed).open();
        } catch (Exception e) {
            e.printStackTrace();
            player.sendMessage("§cAn error occurred while opening the GUI. Check the console.");
        }

        return true;
    }

    private YamlConfiguration getCraftConfig(String craftTypeName) {
        try {
            File craftTypeFile = new File(
                    Bukkit.getPluginManager().getPlugin("Movecraft").getDataFolder(),
                    "types/" + craftTypeName + ".craft"
            );

            if (craftTypeFile.exists()) {
                return YamlConfiguration.loadConfiguration(craftTypeFile);
            }
        } catch (Exception e) {
            Bukkit.getLogger().warning("Failed to load craft config for type: " + craftTypeName);
            e.printStackTrace();
        }
        return null;
    }

    private Set<Material> getAllowedMaterialsFromConfig(YamlConfiguration config) {
        Set<Material> materials = EnumSet.noneOf(Material.class);
        List<String> blockLines = config.getStringList("flyblocks");

        Bukkit.getLogger().info("[AllowedBlocks] Logging flyblocks from config...");

        for (String line : blockLines) {
            String raw = line.split(":")[0].trim();
            Material mat = Material.matchMaterial(raw.toUpperCase());

            if (mat == null) {
                Bukkit.getLogger().warning("[AllowedBlocks] Unknown material: " + raw);
                continue;
            }

            Material normalized = BlockCountConsolidator.normalizeMaterial(mat);
            materials.add(normalized);
            Bukkit.getLogger().info("[AllowedBlocks] Parsed: " + normalized.name());
        }

        Bukkit.getLogger().info("[AllowedBlocks] Total parsed materials: " + materials.size());
        return materials;
    }
}