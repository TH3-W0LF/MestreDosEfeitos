package com.seunome.mestredosfx.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

public class PlayerUtils {

    private static final MiniMessage mm = MiniMessage.miniMessage();

    public static void sendMessage(Player player, String message) {
        if (player != null && message != null && !message.isEmpty()) {
            Component component = mm.deserialize(message);
            player.sendMessage(component);
        }
    }

    public static void sendMessage(Player player, String message, String... replacements) {
        if (player != null && message != null && !message.isEmpty()) {
            String finalMessage = message;
            for (int i = 0; i < replacements.length; i += 2) {
                if (i + 1 < replacements.length) {
                    finalMessage = finalMessage.replace(replacements[i], replacements[i + 1]);
                }
            }
            Component component = mm.deserialize(finalMessage);
            player.sendMessage(component);
        }
    }
}

