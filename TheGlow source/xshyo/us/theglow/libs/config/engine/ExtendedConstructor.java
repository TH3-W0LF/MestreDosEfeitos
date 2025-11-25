package xshyo.us.theglow.libs.config.engine;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor.StandardConstructor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.serialization.YamlSerializer;

public class ExtendedConstructor extends StandardConstructor {
   private final YamlSerializer serializer;
   private final Map<Node, Object> constructed = new HashMap();

   public ExtendedConstructor(@NotNull LoadSettings var1, @NotNull YamlSerializer var2) {
      super(var1);
      this.serializer = var2;
      this.tagConstructors.put(Tag.MAP, new ExtendedConstructor.ConstructMap((StandardConstructor.ConstructYamlMap)this.tagConstructors.get(Tag.MAP)));
   }

   protected Object construct(Node var1) {
      Object var2 = super.construct(var1);
      this.constructed.put(var1, var2);
      return var2;
   }

   protected Object constructObjectNoCheck(Node var1) {
      Object var2 = super.constructObjectNoCheck(var1);
      this.constructed.put(var1, var2);
      return var2;
   }

   @NotNull
   public Object getConstructed(@NotNull Node var1) {
      return this.constructed.get(var1);
   }

   public void clear() {
      this.constructed.clear();
   }

   private class ConstructMap extends StandardConstructor.ConstructYamlMap {
      private final StandardConstructor.ConstructYamlMap previous;

      private ConstructMap(@NotNull StandardConstructor.ConstructYamlMap var2) {
         super();
         this.previous = var2;
      }

      public Object construct(Node var1) {
         Map var2 = (Map)this.previous.construct(var1);
         Object var3 = ExtendedConstructor.this.serializer.deserialize(var2);
         return var3 == null ? var2 : var3;
      }

      // $FF: synthetic method
      ConstructMap(StandardConstructor.ConstructYamlMap var2, Object var3) {
         this(var2);
      }
   }
}
