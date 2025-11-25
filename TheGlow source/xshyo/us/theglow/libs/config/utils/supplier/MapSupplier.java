package xshyo.us.theglow.libs.config.utils.supplier;

import java.util.Map;
import org.jetbrains.annotations.NotNull;

public interface MapSupplier {
   @NotNull
   <K, V> Map<K, V> supply(int var1);
}
