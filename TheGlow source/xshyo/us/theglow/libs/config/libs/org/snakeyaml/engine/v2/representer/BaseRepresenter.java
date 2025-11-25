package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.representer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.RepresentToNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.FlowStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.AnchorNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;

public abstract class BaseRepresenter {
   protected final Map<Class<?>, RepresentToNode> representers = new HashMap();
   protected final Map<Class<?>, RepresentToNode> parentClassRepresenters = new LinkedHashMap();
   protected final Map<Object, Node> representedObjects = new IdentityHashMap<Object, Node>() {
      public Node put(Object var1, Node var2) {
         return (Node)super.put(var1, new AnchorNode(var2));
      }
   };
   protected RepresentToNode nullRepresenter;
   protected ScalarStyle defaultScalarStyle;
   protected FlowStyle defaultFlowStyle;
   protected Object objectToRepresent;

   public BaseRepresenter() {
      this.defaultScalarStyle = ScalarStyle.PLAIN;
      this.defaultFlowStyle = FlowStyle.AUTO;
   }

   public Node represent(Object var1) {
      Node var2 = this.representData(var1);
      this.representedObjects.clear();
      this.objectToRepresent = null;
      return var2;
   }

   protected Optional<RepresentToNode> findRepresenterFor(Object var1) {
      Class var2 = var1.getClass();
      if (this.representers.containsKey(var2)) {
         return Optional.of((RepresentToNode)this.representers.get(var2));
      } else {
         Iterator var3 = this.parentClassRepresenters.entrySet().iterator();

         Entry var4;
         do {
            if (!var3.hasNext()) {
               return Optional.empty();
            }

            var4 = (Entry)var3.next();
         } while(!((Class)var4.getKey()).isInstance(var1));

         return Optional.of((RepresentToNode)var4.getValue());
      }
   }

   protected final Node representData(Object var1) {
      this.objectToRepresent = var1;
      if (this.representedObjects.containsKey(this.objectToRepresent)) {
         return (Node)this.representedObjects.get(this.objectToRepresent);
      } else if (var1 == null) {
         return this.nullRepresenter.representData((Object)null);
      } else {
         RepresentToNode var2 = (RepresentToNode)this.findRepresenterFor(var1).orElseThrow(() -> {
            return new YamlEngineException("Representer is not defined for " + var1.getClass());
         });
         return var2.representData(var1);
      }
   }

   protected Node representScalar(Tag var1, String var2, ScalarStyle var3) {
      if (var3 == ScalarStyle.PLAIN) {
         var3 = this.defaultScalarStyle;
      }

      return new ScalarNode(var1, var2, var3);
   }

   protected Node representScalar(Tag var1, String var2) {
      return this.representScalar(var1, var2, ScalarStyle.PLAIN);
   }

   protected Node representSequence(Tag var1, Iterable<?> var2, FlowStyle var3) {
      int var4 = 10;
      if (var2 instanceof List) {
         var4 = ((List)var2).size();
      }

      ArrayList var5 = new ArrayList(var4);
      SequenceNode var6 = new SequenceNode(var1, var5, var3);
      this.representedObjects.put(this.objectToRepresent, var6);
      FlowStyle var7 = FlowStyle.FLOW;

      Node var10;
      for(Iterator var8 = var2.iterator(); var8.hasNext(); var5.add(var10)) {
         Object var9 = var8.next();
         var10 = this.representData(var9);
         if (!(var10 instanceof ScalarNode) || !((ScalarNode)var10).isPlain()) {
            var7 = FlowStyle.BLOCK;
         }
      }

      if (var3 == FlowStyle.AUTO) {
         if (this.defaultFlowStyle != FlowStyle.AUTO) {
            var6.setFlowStyle(this.defaultFlowStyle);
         } else {
            var6.setFlowStyle(var7);
         }
      }

      return var6;
   }

   protected NodeTuple representMappingEntry(Entry<?, ?> var1) {
      return new NodeTuple(this.representData(var1.getKey()), this.representData(var1.getValue()));
   }

   protected Node representMapping(Tag var1, Map<?, ?> var2, FlowStyle var3) {
      ArrayList var4 = new ArrayList(var2.size());
      MappingNode var5 = new MappingNode(var1, var4, var3);
      this.representedObjects.put(this.objectToRepresent, var5);
      FlowStyle var6 = FlowStyle.FLOW;

      NodeTuple var9;
      for(Iterator var7 = var2.entrySet().iterator(); var7.hasNext(); var4.add(var9)) {
         Entry var8 = (Entry)var7.next();
         var9 = this.representMappingEntry(var8);
         if (!(var9.getKeyNode() instanceof ScalarNode) || !((ScalarNode)var9.getKeyNode()).isPlain()) {
            var6 = FlowStyle.BLOCK;
         }

         if (!(var9.getValueNode() instanceof ScalarNode) || !((ScalarNode)var9.getValueNode()).isPlain()) {
            var6 = FlowStyle.BLOCK;
         }
      }

      if (var3 == FlowStyle.AUTO) {
         if (this.defaultFlowStyle != FlowStyle.AUTO) {
            var5.setFlowStyle(this.defaultFlowStyle);
         } else {
            var5.setFlowStyle(var6);
         }
      }

      return var5;
   }
}
