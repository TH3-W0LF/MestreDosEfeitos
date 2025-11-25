package xshyo.us.theglow.libs.guis.components.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.GuiType;
import xshyo.us.theglow.libs.guis.components.exception.GuiException;
import xshyo.us.theglow.libs.guis.guis.BaseGui;
import xshyo.us.theglow.libs.guis.guis.GuiItem;
import xshyo.us.theglow.libs.guis.guis.PaginatedGui;

public final class GuiFiller {
   private final BaseGui gui;

   public GuiFiller(BaseGui var1) {
      this.gui = var1;
   }

   public void fillTop(@NotNull GuiItem var1) {
      this.fillTop(Collections.singletonList(var1));
   }

   public void fillTop(@NotNull List<GuiItem> var1) {
      List var2 = this.repeatList(var1);

      for(int var3 = 0; var3 < 9; ++var3) {
         if (!this.gui.getGuiItems().containsKey(var3)) {
            this.gui.setItem(var3, (GuiItem)var2.get(var3));
         }
      }

   }

   public void fillBottom(@NotNull GuiItem var1) {
      this.fillBottom(Collections.singletonList(var1));
   }

   public void fillBottom(@NotNull List<GuiItem> var1) {
      int var2 = this.gui.getRows();
      List var3 = this.repeatList(var1);

      for(int var4 = 9; var4 > 0; --var4) {
         if (this.gui.getGuiItems().get(var2 * 9 - var4) == null) {
            this.gui.setItem(var2 * 9 - var4, (GuiItem)var3.get(var4));
         }
      }

   }

   public void fillBorder(@NotNull GuiItem var1) {
      this.fillBorder(Collections.singletonList(var1));
   }

   public void fillBorder(@NotNull List<GuiItem> var1) {
      int var2 = this.gui.getRows();
      if (var2 > 2) {
         List var3 = this.repeatList(var1);

         for(int var4 = 0; var4 < var2 * 9; ++var4) {
            if (var4 <= 8 || var4 >= var2 * 9 - 8 && var4 <= var2 * 9 - 2 || var4 % 9 == 0 || var4 % 9 == 8) {
               this.gui.setItem(var4, (GuiItem)var3.get(var4));
            }
         }

      }
   }

   public void fillBetweenPoints(int var1, int var2, int var3, int var4, @NotNull GuiItem var5) {
      this.fillBetweenPoints(var1, var2, var3, var4, Collections.singletonList(var5));
   }

   public void fillBetweenPoints(int var1, int var2, int var3, int var4, @NotNull List<GuiItem> var5) {
      int var6 = Math.min(var1, var3);
      int var7 = Math.max(var1, var3);
      int var8 = Math.min(var2, var4);
      int var9 = Math.max(var2, var4);
      int var10 = this.gui.getRows();
      List var11 = this.repeatList(var5);

      for(int var12 = 1; var12 <= var10; ++var12) {
         for(int var13 = 1; var13 <= 9; ++var13) {
            int var14 = this.getSlotFromRowCol(var12, var13);
            if (var12 >= var6 && var12 <= var7 && var13 >= var8 && var13 <= var9) {
               this.gui.setItem(var14, (GuiItem)var11.get(var14));
            }
         }
      }

   }

   public void fill(@NotNull GuiItem var1) {
      this.fill(Collections.singletonList(var1));
   }

   public void fill(@NotNull List<GuiItem> var1) {
      if (this.gui instanceof PaginatedGui) {
         throw new GuiException("Full filling a GUI is not supported in a Paginated GUI!");
      } else {
         GuiType var2 = this.gui.guiType();
         int var3;
         if (var2 == GuiType.CHEST) {
            var3 = this.gui.getRows() * var2.getLimit();
         } else {
            var3 = var2.getLimit();
         }

         List var4 = this.repeatList(var1);

         for(int var5 = 0; var5 < var3; ++var5) {
            if (this.gui.getGuiItems().get(var5) == null) {
               this.gui.setItem(var5, (GuiItem)var4.get(var5));
            }
         }

      }
   }

   public void fillSide(@NotNull GuiFiller.Side var1, @NotNull List<GuiItem> var2) {
      switch(var1) {
      case LEFT:
         this.fillBetweenPoints(1, 1, this.gui.getRows(), 1, (List)var2);
      case RIGHT:
         this.fillBetweenPoints(1, 9, this.gui.getRows(), 9, (List)var2);
      case BOTH:
         this.fillBetweenPoints(1, 1, this.gui.getRows(), 1, (List)var2);
         this.fillBetweenPoints(1, 9, this.gui.getRows(), 9, (List)var2);
      default:
      }
   }

   private List<GuiItem> repeatList(@NotNull List<GuiItem> var1) {
      ArrayList var2 = new ArrayList();
      List var10000 = Collections.nCopies(this.gui.getRows() * 9, var1);
      Objects.requireNonNull(var2);
      var10000.forEach(var2::addAll);
      return var2;
   }

   private int getSlotFromRowCol(int var1, int var2) {
      return var2 + (var1 - 1) * 9 - 1;
   }

   public static enum Side {
      LEFT,
      RIGHT,
      BOTH;

      // $FF: synthetic method
      private static GuiFiller.Side[] $values() {
         return new GuiFiller.Side[]{LEFT, RIGHT, BOTH};
      }
   }
}
