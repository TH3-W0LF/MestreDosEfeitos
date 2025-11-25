package xshyo.us.theglow.libs.theAPI.requirements;

import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class PlaceholderAPIProvider implements RequirementProvider {
   public String getValue(Player var1, String var2) {
      String var3 = Utils.setPAPI(var1, var2);
      return var3 != null && !var3.trim().isEmpty() ? var3 : "0";
   }
}
