package xshyo.us.theglow.libs.guis.guis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.InteractionModifier;

public class PaginatedGui extends BaseGui {
   private final List<GuiItem> pageItems;
   private final Map<Integer, GuiItem> currentPage;
   private int pageSize;
   private int pageNum;

   public PaginatedGui(int var1, int var2, @NotNull String var3, @NotNull Set<InteractionModifier> var4) {
      super(var1, var3, var4);
      this.pageItems = new ArrayList();
      this.pageNum = 1;
      this.pageSize = var2;
      int var5 = var1 * 9;
      this.currentPage = new LinkedHashMap(var5);
   }

   @Deprecated
   public PaginatedGui(int var1, int var2, @NotNull String var3) {
      super(var1, var3);
      this.pageItems = new ArrayList();
      this.pageNum = 1;
      this.pageSize = var2;
      int var4 = var1 * 9;
      this.currentPage = new LinkedHashMap(var4);
   }

   @Deprecated
   public PaginatedGui(int var1, @NotNull String var2) {
      this(var1, 0, var2);
   }

   @Deprecated
   public PaginatedGui(@NotNull String var1) {
      this(2, var1);
   }

   public BaseGui setPageSize(int var1) {
      this.pageSize = var1;
      return this;
   }

   public void addItem(@NotNull GuiItem var1) {
      this.pageItems.add(var1);
   }

   public void addItem(@NotNull GuiItem... var1) {
      this.pageItems.addAll(Arrays.asList(var1));
   }

   public void update() {
      this.getInventory().clear();
      this.populateGui();
      this.updatePage();
   }

   public void updatePageItem(int var1, @NotNull ItemStack var2) {
      if (this.currentPage.containsKey(var1)) {
         GuiItem var3 = (GuiItem)this.currentPage.get(var1);
         var3.setItemStack(var2);
         this.getInventory().setItem(var1, var3.getItemStack());
      }
   }

   public void updatePageItem(int var1, int var2, @NotNull ItemStack var3) {
      this.updateItem(this.getSlotFromRowCol(var1, var2), var3);
   }

   public void updatePageItem(int var1, @NotNull GuiItem var2) {
      if (this.currentPage.containsKey(var1)) {
         GuiItem var10000 = (GuiItem)this.currentPage.get(var1);
         int var4 = this.pageItems.indexOf(this.currentPage.get(var1));
         this.currentPage.put(var1, var2);
         this.pageItems.set(var4, var2);
         this.getInventory().setItem(var1, var2.getItemStack());
      }
   }

   public void updatePageItem(int var1, int var2, @NotNull GuiItem var3) {
      this.updateItem(this.getSlotFromRowCol(var1, var2), var3);
   }

   public void removePageItem(@NotNull GuiItem var1) {
      this.pageItems.remove(var1);
      this.updatePage();
   }

   public void removePageItem(@NotNull ItemStack var1) {
      Optional var2 = this.pageItems.stream().filter((var1x) -> {
         return var1x.getItemStack().equals(var1);
      }).findFirst();
      var2.ifPresent(this::removePageItem);
   }

   public void open(@NotNull HumanEntity var1) {
      this.open(var1, 1);
   }

   public void open(@NotNull HumanEntity var1, int var2) {
      if (!var1.isSleeping()) {
         if (var2 <= this.getPagesNum() || var2 > 0) {
            this.pageNum = var2;
         }

         this.getInventory().clear();
         this.currentPage.clear();
         this.populateGui();
         if (this.pageSize == 0) {
            this.pageSize = this.calculatePageSize();
         }

         this.populatePage();
         var1.openInventory(this.getInventory());
      }
   }

   @NotNull
   public BaseGui updateTitle(@NotNull String var1) {
      this.setUpdating(true);
      ArrayList var2 = new ArrayList(this.getInventory().getViewers());
      this.setInventory(Bukkit.createInventory(this, this.getInventory().getSize(), var1));
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         HumanEntity var4 = (HumanEntity)var3.next();
         this.open(var4, this.getPageNum());
      }

      this.setUpdating(false);
      return this;
   }

   @NotNull
   public Map<Integer, GuiItem> getCurrentPageItems() {
      return Collections.unmodifiableMap(this.currentPage);
   }

   @NotNull
   public List<GuiItem> getPageItems() {
      return Collections.unmodifiableList(this.pageItems);
   }

   public int getCurrentPageNum() {
      return this.pageNum;
   }

   public int getNextPageNum() {
      return this.pageNum + 1 > this.getPagesNum() ? this.pageNum : this.pageNum + 1;
   }

   public int getPrevPageNum() {
      return this.pageNum - 1 == 0 ? this.pageNum : this.pageNum - 1;
   }

   public boolean next() {
      if (this.pageNum + 1 > this.getPagesNum()) {
         return false;
      } else {
         ++this.pageNum;
         this.updatePage();
         return true;
      }
   }

   public boolean previous() {
      if (this.pageNum - 1 == 0) {
         return false;
      } else {
         --this.pageNum;
         this.updatePage();
         return true;
      }
   }

   GuiItem getPageItem(int var1) {
      return (GuiItem)this.currentPage.get(var1);
   }

   private List<GuiItem> getPageNum(int var1) {
      int var2 = var1 - 1;
      ArrayList var3 = new ArrayList();
      int var4 = var2 * this.pageSize + this.pageSize;
      if (var4 > this.pageItems.size()) {
         var4 = this.pageItems.size();
      }

      for(int var5 = var2 * this.pageSize; var5 < var4; ++var5) {
         var3.add((GuiItem)this.pageItems.get(var5));
      }

      return var3;
   }

   public int getPagesNum() {
      return (int)Math.ceil((double)this.pageItems.size() / (double)this.pageSize);
   }

   private void populatePage() {
      int var1 = 0;
      Iterator var2 = this.getPageNum(this.pageNum).iterator();

      while(true) {
         while(var2.hasNext()) {
            if (this.getGuiItem(var1) == null && this.getInventory().getItem(var1) == null) {
               GuiItem var3 = (GuiItem)var2.next();
               this.currentPage.put(var1, var3);
               this.getInventory().setItem(var1, var3.getItemStack());
               ++var1;
            } else {
               ++var1;
            }
         }

         return;
      }
   }

   Map<Integer, GuiItem> getMutableCurrentPageItems() {
      return this.currentPage;
   }

   void clearPage() {
      Iterator var1 = this.currentPage.entrySet().iterator();

      while(var1.hasNext()) {
         Entry var2 = (Entry)var1.next();
         this.getInventory().setItem((Integer)var2.getKey(), (ItemStack)null);
      }

   }

   public void clearPageItems(boolean var1) {
      this.pageItems.clear();
      if (var1) {
         this.update();
      }

   }

   public void clearPageItems() {
      this.clearPageItems(false);
   }

   int getPageSize() {
      return this.pageSize;
   }

   int getPageNum() {
      return this.pageNum;
   }

   public void setPageNum(int var1) {
      this.pageNum = var1;
   }

   void updatePage() {
      this.clearPage();
      this.populatePage();
   }

   int calculatePageSize() {
      int var1 = 0;

      for(int var2 = 0; var2 < this.getRows() * 9; ++var2) {
         if (this.getInventory().getItem(var2) == null) {
            ++var1;
         }
      }

      return var1;
   }
}
