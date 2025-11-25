package xshyo.us.theglow.libs.guis.guis;

import com.google.common.base.Preconditions;
import java.util.UUID;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.guis.components.GuiAction;
import xshyo.us.theglow.libs.guis.components.util.ItemNbt;

public class GuiItem {
   private final UUID uuid;
   private GuiAction<InventoryClickEvent> action;
   private ItemStack itemStack;

   public GuiItem(@NotNull ItemStack var1, @Nullable GuiAction<InventoryClickEvent> var2) {
      this.uuid = UUID.randomUUID();
      Preconditions.checkNotNull(var1, "The ItemStack for the GUI Item cannot be null!");
      this.action = var2;
      this.setItemStack(var1);
   }

   public GuiItem(@NotNull ItemStack var1) {
      this((ItemStack)var1, (GuiAction)null);
   }

   public GuiItem(@NotNull Material var1) {
      this((ItemStack)(new ItemStack(var1)), (GuiAction)null);
   }

   public GuiItem(@NotNull Material var1, @Nullable GuiAction<InventoryClickEvent> var2) {
      this(new ItemStack(var1), var2);
   }

   @NotNull
   public ItemStack getItemStack() {
      return this.itemStack;
   }

   public void setItemStack(@NotNull ItemStack var1) {
      Preconditions.checkNotNull(var1, "The ItemStack for the GUI Item cannot be null!");
      if (var1.getType() != Material.AIR) {
         this.itemStack = ItemNbt.setString(var1.clone(), "mf-gui", this.uuid.toString());
      } else {
         this.itemStack = var1.clone();
      }

   }

   @NotNull
   UUID getUuid() {
      return this.uuid;
   }

   @Nullable
   GuiAction<InventoryClickEvent> getAction() {
      return this.action;
   }

   public void setAction(@Nullable GuiAction<InventoryClickEvent> var1) {
      this.action = var1;
   }
}
