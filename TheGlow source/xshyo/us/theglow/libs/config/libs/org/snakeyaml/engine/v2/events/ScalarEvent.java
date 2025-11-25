package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.events;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.Anchor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.CharConstants;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;

public final class ScalarEvent extends NodeEvent {
   private final Optional<String> tag;
   private final ScalarStyle style;
   private final String value;
   private final ImplicitTuple implicit;

   public ScalarEvent(Optional<Anchor> var1, Optional<String> var2, ImplicitTuple var3, String var4, ScalarStyle var5, Optional<Mark> var6, Optional<Mark> var7) {
      super(var1, var6, var7);
      Objects.requireNonNull(var2);
      this.tag = var2;
      this.implicit = var3;
      Objects.requireNonNull(var4);
      this.value = var4;
      Objects.requireNonNull(var5);
      this.style = var5;
   }

   public ScalarEvent(Optional<Anchor> var1, Optional<String> var2, ImplicitTuple var3, String var4, ScalarStyle var5) {
      this(var1, var2, var3, var4, var5, Optional.empty(), Optional.empty());
   }

   public Optional<String> getTag() {
      return this.tag;
   }

   public ScalarStyle getScalarStyle() {
      return this.style;
   }

   public String getValue() {
      return this.value;
   }

   public ImplicitTuple getImplicit() {
      return this.implicit;
   }

   public Event.ID getEventId() {
      return Event.ID.Scalar;
   }

   public boolean isPlain() {
      return this.style == ScalarStyle.PLAIN;
   }

   public boolean isLiteral() {
      return this.style == ScalarStyle.LITERAL;
   }

   public boolean isSQuoted() {
      return this.style == ScalarStyle.SINGLE_QUOTED;
   }

   public boolean isDQuoted() {
      return this.style == ScalarStyle.DOUBLE_QUOTED;
   }

   public boolean isFolded() {
      return this.style == ScalarStyle.FOLDED;
   }

   public boolean isJson() {
      return this.style == ScalarStyle.JSON_SCALAR_STYLE;
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder("=VAL");
      this.getAnchor().ifPresent((var1x) -> {
         var1.append(" &" + var1x);
      });
      if (this.implicit.bothFalse()) {
         this.getTag().ifPresent((var1x) -> {
            var1.append(" <" + var1x + ">");
         });
      }

      var1.append(" ");
      var1.append(this.getScalarStyle().toString());
      var1.append(this.escapedValue());
      return var1.toString();
   }

   public String escapedValue() {
      return (String)this.value.codePoints().filter((var0) -> {
         return var0 < 65535;
      }).mapToObj((var0) -> {
         return CharConstants.escapeChar(String.valueOf(Character.toChars(var0)));
      }).collect(Collectors.joining(""));
   }
}
