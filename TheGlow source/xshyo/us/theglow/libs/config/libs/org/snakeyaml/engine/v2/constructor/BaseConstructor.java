package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;

public abstract class BaseConstructor {
   protected final Map<Tag, ConstructNode> tagConstructors;
   final Map<Node, Object> constructedObjects;
   private final Set<Node> recursiveObjects;
   private final ArrayList<BaseConstructor.RecursiveTuple<Map<Object, Object>, BaseConstructor.RecursiveTuple<Object, Object>>> maps2fill;
   private final ArrayList<BaseConstructor.RecursiveTuple<Set<Object>, Object>> sets2fill;
   protected LoadSettings settings;

   public BaseConstructor(LoadSettings var1) {
      this.settings = var1;
      this.tagConstructors = new HashMap();
      this.constructedObjects = new HashMap();
      this.recursiveObjects = new HashSet();
      this.maps2fill = new ArrayList();
      this.sets2fill = new ArrayList();
   }

   public Object constructSingleDocument(Optional<Node> var1) {
      if (var1.isPresent() && !Tag.NULL.equals(((Node)var1.get()).getTag())) {
         return this.construct((Node)var1.get());
      } else {
         ConstructNode var2 = (ConstructNode)this.tagConstructors.get(Tag.NULL);
         return var2.construct((Node)var1.orElse((Object)null));
      }
   }

   protected Object construct(Node var1) {
      Object var3;
      try {
         Object var2 = this.constructObject(var1);
         this.fillRecursive();
         var3 = var2;
      } catch (YamlEngineException var8) {
         throw var8;
      } catch (RuntimeException var9) {
         throw new YamlEngineException(var9);
      } finally {
         this.constructedObjects.clear();
         this.recursiveObjects.clear();
      }

      return var3;
   }

   private void fillRecursive() {
      Iterator var1;
      BaseConstructor.RecursiveTuple var2;
      if (!this.maps2fill.isEmpty()) {
         var1 = this.maps2fill.iterator();

         while(var1.hasNext()) {
            var2 = (BaseConstructor.RecursiveTuple)var1.next();
            BaseConstructor.RecursiveTuple var3 = (BaseConstructor.RecursiveTuple)var2.getValue2();
            ((Map)var2.getValue1()).put(var3.getValue1(), var3.getValue2());
         }

         this.maps2fill.clear();
      }

      if (!this.sets2fill.isEmpty()) {
         var1 = this.sets2fill.iterator();

         while(var1.hasNext()) {
            var2 = (BaseConstructor.RecursiveTuple)var1.next();
            ((Set)var2.getValue1()).add(var2.getValue2());
         }

         this.sets2fill.clear();
      }

   }

   protected Object constructObject(Node var1) {
      Objects.requireNonNull(var1, "Node cannot be null");
      return this.constructedObjects.containsKey(var1) ? this.constructedObjects.get(var1) : this.constructObjectNoCheck(var1);
   }

   protected Object constructObjectNoCheck(Node var1) {
      if (this.recursiveObjects.contains(var1)) {
         throw new ConstructorException((String)null, Optional.empty(), "found unconstructable recursive node", var1.getStartMark());
      } else {
         this.recursiveObjects.add(var1);
         ConstructNode var2 = (ConstructNode)this.findConstructorFor(var1).orElseThrow(() -> {
            return new ConstructorException((String)null, Optional.empty(), "could not determine a constructor for the tag " + var1.getTag(), var1.getStartMark());
         });
         Object var3 = this.constructedObjects.containsKey(var1) ? this.constructedObjects.get(var1) : var2.construct(var1);
         this.constructedObjects.put(var1, var3);
         this.recursiveObjects.remove(var1);
         if (var1.isRecursive()) {
            var2.constructRecursive(var1, var3);
         }

         return var3;
      }
   }

   protected Optional<ConstructNode> findConstructorFor(Node var1) {
      Tag var2 = var1.getTag();
      if (this.settings.getTagConstructors().containsKey(var2)) {
         return Optional.of((ConstructNode)this.settings.getTagConstructors().get(var2));
      } else {
         return this.tagConstructors.containsKey(var2) ? Optional.of((ConstructNode)this.tagConstructors.get(var2)) : Optional.empty();
      }
   }

