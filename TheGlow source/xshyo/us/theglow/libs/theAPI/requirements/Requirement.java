package xshyo.us.theglow.libs.theAPI.requirements;

import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class Requirement {
   private String placeholder;
   private String condition;
   private Object value;
   private String successMessage;
   private String failMessage;

   public RequirementResult check(Player var1, RequirementProvider var2) {
      String var3 = var2.getValue(var1, this.placeholder);
      RequirementCondition var4 = this.getConditionChecker(this.condition, var3);
      boolean var5 = var4.check(var3, this.value);
      String var6 = (var5 ? this.successMessage : this.failMessage).replace("{value}", String.valueOf(this.value));
      return new RequirementResult(var5, Utils.translate(var6));
   }

   private RequirementCondition getConditionChecker(String var1, String var2) {
      byte var5 = -1;
      switch(var1.hashCode()) {
      case 60:
         if (var1.equals("<")) {
            var5 = 3;
         }
         break;
      case 62:
         if (var1.equals(">")) {
            var5 = 1;
         }
         break;
      case 1084:
         if (var1.equals("!=")) {
            var5 = 5;
         }
         break;
      case 1921:
         if (var1.equals("<=")) {
            var5 = 2;
         }
         break;
      case 1952:
         if (var1.equals("==")) {
            var5 = 4;
         }
         break;
      case 1983:
         if (var1.equals(">=")) {
            var5 = 0;
         }
      }

      boolean var10000;
      switch(var5) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
         var10000 = true;
         break;
      default:
         var10000 = false;
      }

      boolean var3 = var10000;
      return (RequirementCondition)(var3 && this.isNumeric(this.value) && this.isNumeric(var2) ? new NumericCondition(var1) : new TextCondition(var1));
   }

   private boolean isNumeric(Object var1) {
      try {
         Double.parseDouble(String.valueOf(var1));
         return true;
      } catch (NumberFormatException var3) {
         return false;
      }
   }

   public String getPlaceholder() {
      return this.placeholder;
   }

   public String getCondition() {
      return this.condition;
   }

   public Object getValue() {
      return this.value;
   }

   public String getSuccessMessage() {
      return this.successMessage;
   }

   public String getFailMessage() {
      return this.failMessage;
   }

   public void setPlaceholder(String var1) {
      this.placeholder = var1;
   }

   public void setCondition(String var1) {
      this.condition = var1;
   }

   public void setValue(Object var1) {
      this.value = var1;
   }

   public void setSuccessMessage(String var1) {
      this.successMessage = var1;
   }

   public void setFailMessage(String var1) {
      this.failMessage = var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof Requirement)) {
         return false;
      } else {
         Requirement var2 = (Requirement)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else {
            label71: {
               String var3 = this.getPlaceholder();
               String var4 = var2.getPlaceholder();
               if (var3 == null) {
                  if (var4 == null) {
                     break label71;
                  }
               } else if (var3.equals(var4)) {
                  break label71;
               }

               return false;
            }

            String var5 = this.getCondition();
            String var6 = var2.getCondition();
            if (var5 == null) {
               if (var6 != null) {
                  return false;
               }
            } else if (!var5.equals(var6)) {
               return false;
            }

            label57: {
               Object var7 = this.getValue();
               Object var8 = var2.getValue();
               if (var7 == null) {
                  if (var8 == null) {
                     break label57;
                  }
               } else if (var7.equals(var8)) {
                  break label57;
               }

               return false;
            }

            String var9 = this.getSuccessMessage();
            String var10 = var2.getSuccessMessage();
            if (var9 == null) {
               if (var10 != null) {
                  return false;
               }
            } else if (!var9.equals(var10)) {
               return false;
            }

            String var11 = this.getFailMessage();
            String var12 = var2.getFailMessage();
            if (var11 == null) {
               if (var12 == null) {
                  return true;
               }
            } else if (var11.equals(var12)) {
               return true;
            }

            return false;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof Requirement;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      String var3 = this.getPlaceholder();
      int var8 = var2 * 59 + (var3 == null ? 43 : var3.hashCode());
      String var4 = this.getCondition();
      var8 = var8 * 59 + (var4 == null ? 43 : var4.hashCode());
      Object var5 = this.getValue();
      var8 = var8 * 59 + (var5 == null ? 43 : var5.hashCode());
      String var6 = this.getSuccessMessage();
      var8 = var8 * 59 + (var6 == null ? 43 : var6.hashCode());
      String var7 = this.getFailMessage();
      var8 = var8 * 59 + (var7 == null ? 43 : var7.hashCode());
      return var8;
   }

   public String toString() {
      String var10000 = this.getPlaceholder();
      return "Requirement(placeholder=" + var10000 + ", condition=" + this.getCondition() + ", value=" + String.valueOf(this.getValue()) + ", successMessage=" + this.getSuccessMessage() + ", failMessage=" + this.getFailMessage() + ")";
   }

   public Requirement(String var1, String var2, Object var3, String var4, String var5) {
      this.placeholder = var1;
      this.condition = var2;
      this.value = var3;
      this.successMessage = var4;
      this.failMessage = var5;
   }
}
