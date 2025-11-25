package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import java.util.Objects;
import java.util.Optional;

public class ComposerException extends MarkedYamlEngineException {
   public ComposerException(String var1, Optional<Mark> var2, String var3, Optional<Mark> var4) {
      super(var1, var2, var3, var4);
      Objects.requireNonNull(var1);
   }

   public ComposerException(String var1, Optional<Mark> var2) {
      super("", Optional.empty(), var1, var2);
   }
}
