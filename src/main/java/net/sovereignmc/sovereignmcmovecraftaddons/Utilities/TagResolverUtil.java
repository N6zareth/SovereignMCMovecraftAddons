package net.sovereignmc.sovereignmcmovecraftaddons.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Tag;

import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

public class TagResolverUtil {

    public static Set<Material> resolveTagToMaterials(String tagId) {
        String tagName = tagId.startsWith("#")
                ? tagId.substring(1).strip().toLowerCase()
                : tagId.strip().toLowerCase();

        Bukkit.getLogger().info("[TagResolver] Resolving tag ID: '" + tagId + "' => '" + tagName + "'");

        Set<Material> result = EnumSet.noneOf(Material.class);

        switch (tagName) {
            case "glass":
                result.addAll(EnumSet.of(
                        Material.GLASS,
                        Material.TINTED_GLASS,
                        Material.WHITE_STAINED_GLASS,
                        Material.LIGHT_GRAY_STAINED_GLASS,
                        Material.GRAY_STAINED_GLASS,
                        Material.BLACK_STAINED_GLASS,
                        Material.BROWN_STAINED_GLASS,
                        Material.RED_STAINED_GLASS,
                        Material.ORANGE_STAINED_GLASS,
                        Material.YELLOW_STAINED_GLASS,
                        Material.LIME_STAINED_GLASS,
                        Material.GREEN_STAINED_GLASS,
                        Material.CYAN_STAINED_GLASS,
                        Material.LIGHT_BLUE_STAINED_GLASS,
                        Material.BLUE_STAINED_GLASS,
                        Material.PURPLE_STAINED_GLASS,
                        Material.MAGENTA_STAINED_GLASS,
                        Material.PINK_STAINED_GLASS,

                        Material.GLASS_PANE,
                        Material.WHITE_STAINED_GLASS_PANE,
                        Material.LIGHT_GRAY_STAINED_GLASS_PANE,
                        Material.GRAY_STAINED_GLASS_PANE,
                        Material.BLACK_STAINED_GLASS_PANE,
                        Material.BROWN_STAINED_GLASS_PANE,
                        Material.RED_STAINED_GLASS_PANE,
                        Material.ORANGE_STAINED_GLASS_PANE,
                        Material.YELLOW_STAINED_GLASS_PANE,
                        Material.LIME_STAINED_GLASS_PANE,
                        Material.GREEN_STAINED_GLASS_PANE,
                        Material.CYAN_STAINED_GLASS_PANE,
                        Material.LIGHT_BLUE_STAINED_GLASS_PANE,
                        Material.BLUE_STAINED_GLASS_PANE,
                        Material.PURPLE_STAINED_GLASS_PANE,
                        Material.MAGENTA_STAINED_GLASS_PANE,
                        Material.PINK_STAINED_GLASS_PANE
                ));
                break;
            case "planks":
                result.addAll(Tag.PLANKS.getValues());
                break;
            case "wool":
                result.addAll(Tag.WOOL.getValues());
                break;
            case "slabs":
                result.addAll(Tag.SLABS.getValues());
                break;
            case "walls":
                result.addAll(Tag.WALLS.getValues());
                break;
            case "stairs":
                result.addAll(Tag.STAIRS.getValues());
                break;
            case "signs":
                result.addAll(Tag.SIGNS.getValues());
                break;
            case "doors":
                result.addAll(Tag.DOORS.getValues());
                break;
            case "pressure_plates":
                result.addAll(Tag.PRESSURE_PLATES.getValues());
                break;
            case "buttons":
                result.addAll(Tag.BUTTONS.getValues());
                break;
            case "fences":
                result.addAll(Tag.FENCES.getValues());
                break;
            case "trapdoors":
                result.addAll(Tag.TRAPDOORS.getValues());
                break;
            case "fence_gates":
                result.addAll(Tag.FENCE_GATES.getValues());
                break;
            default:
                Bukkit.getLogger().warning("[TagResolver] Unknown tag: #" + tagName);
                break;
        }

        return result.stream()
                .map(BlockCountConsolidator::normalizeMaterial)
                .collect(Collectors.toSet());
    }
}