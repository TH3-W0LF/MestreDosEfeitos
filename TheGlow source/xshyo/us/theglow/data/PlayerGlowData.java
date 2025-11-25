package xshyo.us.theglow.data;

import java.util.Collections;
import java.util.UUID;
import xshyo.us.theglow.enums.Filter;
import xshyo.us.theglow.enums.Order;

public class PlayerGlowData {
   private final UUID uuid;
   private final String name;
   private final CurrentGlow currentGlow = new CurrentGlow("", Collections.emptyList(), true);
   private final MenuData menuData;

   public String toString() {
      return "PlayerGlowData{uuid=" + this.uuid + ", name='" + this.name + '\'' + ", currentGlow=" + this.currentGlow + ", menuData=" + this.menuData + '}';
   }

   public PlayerGlowData(UUID var1, String var2) {
      this.menuData = new MenuData(Order.ALL, Filter.ALL);
      this.uuid = var1;
      this.name = var2;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }

   public CurrentGlow getCurrentGlow() {
      return this.currentGlow;
   }

   public MenuData getMenuData() {
      return this.menuData;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof PlayerGlowData)) {
         return false;
      } else {
         PlayerGlowData var2 = (PlayerGlowData)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            label59: {
               UUID var3 = this.getUuid();
               UUID var4 = var2.getUuid();
               if (var3 == null) {
                  if (var4 == null) {
                     break label59;
                  }
               } else if (var3.equals(var4)) {
                  break label59;
               }

               return false;
            }

            String var5 = this.getName();
            String var6 = var2.getName();
            if (var5 == null) {
               if (var6 != null) {
                  return false;
               }
            } else if (!var5.equals(var6)) {
               return false;
            }

            CurrentGlow var7 = this.getCurrentGlow();
            CurrentGlow var8 = var2.getCurrentGlow();
            if (var7 == null) {
               if (var8 != null) {
                  return false;
               }
            } else if (!var7.equals(var8)) {
               return false;
            }

            MenuData var9 = this.getMenuData();
            MenuData var10 = var2.getMenuData();
            if (var9 == null) {
               if (var10 != null) {
                  return false;
               }
            } else if (!var9.equals(var10)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof PlayerGlowData;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      UUID var3 = this.getUuid();
      int var7 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getName();
      var7 = var7 * 59 + (var4 == null ? 43 : var4.hashCode());
      CurrentGlow var5 = this.getCurrentGlow();
      var7 = var7 * 59 + (var5 == null ? 43 : var5.hashCode());
      MenuData var6 = this.getMenuData();
      var7 = var7 * 59 + (var6 == null ? 43 : var6.hashCode());
      return var7;
   }
}
