package xshyo.us.theglow.libs.theAPI.requirements;

public class TextCondition implements RequirementCondition {
   private final String operator;

   public boolean check(String var1, Object var2) {
      String var3 = String.valueOf(var2);
      String var4 = this.operator;
      byte var5 = -1;
      switch(var4.hashCode()) {
      case 61:
         if (var4.equals("=")) {
            var5 = 2;
         }
         break;
      case 1084:
         if (var4.equals("!=")) {
            var5 = 1;
         }
         break;
      case 1519:
         if (var4.equals("-|")) {
            var5 = 4;
         }
         break;
      case 1905:
         if (var4.equals("<-")) {
            var5 = 0;
         }
         break;
      case 3889:
         if (var4.equals("|-")) {
            var5 = 3;
         }
      }

      boolean var10000;
      switch(var5) {
      case 0:
         var10000 = var1.contains(var3);
         break;
      case 1:
         var10000 = !var1.equals(var3);
         break;
      case 2:
         var10000 = var1.equals(var3);
         break;
      case 3:
         var10000 = var1.startsWith(var3);
         break;
      case 4:
         var10000 = var1.endsWith(var3);
         break;
      default:
         var10000 = false;
      }

      return var10000;
   }

   public TextCondition(String var1) {
      this.operator = var1;
   }
}
