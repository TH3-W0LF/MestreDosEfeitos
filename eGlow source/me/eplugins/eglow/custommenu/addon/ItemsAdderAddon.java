package me.eplugins.eglow.custommenu.addon;

import dev.lone.itemsadder.api.CustomStack;
import org.bukkit.inventory.ItemStack;

public class ItemsAdderAddon {
   public ItemStack getItemsAdderItem(String name) {
      CustomStack stack = CustomStack.getInstance(name);
      return stack != null ? stack.getItemStack() : null;
   }
}
