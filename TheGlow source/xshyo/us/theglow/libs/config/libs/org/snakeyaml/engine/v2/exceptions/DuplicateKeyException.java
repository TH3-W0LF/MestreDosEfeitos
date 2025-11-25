package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import java.util.Optional;

public class DuplicateKeyException extends ConstructorException {
   public DuplicateKeyException(Optional<Mark> var1, Object var2, Optional<Mark> var3) {
      super("while constructing a mapping", var1, "found duplicate key " + var2.toString(), var3);
   }
}
