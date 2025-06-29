package net.sovereignmc.sovereignmcmovecraftaddons.InventoryModule.CraftACCA.CraftBlocks;

import net.countercraft.movecraft.craft.CraftManager;
import net.countercraft.movecraft.craft.type.CraftType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;

public class AllowedBlocksTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0].toLowerCase();

            return CraftManager.getInstance().getCraftTypes().stream()
                    .map(type -> type.getStringProperty(CraftType.NAME))
                    .filter(name -> name.toLowerCase().startsWith(input))
                    .toList();
        }

        return Collections.emptyList();
    }
}