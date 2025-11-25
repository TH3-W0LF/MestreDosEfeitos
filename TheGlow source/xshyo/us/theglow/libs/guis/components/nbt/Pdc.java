package xshyo.us.theglow.libs.guis.components.nbt;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Pdc implements NbtWrapper {
   private static final Plugin PLUGIN = JavaPlugin.getProvidingPlugin(Pdc.class);

   public ItemStack setString(@NotNull ItemStack var1, String var2, String var3) {
      ItemMeta var4 = var1.getItemMeta();
      if (var4 == null) {
         return var1;
      } else {
         var4.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, var2), PersistentDataType.STRING, var3);
         var1.setItemMeta(var4);
         return var1;
      }
   }

   public ItemStack removeTag(@NotNull ItemStack var1, String var2) {
      ItemMeta var3 = var1.getItemMeta();
      if (var3 == null) {
         return var1;
      } else {
         var3.getPersistentDataContainer().remove(new NamespacedKey(PLUGIN, var2));
         var1.setItemMeta(var3);
         return var1;
      }
   }

   public ItemStack setBoolean(@NotNull ItemStack var1, String var2, boolean var3) {
      ItemMeta var4 = var1.getItemMeta();
      if (var4 == null) {
         return var1;
      } else {
         var4.getPersistentDataContainer().set(new NamespacedKey(PLUGIN, var2), PersistentDataType.BYTE, Byte.valueOf((byte)(var3 ? 1 : 0)));
         var1.setItemMeta(var4);
         return var1;
      }
   }

   @Nullable
   public String getString(@NotNull ItemStack var1, String var2) {
      ItemMeta var3 = var1.getItemMeta();
      return var3 == null ? null : (String)var3.getPersistentDataContainer().get(new NamespacedKey(PLUGIN, var2), PersistentDataType.STRING);
   }
}
