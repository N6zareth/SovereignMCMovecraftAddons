package net.sovereignmc.sovereignmcmovecraftaddons;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class Deserializer {
    private static final MiniMessage miniMessage = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();

    public static String NazyDeserializer(String mmText) {
        Component component = miniMessage.deserialize(mmText);
        return legacySerializer.serialize(component);
    }
}