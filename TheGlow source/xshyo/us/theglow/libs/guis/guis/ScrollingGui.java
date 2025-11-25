package xshyo.us.theglow.libs.guis.guis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.bukkit.entity.HumanEntity;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.InteractionModifier;
import xshyo.us.theglow.libs.guis.components.ScrollType;

public class ScrollingGui extends PaginatedGui {
   private final ScrollType scrollType;
   private int scrollSize;

   public ScrollingGui(int var1, int var2, @NotNull String var3, @NotNull ScrollType var4, @NotNull Set<InteractionModifier> var5) {
      super(var1, var2, var3, var5);
      this.scrollSize = 0;
      this.scrollType = var4;
   }

   @Deprecated
   public ScrollingGui(int var1, int var2, @NotNull String var3, @NotNull ScrollType var4) {
      super(var1, var2, var3);
      this.scrollSize = 0;
      this.scrollType = var4;
   }

   @Deprecated
   public ScrollingGui(int var1, int var2, @NotNull String var3) {
      this(var1, var2, var3, ScrollType.VERTICAL);
   }

   @Deprecated
   public ScrollingGui(int var1, @NotNull String var2) {
      this(var1, 0, var2, ScrollType.VERTICAL);
   }

   @Deprecated
   public ScrollingGui(int var1, @NotNull String var2, @NotNull ScrollType var3) {
      this(var1, 0, var2, var3);
   }

   @Deprecated
   public ScrollingGui(@NotNull String var1) {
      this(2, var1);
   }

   @Deprecated
   public ScrollingGui(@NotNull String var1, @NotNull ScrollType var2) {
      this(2, var1, var2);
   }

   public boolean next() {
      if (this.getPageNum() * this.scrollSize + this.getPageSize() > this.getPageItems().size() + this.scrollSize) {
         return false;
      } else {
         this.setPageNum(this.getPageNum() + 1);
         this.updatePage();
         return true;
      }
   }

   public boolean previous() {
      if (this.getPageNum() - 1 == 0) {
         return false;
      } else {
         this.setPageNum(this.getPageNum() - 1);
         this.updatePage();
         return true;
      }
   }

   public void open(@NotNull HumanEntity var1) {
      this.open(var1, 1);
   }

   public void open(@NotNull HumanEntity var1, int var2) {
      if (!var1.isSleeping()) {
         this.getInventory().clear();
         this.getMutableCurrentPageItems().clear();
         this.populateGui();
         if (this.getPageSize() == 0) {
            this.setPageSize(this.calculatePageSize());
         }

         if (this.scrollSize == 0) {
            this.scrollSize = this.calculateScrollSize();
         }

         if (var2 > 0 && var2 * this.scrollSize + this.getPageSize() <= this.getPageItems().size() + this.scrollSize) {
            this.setPageNum(var2);
         }

         this.populatePage();
         var1.openInventory(this.getInventory());
      }
   }

   void updatePage() {
      this.clearPage();
      this.populatePage();
   }

   private void populatePage() {
      Iterator var1 = this.getPage(this.getPageNum()).iterator();

      while(var1.hasNext()) {
         GuiItem var2 = (GuiItem)var1.next();
         if (this.scrollType == ScrollType.HORIZONTAL) {
            this.putItemHorizontally(var2);
         } else {
            this.putItemVertically(var2);
         }
      }

   }

   private int calculateScrollSize() {
      int var1 = 0;
      boolean var2;
      int var3;
      int var4;
      int var5;
      if (this.scrollType == ScrollType.VERTICAL) {
         var2 = false;

         for(var3 = 1; var3 <= this.getRows(); ++var3) {
            for(var4 = 1; var4 <= 9; ++var4) {
               var5 = this.getSlotFromRowCol(var3, var4);
               if (this.getInventory().getItem(var5) == null) {
                  if (!var2) {
                     var2 = true;
                  }

                  ++var1;
               }
            }

            if (var2) {
               return var1;
            }
         }

         return var1;
      } else {
         var2 = false;

         for(var3 = 1; var3 <= 9; ++var3) {
            for(var4 = 1; var4 <= this.getRows(); ++var4) {
               var5 = this.getSlotFromRowCol(var4, var3);
               if (this.getInventory().getItem(var5) == null) {
                  if (!var2) {
                     var2 = true;
                  }

                  ++var1;
               }
            }

            if (var2) {
               return var1;
            }
         }

         return var1;
      }
   }

   private void putItemVertically(GuiItem var1) {
      for(int var2 = 0; var2 < this.getRows() * 9; ++var2) {
         if (this.getGuiItem(var2) == null && this.getInventory().getItem(var2) == null) {
            this.getMutableCurrentPageItems().put(var2, var1);
            this.getInventory().setItem(var2, var1.getItemStack());
            break;
         }
      }

   }

   private void putItemHorizontally(GuiItem var1) {
      for(int var2 = 1; var2 < 10; ++var2) {
         for(int var3 = 1; var3 <= this.getRows(); ++var3) {
            int var4 = this.getSlotFromRowCol(var3, var2);
            if (this.getGuiItem(var4) == null && this.getInventory().getItem(var4) == null) {
               this.getMutableCurrentPageItems().put(var4, var1);
               this.getInventory().setItem(var4, var1.getItemStack());
               return;
            }
         }
      }

   }

   private List<GuiItem> getPage(int var1) {
      int var2 = var1 - 1;
      int var3 = this.getPageItems().size();
      ArrayList var4 = new ArrayList();
      int var5 = var2 * this.scrollSize + this.getPageSize();
      if (var5 > var3) {
         var5 = var3;
      }

      for(int var6 = var2 * this.scrollSize; var6 < var5; ++var6) {
         var4.add((GuiItem)this.getPageItems().get(var6));
      }

      return var4;
   }
}
