package xshyo.us.theglow.data;

import xshyo.us.theglow.enums.Filter;
import xshyo.us.theglow.enums.Order;

public class MenuData {
   private Order order;
   private Filter filter;

   public Order getOrder() {
      return this.order;
   }

   public Filter getFilter() {
      return this.filter;
   }

   public void setOrder(Order var1) {
      this.order = var1;
   }

   public void setFilter(Filter var1) {
      this.filter = var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof MenuData)) {
         return false;
      } else {
         MenuData var2 = (MenuData)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            Order var3 = this.getOrder();
            Order var4 = var2.getOrder();
            if (var3 == null) {
               if (var4 != null) {
                  return false;
               }
            } else if (!var3.equals(var4)) {
               return false;
            }

            Filter var5 = this.getFilter();
            Filter var6 = var2.getFilter();
            if (var5 == null) {
               if (var6 != null) {
                  return false;
               }
            } else if (!var5.equals(var6)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof MenuData;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      Order var3 = this.getOrder();
      int var5 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      Filter var4 = this.getFilter();
      var5 = var5 * 59 + (var4 == null ? 43 : var4.hashCode());
      return var5;
   }

   public String toString() {
      return "MenuData(order=" + this.getOrder() + ", filter=" + this.getFilter() + ")";
   }

   public MenuData(Order var1, Filter var2) {
      this.order = var1;
      this.filter = var2;
   }
}
