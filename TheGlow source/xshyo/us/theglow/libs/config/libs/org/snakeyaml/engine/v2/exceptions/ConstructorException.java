package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import java.util.Optional;

public class ConstructorException extends MarkedYamlEngineException {
   public ConstructorException(String var1, Optional<Mark> var2, String var3, Optional<Mark> var4, Throwable var5) {
      super(var1, var2, var3, var4, var5);
   }

   public ConstructorException(String var1, Optional<Mark> var2, String var3, Optional<Mark> var4) {
      this(var1, var2, var3, var4, (Throwable)null);
   }
}
