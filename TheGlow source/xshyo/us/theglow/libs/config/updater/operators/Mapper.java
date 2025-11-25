package xshyo.us.theglow.libs.config.updater.operators;

import java.util.Map;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.route.Route;
import xshyo.us.theglow.libs.config.settings.updater.ValueMapper;

public class Mapper {
   public static void apply(@NotNull Section var0, @NotNull Map<Route, ValueMapper> var1) {
      var1.forEach((var1x, var2) -> {
         var0.getParent(var1x).ifPresent((var2x) -> {
            Route var3 = Route.fromSingleKey(var1x.get(var1x.length() - 1));
            if (((Map)var2x.getStoredValue()).containsKey(var3.get(0))) {
               var2x.set(var3, var2.map(var2x, var3));
            }
         });
      });
   }
}
