package com.seunome.mestredosfx.hooks;

import com.seunome.mestredosfx.MestreDosEfeitos;
import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderHook {

    private final MestreDosEfeitos plugin;

    public ItemsAdderHook(MestreDosEfeitos plugin) {
        this.plugin = plugin;
    }

    public ItemStack getCustomItem(String namespaceId) {
        try {
            CustomStack customStack = CustomStack.getInstance(namespaceId);
            if (customStack != null) {
                return customStack.getItemStack();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Item do ItemsAdder não encontrado: " + namespaceId + " - Usando item padrão");
        }
        
        // Fallback para item padrão se o custom não existir
        return new ItemStack(Material.BARRIER);
    }

    public boolean hasCustomItem(String namespaceId) {
        try {
            CustomStack customStack = CustomStack.getInstance(namespaceId);
            return customStack != null;
        } catch (Exception e) {
            return false;
        }
    }
}

