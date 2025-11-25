package xshyo.us.theglow.libs.config.settings.updater;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.YamlDocument;
import xshyo.us.theglow.libs.config.dvs.Pattern;
import xshyo.us.theglow.libs.config.dvs.versioning.AutomaticVersioning;
import xshyo.us.theglow.libs.config.dvs.versioning.ManualVersioning;
import xshyo.us.theglow.libs.config.dvs.versioning.Versioning;
import xshyo.us.theglow.libs.config.route.Route;
import xshyo.us.theglow.libs.config.route.RouteFactory;
import xshyo.us.theglow.libs.config.settings.Settings;

public class UpdaterSettings implements Settings {
   public static final boolean DEFAULT_AUTO_SAVE = true;
   public static final boolean DEFAULT_ENABLE_DOWNGRADING = true;
   public static final boolean DEFAULT_KEEP_ALL = false;
   public static final UpdaterSettings.OptionSorting DEFAULT_OPTION_SORTING;
   public static final Map<MergeRule, Boolean> DEFAULT_MERGE_RULES;
   public static final Versioning DEFAULT_VERSIONING;
   public static final UpdaterSettings DEFAULT;
   private final boolean autoSave;
   private final boolean enableDowngrading;
   private final boolean keepAll;
   private final Map<MergeRule, Boolean> mergeRules;
   private final Map<String, UpdaterSettings.RouteSet> ignored;
   private final Map<String, UpdaterSettings.RouteMap<Route, String>> relocations;
   private final Map<String, Map<Route, ValueMapper>> mappers;
   private final Map<String, List<Consumer<YamlDocument>>> customLogic;
   private final Versioning versioning;
   private final UpdaterSettings.OptionSorting optionSorting;

   public UpdaterSettings(UpdaterSettings.Builder var1) {
      this.autoSave = var1.autoSave;
      this.enableDowngrading = var1.enableDowngrading;
      this.keepAll = var1.keepAll;
      this.optionSorting = var1.optionSorting;
      this.mergeRules = var1.mergeRules;
      this.ignored = var1.ignored;
      this.relocations = var1.relocations;
      this.mappers = var1.mappers;
      this.customLogic = var1.customLogic;
      this.versioning = var1.versioning;
   }

   public Map<MergeRule, Boolean> getMergeRules() {
      return this.mergeRules;
   }

   public Set<Route> getIgnoredRoutes(@NotNull String var1, char var2) {
      UpdaterSettings.RouteSet var3 = (UpdaterSettings.RouteSet)this.ignored.get(var1);
      return var3 == null ? Collections.emptySet() : var3.merge(var2);
   }

   public Map<Route, Route> getRelocations(@NotNull String var1, char var2) {
      UpdaterSettings.RouteMap var3 = (UpdaterSettings.RouteMap)this.relocations.get(var1);
      return var3 == null ? Collections.emptyMap() : var3.merge(Function.identity(), (var1x) -> {
         return Route.fromString(var1x, var2);
      }, var2);
   }

   public Map<Route, ValueMapper> getMappers(@NotNull String var1, char var2) {
      return (Map)this.mappers.getOrDefault(var1, Collections.emptyMap());
   }

   public List<Consumer<YamlDocument>> getCustomLogic(@NotNull String var1) {
      return (List)this.customLogic.getOrDefault(var1, Collections.emptyList());
   }

   public Versioning getVersioning() {
      return this.versioning;
   }

   public boolean isEnableDowngrading() {
      return this.enableDowngrading;
   }

   public boolean isKeepAll() {
      return this.keepAll;
   }

   public boolean isAutoSave() {
      return this.autoSave;
   }

   public UpdaterSettings.OptionSorting getOptionSorting() {
      return this.optionSorting;
   }

   public static UpdaterSettings.Builder builder() {
      return new UpdaterSettings.Builder();
   }

