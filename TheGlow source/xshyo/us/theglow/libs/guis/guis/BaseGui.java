package xshyo.us.theglow.libs.guis.guis;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.guis.components.GuiAction;
import xshyo.us.theglow.libs.guis.components.GuiType;
import xshyo.us.theglow.libs.guis.components.InteractionModifier;
import xshyo.us.theglow.libs.guis.components.exception.GuiException;
import xshyo.us.theglow.libs.guis.components.util.GuiFiller;
import xshyo.us.theglow.libs.guis.components.util.Legacy;
import xshyo.us.theglow.libs.guis.components.util.VersionHelper;
import xshyo.us.theglow.libs.kyori.adventure.text.Component;

public abstract class BaseGui implements InventoryHolder {
   private static final Plugin plugin = JavaPlugin.getProvidingPlugin(BaseGui.class);
   private static Method GET_SCHEDULER_METHOD = null;
   private static Method EXECUTE_METHOD = null;
   private final GuiFiller filler = new GuiFiller(this);
   private final Map<Integer, GuiItem> guiItems;
   private final Map<Integer, GuiAction<InventoryClickEvent>> slotActions;
   private final Set<InteractionModifier> interactionModifiers;
   private Inventory inventory;
   private String title;
   private int rows = 1;
   private GuiType guiType;
   private GuiAction<InventoryClickEvent> defaultClickAction;
   private GuiAction<InventoryClickEvent> defaultTopClickAction;
   private GuiAction<InventoryClickEvent> playerInventoryAction;
   private GuiAction<InventoryDragEvent> dragAction;
   private GuiAction<InventoryCloseEvent> closeGuiAction;
   private GuiAction<InventoryOpenEvent> openGuiAction;
   private GuiAction<InventoryClickEvent> outsideClickAction;
   private boolean updating;
   private boolean runCloseAction;
   private boolean runOpenAction;

   public BaseGui(int var1, @NotNull String var2, @NotNull Set<InteractionModifier> var3) {
      this.guiType = GuiType.CHEST;
      this.runCloseAction = true;
      this.runOpenAction = true;
      int var4 = var1;
      if (var1 < 1 || var1 > 6) {
         var4 = 1;
      }

      this.rows = var4;
      this.interactionModifiers = this.safeCopyOf(var3);
      this.title = var2;
      int var5 = this.rows * 9;
      this.inventory = Bukkit.createInventory(this, var5, var2);
      this.slotActions = new LinkedHashMap(var5);
      this.guiItems = new LinkedHashMap(var5);
   }

   public BaseGui(@NotNull GuiType var1, @NotNull String var2, @NotNull Set<InteractionModifier> var3) {
      this.guiType = GuiType.CHEST;
      this.runCloseAction = true;
      this.runOpenAction = true;
      this.guiType = var1;
      this.interactionModifiers = this.safeCopyOf(var3);
      this.title = var2;
      int var4 = var1.getLimit();
      this.inventory = Bukkit.createInventory(this, var1.getInventoryType(), var2);
      this.slotActions = new LinkedHashMap(var4);
      this.guiItems = new LinkedHashMap(var4);
   }

   @Deprecated
   public BaseGui(int var1, @NotNull String var2) {
      this.guiType = GuiType.CHEST;
      this.runCloseAction = true;
      this.runOpenAction = true;
      int var3 = var1;
      if (var1 < 1 || var1 > 6) {
         var3 = 1;
      }

      this.rows = var3;
      this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
      this.title = var2;
      this.inventory = Bukkit.createInventory(this, this.rows * 9, var2);
      this.slotActions = new LinkedHashMap();
      this.guiItems = new LinkedHashMap();
   }

   @Deprecated
   public BaseGui(@NotNull GuiType var1, @NotNull String var2) {
      this.guiType = GuiType.CHEST;
      this.runCloseAction = true;
      this.runOpenAction = true;
      this.guiType = var1;
      this.interactionModifiers = EnumSet.noneOf(InteractionModifier.class);
      this.title = var2;
      this.inventory = Bukkit.createInventory(this, this.guiType.getInventoryType(), var2);
      this.slotActions = new LinkedHashMap();
      this.guiItems = new LinkedHashMap();
   }

