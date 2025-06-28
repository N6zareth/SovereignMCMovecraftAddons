package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.AllowedBlocks;

import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.BlockCountConsolidator;
import net.sovereignmc.sovereignmcmovecraftaddons.Utilities.TagResolverUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class AllowedBlocksCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) return true;

        if (args.length == 0) {
            player.sendMessage("Usage: /allowedblocks <craftName>");
            return true;
        }

        String craftName = args[0];
        CraftType type = CraftManager.getInstance().getCraftTypeFromString(craftName);

        if (type == null) {
            player.sendRichMessage("<red>Unknown craft type: <white>" + craftName);
            return true;
        }

        Set<Material> flyblockMaterials = type.getRequiredBlockProperty(CraftType.FLY_BLOCKS)
                .stream()
                .flatMap(entry -> entry.getMaterials().stream())
                .map(matType -> Material.matchMaterial(matType.name()))
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<Material> allowedBlockMaterials = parseAllowedBlocksFromCraftFile(craftName);

        Set<Material> allDetectableMaterialsRaw = new HashSet<>();
        allDetectableMaterialsRaw.addAll(flyblockMaterials);
        allDetectableMaterialsRaw.addAll(allowedBlockMaterials);

        Set<Material> normalizedDetectableMaterials = allDetectableMaterialsRaw.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Material.class)));

        for (Material block : allowedBlockMaterials){
            System.out.println(block);
        }

        //System.out.println("normalizedDetectableMaterials:");
        //normalizedDetectableMaterials.forEach(mat -> System.out.println(" - " + mat));

        if (normalizedDetectableMaterials.isEmpty()) {
            player.sendRichMessage("<red>No detectable blocks found for <white>" + craftName + "</white>.");
            return true;
        }

        new AllowedBlocksGUI(player, craftName, normalizedDetectableMaterials).open();
        return true;
    }

    private Set<Material> parseAllowedBlocksFromCraftFile(String craftTypeName) {
        Set<Material> allowedMaterials = EnumSet.noneOf(Material.class);
        File craftTypeFile = new File(
                Bukkit.getPluginManager().getPlugin("Movecraft").getDataFolder(),
                "types/" + craftTypeName + ".craft"
        );

        if (!craftTypeFile.exists()) {
            Bukkit.getLogger().warning("[AllowedBlocks] File not found: " + craftTypeFile.getName());
            return allowedMaterials;
        }

        boolean inAllowedBlocks = false;

        try (BufferedReader reader = new BufferedReader(new FileReader(craftTypeFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.startsWith("allowedBlocks:")) {
                    inAllowedBlocks = true;
                    continue;
                }

                if (inAllowedBlocks) {
                    String trimmedLine = line.trim();

                    if (trimmedLine.isEmpty() || !trimmedLine.startsWith("-")) break;

                    String blockId = trimmedLine.substring(1).trim();
                    Bukkit.getLogger().info("Raw block/tag entry: " + blockId);

                    if ((blockId.startsWith("\"") && blockId.endsWith("\"")) || (blockId.startsWith("'") && blockId.endsWith("'"))) {
                        blockId = blockId.substring(1, blockId.length() - 1);
                    }

                    Bukkit.getLogger().info("Normalized block/tag entry: " + blockId);

                    if (blockId.startsWith("#")) {
                        allowedMaterials.addAll(TagResolverUtil.resolveTagToMaterials(blockId));
                    } else {
                        Material mat = Material.matchMaterial(blockId.replace("minecraft:", ""));
                        if (mat != null) {
                            allowedMaterials.add(mat);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allowedMaterials;
    }
}