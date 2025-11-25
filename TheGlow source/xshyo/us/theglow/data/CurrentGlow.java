package xshyo.us.theglow.data;

import java.util.List;

public class CurrentGlow {
   private String glowName;
   private List<String> colorList;
   private Boolean enable;

   public String getGlowName() {
      return this.glowName;
   }

   public List<String> getColorList() {
      return this.colorList;
   }

   public Boolean getEnable() {
      return this.enable;
   }

   public void setGlowName(String var1) {
      this.glowName = var1;
   }

   public void setColorList(List<String> var1) {
      this.colorList = var1;
   }

   public void setEnable(Boolean var1) {
      this.enable = var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof CurrentGlow)) {
         return false;
      } else {
         CurrentGlow var2 = (CurrentGlow)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            label47: {
               Boolean var3 = this.getEnable();
               Boolean var4 = var2.getEnable();
               if (var3 == null) {
                  if (var4 == null) {
                     break label47;
                  }
               } else if (var3.equals(var4)) {
                  break label47;
               }

               return false;
            }

            String var5 = this.getGlowName();
            String var6 = var2.getGlowName();
            if (var5 == null) {
               if (var6 != null) {
                  return false;
               }
            } else if (!var5.equals(var6)) {
               return false;
            }

            List var7 = this.getColorList();
            List var8 = var2.getColorList();
            if (var7 == null) {
               if (var8 != null) {
                  return false;
               }
            } else if (!var7.equals(var8)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof CurrentGlow;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      Boolean var3 = this.getEnable();
      int var6 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getGlowName();
      var6 = var6 * 59 + (var4 == null ? 43 : var4.hashCode());
      List var5 = this.getColorList();
      var6 = var6 * 59 + (var5 == null ? 43 : var5.hashCode());
      return var6;
   }

   public String toString() {
      return "CurrentGlow(glowName=" + this.getGlowName() + ", colorList=" + this.getColorList() + ", enable=" + this.getEnable() + ")";
   }

   public CurrentGlow(String var1, List<String> var2, Boolean var3) {
      this.glowName = var1;
      this.colorList = var2;
      this.enable = var3;
   }
}
