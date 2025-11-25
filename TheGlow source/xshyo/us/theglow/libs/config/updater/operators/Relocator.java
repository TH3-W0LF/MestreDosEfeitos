package xshyo.us.theglow.libs.config.updater.operators;

import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.Block;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.route.Route;

public class Relocator {
   private static final Relocator INSTANCE = new Relocator();

   public static void apply(@NotNull Section var0, @NotNull Map<Route, Route> var1) {
      while(var1.size() > 0) {
         INSTANCE.apply(var0, var1, (Route)var1.keySet().iterator().next());
      }

   }

   private void apply(@NotNull Section var1, @NotNull Map<Route, Route> var2, @Nullable Route var3) {
      if (var3 != null && var2.containsKey(var3)) {
         Optional var4 = var1.getParent(var3);
         if (!var4.isPresent()) {
            var2.remove(var3);
         } else {
            Object var5 = var3.get(var3.length() - 1);
            Block var6 = (Block)((Map)((Section)var4.get()).getStoredValue()).get(var5);
            if (var6 == null) {
               var2.remove(var3);
            } else {
               Route var7 = (Route)var2.get(var3);
               var2.remove(var3);
               ((Map)((Section)var4.get()).getStoredValue()).remove(var5);
               this.removeParents((Section)var4.get());
               this.apply(var1, var2, var7);
               var1.set((Route)var7, var6);
            }
         }
      }
   }

   private void removeParents(@NotNull Section var1) {
      if (var1.isEmpty(false) && !var1.isRoot()) {
         ((Map)var1.getParent().getStoredValue()).remove(var1.getName());
         this.removeParents(var1.getParent());
      }

   }
}