   protected String constructScalar(ScalarNode var1) {
      return var1.getValue();
   }

   protected List<Object> createEmptyListForNode(SequenceNode var1) {
      return (List)this.settings.getDefaultList().apply(var1.getValue().size());
   }

   protected Set<Object> createEmptySetForNode(MappingNode var1) {
      return (Set)this.settings.getDefaultSet().apply(var1.getValue().size());
   }

   protected Map<Object, Object> createEmptyMapFor(MappingNode var1) {
      return (Map)this.settings.getDefaultMap().apply(var1.getValue().size());
   }

   protected List<Object> constructSequence(SequenceNode var1) {
      List var2 = (List)this.settings.getDefaultList().apply(var1.getValue().size());
      this.constructSequenceStep2(var1, var2);
      return var2;
   }

   protected void constructSequenceStep2(SequenceNode var1, Collection<Object> var2) {
      Iterator var3 = var1.getValue().iterator();

      while(var3.hasNext()) {
         Node var4 = (Node)var3.next();
         var2.add(this.constructObject(var4));
      }

   }

   protected Set<Object> constructSet(MappingNode var1) {
      Set var2 = (Set)this.settings.getDefaultSet().apply(var1.getValue().size());
      this.constructSet2ndStep(var1, var2);
      return var2;
   }

   protected Map<Object, Object> constructMapping(MappingNode var1) {
      Map var2 = (Map)this.settings.getDefaultMap().apply(var1.getValue().size());
      this.constructMapping2ndStep(var1, var2);
      return var2;
   }

   protected void constructMapping2ndStep(MappingNode var1, Map<Object, Object> var2) {
      List var3 = var1.getValue();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         NodeTuple var5 = (NodeTuple)var4.next();
         Node var6 = var5.getKeyNode();
         Node var7 = var5.getValueNode();
         Object var8 = this.constructObject(var6);
         if (var8 != null) {
            try {
               var8.hashCode();
            } catch (Exception var10) {
               throw new ConstructorException("while constructing a mapping", var1.getStartMark(), "found unacceptable key " + var8, var5.getKeyNode().getStartMark(), var10);
            }
         }

         Object var9 = this.constructObject(var7);
         if (var6.isRecursive()) {
            if (!this.settings.getAllowRecursiveKeys()) {
               throw new YamlEngineException("Recursive key for mapping is detected but it is not configured to be allowed.");
            }

            this.postponeMapFilling(var2, var8, var9);
         } else {
            var2.put(var8, var9);
         }
      }

   }

   protected void postponeMapFilling(Map<Object, Object> var1, Object var2, Object var3) {
      this.maps2fill.add(0, new BaseConstructor.RecursiveTuple(var1, new BaseConstructor.RecursiveTuple(var2, var3)));
   }

   protected void constructSet2ndStep(MappingNode var1, Set<Object> var2) {
      List var3 = var1.getValue();
      Iterator var4 = var3.iterator();

      while(var4.hasNext()) {
         NodeTuple var5 = (NodeTuple)var4.next();
         Node var6 = var5.getKeyNode();
         Object var7 = this.constructObject(var6);
         if (var7 != null) {
            try {
               var7.hashCode();
            } catch (Exception var9) {
               throw new ConstructorException("while constructing a Set", var1.getStartMark(), "found unacceptable key " + var7, var5.getKeyNode().getStartMark(), var9);
            }
         }

         if (var6.isRecursive()) {
            if (!this.settings.getAllowRecursiveKeys()) {
               throw new YamlEngineException("Recursive key for mapping is detected but it is not configured to be allowed.");
            }

            this.postponeSetFilling(var2, var7);
         } else {
            var2.add(var7);
         }
      }

   }

   protected void postponeSetFilling(Set<Object> var1, Object var2) {
      this.sets2fill.add(0, new BaseConstructor.RecursiveTuple(var1, var2));
   }

   private static class RecursiveTuple<T, K> {
      private final T value1;
      private final K value2;

      public RecursiveTuple(T var1, K var2) {
         this.value1 = var1;
         this.value2 = var2;
      }

      public K getValue2() {
         return this.value2;
      }

      public T getValue1() {
         return this.value1;
      }
   }
}
