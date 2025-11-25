package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.tokens;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;

public final class DirectiveToken<T> extends Token {
   public static final String YAML_DIRECTIVE = "YAML";
   public static final String TAG_DIRECTIVE = "TAG";
   private final String name;
   private final Optional<List<T>> value;

   public DirectiveToken(String var1, Optional<List<T>> var2, Optional<Mark> var3, Optional<Mark> var4) {
      super(var3, var4);
      Objects.requireNonNull(var1);
      this.name = var1;
      Objects.requireNonNull(var2);
      if (var2.isPresent() && ((List)var2.get()).size() != 2) {
         throw new YamlEngineException("Two strings/integers must be provided instead of " + ((List)var2.get()).size());
      } else {
         this.value = var2;
      }
   }

   public String getName() {
      return this.name;
   }

   public Optional<List<T>> getValue() {
      return this.value;
   }

   public Token.ID getTokenId() {
      return Token.ID.Directive;
   }
}