   @NotNull
   private Set<InteractionModifier> safeCopyOf(@NotNull Set<InteractionModifier> var1) {
      return var1.isEmpty() ? EnumSet.noneOf(InteractionModifier.class) : EnumSet.copyOf(var1);
   }

   @Deprecated
   @NotNull
   public String getTitle() {
      return this.title;
   }

   @NotNull
   public Component title() {
      return Legacy.SERIALIZER.deserialize(this.title);
   }

   public void setItem(int var1, @NotNull GuiItem var2) {
      this.validateSlot(var1);
      this.guiItems.put(var1, var2);
   }

   public void removeItem(@NotNull GuiItem var1) {
      Optional var2 = this.guiItems.entrySet().stream().filter((var1x) -> {
         return ((GuiItem)var1x.getValue()).equals(var1);
      }).findFirst();
      var2.ifPresent((var1x) -> {
         this.guiItems.remove(var1x.getKey());
         this.inventory.remove(((GuiItem)var1x.getValue()).getItemStack());
      });
   }

   public void removeItem(@NotNull ItemStack var1) {
      Optional var2 = this.guiItems.entrySet().stream().filter((var1x) -> {
         return ((GuiItem)var1x.getValue()).getItemStack().equals(var1);
      }).findFirst();
      var2.ifPresent((var2x) -> {
         this.guiItems.remove(var2x.getKey());
         this.inventory.remove(var1);
      });
   }

   public void removeItem(int var1) {
      this.validateSlot(var1);
      this.guiItems.remove(var1);
      this.inventory.setItem(var1, (ItemStack)null);
   }

   public void removeItem(int var1, int var2) {
      this.removeItem(this.getSlotFromRowCol(var1, var2));
   }

