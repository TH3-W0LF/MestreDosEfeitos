package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import java.util.Optional;

public class ScannerException extends MarkedYamlEngineException {
   public ScannerException(String var1, Optional<Mark> var2, String var3, Optional<Mark> var4) {
      super(var1, var2, var3, var4, (Throwable)null);
   }

   public ScannerException(String var1, Optional<Mark> var2) {
      super((String)null, Optional.empty(), var1, var2, (Throwable)null);
   }
}
