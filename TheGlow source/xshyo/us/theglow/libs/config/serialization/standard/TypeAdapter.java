package xshyo.us.theglow.libs.config.serialization.standard;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;

public interface TypeAdapter<T> {
   @NotNull
   Map<Object, Object> serialize(@NotNull T var1);

   @NotNull
   T deserialize(@NotNull Map<Object, Object> var1);

   @NotNull
   default Map<String, Object> toStringKeyedMap(@NotNull Map<?, ?> var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = var1.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (var4.getValue() instanceof Map) {
            var2.put(var4.getKey().toString(), this.toStringKeyedMap((Map)var4.getValue()));
         } else {
            var2.put(var4.getKey().toString(), var4.getValue());
         }
      }

      return var2;
   }
}
