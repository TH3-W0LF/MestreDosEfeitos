package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.parser;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;

class VersionTagsTuple {
   private final Optional<SpecVersion> specVersion;
   private final Map<String, String> tags;

   public VersionTagsTuple(Optional<SpecVersion> var1, Map<String, String> var2) {
      Objects.requireNonNull(var1);
      this.specVersion = var1;
      this.tags = var2;
   }

   public Optional<SpecVersion> getSpecVersion() {
      return this.specVersion;
   }

   public Map<String, String> getTags() {
      return this.tags;
   }

   public String toString() {
      return String.format("VersionTagsTuple<%s, %s>", this.specVersion, this.tags);
   }
}
