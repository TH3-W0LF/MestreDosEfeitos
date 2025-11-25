package xshyo.us.theglow.libs.config.serialization;

import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.utils.supplier.MapSupplier;

public interface YamlSerializer {
   @Nullable
   Object deserialize(@NotNull Map<Object, Object> var1);

   @Nullable
   <T> Map<Object, Object> serialize(@NotNull T var1, @NotNull MapSupplier var2);

   @NotNull
   Set<Class<?>> getSupportedClasses();

   @NotNull
   Set<Class<?>> getSupportedParentClasses();
}