   public static UpdaterSettings.Builder builder(UpdaterSettings var0) {
      return builder().setAutoSave(var0.autoSave).setEnableDowngrading(var0.enableDowngrading).setKeepAll(var0.keepAll).setOptionSorting(var0.optionSorting).setMergeRules(var0.mergeRules).setIgnoredRoutesInternal(var0.ignored).setRelocationsInternal(var0.relocations).addMappers(var0.mappers).addCustomLogic(var0.customLogic).setVersioning(var0.versioning);
   }

   static {
      DEFAULT_OPTION_SORTING = UpdaterSettings.OptionSorting.SORT_BY_DEFAULTS;
      DEFAULT_MERGE_RULES = Collections.unmodifiableMap(new HashMap<MergeRule, Boolean>() {
         {
            this.put(MergeRule.MAPPINGS, true);
            this.put(MergeRule.MAPPING_AT_SECTION, false);
            this.put(MergeRule.SECTION_AT_MAPPING, false);
         }
      });
      DEFAULT_VERSIONING = null;
      DEFAULT = builder().build();
   }

   private static class RouteSet {
      private Set<Route> routes;
      private Set<String> strings;

      private RouteSet() {
         this.routes = null;
         this.strings = null;
      }

      public Set<Route> merge(char var1) {
         if ((this.routes == null || this.routes.isEmpty()) && (this.strings == null || this.strings.isEmpty())) {
            return Collections.emptySet();
         } else {
            HashSet var2 = new HashSet();
            if (this.strings != null) {
               this.strings.forEach((var2x) -> {
                  var2.add(Route.fromString(var2x, var1));
               });
            }

            if (this.routes != null) {
               var2.addAll(this.routes);
            }

            return var2;
         }
      }

      public Set<Route> getRouteSet() {
         return this.routes == null ? (this.routes = new HashSet()) : this.routes;
      }

      public Set<String> getStringSet() {
         return this.strings == null ? (this.strings = new HashSet()) : this.strings;
      }

      // $FF: synthetic method
      RouteSet(Object var1) {
         this();
      }
   }

   private static class RouteMap<R, S> {
      private Map<Route, R> routes;
      private Map<String, S> strings;

      private RouteMap() {
         this.routes = null;
         this.strings = null;
      }

      @NotNull
      public <T> Map<Route, T> merge(@NotNull Function<R, T> var1, @NotNull Function<S, T> var2, char var3) {
         if ((this.routes == null || this.routes.isEmpty()) && (this.strings == null || this.strings.isEmpty())) {
            return Collections.emptyMap();
         } else {
            HashMap var4 = new HashMap();
            if (this.strings != null) {
               this.strings.forEach((var3x, var4x) -> {
                  var4.put(Route.fromString(var3x, var3), var2.apply(var4x));
               });
            }

            if (this.routes != null) {
               this.routes.forEach((var2x, var3x) -> {
                  var4.put(var2x, var1.apply(var3x));
               });
            }

            return var4;
         }
      }

      @NotNull
      public Map<Route, R> getRouteMap() {
         return this.routes == null ? (this.routes = new HashMap()) : this.routes;
      }

      @NotNull
      public Map<String, S> getStringMap() {
         return this.strings == null ? (this.strings = new HashMap()) : this.strings;
      }

      // $FF: synthetic method
      RouteMap(Object var1) {
         this();
      }
   }

   public static class Builder {
      private boolean autoSave;
      private boolean enableDowngrading;
      private boolean keepAll;
      private final Map<MergeRule, Boolean> mergeRules;
      private final Map<String, UpdaterSettings.RouteSet> ignored;
      private final Map<String, UpdaterSettings.RouteMap<Route, String>> relocations;
      private final Map<String, Map<Route, ValueMapper>> mappers;
      private final Map<String, List<Consumer<YamlDocument>>> customLogic;
      private Versioning versioning;
      private UpdaterSettings.OptionSorting optionSorting;

