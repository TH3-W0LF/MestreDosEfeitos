package xshyo.us.theglow.libs.theAPI.requirements;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RequirementManager {
   private final Map<String, RequirementProvider> providers = new HashMap();

   public RequirementManager() {
      if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
         this.registerProvider("placeholderapi", new PlaceholderAPIProvider());
      }

   }

   public void registerProvider(String var1, RequirementProvider var2) {
      this.providers.put(var1.toLowerCase(), var2);
   }

   public List<RequirementResult> checkRequirements(Player var1, List<Requirement> var2) {
      RequirementProvider var3 = this.getDefaultProvider();
      return var3 == null ? (List)var2.stream().map((var0) -> {
         return new RequirementResult(false, "&c[!] Missing requirement provider: PlaceholderAPI");
      }).collect(Collectors.toList()) : (List)var2.stream().map((var2x) -> {
         return var2x.check(var1, var3);
      }).collect(Collectors.toList());
   }

   private RequirementProvider getDefaultProvider() {
      return (RequirementProvider)this.providers.get("placeholderapi");
   }

   public Map<String, RequirementProvider> getProviders() {
      return this.providers;
   }
}
