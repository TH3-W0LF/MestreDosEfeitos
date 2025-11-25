package com.seunome.mestredosfx.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuBuilder {

    private final Inventory inventory;

    public MenuBuilder(int size, String title) {
        Component titleComponent = MiniMessage.miniMessage().deserialize(title);
        this.inventory = Bukkit.createInventory(null, size, titleComponent);
    }

    public MenuBuilder setItem(int slot, ItemStack item) {
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
        }
        return this;
    }

    public MenuBuilder fillBorder(ItemStack item) {
        int size = inventory.getSize();
        int rows = size / 9;
        
        // Preencher primeira linha
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, item);
        }
        
        // Preencher última linha
        for (int i = size - 9; i < size; i++) {
            inventory.setItem(i, item);
        }
        
        // Preencher lados
        for (int i = 0; i < rows; i++) {
            int leftSlot = i * 9;
            int rightSlot = leftSlot + 8;
            inventory.setItem(leftSlot, item);
            inventory.setItem(rightSlot, item);
        }
        
        return this;
    }

    public MenuBuilder fillEmpty(ItemStack item) {
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, item);
            }
        }
        return this;
    }

    public Inventory build() {
        return inventory;
    }
}

