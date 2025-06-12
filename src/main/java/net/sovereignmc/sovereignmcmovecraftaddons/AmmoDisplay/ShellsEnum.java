package net.sovereignmc.sovereignmcmovecraftaddons.AmmoDisplay;

import org.bukkit.Material;

public enum ShellsEnum {
    SHELL_5IN("5-Inch Shell", "5inch_shell", 555511),
    SHELL_6IN("6-Inch Shell", "6inch_shell", 555512),
    SHELL_8IN("8-Inch Shell", "8inch_shell", 555513),
    SHELL_12IN("12-Inch Shell", "12inch_shell", 555514),
    SHELL_14IN("14-Inch Shell", "14inch_shell", 555515),
    SHELL_16IN("16-Inch Shell", "16inch_shell", 555516),
    TORPEDO("Torpedo", "torpedo", 555521),
    LIGHT_TORPEDO("Light Torpedo", "light_torpedo", 555522),
    HEAVY_TORPEDO("Heavy Torpedo", "heavy_torpedo", 555523);

    private final String displayName;
    private final String sfId;
    private final int customModelData;

    ShellsEnum(String displayName, String sfId, int customModelData) {
        this.displayName = displayName;
        this.sfId = sfId;
        this.customModelData = customModelData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getSfId() {
        return sfId;
    }

    public Material getDisplayItem() {
        return Material.FIREWORK_STAR;
    }

    public int getCustomModelData() {
        return customModelData;
    }
}