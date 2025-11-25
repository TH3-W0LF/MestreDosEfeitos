package me.eplugins.eglow.custommenu.addon;

import java.lang.reflect.Method;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.packets.nms.NMSStorage;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.inventory.ItemStack;

public class OraxenAddon {
   Class<?> OraxenItems;
   Class<?> OraxenItemBuilder;
   Method OraxenItems_getItemById;
   Method OraxenItemBuilder_build;

   public OraxenAddon() {
      NMSStorage nmsStorage = NMSHook.nms;

      try {
         this.OraxenItems = nmsStorage.getNMSClass("io.th0rgal.oraxen.api.OraxenItems");
         this.OraxenItemBuilder = nmsStorage.getNMSClass("io.th0rgal.oraxen.items.ItemBuilder");
         this.OraxenItems_getItemById = nmsStorage.getMethod(this.OraxenItems, new String[]{"getItemById"}, String.class);
         this.OraxenItemBuilder_build = nmsStorage.getMethod(this.OraxenItemBuilder, "build");
      } catch (Exception var3) {
         ChatUtil.printException("Failed to setup reflection for Oraxen support!", var3);
      }

   }

   public ItemStack getOraxenItem(String id) {
      try {
         Object itemBuilder = this.OraxenItems_getItemById.invoke(id);
         return itemBuilder != null ? (ItemStack)this.OraxenItemBuilder_build.invoke(itemBuilder) : null;
      } catch (Exception var3) {
         return null;
      }
   }
}
