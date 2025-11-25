package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.SpecVersion;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class DocumentStartEvent extends Event {
   private final boolean explicit;
   private final Optional<SpecVersion> specVersion;
   private final Map<String, String> tags;

   public DocumentStartEvent(boolean var1, Optional<SpecVersion> var2, Map<String, String> var3, Optional<Mark> var4, Optional<Mark> var5) {
      super(var4, var5);
      this.explicit = var1;
      Objects.requireNonNull(var2);
      this.specVersion = var2;
      Objects.requireNonNull(var3);
      this.tags = var3;
   }

   public DocumentStartEvent(boolean var1, Optional<SpecVersion> var2, Map<String, String> var3) {
      this(var1, var2, var3, Optional.empty(), Optional.empty());
   }

   public boolean isExplicit() {
      return this.explicit;
   }

   public Optional<SpecVersion> getSpecVersion() {
      return this.specVersion;
   }

   public Map<String, String> getTags() {
      return this.tags;
   }

   public Event.ID getEventId() {
      return Event.ID.DocumentStart;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("+DOC");
      if (this.isExplicit()) {
         var1.append(" ---");
      }

      return var1.toString();
   }
}
