package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.Objects;
import java.util.Optional;

public final class TagTuple {
   private final Optional<String> handle;
   private final String suffix;

   public TagTuple(Optional<String> var1, String var2) {
      Objects.requireNonNull(var1);
      this.handle = var1;
      Objects.requireNonNull(var2);
      this.suffix = var2;
   }

   public Optional<String> getHandle() {
      return this.handle;
   }

   public String getSuffix() {
      return this.suffix;
   }
}
