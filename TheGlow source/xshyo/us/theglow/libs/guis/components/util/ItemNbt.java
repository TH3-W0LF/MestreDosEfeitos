package xshyo.us.theglow.libs.guis.components.util;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.nbt.LegacyNbt;
import xshyo.us.theglow.libs.guis.components.nbt.NbtWrapper;
import xshyo.us.theglow.libs.guis.components.nbt.Pdc;

public final class ItemNbt {
   private static final NbtWrapper nbt = selectNbt();

   public static ItemStack setString(@NotNull ItemStack var0, @NotNull String var1, @NotNull String var2) {
      return nbt.setString(var0, var1, var2);
   }

   public static String getString(@NotNull ItemStack var0, @NotNull String var1) {
      return nbt.getString(var0, var1);
   }

   public static ItemStack setBoolean(@NotNull ItemStack var0, @NotNull String var1, boolean var2) {
      return nbt.setBoolean(var0, var1, var2);
   }

   public static ItemStack removeTag(@NotNull ItemStack var0, @NotNull String var1) {
      return nbt.removeTag(var0, var1);
   }

   private static NbtWrapper selectNbt() {
      return (NbtWrapper)(VersionHelper.IS_PDC_VERSION ? new Pdc() : new LegacyNbt());
   }
}
