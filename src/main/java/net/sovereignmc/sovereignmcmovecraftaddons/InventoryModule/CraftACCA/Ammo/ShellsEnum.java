package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.Ammo;

import org.bukkit.Material;

public enum ShellsEnum {
    SHELL_5IN("5-Inch Shell", "5inch_shell", 2211006),
    SHELL_6IN("6-Inch Shell", "6inch_shell", 2211005),
    SHELL_8IN("8-Inch Shell", "8inch_shell", 2211004),
    SHELL_12IN("12-Inch Shell", "12inch_shell", 2211003),
    SHELL_14IN("14-Inch Shell", "14inch_shell", 2211002),
    SHELL_16IN("16-Inch Shell", "16inch_shell", 2211001),
    TORPEDO("Torpedo", "torpedo", 104),
    LIGHT_TORPEDO("Light Torpedo", "light_torpedo", 101),
    HEAVY_TORPEDO("Heavy Torpedo", "heavy_torpedo", 102);

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
        return isTorpedo() ? Material.STICK : Material.FIREWORK_STAR;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    private boolean isTorpedo() {
        return this == TORPEDO || this == LIGHT_TORPEDO || this == HEAVY_TORPEDO;
    }
}