package xshyo.us.theglow.libs.config.utils.format;

import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;

public interface Formatter<S, V> {
   @NotNull
   S format(@NotNull Tag var1, @NotNull V var2, @NotNull NodeRole var3, @NotNull S var4);

   @NotNull
   static <S, V> Formatter<S, V> identity() {
      return (var0, var1, var2, var3) -> {
         return var3;
      };
   }
}
