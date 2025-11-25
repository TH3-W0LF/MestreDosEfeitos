package xshyo.us.theglow.libs.config.utils.conversion;

import java.math.BigInteger;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrimitiveConversions {
   public static final Map<Class<?>, Class<?>> NUMERIC_PRIMITIVES = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
      {
         this.put(Integer.TYPE, Integer.class);
         this.put(Byte.TYPE, Byte.class);
         this.put(Short.TYPE, Short.class);
         this.put(Long.TYPE, Long.class);
         this.put(Float.TYPE, Float.class);
         this.put(Double.TYPE, Double.class);
      }
   });
   public static final Map<Class<?>, Class<?>> PRIMITIVES_TO_OBJECTS = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
      {
         this.putAll(PrimitiveConversions.NUMERIC_PRIMITIVES);
         this.put(Boolean.TYPE, Boolean.class);
         this.put(Character.TYPE, Character.class);
      }
   });
   public static final Map<Class<?>, Class<?>> NON_NUMERIC_CONVERSIONS = Collections.unmodifiableMap(new HashMap<Class<?>, Class<?>>() {
      {
         this.put(Boolean.TYPE, Boolean.class);
         this.put(Character.TYPE, Character.class);
         this.put(Boolean.class, Boolean.TYPE);
         this.put(Character.class, Character.TYPE);
      }
   });
   public static final Set<Class<?>> NUMERIC_CLASSES = Collections.unmodifiableSet(new HashSet<Class<?>>() {
      {
         this.add(Integer.TYPE);
         this.add(Byte.TYPE);
         this.add(Short.TYPE);
         this.add(Long.TYPE);
         this.add(Float.TYPE);
         this.add(Double.TYPE);
         this.add(Integer.class);
         this.add(Byte.class);
         this.add(Short.class);
         this.add(Long.class);
         this.add(Float.class);
         this.add(Double.class);
      }
   });

   public static boolean isNumber(@NotNull Class<?> var0) {
      return NUMERIC_CLASSES.contains(var0);
   }

   public static Object convertNumber(@NotNull Object var0, @NotNull Class<?> var1) {
      Number var2 = (Number)var0;
      boolean var3 = var1.isPrimitive();
      if (var3) {
         var1 = (Class)NUMERIC_PRIMITIVES.get(var1);
      }

      if (var1 == Integer.class) {
         return var2.intValue();
      } else if (var1 == Byte.class) {
         return var2.byteValue();
      } else if (var1 == Short.class) {
         return var2.shortValue();
      } else if (var1 == Long.class) {
         return var2.longValue();
      } else {
         return var1 == Float.class ? var2.floatValue() : var2.doubleValue();
      }
   }

   public static Optional<Integer> toInt(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof Number) {
         return Optional.of(((Number)var0).intValue());
      } else {
         try {
            return Optional.of(Integer.valueOf(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }

   public static Optional<Byte> toByte(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof Number) {
         return Optional.of(((Number)var0).byteValue());
      } else {
         try {
            return Optional.of(Byte.valueOf(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }

   public static Optional<Long> toLong(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof Number) {
         return Optional.of(((Number)var0).longValue());
      } else {
         try {
            return Optional.of(Long.valueOf(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }

   public static Optional<Double> toDouble(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof Number) {
         return Optional.of(((Number)var0).doubleValue());
      } else {
         try {
            return Optional.of(Double.valueOf(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }

   public static Optional<Float> toFloat(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof Number) {
         return Optional.of(((Number)var0).floatValue());
      } else {
         try {
            return Optional.of(Float.valueOf(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }

   public static Optional<Short> toShort(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof Number) {
         return Optional.of(((Number)var0).shortValue());
      } else {
         try {
            return Optional.of(Short.valueOf(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }

   public static Optional<BigInteger> toBigInt(@Nullable Object var0) {
      if (var0 == null) {
         return Optional.empty();
      } else if (var0 instanceof BigInteger) {
         return Optional.of((BigInteger)var0);
      } else if (var0 instanceof Number) {
         return Optional.of(BigInteger.valueOf(((Number)var0).longValue()));
      } else {
         try {
            return Optional.of(new BigInteger(var0.toString()));
         } catch (NumberFormatException var2) {
            return Optional.empty();
         }
      }
   }
}
