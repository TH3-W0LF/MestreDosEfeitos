package xshyo.us.theglow.libs.guis.guis;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.guis.components.GuiAction;
import xshyo.us.theglow.libs.guis.components.util.ItemNbt;

public final class GuiListener implements Listener {
   @EventHandler
   public void onGuiClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() instanceof BaseGui) {
         BaseGui var2 = (BaseGui)var1.getInventory().getHolder();
         GuiAction var3 = var2.getOutsideClickAction();
         if (var3 != null && var1.getClickedInventory() == null) {
            var3.execute(var1);
         } else if (var1.getClickedInventory() != null) {
            GuiAction var4 = var2.getDefaultTopClickAction();
            if (var4 != null && var1.getClickedInventory().getType() != InventoryType.PLAYER) {
               var4.execute(var1);
            }

            GuiAction var5 = var2.getPlayerInventoryAction();
            if (var5 != null && var1.getClickedInventory().getType() == InventoryType.PLAYER) {
               var5.execute(var1);
            }

            GuiAction var6 = var2.getDefaultClickAction();
            if (var6 != null) {
               var6.execute(var1);
            }

            GuiAction var7 = var2.getSlotAction(var1.getSlot());
            if (var7 != null && var1.getClickedInventory().getType() != InventoryType.PLAYER) {
               var7.execute(var1);
            }

            GuiItem var8;
            if (var2 instanceof PaginatedGui) {
               PaginatedGui var9 = (PaginatedGui)var2;
               var8 = var9.getGuiItem(var1.getSlot());
               if (var8 == null) {
                  var8 = var9.getPageItem(var1.getSlot());
               }
            } else {
               var8 = var2.getGuiItem(var1.getSlot());
            }

            if (this.isGuiItem(var1.getCurrentItem(), var8)) {
               GuiAction var10 = var8.getAction();
               if (var10 != null) {
                  var10.execute(var1);
               }

            }
         }
      }
   }

   @EventHandler
   public void onGuiDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof BaseGui) {
         BaseGui var2 = (BaseGui)var1.getInventory().getHolder();
         GuiAction var3 = var2.getDragAction();
         if (var3 != null) {
            var3.execute(var1);
         }

      }
   }

   @EventHandler
   public void onGuiClose(InventoryCloseEvent var1) {
      if (var1.getInventory().getHolder() instanceof BaseGui) {
         BaseGui var2 = (BaseGui)var1.getInventory().getHolder();
         GuiAction var3 = var2.getCloseGuiAction();
         if (var3 != null && !var2.isUpdating() && var2.shouldRunCloseAction()) {
            var3.execute(var1);
         }

      }
   }

   @EventHandler
   public void onGuiOpen(InventoryOpenEvent var1) {
      if (var1.getInventory().getHolder() instanceof BaseGui) {
         BaseGui var2 = (BaseGui)var1.getInventory().getHolder();
         GuiAction var3 = var2.getOpenGuiAction();
         if (var3 != null && !var2.isUpdating()) {
            var3.execute(var1);
         }

      }
   }

   private boolean isGuiItem(@Nullable ItemStack var1, @Nullable GuiItem var2) {
      if (var1 != null && var2 != null) {
         String var3 = ItemNbt.getString(var1, "mf-gui");
         return var3 == null ? false : var3.equals(var2.getUuid().toString());
      } else {
         return false;
      }
   }
}
