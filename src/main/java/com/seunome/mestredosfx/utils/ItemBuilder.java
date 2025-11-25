package com.seunome.mestredosfx.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private final ItemStack item;
    private ItemMeta meta;

    public ItemBuilder(Material material) {
        this.item = new ItemStack(material);
        this.meta = item.getItemMeta();
    }

    public ItemBuilder(ItemStack item) {
        this.item = item.clone();
        this.meta = item.getItemMeta();
    }

    public ItemBuilder name(String name) {
        if (meta != null && name != null) {
            Component component = MiniMessage.miniMessage().deserialize(name);
            meta.displayName(component);
        }
        return this;
    }

    public ItemBuilder lore(String... lines) {
        if (meta != null && lines != null) {
            List<Component> lore = new ArrayList<>();
            MiniMessage mm = MiniMessage.miniMessage();
            for (String line : lines) {
                if (line != null && !line.isEmpty()) {
                    lore.add(mm.deserialize(line));
                }
            }
            meta.lore(lore);
        }
        return this;
    }

    public ItemBuilder lore(List<String> lines) {
        if (meta != null && lines != null) {
            List<Component> lore = new ArrayList<>();
            MiniMessage mm = MiniMessage.miniMessage();
            for (String line : lines) {
                if (line != null && !line.isEmpty()) {
                    lore.add(mm.deserialize(line));
                }
            }
            meta.lore(lore);
        }
        return this;
    }

    public ItemBuilder amount(int amount) {
        item.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        if (meta != null) {
            item.setItemMeta(meta);
        }
        return item;
    }
}

