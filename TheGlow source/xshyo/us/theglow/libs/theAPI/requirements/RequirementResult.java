package xshyo.us.theglow.libs.theAPI.requirements;

public class RequirementResult {
   private boolean meets;
   private String message;

   public boolean isMeets() {
      return this.meets;
   }

   public String getMessage() {
      return this.message;
   }

   public void setMeets(boolean var1) {
      this.meets = var1;
   }

   public void setMessage(String var1) {
      this.message = var1;
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof RequirementResult)) {
         return false;
      } else {
         RequirementResult var2 = (RequirementResult)var1;
         if (!var2.canEqual(this)) {
            return false;
         } else if (this.isMeets() != var2.isMeets()) {
            return false;
         } else {
            String var3 = this.getMessage();
            String var4 = var2.getMessage();
            if (var3 == null) {
               if (var4 != null) {
                  return false;
               }
            } else if (!var3.equals(var4)) {
               return false;
            }

            return true;
         }
      }
   }

   protected boolean canEqual(Object var1) {
      return var1 instanceof RequirementResult;
   }

   public int hashCode() {
      boolean var1 = true;
      byte var2 = 1;
      int var4 = var2 * 59 + (this.isMeets() ? 79 : 97);
      String var3 = this.getMessage();
      var4 = var4 * 59 + (var3 == null ? 43 : var3.hashCode());
      return var4;
   }

   public String toString() {
      boolean var10000 = this.isMeets();
      return "RequirementResult(meets=" + var10000 + ", message=" + this.getMessage() + ")";
   }

   public RequirementResult(boolean var1, String var2) {
      this.meets = var1;
      this.message = var2;
   }
}
