package xshyo.us.theglow.libs.config.utils.supplier;

import java.util.List;
import org.jetbrains.annotations.NotNull;

public interface ListSupplier {
   @NotNull
   <T> List<T> supply(int var1);
}
