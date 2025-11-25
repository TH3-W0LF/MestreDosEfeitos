package xshyo.us.theglow.libs.guis.components;

import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;

public enum GuiType {
   CHEST(InventoryType.CHEST, 9),
   WORKBENCH(InventoryType.WORKBENCH, 9),
   HOPPER(InventoryType.HOPPER, 5),
   DISPENSER(InventoryType.DISPENSER, 8),
   BREWING(InventoryType.BREWING, 4);

   @NotNull
   private final InventoryType inventoryType;
   private final int limit;

   private GuiType(@NotNull InventoryType var3, int var4) {
      this.inventoryType = var3;
      this.limit = var4;
   }

   @NotNull
   public InventoryType getInventoryType() {
      return this.inventoryType;
   }

   public int getLimit() {
      return this.limit;
   }

   // $FF: synthetic method
   private static GuiType[] $values() {
      return new GuiType[]{CHEST, WORKBENCH, HOPPER, DISPENSER, BREWING};
   }
}
