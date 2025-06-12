package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftComposition;

import org.bukkit.Material;

public class BlockCountConsolidator {
    public static Material normalizeMaterial(Material material) {
        if (material.name().endsWith("_CONCRETE")) {
            return Material.WHITE_CONCRETE;
        }
        if (material.name().endsWith("_TERRACOTTA")) {
            return Material.TERRACOTTA;
        }
        if (material.name().endsWith("_WOOL")) {
            return Material.WHITE_WOOL;
        }
        return material;
    }
}