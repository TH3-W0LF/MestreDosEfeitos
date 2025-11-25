package xshyo.us.theglow.libs.config.settings.updater;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.block.Block;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.route.Route;

public interface ValueMapper {
   @Nullable
   default Object map(@NotNull Section var1, @NotNull Route var2) {
      return this.map(var1.getBlock(var2));
   }

   @Nullable
   default Object map(@NotNull Block<?> var1) {
      return this.map(var1.getStoredValue());
   }

   @Nullable
   default Object map(@Nullable Object var1) {
      return var1;
   }

   static ValueMapper section(final BiFunction<Section, Route, Object> var0) {
      return new ValueMapper() {
         public Object map(@NotNull Section var1, @NotNull Route var2) {
            return var0.apply(var1, var2);
         }
      };
   }

   static ValueMapper block(final Function<Block<?>, Object> var0) {
      return new ValueMapper() {
         public Object map(@NotNull Block<?> var1) {
            return var0.apply(var1);
         }
      };
   }

   static ValueMapper value(final Function<Object, Object> var0) {
      return new ValueMapper() {
         public Object map(@Nullable Object var1) {
            return var0.apply(var1);
         }
      };
   }
}
