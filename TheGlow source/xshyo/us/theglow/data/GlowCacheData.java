package xshyo.us.theglow.data;

import java.util.List;

public class GlowCacheData {
   private final String permission;
   private final String displayName;
   private final String material;
   private final int amount;
   private final int slot;
   private final int page;
   private final int modelData;
   private final List<String> item_flags;
   private final boolean glowing;
   private final List<String> patterns;

   public String getPermission() {
      return this.permission;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public String getMaterial() {
      return this.material;
   }

   public int getAmount() {
      return this.amount;
   }

   public int getSlot() {
      return this.slot;
   }

   public int getPage() {
      return this.page;
   }

   public int getModelData() {
      return this.modelData;
   }

   public List<String> getItem_flags() {
      return this.item_flags;
   }

   public boolean isGlowing() {
      return this.glowing;
   }

   public List<String> getPatterns() {
      return this.patterns;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof GlowCacheData)) {
         return false;
      } else {
         GlowCacheData var2 = (GlowCacheData)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.getAmount() != var2.getAmount()) {
            return false;
         } else if (this.getSlot() != var2.getSlot()) {
            return false;
         } else if (this.getPage() != var2.getPage()) {
            return false;
         } else if (this.getModelData() != var2.getModelData()) {
            return false;
         } else if (this.isGlowing() != var2.isGlowing()) {
            return false;
         } else {
            String var3 = this.getPermission();
            String var4 = var2.getPermission();
            if (var3 == null) {
               if (var4 != null) {
                  return false;
               }
            } else if (!var3.equals(var4)) {
               return false;
            }

            label76: {
               String var5 = this.getDisplayName();
               String var6 = var2.getDisplayName();
               if (var5 == null) {
                  if (var6 == null) {
                     break label76;
                  }
               } else if (var5.equals(var6)) {
                  break label76;
               }

               return false;
            }

            label69: {
               String var7 = this.getMaterial();
               String var8 = var2.getMaterial();
               if (var7 == null) {
                  if (var8 == null) {
                     break label69;
                  }
               } else if (var7.equals(var8)) {
                  break label69;
               }

               return false;
            }

            List var9 = this.getItem_flags();
            List var10 = var2.getItem_flags();
            if (var9 == null) {
               if (var10 != null) {
                  return false;
               }
            } else if (!var9.equals(var10)) {
               return false;
            }

            List var11 = this.getPatterns();
            List var12 = var2.getPatterns();
            if (var11 == null) {
               if (var12 != null) {
                  return false;
               }
            } else if (!var11.equals(var12)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof GlowCacheData;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var8 = var2 * 59 + this.getAmount();
      var8 = var8 * 59 + this.getSlot();
      var8 = var8 * 59 + this.getPage();
      var8 = var8 * 59 + this.getModelData();
      var8 = var8 * 59 + (this.isGlowing() ? 79 : 97);
      String var3 = this.getPermission();
      var8 = var8 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getDisplayName();
      var8 = var8 * 59 + (var4 == null ? 43 : var4.hashCode());
      String var5 = this.getMaterial();
      var8 = var8 * 59 + (var5 == null ? 43 : var5.hashCode());
      List var6 = this.getItem_flags();
      var8 = var8 * 59 + (var6 == null ? 43 : var6.hashCode());
      List var7 = this.getPatterns();
      var8 = var8 * 59 + (var7 == null ? 43 : var7.hashCode());
      return var8;
   }

   public String toString() {
      return "GlowCacheData(permission=" + this.getPermission() + ", displayName=" + this.getDisplayName() + ", material=" + this.getMaterial() + ", amount=" + this.getAmount() + ", slot=" + this.getSlot() + ", page=" + this.getPage() + ", modelData=" + this.getModelData() + ", item_flags=" + this.getItem_flags() + ", glowing=" + this.isGlowing() + ", patterns=" + this.getPatterns() + ")";
   }

   public GlowCacheData(String var1, String var2, String var3, int var4, int var5, int var6, int var7, List<String> var8, boolean var9, List<String> var10) {
      this.permission = var1;
      this.displayName = var2;
      this.material = var3;
      this.amount = var4;
      this.slot = var5;
      this.page = var6;
      this.modelData = var7;
      this.item_flags = var8;
      this.glowing = var9;
      this.patterns = var10;
   }
}
