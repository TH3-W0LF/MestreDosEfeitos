package xshyo.us.theglow.libs.config.utils.supplier;

import java.util.Set;
import org.jetbrains.annotations.NotNull;

public interface SetSupplier {
   @NotNull
   <T> Set<T> supply(int var1);
}
