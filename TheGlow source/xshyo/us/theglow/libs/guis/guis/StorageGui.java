package xshyo.us.theglow.libs.guis.guis;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.InteractionModifier;

public class StorageGui extends BaseGui {
   public StorageGui(int var1, @NotNull String var2, @NotNull Set<InteractionModifier> var3) {
      super(var1, var2, var3);
   }

   @Deprecated
   public StorageGui(int var1, @NotNull String var2) {
      super(var1, var2);
   }

   @Deprecated
   public StorageGui(@NotNull String var1) {
      super(1, var1);
   }

   @NotNull
   public Map<Integer, ItemStack> addItem(@NotNull ItemStack... var1) {
      return Collections.unmodifiableMap(this.getInventory().addItem(var1));
   }

   public Map<Integer, ItemStack> addItem(@NotNull List<ItemStack> var1) {
      return this.addItem((ItemStack[])var1.toArray(new ItemStack[0]));
   }

   public void open(@NotNull HumanEntity var1) {
      if (!var1.isSleeping()) {
         this.populateGui();
         var1.openInventory(this.getInventory());
      }
   }
}
