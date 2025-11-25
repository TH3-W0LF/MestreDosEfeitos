package xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.resolver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;

public abstract class BaseScalarResolver implements ScalarResolver {
   public static final Pattern EMPTY = Pattern.compile("^$");
   public static final Pattern ENV_FORMAT = Pattern.compile("^\\$\\{\\s*(?:(\\w+)(?:(:?[-?])(\\w+)?)?)\\s*\\}$");
   protected Map<Character, List<ResolverTuple>> yamlImplicitResolvers = new HashMap();

   public BaseScalarResolver() {
      this.addImplicitResolvers();
   }

   public void addImplicitResolver(Tag var1, Pattern var2, String var3) {
      if (var3 == null) {
         List var4 = (List)this.yamlImplicitResolvers.computeIfAbsent((Object)null, (var0) -> {
            return new ArrayList();
         });
         var4.add(new ResolverTuple(var1, var2));
      } else {
         char[] var9 = var3.toCharArray();
         int var5 = 0;

         for(int var6 = var9.length; var5 < var6; ++var5) {
            Character var7 = var9[var5];
            if (var7 == 0) {
               var7 = null;
            }

            Object var8 = (List)this.yamlImplicitResolvers.get(var7);
            if (var8 == null) {
               var8 = new ArrayList();
               this.yamlImplicitResolvers.put(var7, var8);
            }

            ((List)var8).add(new ResolverTuple(var1, var2));
         }
      }

   }

   abstract void addImplicitResolvers();

   public Tag resolve(String var1, Boolean var2) {
      if (!var2) {
         return Tag.STR;
      } else {
         List var3;
         if (var1.isEmpty()) {
            var3 = (List)this.yamlImplicitResolvers.get('\u0000');
         } else {
            var3 = (List)this.yamlImplicitResolvers.get(var1.charAt(0));
         }

         Iterator var4;
         ResolverTuple var5;
         Tag var6;
         Pattern var7;
         if (var3 != null) {
            var4 = var3.iterator();

            while(var4.hasNext()) {
               var5 = (ResolverTuple)var4.next();
               var6 = var5.getTag();
               var7 = var5.getRegexp();
               if (var7.matcher(var1).matches()) {
                  return var6;
               }
            }
         }

         if (this.yamlImplicitResolvers.containsKey((Object)null)) {
            var4 = ((List)this.yamlImplicitResolvers.get((Object)null)).iterator();

            while(var4.hasNext()) {
               var5 = (ResolverTuple)var4.next();
               var6 = var5.getTag();
               var7 = var5.getRegexp();
               if (var7.matcher(var1).matches()) {
                  return var6;
               }
            }
         }

         return Tag.STR;
      }
   }
}
