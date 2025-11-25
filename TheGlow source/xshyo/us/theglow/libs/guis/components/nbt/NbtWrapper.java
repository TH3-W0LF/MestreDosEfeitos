package xshyo.us.theglow.libs.guis.components.nbt;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface NbtWrapper {
   ItemStack setString(@NotNull ItemStack var1, String var2, String var3);

   ItemStack removeTag(@NotNull ItemStack var1, String var2);

   ItemStack setBoolean(@NotNull ItemStack var1, String var2, boolean var3);

   @Nullable
   String getString(@NotNull ItemStack var1, String var2);
}
