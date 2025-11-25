package xshyo.us.theglow.libs.config.updater.operators;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.config.YamlDocument;
import xshyo.us.theglow.libs.config.block.Block;
import xshyo.us.theglow.libs.config.block.implementation.Section;
import xshyo.us.theglow.libs.config.block.implementation.TerminatedBlock;
import xshyo.us.theglow.libs.config.engine.ExtendedConstructor;
import xshyo.us.theglow.libs.config.engine.ExtendedRepresenter;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.common.ScalarStyle;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.ScalarNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Tag;
import xshyo.us.theglow.libs.config.route.Route;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;
import xshyo.us.theglow.libs.config.settings.updater.MergeRule;
import xshyo.us.theglow.libs.config.settings.updater.UpdaterSettings;

public class Merger {
   private static final Merger INSTANCE = new Merger();

   public static void merge(@NotNull Section var0, @NotNull Section var1, @NotNull UpdaterSettings var2) {
      INSTANCE.iterate(var0, var1, var2);
   }

   private void iterate(Section var1, Section var2, UpdaterSettings var3) {
      HashSet var4 = new HashSet(((Map)var1.getStoredValue()).keySet());
      boolean var5 = var3.getOptionSorting() == UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS;
      Map var6 = var5 ? var1.getRoot().getGeneralSettings().getDefaultMap() : null;
      Iterator var7 = ((Map)var2.getStoredValue()).entrySet().iterator();

      while(true) {
         while(true) {
            while(var7.hasNext()) {
               Entry var8 = (Entry)var7.next();
               Object var9 = var8.getKey();
               Route var10 = Route.from(var9);
               var4.remove(var9);
               Block var11 = (Block)var1.getOptionalBlock(var10).orElse((Object)null);
               Block var12 = (Block)var8.getValue();
               if (var11 != null) {
                  if (var11.isIgnored()) {
                     var11.setIgnored(false);
                     if (var11 instanceof Section) {
                        this.resetIgnored((Section)var11);
                     }

                     if (var5) {
                        var6.put(var9, var11);
                     }
                  } else {
                     boolean var13 = var11 instanceof Section;
                     boolean var14 = var12 instanceof Section;
                     if (var14 && var13) {
                        this.iterate((Section)var11, (Section)var12, var3);
                        if (var5) {
                           var6.put(var9, var11);
                        }
                     } else if (var5) {
                        var6.put(var9, this.getPreservedValue(var3.getMergeRules(), var11, () -> {
                           return this.cloneBlock(var12, var1);
                        }, var13, var14));
                     } else {
                        var1.set((Route)var10, this.getPreservedValue(var3.getMergeRules(), var11, () -> {
                           return this.cloneBlock(var12, var1);
                        }, var13, var14));
                     }
                  }
               } else if (var5) {
                  var6.put(var9, this.cloneBlock(var12, var1));
               } else {
                  var1.set((Route)var10, this.cloneBlock(var12, var1));
               }
            }

            if (var3.isKeepAll()) {
               if (var5) {
                  var4.forEach((var2x) -> {
                     Block var10000 = (Block)var6.put(var2x, ((Map)var1.getStoredValue()).get(var2x));
                  });
                  var1.repopulate(var6);
               }

               return;
            }

            var7 = var4.iterator();

            while(true) {
               while(var7.hasNext()) {
                  Object var15 = var7.next();
                  Route var16 = Route.fromSingleKey(var15);
                  Block var17 = (Block)var1.getOptionalBlock(var16).orElse((Object)null);
                  if (var17 != null && var17.isIgnored()) {
                     var17.setIgnored(false);
                     if (var17 instanceof Section) {
                        this.resetIgnored((Section)var17);
                     }

                     if (var5) {
                        var6.put(var15, var17);
                     }
                  } else if (!var5) {
                     var1.remove(var16);
                  }
               }

               if (var5) {
                  var1.repopulate(var6);
               }

               return;
            }
         }
      }
   }

   private void resetIgnored(@NotNull Section var1) {
      ((Map)var1.getStoredValue()).values().forEach((var1x) -> {
         var1x.setIgnored(false);
         if (var1x instanceof Section) {
            this.resetIgnored((Section)var1x);
         }

      });
   }

   @NotNull
   private Block<?> cloneBlock(@NotNull Block<?> var1, @NotNull Section var2) {
      return (Block)(var1 instanceof Section ? this.cloneSection((Section)var1, var2) : this.cloneTerminated((TerminatedBlock)var1, var2));
   }

   @NotNull
   private Section cloneSection(@NotNull Section var1, @NotNull Section var2) {
      if (var1.getRoute() == null) {
         throw new IllegalArgumentException("Cannot clone the root!");
      } else {
         YamlDocument var3 = var1.getRoot();
         GeneralSettings var4 = var3.getGeneralSettings();
         ExtendedRepresenter var5 = new ExtendedRepresenter(var4, var3.getDumperSettings());
         ExtendedConstructor var6 = new ExtendedConstructor(var3.getLoaderSettings().buildEngineSettings(var4), var4.getSerializer());
         Node var7 = var5.represent(var1);
         var6.constructSingleDocument(Optional.of(var7));
         var1 = new Section(var2.getRoot(), var2, var1.getRoute(), this.moveComments(var7), (MappingNode)var7, var6);
         var6.clear();
         return var1;
      }
   }

   @NotNull
   private TerminatedBlock cloneTerminated(@NotNull TerminatedBlock var1, @NotNull Section var2) {
      YamlDocument var3 = var2.getRoot();
      GeneralSettings var4 = var3.getGeneralSettings();
      ExtendedRepresenter var5 = new ExtendedRepresenter(var4, var3.getDumperSettings());
      ExtendedConstructor var6 = new ExtendedConstructor(var3.getLoaderSettings().buildEngineSettings(var4), var4.getSerializer());
      Node var7 = var5.represent(var1.getStoredValue());
      var6.constructSingleDocument(Optional.of(var7));
      var1 = new TerminatedBlock(var1, var6.getConstructed(var7));
      var6.clear();
      return var1;
   }

   private Node moveComments(@NotNull Node var1) {
      ScalarNode var2 = new ScalarNode(Tag.STR, "", ScalarStyle.PLAIN);
      var2.setBlockComments(var1.getBlockComments());
      var2.setInLineComments(var1.getInLineComments());
      var2.setEndComments(var1.getEndComments());
      var1.setBlockComments(Collections.emptyList());
      var1.setInLineComments((List)null);
      var1.setEndComments((List)null);
      return var2;
   }

   @NotNull
   private Block<?> getPreservedValue(@NotNull Map<MergeRule, Boolean> var1, @NotNull Block<?> var2, @NotNull Supplier<Block<?>> var3, boolean var4, boolean var5) {
      return (Boolean)var1.get(MergeRule.getFor(var4, var5)) ? var2 : (Block)var3.get();
   }
}
