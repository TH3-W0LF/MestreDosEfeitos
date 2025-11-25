package xshyo.us.theglow.libs.config.utils.conversion;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ListConversions {
   @NotNull
   public static Optional<List<String>> toStringList(@Nullable List<?> var0) {
      return construct(var0, (var0x) -> {
         return Optional.ofNullable(var0x.toString());
      });
   }

   @NotNull
   public static Optional<List<Integer>> toIntList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toInt);
   }

   @NotNull
   public static Optional<List<BigInteger>> toBigIntList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toBigInt);
   }

   @NotNull
   public static Optional<List<Byte>> toByteList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toByte);
   }

   @NotNull
   public static Optional<List<Long>> toLongList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toLong);
   }

   @NotNull
   public static Optional<List<Double>> toDoubleList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toDouble);
   }

   @NotNull
   public static Optional<List<Float>> toFloatList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toFloat);
   }

   @NotNull
   public static Optional<List<Short>> toShortList(@Nullable List<?> var0) {
      return construct(var0, PrimitiveConversions::toShort);
   }

   @NotNull
   public static Optional<List<Map<?, ?>>> toMapList(@Nullable List<?> var0) {
      return construct(var0, (var0x) -> {
         return var0x instanceof Map ? Optional.of((Map)var0x) : Optional.empty();
      });
   }

   @NotNull
   private static <T> Optional<List<T>> construct(@Nullable List<?> var0, @NotNull Function<Object, Optional<T>> var1) {
      if (var0 == null) {
         return Optional.empty();
      } else {
         ArrayList var2 = new ArrayList();
         Iterator var3 = var0.iterator();

         while(var3.hasNext()) {
            Object var4 = var3.next();
            if (var4 != null) {
               ((Optional)var1.apply(var4)).ifPresent(var2::add);
            }
         }

         return Optional.of(var2);
      }
   }
}
