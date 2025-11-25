package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.env;

import java.util.Optional;

public interface EnvConfig {
   default Optional<String> getValueFor(String var1, String var2, String var3, String var4) {
      return Optional.empty();
   }
}
