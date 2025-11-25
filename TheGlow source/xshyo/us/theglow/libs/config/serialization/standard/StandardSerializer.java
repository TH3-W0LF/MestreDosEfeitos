package xshyo.us.theglow.libs.config.serialization.standard;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.serialization.YamlSerializer;
import xshyo.us.theglow.libs.config.utils.supplier.MapSupplier;

public class StandardSerializer implements YamlSerializer {
   public static final String DEFAULT_SERIALIZED_TYPE_KEY = "==";
   private static final StandardSerializer defaultSerializer = new StandardSerializer("==");
   private final Map<Class<?>, TypeAdapter<?>> adapters = new HashMap();
   private final Map<String, Class<?>> aliases = new HashMap();
   private final Object serializedTypeKey;

   public StandardSerializer(@NotNull Object var1) {
      this.serializedTypeKey = var1;
   }

   public <T> void register(@NotNull Class<T> var1, @NotNull TypeAdapter<T> var2) {
      this.adapters.put(var1, var2);
      this.aliases.put(var1.getCanonicalName(), var1);
   }

   public <T> void register(@NotNull String var1, @NotNull Class<T> var2) {
      if (!this.adapters.containsKey(var2)) {
         throw new IllegalStateException("Cannot register an alias for yet unregistered type!");
      } else {
         this.aliases.put(var1, var2);
      }
   }

   @Nullable
   public Object deserialize(@NotNull Map<Object, Object> var1) {
      if (!var1.containsKey(this.serializedTypeKey)) {
         return null;
      } else {
         Class var2 = (Class)this.aliases.get(var1.get(this.serializedTypeKey).toString());
         return var2 == null ? null : ((TypeAdapter)this.adapters.get(var2)).deserialize(var1);
      }
   }

   @Nullable
   public <T> Map<Object, Object> serialize(@NotNull T var1, @NotNull MapSupplier var2) {
      if (!this.adapters.containsKey(var1.getClass())) {
         return null;
      } else {
         Map var3 = var2.supply(1);
         var3.putAll(((TypeAdapter)this.adapters.get(var1.getClass())).serialize(var1));
         var3.computeIfAbsent(this.serializedTypeKey, (var1x) -> {
            return var1.getClass().getCanonicalName();
         });
         return var3;
      }
   }

   @NotNull
   public Set<Class<?>> getSupportedClasses() {
      return this.adapters.keySet();
   }

   @NotNull
   public Set<Class<?>> getSupportedParentClasses() {
      return Collections.emptySet();
   }

   public static StandardSerializer getDefault() {
      return defaultSerializer;
   }
}