   public void setItem(@NotNull List<Integer> var1, @NotNull GuiItem var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         int var4 = (Integer)var3.next();
         this.setItem(var4, var2);
      }

   }

   public void setItem(int var1, int var2, @NotNull GuiItem var3) {
      this.setItem(this.getSlotFromRowCol(var1, var2), var3);
   }

   public void addItem(@NotNull GuiItem... var1) {
      this.addItem(false, var1);
   }

   public void addItem(boolean var1, @NotNull GuiItem... var2) {
      ArrayList var3 = new ArrayList();
      GuiItem[] var4 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         GuiItem var7 = var4[var6];

         for(int var8 = 0; var8 < this.rows * 9; ++var8) {
            if (this.guiItems.get(var8) == null) {
               this.guiItems.put(var8, var7);
               break;
            }

            if (var8 == this.rows * 9 - 1) {
               var3.add(var7);
            }
         }
      }

      if (var1 && this.rows < 6 && !var3.isEmpty() && (this.guiType == null || this.guiType == GuiType.CHEST)) {
         ++this.rows;
         this.inventory = Bukkit.createInventory(this, this.rows * 9, this.title);
         this.update();
         this.addItem(true, (GuiItem[])var3.toArray(new GuiItem[0]));
      }
   }

   public void addSlotAction(int var1, @Nullable GuiAction<InventoryClickEvent> var2) {
      this.validateSlot(var1);
      this.slotActions.put(var1, var2);
   }

   public void addSlotAction(int var1, int var2, @Nullable GuiAction<InventoryClickEvent> var3) {
      this.addSlotAction(this.getSlotFromRowCol(var1, var2), var3);
   }

   @Nullable
   public GuiItem getGuiItem(int var1) {
      return (GuiItem)this.guiItems.get(var1);
   }

   public boolean isUpdating() {
      return this.updating;
   }

   public void setUpdating(boolean var1) {
      this.updating = var1;
   }

   public void open(@NotNull HumanEntity var1) {
      if (!var1.isSleeping()) {
         this.inventory.clear();
         this.populateGui();
         var1.openInventory(this.inventory);
      }
   }

   public void close(@NotNull HumanEntity var1) {
      this.close(var1, true);
   }

   public void close(@NotNull HumanEntity var1, boolean var2) {
      Runnable var3 = () -> {
         this.runCloseAction = var2;
         var1.closeInventory();
         this.runCloseAction = true;
      };
      if (VersionHelper.IS_FOLIA) {
         if (GET_SCHEDULER_METHOD != null && EXECUTE_METHOD != null) {
            try {
               EXECUTE_METHOD.invoke(GET_SCHEDULER_METHOD.invoke(var1), plugin, var3, null, 2L);
            } catch (InvocationTargetException | IllegalAccessException var5) {
               throw new GuiException("Could not invoke Folia task.", var5);
            }
         } else {
            throw new GuiException("Could not find Folia Scheduler methods.");
         }
      } else {
         Bukkit.getScheduler().runTaskLater(plugin, var3, 2L);
      }
   }

   public void update() {
      this.inventory.clear();
      this.populateGui();
      Iterator var1 = (new ArrayList(this.inventory.getViewers())).iterator();

      while(var1.hasNext()) {
         HumanEntity var2 = (HumanEntity)var1.next();
         ((Player)var2).updateInventory();
      }

   }

   @Contract("_ -> this")
   @NotNull
   public BaseGui updateTitle(@NotNull String var1) {
      this.updating = true;
      ArrayList var2 = new ArrayList(this.inventory.getViewers());
      this.inventory = Bukkit.createInventory(this, this.inventory.getSize(), var1);
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         HumanEntity var4 = (HumanEntity)var3.next();
         this.open(var4);
      }

      this.updating = false;
      this.title = var1;
      return this;
   }

   public void updateItem(int var1, @NotNull ItemStack var2) {
      GuiItem var3 = (GuiItem)this.guiItems.get(var1);
      if (var3 == null) {
         this.updateItem(var1, new GuiItem(var2));
      } else {
         var3.setItemStack(var2);
         this.updateItem(var1, var3);
      }
   }

   public void updateItem(int var1, int var2, @NotNull ItemStack var3) {
      this.updateItem(this.getSlotFromRowCol(var1, var2), var3);
   }

   public void updateItem(int var1, @NotNull GuiItem var2) {
      this.guiItems.put(var1, var2);
      this.inventory.setItem(var1, var2.getItemStack());
   }

   public void updateItem(int var1, int var2, @NotNull GuiItem var3) {
      this.updateItem(this.getSlotFromRowCol(var1, var2), var3);
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui disableItemPlace() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_PLACE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui disableItemTake() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_TAKE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui disableItemSwap() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_SWAP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui disableItemDrop() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_ITEM_DROP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui disableOtherActions() {
      this.interactionModifiers.add(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui disableAllInteractions() {
      this.interactionModifiers.addAll(InteractionModifier.VALUES);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui enableItemPlace() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_PLACE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui enableItemTake() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_TAKE);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui enableItemSwap() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_SWAP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui enableItemDrop() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_ITEM_DROP);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui enableOtherActions() {
      this.interactionModifiers.remove(InteractionModifier.PREVENT_OTHER_ACTIONS);
      return this;
   }

   @NotNull
   @Contract(" -> this")
   public BaseGui enableAllInteractions() {
      this.interactionModifiers.clear();
      return this;
   }

   public boolean allInteractionsDisabled() {
      return this.interactionModifiers.size() == InteractionModifier.VALUES.size();
   }

   public boolean canPlaceItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_PLACE);
   }

   public boolean canTakeItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_TAKE);
   }

   public boolean canSwapItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_SWAP);
   }

   public boolean canDropItems() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_ITEM_DROP);
   }

   public boolean allowsOtherActions() {
      return !this.interactionModifiers.contains(InteractionModifier.PREVENT_OTHER_ACTIONS);
   }

   @NotNull
   public GuiFiller getFiller() {
      return this.filler;
   }

   @NotNull
   public Map<Integer, GuiItem> getGuiItems() {
      return this.guiItems;
   }

   @NotNull
   public Inventory getInventory() {
      return this.inventory;
   }

   public void setInventory(@NotNull Inventory var1) {
      this.inventory = var1;
   }

   public int getRows() {
      return this.rows;
   }

   @NotNull
   public GuiType guiType() {
      return this.guiType;
   }

   @Nullable
   GuiAction<InventoryClickEvent> getDefaultClickAction() {
      return this.defaultClickAction;
   }

   public void setDefaultClickAction(@Nullable GuiAction<InventoryClickEvent> var1) {
      this.defaultClickAction = var1;
   }

   @Nullable
   GuiAction<InventoryClickEvent> getDefaultTopClickAction() {
      return this.defaultTopClickAction;
   }

   public void setDefaultTopClickAction(@Nullable GuiAction<InventoryClickEvent> var1) {
      this.defaultTopClickAction = var1;
   }

   @Nullable
   GuiAction<InventoryClickEvent> getPlayerInventoryAction() {
      return this.playerInventoryAction;
   }

   public void setPlayerInventoryAction(@Nullable GuiAction<InventoryClickEvent> var1) {
      this.playerInventoryAction = var1;
   }

   @Nullable
   GuiAction<InventoryDragEvent> getDragAction() {
      return this.dragAction;
   }

   public void setDragAction(@Nullable GuiAction<InventoryDragEvent> var1) {
      this.dragAction = var1;
   }

   @Nullable
   GuiAction<InventoryCloseEvent> getCloseGuiAction() {
      return this.closeGuiAction;
   }

   public void setCloseGuiAction(@Nullable GuiAction<InventoryCloseEvent> var1) {
      this.closeGuiAction = var1;
   }

   @Nullable
   GuiAction<InventoryOpenEvent> getOpenGuiAction() {
      return this.openGuiAction;
   }

   public void setOpenGuiAction(@Nullable GuiAction<InventoryOpenEvent> var1) {
      this.openGuiAction = var1;
   }

   @Nullable
   GuiAction<InventoryClickEvent> getOutsideClickAction() {
      return this.outsideClickAction;
   }

   public void setOutsideClickAction(@Nullable GuiAction<InventoryClickEvent> var1) {
      this.outsideClickAction = var1;
   }

   @Nullable
   GuiAction<InventoryClickEvent> getSlotAction(int var1) {
      return (GuiAction)this.slotActions.get(var1);
   }

   void populateGui() {
      Iterator var1 = this.guiItems.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         this.inventory.setItem((Integer)var2.getKey(), ((GuiItem)var2.getValue()).getItemStack());
      }

   }

   boolean shouldRunCloseAction() {
      return this.runCloseAction;
   }

   boolean shouldRunOpenAction() {
      return this.runOpenAction;
   }

   int getSlotFromRowCol(int var1, int var2) {
      return var2 + (var1 - 1) * 9 - 1;
   }

   private void validateSlot(int var1) {
      int var2 = this.guiType.getLimit();
      if (this.guiType == GuiType.CHEST) {
         if (var1 < 0 || var1 >= this.rows * var2) {
            this.throwInvalidSlot(var1);
         }

      } else {
         if (var1 < 0 || var1 > var2) {
            this.throwInvalidSlot(var1);
         }

      }
   }

   private void throwInvalidSlot(int var1) {
      if (this.guiType == GuiType.CHEST) {
         throw new GuiException("Slot " + var1 + " is not valid for the gui type - " + this.guiType.name() + " and rows - " + this.rows + "!");
      } else {
         throw new GuiException("Slot " + var1 + " is not valid for the gui type - " + this.guiType.name() + "!");
      }
   }

   static {
      try {
         GET_SCHEDULER_METHOD = Entity.class.getMethod("getScheduler");
         Class var0 = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
         EXECUTE_METHOD = var0.getMethod("execute", Plugin.class, Runnable.class, Runnable.class, Long.TYPE);
      } catch (ClassNotFoundException | NoSuchMethodException var1) {
      }

      Bukkit.getPluginManager().registerEvents(new GuiListener(), plugin);
      Bukkit.getPluginManager().registerEvents(new InteractionModifierListener(), plugin);
   }
}
