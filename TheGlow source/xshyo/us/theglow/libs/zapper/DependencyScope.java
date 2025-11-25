package xshyo.us.theglow.libs.zapper;

import java.util.Iterator;
import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface DependencyScope {
   void dependency(@NotNull Dependency var1);

   default void dependency(@NotNull List<Dependency> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Dependency var3 = (Dependency)var2.next();
         this.dependency(var3);
      }

   }
}
