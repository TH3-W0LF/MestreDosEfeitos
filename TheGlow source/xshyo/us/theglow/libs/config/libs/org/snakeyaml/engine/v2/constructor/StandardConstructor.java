package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.constructor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.ConstructNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.api.LoadSettings;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.env.EnvConfig;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.ConstructorException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.DuplicateKeyException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.Mark;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.MissingEnvironmentVariableException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.SequenceNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver.JsonScalarResolver;

public class StandardConstructor extends BaseConstructor {
   public StandardConstructor(LoadSettings var1) {
      super(var1);
      this.tagConstructors.put(Tag.SET, new StandardConstructor.ConstructYamlSet());
      this.tagConstructors.put(Tag.STR, new StandardConstructor.ConstructYamlStr());
      this.tagConstructors.put(Tag.SEQ, new StandardConstructor.ConstructYamlSeq());
      this.tagConstructors.put(Tag.MAP, new StandardConstructor.ConstructYamlMap());
      this.tagConstructors.put(Tag.ENV_TAG, new StandardConstructor.ConstructEnv());
      this.tagConstructors.putAll(var1.getSchema().getSchemaTagConstructors());
      this.tagConstructors.putAll(var1.getTagConstructors());
   }

   protected void flattenMapping(MappingNode var1) {
      this.processDuplicateKeys(var1);
   }

   protected void processDuplicateKeys(MappingNode var1) {
      List var2 = var1.getValue();
      HashMap var3 = new HashMap(var2.size());
      TreeSet var4 = new TreeSet();
      int var5 = 0;

      Iterator var6;
      for(var6 = var2.iterator(); var6.hasNext(); ++var5) {
         NodeTuple var7 = (NodeTuple)var6.next();
         Node var8 = var7.getKeyNode();
         Object var9 = this.constructKey(var8, var1.getStartMark(), var7.getKeyNode().getStartMark());
         Integer var10 = (Integer)var3.put(var9, var5);
         if (var10 != null) {
            if (!this.settings.getAllowDuplicateKeys()) {
               throw new DuplicateKeyException(var1.getStartMark(), var9, var7.getKeyNode().getStartMark());
            }

            var4.add(var10);
         }
      }

      var6 = var4.descendingIterator();

      while(var6.hasNext()) {
         var2.remove((Integer)var6.next());
      }

   }

   private Object constructKey(Node var1, Optional<Mark> var2, Optional<Mark> var3) {
      Object var4 = this.constructObject(var1);
      if (var4 != null) {
         try {
            var4.hashCode();
         } catch (Exception var6) {
            throw new ConstructorException("while constructing a mapping", var2, "found unacceptable key " + var4, var3, var6);
         }
      }

      return var4;
   }

   protected void constructMapping2ndStep(MappingNode var1, Map<Object, Object> var2) {
      this.flattenMapping(var1);
      super.constructMapping2ndStep(var1, var2);
   }

   protected void constructSet2ndStep(MappingNode var1, Set<Object> var2) {
      this.flattenMapping(var1);
      super.constructSet2ndStep(var1, var2);
   }

   public class ConstructYamlSet implements ConstructNode {
      public Object construct(Node var1) {
         if (var1.isRecursive()) {
            return StandardConstructor.this.constructedObjects.containsKey(var1) ? StandardConstructor.this.constructedObjects.get(var1) : StandardConstructor.this.createEmptySetForNode((MappingNode)var1);
         } else {
            return StandardConstructor.this.constructSet((MappingNode)var1);
         }
      }

      public void constructRecursive(Node var1, Object var2) {
         if (var1.isRecursive()) {
            StandardConstructor.this.constructSet2ndStep((MappingNode)var1, (Set)var2);
         } else {
            throw new YamlEngineException("Unexpected recursive set structure. Node: " + var1);
         }
      }
   }

   public class ConstructYamlStr extends ConstructScalar {
      public Object construct(Node var1) {
         return this.constructScalar(var1);
      }
   }

   public class ConstructYamlSeq implements ConstructNode {
      public Object construct(Node var1) {
         SequenceNode var2 = (SequenceNode)var1;
         return var1.isRecursive() ? StandardConstructor.this.createEmptyListForNode(var2) : StandardConstructor.this.constructSequence(var2);
      }

      public void constructRecursive(Node var1, Object var2) {
         if (var1.isRecursive()) {
            StandardConstructor.this.constructSequenceStep2((SequenceNode)var1, (List)var2);
         } else {
            throw new YamlEngineException("Unexpected recursive sequence structure. Node: " + var1);
         }
      }
   }

   public class ConstructYamlMap implements ConstructNode {
      public Object construct(Node var1) {
         MappingNode var2 = (MappingNode)var1;
         return var1.isRecursive() ? StandardConstructor.this.createEmptyMapFor(var2) : StandardConstructor.this.constructMapping(var2);
      }

      public void constructRecursive(Node var1, Object var2) {
         if (var1.isRecursive()) {
            StandardConstructor.this.constructMapping2ndStep((MappingNode)var1, (Map)var2);
         } else {
            throw new YamlEngineException("Unexpected recursive mapping structure. Node: " + var1);
         }
      }
   }

   public class ConstructEnv extends ConstructScalar {
      public Object construct(Node var1) {
         String var2 = this.constructScalar(var1);
         Optional var3 = StandardConstructor.this.settings.getEnvConfig();
         if (var3.isPresent()) {
            EnvConfig var4 = (EnvConfig)var3.get();
            Matcher var5 = JsonScalarResolver.ENV_FORMAT.matcher(var2);
            var5.matches();
            String var6 = var5.group(1);
            String var7 = var5.group(3);
            String var8 = var7 != null ? var7 : "";
            String var9 = var5.group(2);
            String var10 = this.getEnv(var6);
            Optional var11 = var4.getValueFor(var6, var9, var8, var10);
            return var11.orElseGet(() -> {
               return this.apply(var6, var9, var8, var10);
            });
         } else {
            return var2;
         }
      }

      public String apply(String var1, String var2, String var3, String var4) {
         if (var4 != null && !var4.isEmpty()) {
            return var4;
         } else {
            if (var2 != null) {
               if (var2.equals("?") && var4 == null) {
                  throw new MissingEnvironmentVariableException("Missing mandatory variable " + var1 + ": " + var3);
               }

               if (var2.equals(":?")) {
                  if (var4 == null) {
                     throw new MissingEnvironmentVariableException("Missing mandatory variable " + var1 + ": " + var3);
                  }

                  if (var4.isEmpty()) {
                     throw new MissingEnvironmentVariableException("Empty mandatory variable " + var1 + ": " + var3);
                  }
               }

               if (var2.startsWith(":")) {
                  if (var4 == null || var4.isEmpty()) {
                     return var3;
                  }
               } else if (var4 == null) {
                  return var3;
               }
            }

            return "";
         }
      }

      public String getEnv(String var1) {
         return System.getenv(var1);
      }
   }
}