      private Builder() {
         this.autoSave = true;
         this.enableDowngrading = true;
         this.keepAll = false;
         this.mergeRules = new HashMap(UpdaterSettings.DEFAULT_MERGE_RULES);
         this.ignored = new HashMap();
         this.relocations = new HashMap();
         this.mappers = new HashMap();
         this.customLogic = new HashMap();
         this.versioning = UpdaterSettings.DEFAULT_VERSIONING;
         this.optionSorting = UpdaterSettings.DEFAULT_OPTION_SORTING;
      }

      public UpdaterSettings.Builder setAutoSave(boolean var1) {
         this.autoSave = var1;
         return this;
      }

      public UpdaterSettings.Builder setEnableDowngrading(boolean var1) {
         this.enableDowngrading = var1;
         return this;
      }

      public UpdaterSettings.Builder setKeepAll(boolean var1) {
         this.keepAll = var1;
         return this;
      }

      public UpdaterSettings.Builder setOptionSorting(@NotNull UpdaterSettings.OptionSorting var1) {
         this.optionSorting = var1;
         return this;
      }

      public UpdaterSettings.Builder setMergeRules(@NotNull Map<MergeRule, Boolean> var1) {
         this.mergeRules.putAll(var1);
         return this;
      }

      public UpdaterSettings.Builder setMergeRule(@NotNull MergeRule var1, boolean var2) {
         this.mergeRules.put(var1, var2);
         return this;
      }

