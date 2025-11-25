package com.drakkar.mestredosefeitos.glow;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

class GlowColor {

    private final String id;
    private final String displayNameRaw;
    private final Material icon;
    private final ChatColor chatColor;
    private final int requiredLevel;
    private final List<String> description;

    GlowColor(String id, String displayNameRaw, Material icon, ChatColor chatColor, int requiredLevel, List<String> description) {
        this.id = id;
        this.displayNameRaw = displayNameRaw;
        this.icon = icon;
        this.chatColor = chatColor;
        this.requiredLevel = requiredLevel;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public Material getIcon() {
        return icon;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public int getRequiredLevel() {
        return requiredLevel;
    }

    public List<String> getDescription() {
        return description;
    }

    public Component displayNameComponent() {
        return MiniMessage.miniMessage().deserialize(displayNameRaw);
    }

    public String getDisplayNameRaw() {
        return displayNameRaw;
    }

    static GlowColor fromConfig(String id, ConfigurationSection section, int defaultRequiredLevel) {
        String display = section.getString("display-name", "<white>" + id + "</white>");
        String materialName = section.getString("material", "WHITE_DYE");
        Material material = Material.matchMaterial(materialName.toUpperCase(Locale.ROOT));
        if (material == null) {
            material = Material.WHITE_DYE;
        }
        String colorName = section.getString("team-color", "WHITE").toUpperCase(Locale.ROOT);
        ChatColor chatColor;
        try {
            chatColor = ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException ex) {
            chatColor = ChatColor.WHITE;
        }
        int required = section.getInt("required-reinc-level", defaultRequiredLevel);
        List<String> lore = section.getStringList("description");
        if (lore == null) {
            lore = Collections.emptyList();
        }
        return new GlowColor(id, display, material, chatColor, required, lore);
    }
}

