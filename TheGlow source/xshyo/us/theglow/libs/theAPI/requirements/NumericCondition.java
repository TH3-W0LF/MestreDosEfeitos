package xshyo.us.theglow.libs.theAPI.requirements;

public class NumericCondition implements RequirementCondition {
   private final String operator;

   public boolean check(String var1, Object var2) {
      try {
         double var3 = Double.parseDouble(var1);
         double var5 = Double.parseDouble(String.valueOf(var2));
         String var7 = this.operator;
         byte var8 = -1;
         switch(var7.hashCode()) {
         case 60:
            if (var7.equals("<")) {
               var8 = 3;
            }
            break;
         case 62:
            if (var7.equals(">")) {
               var8 = 1;
            }
            break;
         case 1084:
            if (var7.equals("!=")) {
               var8 = 5;
            }
            break;
         case 1921:
            if (var7.equals("<=")) {
               var8 = 2;
            }
            break;
         case 1952:
            if (var7.equals("==")) {
               var8 = 4;
            }
            break;
         case 1983:
            if (var7.equals(">=")) {
               var8 = 0;
            }
         }

         boolean var10000;
         switch(var8) {
         case 0:
            var10000 = var3 >= var5;
            break;
         case 1:
            var10000 = var3 > var5;
            break;
         case 2:
            var10000 = var3 <= var5;
            break;
         case 3:
            var10000 = var3 < var5;
            break;
         case 4:
            var10000 = var3 == var5;
            break;
         case 5:
            var10000 = var3 != var5;
            break;
         default:
            var10000 = false;
         }

         return var10000;
      } catch (NumberFormatException var9) {
         return false;
      }
   }

   public NumericCondition(String var1) {
      this.operator = var1;
   }
}
