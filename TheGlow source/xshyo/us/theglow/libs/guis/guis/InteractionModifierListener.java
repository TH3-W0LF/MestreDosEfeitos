package xshyo.us.theglow.libs.guis.guis;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

public final class InteractionModifierListener implements Listener {
   private static final Set<InventoryAction> ITEM_TAKE_ACTIONS;
   private static final Set<InventoryAction> ITEM_PLACE_ACTIONS;
   private static final Set<InventoryAction> ITEM_SWAP_ACTIONS;
   private static final Set<InventoryAction> ITEM_DROP_ACTIONS;

   @EventHandler
   public void onGuiClick(InventoryClickEvent var1) {
      if (var1.getInventory().getHolder() instanceof BaseGui) {
         BaseGui var2 = (BaseGui)var1.getInventory().getHolder();
         if (var2.allInteractionsDisabled()) {
            var1.setCancelled(true);
            var1.setResult(Result.DENY);
         } else {
            if (!var2.canPlaceItems() && this.isPlaceItemEvent(var1) || !var2.canTakeItems() && this.isTakeItemEvent(var1) || !var2.canSwapItems() && this.isSwapItemEvent(var1) || !var2.canDropItems() && this.isDropItemEvent(var1) || !var2.allowsOtherActions() && this.isOtherEvent(var1)) {
               var1.setCancelled(true);
               var1.setResult(Result.DENY);
            }

         }
      }
   }

   @EventHandler
   public void onGuiDrag(InventoryDragEvent var1) {
      if (var1.getInventory().getHolder() instanceof BaseGui) {
         BaseGui var2 = (BaseGui)var1.getInventory().getHolder();
         if (var2.allInteractionsDisabled()) {
            var1.setCancelled(true);
            var1.setResult(Result.DENY);
         } else if (!var2.canPlaceItems() && this.isDraggingOnGui(var1)) {
            var1.setCancelled(true);
            var1.setResult(Result.DENY);
         }
      }
   }

   private boolean isTakeItemEvent(InventoryClickEvent var1) {
      Preconditions.checkNotNull(var1, "event cannot be null");
      Inventory var2 = var1.getInventory();
      Inventory var3 = var1.getClickedInventory();
      InventoryAction var4 = var1.getAction();
      if ((var3 == null || var3.getType() != InventoryType.PLAYER) && var2.getType() != InventoryType.PLAYER) {
         return var4 == InventoryAction.MOVE_TO_OTHER_INVENTORY || this.isTakeAction(var4);
      } else {
         return false;
      }
   }

   private boolean isPlaceItemEvent(InventoryClickEvent var1) {
      Preconditions.checkNotNull(var1, "event cannot be null");
      Inventory var2 = var1.getInventory();
      Inventory var3 = var1.getClickedInventory();
      InventoryAction var4 = var1.getAction();
      if (var4 == InventoryAction.MOVE_TO_OTHER_INVENTORY && var3 != null && var3.getType() == InventoryType.PLAYER && var2.getType() != var3.getType()) {
         return true;
      } else {
         return this.isPlaceAction(var4) && (var3 == null || var3.getType() != InventoryType.PLAYER) && var2.getType() != InventoryType.PLAYER;
      }
   }

   private boolean isSwapItemEvent(InventoryClickEvent var1) {
      Preconditions.checkNotNull(var1, "event cannot be null");
      Inventory var2 = var1.getInventory();
      Inventory var3 = var1.getClickedInventory();
      InventoryAction var4 = var1.getAction();
      return this.isSwapAction(var4) && (var3 == null || var3.getType() != InventoryType.PLAYER) && var2.getType() != InventoryType.PLAYER;
   }

   private boolean isDropItemEvent(InventoryClickEvent var1) {
      Preconditions.checkNotNull(var1, "event cannot be null");
      Inventory var2 = var1.getInventory();
      Inventory var3 = var1.getClickedInventory();
      InventoryAction var4 = var1.getAction();
      return this.isDropAction(var4) && (var3 != null || var2.getType() != InventoryType.PLAYER);
   }

   private boolean isOtherEvent(InventoryClickEvent var1) {
      Preconditions.checkNotNull(var1, "event cannot be null");
      Inventory var2 = var1.getInventory();
      Inventory var3 = var1.getClickedInventory();
      InventoryAction var4 = var1.getAction();
      return this.isOtherAction(var4) && (var3 != null || var2.getType() != InventoryType.PLAYER);
   }

   private boolean isDraggingOnGui(InventoryDragEvent var1) {
      Preconditions.checkNotNull(var1, "event cannot be null");
      int var2 = var1.getView().getTopInventory().getSize();
      return var1.getRawSlots().stream().anyMatch((var1x) -> {
         return var1x < var2;
      });
   }

   private boolean isTakeAction(InventoryAction var1) {
      Preconditions.checkNotNull(var1, "action cannot be null");
      return ITEM_TAKE_ACTIONS.contains(var1);
   }

   private boolean isPlaceAction(InventoryAction var1) {
      Preconditions.checkNotNull(var1, "action cannot be null");
      return ITEM_PLACE_ACTIONS.contains(var1);
   }

   private boolean isSwapAction(InventoryAction var1) {
      Preconditions.checkNotNull(var1, "action cannot be null");
      return ITEM_SWAP_ACTIONS.contains(var1);
   }

   private boolean isDropAction(InventoryAction var1) {
      Preconditions.checkNotNull(var1, "action cannot be null");
      return ITEM_DROP_ACTIONS.contains(var1);
   }

   private boolean isOtherAction(InventoryAction var1) {
      Preconditions.checkNotNull(var1, "action cannot be null");
      return var1 == InventoryAction.CLONE_STACK || var1 == InventoryAction.UNKNOWN;
   }

   static {
      ITEM_TAKE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PICKUP_ONE, InventoryAction.PICKUP_SOME, InventoryAction.PICKUP_HALF, InventoryAction.PICKUP_ALL, InventoryAction.COLLECT_TO_CURSOR, InventoryAction.HOTBAR_SWAP, InventoryAction.MOVE_TO_OTHER_INVENTORY));
      ITEM_PLACE_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.PLACE_ONE, InventoryAction.PLACE_SOME, InventoryAction.PLACE_ALL));
      ITEM_SWAP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.HOTBAR_SWAP, InventoryAction.SWAP_WITH_CURSOR, InventoryAction.HOTBAR_MOVE_AND_READD));
      ITEM_DROP_ACTIONS = Collections.unmodifiableSet(EnumSet.of(InventoryAction.DROP_ONE_SLOT, InventoryAction.DROP_ALL_SLOT, InventoryAction.DROP_ONE_CURSOR, InventoryAction.DROP_ALL_CURSOR));
   }
}