      private UpdaterSettings.Builder setIgnoredRoutesInternal(@NotNull Map<String, UpdaterSettings.RouteSet> var1) {
         this.ignored.putAll(var1);
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setIgnoredRoutes(@NotNull Map<String, Set<Route>> var1) {
         var1.forEach((var1x, var2) -> {
            ((UpdaterSettings.RouteSet)this.ignored.computeIfAbsent(var1x, (var0) -> {
               return new UpdaterSettings.RouteSet();
            })).getRouteSet().addAll(var2);
         });
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setIgnoredRoutes(@NotNull String var1, @NotNull Set<Route> var2) {
         ((UpdaterSettings.RouteSet)this.ignored.computeIfAbsent(var1, (var0) -> {
            return new UpdaterSettings.RouteSet();
         })).getRouteSet().addAll(var2);
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setIgnoredStringRoutes(@NotNull Map<String, Set<String>> var1) {
         var1.forEach((var1x, var2) -> {
            ((UpdaterSettings.RouteSet)this.ignored.computeIfAbsent(var1x, (var0) -> {
               return new UpdaterSettings.RouteSet();
            })).getStringSet().addAll(var2);
         });
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setIgnoredStringRoutes(@NotNull String var1, @NotNull Set<String> var2) {
         ((UpdaterSettings.RouteSet)this.ignored.computeIfAbsent(var1, (var0) -> {
            return new UpdaterSettings.RouteSet();
         })).getStringSet().addAll(var2);
         return this;
      }

      public UpdaterSettings.Builder addIgnoredRoute(@NotNull String var1, @NotNull Route var2) {
         return this.addIgnoredRoutes(var1, Collections.singleton(var2));
      }

      public UpdaterSettings.Builder addIgnoredRoutes(@NotNull String var1, @NotNull Set<Route> var2) {
         return this.addIgnoredRoutes(Collections.singletonMap(var1, var2));
      }

      public UpdaterSettings.Builder addIgnoredRoutes(@NotNull Map<String, Set<Route>> var1) {
         var1.forEach((var1x, var2) -> {
            ((UpdaterSettings.RouteSet)this.ignored.computeIfAbsent(var1x, (var0) -> {
               return new UpdaterSettings.RouteSet();
            })).getRouteSet().addAll(var2);
         });
         return this;
      }

      public UpdaterSettings.Builder addIgnoredRoute(@NotNull String var1, @NotNull String var2, char var3) {
         return this.addIgnoredRoutes(var1, Collections.singleton(var2), var3);
      }

      public UpdaterSettings.Builder addIgnoredRoutes(@NotNull String var1, @NotNull Set<String> var2, char var3) {
         this.addIgnoredRoutes(var1, var2, new RouteFactory(var3));
         return this;
      }

      public UpdaterSettings.Builder addIgnoredRoutes(@NotNull Map<String, Set<String>> var1, char var2) {
         RouteFactory var3 = new RouteFactory(var2);
         var1.forEach((var2x, var3x) -> {
            this.addIgnoredRoutes(var2x, var3x, var3);
         });
         return this;
      }

      private void addIgnoredRoutes(@NotNull String var1, @NotNull Set<String> var2, @NotNull RouteFactory var3) {
         Set var4 = ((UpdaterSettings.RouteSet)this.ignored.computeIfAbsent(var1, (var0) -> {
            return new UpdaterSettings.RouteSet();
         })).getRouteSet();
         var2.forEach((var2x) -> {
            var4.add(var3.create(var2x));
         });
      }

      private UpdaterSettings.Builder setRelocationsInternal(@NotNull Map<String, UpdaterSettings.RouteMap<Route, String>> var1) {
         this.relocations.putAll(var1);
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setRelocations(@NotNull Map<String, Map<Route, Route>> var1) {
         var1.forEach((var1x, var2) -> {
            ((UpdaterSettings.RouteMap)this.relocations.computeIfAbsent(var1x, (var0) -> {
               return new UpdaterSettings.RouteMap();
            })).getRouteMap().putAll(var2);
         });
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setRelocations(@NotNull String var1, @NotNull Map<Route, Route> var2) {
         ((UpdaterSettings.RouteMap)this.relocations.computeIfAbsent(var1, (var0) -> {
            return new UpdaterSettings.RouteMap();
         })).getRouteMap().putAll(var2);
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setStringRelocations(@NotNull Map<String, Map<String, String>> var1) {
         var1.forEach((var1x, var2) -> {
            ((UpdaterSettings.RouteMap)this.relocations.computeIfAbsent(var1x, (var0) -> {
               return new UpdaterSettings.RouteMap();
            })).getStringMap().putAll(var2);
         });
         return this;
      }

      @Deprecated
      public UpdaterSettings.Builder setStringRelocations(@NotNull String var1, @NotNull Map<String, String> var2) {
         ((UpdaterSettings.RouteMap)this.relocations.computeIfAbsent(var1, (var0) -> {
            return new UpdaterSettings.RouteMap();
         })).getStringMap().putAll(var2);
         return this;
      }

      public UpdaterSettings.Builder addRelocation(@NotNull String var1, @NotNull Route var2, @NotNull Route var3) {
         return this.addRelocations(var1, Collections.singletonMap(var2, var3));
      }

      public UpdaterSettings.Builder addRelocations(@NotNull String var1, @NotNull Map<Route, Route> var2) {
         return this.addRelocations(Collections.singletonMap(var1, var2));
      }

      public UpdaterSettings.Builder addRelocations(@NotNull Map<String, Map<Route, Route>> var1) {
         var1.forEach((var1x, var2) -> {
            ((UpdaterSettings.RouteMap)this.relocations.computeIfAbsent(var1x, (var0) -> {
               return new UpdaterSettings.RouteMap();
            })).getRouteMap().putAll(var2);
         });
         return this;
      }

      public UpdaterSettings.Builder addRelocation(@NotNull String var1, @NotNull String var2, @NotNull String var3, char var4) {
         return this.addRelocations(var1, Collections.singletonMap(var2, var3), var4);
      }

      public UpdaterSettings.Builder addRelocations(@NotNull String var1, @NotNull Map<String, String> var2, char var3) {
         this.addRelocations(Collections.singletonMap(var1, var2), var3);
         return this;
      }

      public UpdaterSettings.Builder addRelocations(@NotNull Map<String, Map<String, String>> var1, char var2) {
         RouteFactory var3 = new RouteFactory(var2);
         var1.forEach((var2x, var3x) -> {
            Map var4 = ((UpdaterSettings.RouteMap)this.relocations.computeIfAbsent(var2x, (var0) -> {
               return new UpdaterSettings.RouteMap();
            })).getRouteMap();
            var3x.forEach((var2, var3xx) -> {
               Route var10000 = (Route)var4.put(var3.create(var2), var3.create(var3xx));
            });
         });
         return this;
      }

      public UpdaterSettings.Builder addMapper(@NotNull String var1, @NotNull Route var2, @NotNull ValueMapper var3) {
         return this.addMappers(var1, Collections.singletonMap(var2, var3));
      }

      public UpdaterSettings.Builder addMappers(@NotNull String var1, @NotNull Map<Route, ValueMapper> var2) {
         return this.addMappers(Collections.singletonMap(var1, var2));
      }

      public UpdaterSettings.Builder addMappers(@NotNull Map<String, Map<Route, ValueMapper>> var1) {
         var1.forEach((var1x, var2) -> {
            ((Map)this.mappers.computeIfAbsent(var1x, (var0) -> {
               return new HashMap();
            })).putAll(var2);
         });
         return this;
      }

      public UpdaterSettings.Builder addMapper(@NotNull String var1, @NotNull String var2, @NotNull ValueMapper var3, char var4) {
         return this.addMappers(var1, Collections.singletonMap(var2, var3), var4);
      }

      public UpdaterSettings.Builder addMappers(@NotNull String var1, @NotNull Map<String, ValueMapper> var2, char var3) {
         return this.addMappers(Collections.singletonMap(var1, var2), var3);
      }

      public UpdaterSettings.Builder addMappers(@NotNull Map<String, Map<String, ValueMapper>> var1, char var2) {
         RouteFactory var3 = new RouteFactory(var2);
         var1.forEach((var2x, var3x) -> {
            Map var4 = (Map)this.mappers.computeIfAbsent(var2x, (var0) -> {
               return new HashMap();
            });
            var3x.forEach((var2, var3xx) -> {
               ValueMapper var10000 = (ValueMapper)var4.put(var3.create(var2), var3xx);
            });
         });
         return this;
      }

      public UpdaterSettings.Builder addCustomLogic(@NotNull String var1, @NotNull Consumer<YamlDocument> var2) {
         return this.addCustomLogic(var1, (Collection)Collections.singletonList(var2));
      }

      public UpdaterSettings.Builder addCustomLogic(@NotNull Map<String, List<Consumer<YamlDocument>>> var1) {
         var1.forEach(this::addCustomLogic);
         return this;
      }

      public UpdaterSettings.Builder addCustomLogic(@NotNull String var1, @NotNull Collection<Consumer<YamlDocument>> var2) {
         ((List)this.customLogic.computeIfAbsent(var1, (var0) -> {
            return new ArrayList();
         })).addAll(var2);
         return this;
      }

      public UpdaterSettings.Builder setVersioning(@NotNull Versioning var1) {
         this.versioning = var1;
         return this;
      }

      public UpdaterSettings.Builder setVersioning(@NotNull Pattern var1, @Nullable String var2, @NotNull String var3) {
         return this.setVersioning(new ManualVersioning(var1, var2, var3));
      }

      public UpdaterSettings.Builder setVersioning(@NotNull Pattern var1, @NotNull Route var2) {
         return this.setVersioning(new AutomaticVersioning(var1, var2));
      }

      public UpdaterSettings.Builder setVersioning(@NotNull Pattern var1, @NotNull String var2) {
         return this.setVersioning(new AutomaticVersioning(var1, var2));
      }

      public UpdaterSettings build() {
         return new UpdaterSettings(this);
      }

      // $FF: synthetic method
      Builder(Object var1) {
         this();
      }
   }

   public static enum OptionSorting {
      NONE,
      SORT_BY_DEFAULTS;
   }
}
