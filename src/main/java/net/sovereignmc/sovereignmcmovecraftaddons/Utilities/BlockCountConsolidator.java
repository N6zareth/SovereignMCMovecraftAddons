package net.sovereignmc.sovereignmcmovecraftaddons.Utilities;

import org.bukkit.Material;

import java.util.Map;

public class BlockCountConsolidator {
    private static final Map<String, Material> SUFFIX_TO_NORMAL = Map.ofEntries(
            Map.entry("_CONCRETE", Material.WHITE_CONCRETE),
            Map.entry("_TERRACOTTA", Material.TERRACOTTA),
            Map.entry("_WOOL", Material.WHITE_WOOL)
    );

    public static Material normalizeMaterial(Material material) {
        String name = material.name();

        for (var entry : SUFFIX_TO_NORMAL.entrySet()) {
            if (name.endsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return material;
    }
}