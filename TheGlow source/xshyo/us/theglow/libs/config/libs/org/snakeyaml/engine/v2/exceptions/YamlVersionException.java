package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions;

import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;

public class YamlVersionException extends YamlEngineException {
   private final SpecVersion specVersion;

   public YamlVersionException(SpecVersion var1) {
      super(var1.toString());
      this.specVersion = var1;
   }

   public SpecVersion getSpecVersion() {
      return this.specVersion;
   }
}
