package xshyo.us.theglow.libs.config.block.implementation;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xshyo.us.theglow.libs.config.YamlDocument;
import xshyo.us.theglow.libs.config.block.Block;
import xshyo.us.theglow.libs.config.engine.ExtendedConstructor;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.MappingNode;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.Node;
import xshyo.us.theglow.libs.config.libs.org.snakeyaml.engine.v2.nodes.NodeTuple;
import xshyo.us.theglow.libs.config.route.Route;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;
import xshyo.us.theglow.libs.config.utils.conversion.ListConversions;
import xshyo.us.theglow.libs.config.utils.conversion.PrimitiveConversions;

public class Section extends Block<Map<Object, Block<?>>> {
   private YamlDocument root;
   private Section defaults = null;
   private Section parent;
   private Object name;
   private Route route;

   public Section(@NotNull YamlDocument var1, @Nullable Section var2, @NotNull Route var3, @Nullable Node var4, @NotNull MappingNode var5, @NotNull ExtendedConstructor var6) {
      super(var4, var5, var1.getGeneralSettings().getDefaultMap());
      this.root = var1;
      this.parent = var2;
      this.name = this.adaptKey(var3.get(var3.length() - 1));
      this.route = var3;
      this.resetDefaults();
      this.init(var1, var4, var5, var6);
   }

   public Section(@NotNull YamlDocument var1, @Nullable Section var2, @NotNull Route var3, @Nullable Block<?> var4, @NotNull Map<?, ?> var5) {
      super(var4, var1.getGeneralSettings().getDefaultMap());
      this.root = var1;
      this.parent = var2;
      this.name = this.adaptKey(var3.get(var3.length() - 1));
      this.route = var3;
      this.resetDefaults();
      Iterator var6 = var5.entrySet().iterator();

      while(var6.hasNext()) {
         Entry var7 = (Entry)var6.next();
         Object var8 = this.adaptKey(var7.getKey());
         Object var9 = var7.getValue();
         ((Map)this.getStoredValue()).put(var8, var9 instanceof Map ? new Section(var1, this, var3.add(var8), (Block)null, (Map)var9) : new TerminatedBlock((Block)null, var9));
      }

   }

   protected Section(@NotNull Map<Object, Block<?>> var1) {
      super(var1);
      this.root = null;
      this.parent = null;
      this.name = null;
      this.route = null;
      this.defaults = null;
   }

   protected void initEmpty(@NotNull YamlDocument var1) {
      if (!var1.isRoot()) {
         throw new IllegalStateException("Cannot init non-root section!");
      } else {
         super.init((Node)null, (Node)null);
         this.root = var1;
         this.resetDefaults();
      }
   }

   protected void init(@NotNull YamlDocument var1, @Nullable Node var2, @NotNull MappingNode var3, @NotNull ExtendedConstructor var4) {
      if (var1 == this && var2 != null) {
         throw new IllegalArgumentException("Root sections cannot have a key node!");
      } else {
         super.init(var2, var3);
         this.root = var1;
         this.resetDefaults();
         Iterator var5 = var3.getValue().iterator();

         while(var5.hasNext()) {
            NodeTuple var6 = (NodeTuple)var5.next();
            Object var7 = this.adaptKey(var4.getConstructed(var6.getKeyNode()));
            Object var8 = var4.getConstructed(var6.getValueNode());
            ((Map)this.getStoredValue()).put(var7, var8 instanceof Map ? new Section(var1, this, this.getSubRoute(var7), var6.getKeyNode(), (MappingNode)var6.getValueNode(), var4) : new TerminatedBlock(var6.getKeyNode(), var6.getValueNode(), var8));
         }

      }
   }

   public boolean isEmpty(boolean var1) {
      if (((Map)this.getStoredValue()).isEmpty()) {
         return true;
      } else if (!var1) {
         return false;
      } else {
         Iterator var2 = ((Map)this.getStoredValue()).values().iterator();

         Block var3;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (Block)var2.next();
         } while(!(var3 instanceof TerminatedBlock) && (!(var3 instanceof Section) || ((Section)var3).isEmpty(true)));

         return false;
      }
   }

   public boolean isSection() {
      return true;
   }

   public boolean isRoot() {
      return false;
   }

   @NotNull
   public YamlDocument getRoot() {
      return this.root;
   }

   public Section getParent() {
      return this.parent;
   }

   @Nullable
   public Object getName() {
      return this.name;
   }

   @Nullable
   public String getNameAsString() {
      return this.name == null ? null : this.name.toString();
   }

   @NotNull
   public Route getNameAsRoute() {
      return Route.from(this.name);
   }

   @Nullable
   public Route getRoute() {
      return this.route;
   }

   @Nullable
   public String getRouteAsString() {
      return this.route == null ? null : this.route.join(this.root.getGeneralSettings().getRouteSeparator());
   }

   @NotNull
   public Route getSubRoute(@NotNull Object var1) {
      return Route.addTo(this.route, var1);
   }

   @Nullable
   public Section getDefaults() {
      return this.defaults;
   }

   public boolean hasDefaults() {
      return this.defaults != null;
   }

   private void adapt(@NotNull YamlDocument var1, @Nullable Section var2, @NotNull Route var3) {
      if (this.parent != null && this.parent != var2 && ((Map)this.parent.getStoredValue()).get(this.name) == this) {
         this.parent.removeInternal(this.parent, this.name);
      }

      this.name = var3.get(var3.length() - 1);
      this.parent = var2;
      this.adapt(var1, var3);
   }

   private void adapt(@NotNull YamlDocument var1, @NotNull Route var2) {
      this.root = var1;
      this.route = var2;
      this.resetDefaults();
      Iterator var3 = ((Map)this.getStoredValue()).entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (var4.getValue() instanceof Section) {
            ((Section)var4.getValue()).adapt(var1, var2.add(var4.getKey()));
         }
      }

   }

   @NotNull
   public Object adaptKey(@NotNull Object var1) {
      Objects.requireNonNull(var1, "Sections cannot contain null keys!");
      return this.root.getGeneralSettings().getKeyFormat() == GeneralSettings.KeyFormat.OBJECT ? var1 : var1.toString();
   }

   private void resetDefaults() {
      this.defaults = (Section)(this.isRoot() ? this.root.getDefaults() : (this.parent != null && this.parent.defaults != null ? this.parent.defaults.getSection((Route)Route.fromSingleKey(this.name), (Section)null) : null));
   }

   private boolean canUseDefaults() {
      return this.hasDefaults() && this.root.getGeneralSettings().isUseDefaults();
   }

   @NotNull
   public Set<Route> getRoutes(boolean var1) {
      Set var2 = this.root.getGeneralSettings().getDefaultSet();
      if (this.canUseDefaults()) {
         var2.addAll(this.defaults.getRoutes(var1));
      }

      this.addData((var1x, var2x) -> {
         var2.add(var1x);
      }, (Route)null, var1);
      return var2;
   }

   @NotNull
   public Set<String> getRoutesAsStrings(boolean var1) {
      Set var2 = this.root.getGeneralSettings().getDefaultSet();
      if (this.canUseDefaults()) {
         var2.addAll(this.defaults.getRoutesAsStrings(var1));
      }

      this.addData((var1x, var2x) -> {
         var2.add(var1x);
      }, new StringBuilder(), this.root.getGeneralSettings().getRouteSeparator(), var1);
      return var2;
   }

   @NotNull
   public Set<Object> getKeys() {
      Set var1 = this.root.getGeneralSettings().getDefaultSet(((Map)this.getStoredValue()).size());
      if (this.canUseDefaults()) {
         var1.addAll(this.defaults.getKeys());
      }

      var1.addAll(((Map)this.getStoredValue()).keySet());
      return var1;
   }

   @NotNull
   public Map<Route, Object> getRouteMappedValues(boolean var1) {
      Map var2 = this.root.getGeneralSettings().getDefaultMap();
      if (this.canUseDefaults()) {
         var2.putAll(this.defaults.getRouteMappedValues(var1));
      }

      this.addData((var1x, var2x) -> {
         var2.put(var1x, var2x.getValue() instanceof Section ? var2x.getValue() : ((Block)var2x.getValue()).getStoredValue());
      }, (Route)null, var1);
      return var2;
   }

   @NotNull
   public Map<String, Object> getStringRouteMappedValues(boolean var1) {
      Map var2 = this.root.getGeneralSettings().getDefaultMap();
      if (this.canUseDefaults()) {
         var2.putAll(this.defaults.getStringRouteMappedValues(var1));
      }

      this.addData((var1x, var2x) -> {
         var2.put(var1x, var2x.getValue() instanceof Section ? var2x.getValue() : ((Block)var2x.getValue()).getStoredValue());
      }, new StringBuilder(), this.root.getGeneralSettings().getRouteSeparator(), var1);
      return var2;
   }

   @NotNull
   public Map<Route, Block<?>> getRouteMappedBlocks(boolean var1) {
      Map var2 = this.root.getGeneralSettings().getDefaultMap();
      if (this.canUseDefaults()) {
         var2.putAll(this.defaults.getRouteMappedBlocks(var1));
      }

      this.addData((var1x, var2x) -> {
         Block var10000 = (Block)var2.put(var1x, var2x.getValue());
      }, (Route)null, var1);
      return var2;
   }

   @NotNull
   public Map<String, Block<?>> getStringRouteMappedBlocks(boolean var1) {
      Map var2 = this.root.getGeneralSettings().getDefaultMap();
      if (this.canUseDefaults()) {
         var2.putAll(this.defaults.getStringRouteMappedBlocks(var1));
      }

      this.addData((var1x, var2x) -> {
         Block var10000 = (Block)var2.put(var1x, var2x.getValue());
      }, new StringBuilder(), this.root.getGeneralSettings().getRouteSeparator(), var1);
      return var2;
   }

   private void addData(@NotNull BiConsumer<Route, Entry<?, Block<?>>> var1, @Nullable Route var2, boolean var3) {
      Iterator var4 = ((Map)this.getStoredValue()).entrySet().iterator();

      while(var4.hasNext()) {
         Entry var5 = (Entry)var4.next();
         Route var6 = Route.addTo(var2, var5.getKey());
         var1.accept(var6, var5);
         if (var3 && var5.getValue() instanceof Section) {
            ((Section)var5.getValue()).addData(var1, var6, true);
         }
      }

   }

   private void addData(@NotNull BiConsumer<String, Entry<?, Block<?>>> var1, @NotNull StringBuilder var2, char var3, boolean var4) {
      int var7;
      for(Iterator var5 = ((Map)this.getStoredValue()).entrySet().iterator(); var5.hasNext(); var2.setLength(var7)) {
         Entry var6 = (Entry)var5.next();
         var7 = var2.length();
         if (var7 > 0) {
            var2.append(var3);
         }

         var1.accept(var2.append(var6.getKey().toString()).toString(), var6);
         if (var4 && var6.getValue() instanceof Section) {
            ((Section)var6.getValue()).addData(var1, var2, var3, true);
         }
      }

   }

   public boolean contains(@NotNull Route var1) {
      return this.getBlock(var1) != null;
   }

   public boolean contains(@NotNull String var1) {
      return this.getBlock(var1) != null;
   }

   public Section createSection(@NotNull Route var1) {
      Section var2 = this;

      for(int var3 = 0; var3 < var1.length(); ++var3) {
         var2 = var2.createSectionInternal(var1.get(var3), (Block)null);
      }

      return var2;
   }

   public Section createSection(@NotNull String var1) {
      int var2 = 0;
      Section var3 = this;

      while(true) {
         int var4 = var1.indexOf(this.root.getGeneralSettings().getRouteSeparator(), var2);
         if (var4 == -1) {
            return var3.createSectionInternal(var1.substring(var2), (Block)null);
         }

         var3 = var3.createSectionInternal(var1.substring(var2, var4), (Block)null);
         var2 = var4 + 1;
      }
   }

   private Section createSectionInternal(@NotNull Object var1, @Nullable Block<?> var2) {
      Object var3 = this.adaptKey(var1);
      return (Section)this.getOptionalSection(Route.from(var3)).orElseGet(() -> {
         Section var3x = new Section(this.root, this, this.getSubRoute(var3), var2, this.root.getGeneralSettings().getDefaultMap());
         ((Map)this.getStoredValue()).put(var3, var3x);
         return var3x;
      });
   }

   public void repopulate(@NotNull Map<Object, Block<?>> var1) {
      this.clear();
      var1.forEach(this::setInternal);
   }

   public void setAll(@NotNull Map<Route, Object> var1) {
      var1.forEach(this::set);
   }

   public void set(@NotNull Route var1, @Nullable Object var2) {
      this.traverse(var1, true).ifPresent((var1x) -> {
         var1x.parent.setInternal(var1x.key, var2);
      });
   }

   public void set(@NotNull String var1, @Nullable Object var2) {
      this.traverse(var1, true).ifPresent((var1x) -> {
         var1x.parent.setInternal(var1x.key, var2);
      });
   }

   private void setInternal(@NotNull Object var1, @Nullable Object var2) {
      if (var2 instanceof Section) {
         Section var4 = (Section)var2;
         if (var4.isRoot()) {
            throw new IllegalArgumentException("Cannot set root section as the value!");
         } else if (var4.getRoot().getGeneralSettings().getKeyFormat() != this.getRoot().getGeneralSettings().getKeyFormat()) {
            throw new IllegalArgumentException("Cannot move sections between files with different key formats!");
         } else {
            ((Map)this.getStoredValue()).put(var1, var4);
            var4.adapt(this.root, this, this.getSubRoute(var1));
         }
      } else if (var2 instanceof TerminatedBlock) {
         ((Map)this.getStoredValue()).put(var1, (TerminatedBlock)var2);
      } else if (var2 instanceof Map) {
         ((Map)this.getStoredValue()).put(var1, new Section(this.root, this, this.getSubRoute(var1), (Block)((Map)this.getStoredValue()).getOrDefault(var1, (Object)null), (Map)var2));
      } else {
         Block var3 = (Block)((Map)this.getStoredValue()).get(var1);
         if (var3 == null) {
            ((Map)this.getStoredValue()).put(var1, new TerminatedBlock((Node)null, (Node)null, var2));
         } else {
            ((Map)this.getStoredValue()).put(var1, new TerminatedBlock(var3, var2));
         }
      }
   }

   @Nullable
   public Block<?> move(@NotNull Route var1, @NotNull Route var2) {
      return (Block)this.traverse(var1, false).map((var0) -> {
         return (Block)((Map)var0.parent.getStoredValue()).remove(var0.key);
      }).map((var2x) -> {
         this.set((Route)var2, var2x);
         return var2x;
      }).orElse((Object)null);
   }

   @Nullable
   public Block<?> move(@NotNull String var1, @NotNull String var2) {
      return (Block)this.traverse(var1, false).map((var0) -> {
         return (Block)((Map)var0.parent.getStoredValue()).remove(var0.key);
      }).map((var2x) -> {
         this.set((String)var2, var2x);
         return var2x;
      }).orElse((Object)null);
   }

   private Optional<Section.BlockReference> traverse(@NotNull Route var1, boolean var2) {
      int var3 = -1;
      Section var4 = this;

      while(true) {
         ++var3;
         Object var5 = this.adaptKey(var1.get(var3));
         if (var3 + 1 == var1.length()) {
            return Optional.of(new Section.BlockReference(var4, var5));
         }

         Block var6 = (Block)((Map)var4.getStoredValue()).getOrDefault(var5, (Object)null);
         if (var6 instanceof Section) {
            var4 = (Section)var6;
         } else {
            if (!var2) {
               return Optional.empty();
            }

            var4 = var4.createSectionInternal(var5, var6);
         }
      }
   }

   private Optional<Section.BlockReference> traverse(@NotNull String var1, boolean var2) {
      int var3 = 0;
      Section var4 = this;

      while(true) {
         int var5 = var1.indexOf(this.root.getGeneralSettings().getRouteSeparator(), var3);
         if (var5 == -1) {
            return Optional.of(new Section.BlockReference(var4, var1.substring(var3)));
         }

         String var6 = var1.substring(var3, var5);
         Block var7 = (Block)((Map)var4.getStoredValue()).getOrDefault(var6, (Object)null);
         var3 = var5 + 1;
         if (var7 instanceof Section) {
            var4 = (Section)var7;
         } else {
            if (!var2) {
               return Optional.empty();
            }

            var4 = var4.createSectionInternal(var6, var7);
         }
      }
   }

   public boolean remove(@NotNull Route var1) {
      return this.removeInternal((Section)this.getParent(var1).orElse((Object)null), this.adaptKey(var1.get(var1.length() - 1)));
   }

   public boolean remove(@NotNull String var1) {
      return this.removeInternal((Section)this.getParent(var1).orElse((Object)null), var1.substring(var1.lastIndexOf(this.root.getGeneralSettings().getRouteSeparator()) + 1));
   }

   private boolean removeInternal(@Nullable Section var1, @Nullable Object var2) {
      if (var1 == null) {
         return false;
      } else {
         return ((Map)var1.getStoredValue()).remove(var2) != null;
      }
   }

   public void clear() {
      ((Map)this.getStoredValue()).clear();
   }

   public Optional<Block<?>> getOptionalBlock(@NotNull Route var1) {
      return this.getBlockInternal(var1, false);
   }

   private Optional<Block<?>> getDirectOptionalBlock(@NotNull Object var1) {
      return Optional.ofNullable(((Map)this.getStoredValue()).get(this.adaptKey(var1)));
   }

   public Optional<Block<?>> getOptionalBlock(@NotNull String var1) {
      return var1.indexOf(this.root.getGeneralSettings().getRouteSeparator()) != -1 ? this.getBlockInternalString(var1, false) : this.getDirectOptionalBlock(var1);
   }

   public Block<?> getBlock(@NotNull Route var1) {
      return (Block)this.getOptionalBlock(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBlock(var1) : null;
      });
   }

   public Block<?> getBlock(@NotNull String var1) {
      return (Block)this.getOptionalBlock(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBlock(var1) : null;
      });
   }

   private Optional<Block<?>> getBlockInternalString(@NotNull String var1, boolean var2) {
      int var3 = 0;
      Section var4 = this;

      while(true) {
         int var5 = var1.indexOf(this.root.getGeneralSettings().getRouteSeparator(), var3);
         if (var5 == -1) {
            return Optional.ofNullable(var2 ? var4 : (Block)((Map)var4.getStoredValue()).get(var1.substring(var3)));
         }

         Block var6 = (Block)((Map)var4.getStoredValue()).getOrDefault(var1.substring(var3, var5), (Object)null);
         if (!(var6 instanceof Section)) {
            return Optional.empty();
         }

         var4 = (Section)var6;
         var3 = var5 + 1;
      }
   }

   private Optional<Block<?>> getBlockInternal(@NotNull Route var1, boolean var2) {
      int var3 = -1;
      Section var4 = this;

      while(true) {
         ++var3;
         if (var3 >= var1.length() - 1) {
            return Optional.ofNullable(var2 ? var4 : (Block)((Map)var4.getStoredValue()).get(this.adaptKey(var1.get(var3))));
         }

         Block var5 = (Block)((Map)var4.getStoredValue()).getOrDefault(this.adaptKey(var1.get(var3)), (Object)null);
         if (!(var5 instanceof Section)) {
            return Optional.empty();
         }

         var4 = (Section)var5;
      }
   }

   public Optional<Section> getParent(@NotNull Route var1) {
      return this.getBlockInternal(var1, true).map((var0) -> {
         return var0 instanceof Section ? (Section)var0 : null;
      });
   }

   public Optional<Section> getParent(@NotNull String var1) {
      return this.getBlockInternalString(var1, true).map((var0) -> {
         return var0 instanceof Section ? (Section)var0 : null;
      });
   }

   public Optional<Object> getOptional(@NotNull Route var1) {
      return this.getOptionalBlock(var1).map((var0) -> {
         return var0 instanceof Section ? var0 : var0.getStoredValue();
      });
   }

   public Optional<Object> getOptional(@NotNull String var1) {
      return this.getOptionalBlock(var1).map((var0) -> {
         return var0 instanceof Section ? var0 : var0.getStoredValue();
      });
   }

   public Object get(@NotNull Route var1) {
      return this.getOptional(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.get(var1) : this.root.getGeneralSettings().getDefaultObject();
      });
   }

   public Object get(@NotNull String var1) {
      return this.getOptional(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.get(var1) : this.root.getGeneralSettings().getDefaultObject();
      });
   }

   public Object get(@NotNull Route var1, @Nullable Object var2) {
      return this.getOptional(var1).orElse(var2);
   }

   public Object get(@NotNull String var1, @Nullable Object var2) {
      return this.getOptional(var1).orElse(var2);
   }

   public <T> Optional<T> getAsOptional(@NotNull Route var1, @NotNull Class<T> var2) {
      return this.getOptional(var1).map((var1x) -> {
         return var2.isInstance(var1x) ? var1x : (PrimitiveConversions.isNumber(var1x.getClass()) && PrimitiveConversions.isNumber(var2) ? PrimitiveConversions.convertNumber(var1x, var2) : (PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(var1x.getClass()) && PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(var2) ? var1x : null));
      });
   }

   public <T> Optional<T> getAsOptional(@NotNull String var1, @NotNull Class<T> var2) {
      return this.getOptional(var1).map((var1x) -> {
         return var2.isInstance(var1x) ? var1x : (PrimitiveConversions.isNumber(var1x.getClass()) && PrimitiveConversions.isNumber(var2) ? PrimitiveConversions.convertNumber(var1x, var2) : (PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(var1x.getClass()) && PrimitiveConversions.NON_NUMERIC_CONVERSIONS.containsKey(var2) ? var1x : null));
      });
   }

   public <T> T getAs(@NotNull Route var1, @NotNull Class<T> var2) {
      return this.getAsOptional(var1, var2).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getAs(var1, var2) : null;
      });
   }

   public <T> T getAs(@NotNull String var1, @NotNull Class<T> var2) {
      return this.getAsOptional(var1, var2).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getAs(var1, var2) : null;
      });
   }

   public <T> T getAs(@NotNull Route var1, @NotNull Class<T> var2, @Nullable T var3) {
      return this.getAsOptional(var1, var2).orElse(var3);
   }

   public <T> T getAs(@NotNull String var1, @NotNull Class<T> var2, @Nullable T var3) {
      return this.getAsOptional(var1, var2).orElse(var3);
   }

   public <T> boolean is(@NotNull Route var1, @NotNull Class<T> var2) {
      Object var3 = this.get(var1);
      return PrimitiveConversions.PRIMITIVES_TO_OBJECTS.containsKey(var2) ? ((Class)PrimitiveConversions.PRIMITIVES_TO_OBJECTS.get(var2)).isInstance(var3) : var2.isInstance(var3);
   }

   public <T> boolean is(@NotNull String var1, @NotNull Class<T> var2) {
      Object var3 = this.get(var1);
      return PrimitiveConversions.PRIMITIVES_TO_OBJECTS.containsKey(var2) ? ((Class)PrimitiveConversions.PRIMITIVES_TO_OBJECTS.get(var2)).isInstance(var3) : var2.isInstance(var3);
   }

   public Optional<Section> getOptionalSection(@NotNull Route var1) {
      return this.getAsOptional(var1, Section.class);
   }

   public Optional<Section> getOptionalSection(@NotNull String var1) {
      return this.getAsOptional(var1, Section.class);
   }

   public Section getSection(@NotNull Route var1) {
      return (Section)this.getOptionalSection(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getSection(var1) : null;
      });
   }

   public Section getSection(@NotNull String var1) {
      return (Section)this.getOptionalSection(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getSection(var1) : null;
      });
   }

   public Section getSection(@NotNull Route var1, @Nullable Section var2) {
      return (Section)this.getOptionalSection(var1).orElse(var2);
   }

   public Section getSection(@NotNull String var1, @Nullable Section var2) {
      return (Section)this.getOptionalSection(var1).orElse(var2);
   }

   public boolean isSection(@NotNull Route var1) {
      return this.get(var1) instanceof Section;
   }

   public boolean isSection(@NotNull String var1) {
      return this.get(var1) instanceof Section;
   }

   public Optional<String> getOptionalString(@NotNull Route var1) {
      return this.getOptional(var1).map(Object::toString);
   }

   public Optional<String> getOptionalString(@NotNull String var1) {
      return this.getOptional(var1).map(Object::toString);
   }

   public String getString(@NotNull Route var1) {
      return (String)this.getOptionalString(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getString(var1) : this.root.getGeneralSettings().getDefaultString();
      });
   }

   public String getString(@NotNull String var1) {
      return (String)this.getOptionalString(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getString(var1) : this.root.getGeneralSettings().getDefaultString();
      });
   }

   public String getString(@NotNull Route var1, @Nullable String var2) {
      return (String)this.getOptionalString(var1).orElse(var2);
   }

   public String getString(@NotNull String var1, @Nullable String var2) {
      return (String)this.getOptionalString(var1).orElse(var2);
   }

   public boolean isString(@NotNull Route var1) {
      return this.get(var1) instanceof String;
   }

   public boolean isString(@NotNull String var1) {
      return this.get(var1) instanceof String;
   }

   public <T extends Enum<T>> Optional<T> getOptionalEnum(@NotNull Route var1, @NotNull Class<T> var2) {
      return this.getOptional(var1).map((var2x) -> {
         return this.toEnum(var2x, var2);
      });
   }

   public <T extends Enum<T>> Optional<T> getOptionalEnum(@NotNull String var1, @NotNull Class<T> var2) {
      return this.getOptionalString(var1).map((var2x) -> {
         return this.toEnum(var2x, var2);
      });
   }

   public <T extends Enum<T>> T getEnum(@NotNull Route var1, @NotNull Class<T> var2) {
      return (Enum)this.getOptionalEnum(var1, var2).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getEnum(var1, var2) : null;
      });
   }

   public <T extends Enum<T>> T getEnum(@NotNull String var1, @NotNull Class<T> var2) {
      return (Enum)this.getOptionalEnum(var1, var2).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getEnum(var1, var2) : null;
      });
   }

   public <T extends Enum<T>> T getEnum(@NotNull Route var1, @NotNull Class<T> var2, @Nullable T var3) {
      return (Enum)this.getOptionalEnum(var1, var2).orElse(var3);
   }

   public <T extends Enum<T>> T getEnum(@NotNull String var1, @NotNull Class<T> var2, @Nullable T var3) {
      return (Enum)this.getOptionalEnum(var1, var2).orElse(var3);
   }

   public <T extends Enum<T>> boolean isEnum(@NotNull Route var1, @NotNull Class<T> var2) {
      return this.toEnum(this.get(var1), var2) != null;
   }

   public <T extends Enum<T>> boolean isEnum(@NotNull String var1, @NotNull Class<T> var2) {
      return this.toEnum(this.get(var1), var2) != null;
   }

   private <T extends Enum<T>> T toEnum(@Nullable Object var1, @NotNull Class<T> var2) {
      if (var1 == null) {
         return null;
      } else if (var2.isInstance(var1)) {
         return (Enum)var1;
      } else if (var1 instanceof Enum) {
         return null;
      } else {
         try {
            return Enum.valueOf(var2, var1.toString());
         } catch (IllegalArgumentException var4) {
            return null;
         }
      }
   }

   public Optional<Character> getOptionalChar(@NotNull Route var1) {
      return this.getOptional(var1).map(this::toChar);
   }

   public Optional<Character> getOptionalChar(@NotNull String var1) {
      return this.getOptional(var1).map(this::toChar);
   }

   public Character getChar(@NotNull Route var1) {
      return (Character)this.getOptionalChar(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getChar(var1) : this.root.getGeneralSettings().getDefaultChar();
      });
   }

   public Character getChar(@NotNull String var1) {
      return (Character)this.getOptionalChar(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getChar(var1) : this.root.getGeneralSettings().getDefaultChar();
      });
   }

   public Character getChar(@NotNull Route var1, @Nullable Character var2) {
      return (Character)this.getOptionalChar(var1).orElse(var2);
   }

   public Character getChar(@NotNull String var1, @Nullable Character var2) {
      return (Character)this.getOptionalChar(var1).orElse(var2);
   }

   public boolean isChar(@NotNull Route var1) {
      return this.toChar(this.get(var1)) != null;
   }

   public boolean isChar(@NotNull String var1) {
      return this.toChar(this.get(var1)) != null;
   }

   private Character toChar(@Nullable Object var1) {
      if (var1 == null) {
         return null;
      } else if (var1 instanceof Character) {
         return (Character)var1;
      } else if (var1 instanceof Integer) {
         return (char)(Integer)var1;
      } else {
         return var1 instanceof String && var1.toString().length() == 1 ? var1.toString().charAt(0) : null;
      }
   }

   public Optional<Number> getOptionalNumber(@NotNull Route var1) {
      return this.getAsOptional(var1, Number.class);
   }

   public Optional<Number> getOptionalNumber(@NotNull String var1) {
      return this.getAsOptional(var1, Number.class);
   }

   public Number getNumber(@NotNull Route var1) {
      return (Number)this.getOptionalNumber(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getNumber(var1) : this.root.getGeneralSettings().getDefaultNumber();
      });
   }

   public Number getNumber(@NotNull String var1) {
      return (Number)this.getOptionalNumber(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getNumber(var1) : this.root.getGeneralSettings().getDefaultNumber();
      });
   }

   public Number getNumber(@NotNull Route var1, @Nullable Number var2) {
      return (Number)this.getOptionalNumber(var1).orElse(var2);
   }

   public Number getNumber(@NotNull String var1, @Nullable Number var2) {
      return (Number)this.getOptionalNumber(var1).orElse(var2);
   }

   public boolean isNumber(@NotNull Route var1) {
      return this.get(var1) instanceof Number;
   }

   public boolean isNumber(@NotNull String var1) {
      return this.get(var1) instanceof Number;
   }

   public Optional<Integer> getOptionalInt(@NotNull Route var1) {
      return PrimitiveConversions.toInt(this.getAs(var1, Number.class));
   }

   public Optional<Integer> getOptionalInt(@NotNull String var1) {
      return PrimitiveConversions.toInt(this.getAs(var1, Number.class));
   }

   public Integer getInt(@NotNull Route var1) {
      return (Integer)this.getOptionalInt(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getInt(var1) : this.root.getGeneralSettings().getDefaultNumber().intValue();
      });
   }

   public Integer getInt(@NotNull String var1) {
      return (Integer)this.getOptionalInt(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getInt(var1) : this.root.getGeneralSettings().getDefaultNumber().intValue();
      });
   }

   public Integer getInt(@NotNull Route var1, @Nullable Integer var2) {
      return (Integer)this.getOptionalInt(var1).orElse(var2);
   }

   public Integer getInt(@NotNull String var1, @Nullable Integer var2) {
      return (Integer)this.getOptionalInt(var1).orElse(var2);
   }

   public boolean isInt(@NotNull Route var1) {
      return this.get(var1) instanceof Integer;
   }

   public boolean isInt(@NotNull String var1) {
      return this.get(var1) instanceof Integer;
   }

   public Optional<BigInteger> getOptionalBigInt(@NotNull Route var1) {
      return PrimitiveConversions.toBigInt(this.getAs(var1, Number.class));
   }

   public Optional<BigInteger> getOptionalBigInt(@NotNull String var1) {
      return PrimitiveConversions.toBigInt(this.getAs(var1, Number.class));
   }

   public BigInteger getBigInt(@NotNull Route var1) {
      return (BigInteger)this.getOptionalBigInt(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBigInt(var1) : BigInteger.valueOf(this.root.getGeneralSettings().getDefaultNumber().longValue());
      });
   }

   public BigInteger getBigInt(@NotNull String var1) {
      return (BigInteger)this.getOptionalBigInt(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBigInt(var1) : BigInteger.valueOf(this.root.getGeneralSettings().getDefaultNumber().longValue());
      });
   }

   public BigInteger getBigInt(@NotNull Route var1, @Nullable BigInteger var2) {
      return (BigInteger)this.getOptionalBigInt(var1).orElse(var2);
   }

   public BigInteger getBigInt(@NotNull String var1, @Nullable BigInteger var2) {
      return (BigInteger)this.getOptionalBigInt(var1).orElse(var2);
   }

   public boolean isBigInt(@NotNull Route var1) {
      return this.get(var1) instanceof BigInteger;
   }

   public boolean isBigInt(@NotNull String var1) {
      return this.get(var1) instanceof BigInteger;
   }

   public Optional<Boolean> getOptionalBoolean(@NotNull Route var1) {
      return this.getAsOptional(var1, Boolean.class);
   }

   public Optional<Boolean> getOptionalBoolean(@NotNull String var1) {
      return this.getAsOptional(var1, Boolean.class);
   }

   public Boolean getBoolean(@NotNull Route var1) {
      return (Boolean)this.getOptionalBoolean(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBoolean(var1) : this.root.getGeneralSettings().getDefaultBoolean();
      });
   }

   public Boolean getBoolean(@NotNull String var1) {
      return (Boolean)this.getOptionalBoolean(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBoolean(var1) : this.root.getGeneralSettings().getDefaultBoolean();
      });
   }

   public Boolean getBoolean(@NotNull Route var1, @Nullable Boolean var2) {
      return (Boolean)this.getOptionalBoolean(var1).orElse(var2);
   }

   public Boolean getBoolean(@NotNull String var1, @Nullable Boolean var2) {
      return (Boolean)this.getOptionalBoolean(var1).orElse(var2);
   }

   public boolean isBoolean(@NotNull Route var1) {
      return this.get(var1) instanceof Boolean;
   }

   public boolean isBoolean(@NotNull String var1) {
      return this.get(var1) instanceof Boolean;
   }

   public Optional<Double> getOptionalDouble(@NotNull Route var1) {
      return PrimitiveConversions.toDouble(this.getAs(var1, Number.class));
   }

   public Optional<Double> getOptionalDouble(@NotNull String var1) {
      return PrimitiveConversions.toDouble(this.getAs(var1, Number.class));
   }

   public Double getDouble(@NotNull Route var1) {
      return (Double)this.getOptionalDouble(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getDouble(var1) : this.root.getGeneralSettings().getDefaultNumber().doubleValue();
      });
   }

   public Double getDouble(@NotNull String var1) {
      return (Double)this.getOptionalDouble(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getDouble(var1) : this.root.getGeneralSettings().getDefaultNumber().doubleValue();
      });
   }

   public Double getDouble(@NotNull Route var1, @Nullable Double var2) {
      return (Double)this.getOptionalDouble(var1).orElse(var2);
   }

   public Double getDouble(@NotNull String var1, @Nullable Double var2) {
      return (Double)this.getOptionalDouble(var1).orElse(var2);
   }

   public boolean isDouble(@NotNull Route var1) {
      return this.get(var1) instanceof Double;
   }

   public boolean isDouble(@NotNull String var1) {
      return this.get(var1) instanceof Double;
   }

   public Optional<Float> getOptionalFloat(@NotNull Route var1) {
      return PrimitiveConversions.toFloat(this.getAs(var1, Number.class));
   }

   public Optional<Float> getOptionalFloat(@NotNull String var1) {
      return PrimitiveConversions.toFloat(this.getAs(var1, Number.class));
   }

   public Float getFloat(@NotNull Route var1) {
      return (Float)this.getOptionalFloat(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getFloat(var1) : this.root.getGeneralSettings().getDefaultNumber().floatValue();
      });
   }

   public Float getFloat(@NotNull String var1) {
      return (Float)this.getOptionalFloat(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getFloat(var1) : this.root.getGeneralSettings().getDefaultNumber().floatValue();
      });
   }

   public Float getFloat(@NotNull Route var1, @Nullable Float var2) {
      return (Float)this.getOptionalFloat(var1).orElse(var2);
   }

   public Float getFloat(@NotNull String var1, @Nullable Float var2) {
      return (Float)this.getOptionalFloat(var1).orElse(var2);
   }

   public boolean isFloat(@NotNull Route var1) {
      return this.get(var1) instanceof Float;
   }

   public boolean isFloat(@NotNull String var1) {
      return this.get(var1) instanceof Float;
   }

   public Optional<Byte> getOptionalByte(@NotNull Route var1) {
      return PrimitiveConversions.toByte(this.getAs(var1, Number.class));
   }

   public Optional<Byte> getOptionalByte(@NotNull String var1) {
      return PrimitiveConversions.toByte(this.getAs(var1, Number.class));
   }

   public Byte getByte(@NotNull Route var1) {
      return (Byte)this.getOptionalByte(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getByte(var1) : this.root.getGeneralSettings().getDefaultNumber().byteValue();
      });
   }

   public Byte getByte(@NotNull String var1) {
      return (Byte)this.getOptionalByte(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getByte(var1) : this.root.getGeneralSettings().getDefaultNumber().byteValue();
      });
   }

   public Byte getByte(@NotNull Route var1, @Nullable Byte var2) {
      return (Byte)this.getOptionalByte(var1).orElse(var2);
   }

   public Byte getByte(@NotNull String var1, @Nullable Byte var2) {
      return (Byte)this.getOptionalByte(var1).orElse(var2);
   }

   public boolean isByte(@NotNull Route var1) {
      return this.get(var1) instanceof Byte;
   }

   public boolean isByte(@NotNull String var1) {
      return this.get(var1) instanceof Byte;
   }

   public Optional<Long> getOptionalLong(@NotNull Route var1) {
      return PrimitiveConversions.toLong(this.getAs(var1, Number.class));
   }

   public Optional<Long> getOptionalLong(String var1) {
      return PrimitiveConversions.toLong(this.getAs(var1, Number.class));
   }

   public Long getLong(@NotNull Route var1) {
      return (Long)this.getOptionalLong(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getLong(var1) : this.root.getGeneralSettings().getDefaultNumber().longValue();
      });
   }

   public Long getLong(@NotNull String var1) {
      return (Long)this.getOptionalLong(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getLong(var1) : this.root.getGeneralSettings().getDefaultNumber().longValue();
      });
   }

   public Long getLong(@NotNull Route var1, @Nullable Long var2) {
      return (Long)this.getOptionalLong(var1).orElse(var2);
   }

   public Long getLong(@NotNull String var1, @Nullable Long var2) {
      return (Long)this.getOptionalLong(var1).orElse(var2);
   }

   public boolean isLong(@NotNull Route var1) {
      return this.get(var1) instanceof Long;
   }

   public boolean isLong(@NotNull String var1) {
      return this.get(var1) instanceof Long;
   }

   public Optional<Short> getOptionalShort(@NotNull Route var1) {
      return PrimitiveConversions.toShort(this.getAs(var1, Number.class));
   }

   public Optional<Short> getOptionalShort(@NotNull String var1) {
      return PrimitiveConversions.toShort(this.getAs(var1, Number.class));
   }

   public Short getShort(@NotNull Route var1) {
      return (Short)this.getOptionalShort(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getShort(var1) : this.root.getGeneralSettings().getDefaultNumber().shortValue();
      });
   }

   public Short getShort(@NotNull String var1) {
      return (Short)this.getOptionalShort(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getShort(var1) : this.root.getGeneralSettings().getDefaultNumber().shortValue();
      });
   }

   public Short getShort(@NotNull Route var1, @Nullable Short var2) {
      return (Short)this.getOptionalShort(var1).orElse(var2);
   }

   public Short getShort(@NotNull String var1, @Nullable Short var2) {
      return (Short)this.getOptionalShort(var1).orElse(var2);
   }

   public boolean isShort(@NotNull Route var1) {
      return this.get(var1) instanceof Short;
   }

   public boolean isShort(@NotNull String var1) {
      return this.get(var1) instanceof Short;
   }

   public boolean isDecimal(@NotNull Route var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Double || var2 instanceof Float;
   }

   public boolean isDecimal(@NotNull String var1) {
      Object var2 = this.get(var1);
      return var2 instanceof Double || var2 instanceof Float;
   }

   public Optional<List<?>> getOptionalList(@NotNull Route var1) {
      return this.getAsOptional(var1, List.class).map((var0) -> {
         return var0;
      });
   }

   public Optional<List<?>> getOptionalList(@NotNull String var1) {
      return this.getAsOptional(var1, List.class).map((var0) -> {
         return var0;
      });
   }

   public List<?> getList(@NotNull Route var1) {
      return (List)this.getOptionalList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<?> getList(@NotNull String var1) {
      return (List)this.getOptionalList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<?> getList(@NotNull Route var1, @Nullable List<?> var2) {
      return (List)this.getOptionalList(var1).orElse(var2);
   }

   public List<?> getList(@NotNull String var1, @Nullable List<?> var2) {
      return (List)this.getOptionalList(var1).orElse(var2);
   }

   public boolean isList(@NotNull Route var1) {
      return this.get(var1) instanceof List;
   }

   public boolean isList(@NotNull String var1) {
      return this.get(var1) instanceof List;
   }

   public Optional<List<String>> getOptionalStringList(@NotNull Route var1) {
      return ListConversions.toStringList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<String>> getOptionalStringList(@NotNull String var1) {
      return ListConversions.toStringList(this.getList((String)var1, (List)null));
   }

   public List<String> getStringList(@NotNull Route var1, @Nullable List<String> var2) {
      return (List)this.getOptionalStringList(var1).orElse(var2);
   }

   public List<String> getStringList(@NotNull String var1, @Nullable List<String> var2) {
      return (List)this.getOptionalStringList(var1).orElse(var2);
   }

   public List<String> getStringList(@NotNull Route var1) {
      return (List)this.getOptionalStringList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getStringList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<String> getStringList(@NotNull String var1) {
      return (List)this.getOptionalStringList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getStringList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Integer>> getOptionalIntList(@NotNull Route var1) {
      return ListConversions.toIntList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Integer>> getOptionalIntList(@NotNull String var1) {
      return ListConversions.toIntList(this.getList((String)var1, (List)null));
   }

   public List<Integer> getIntList(@NotNull Route var1, @Nullable List<Integer> var2) {
      return (List)this.getOptionalIntList(var1).orElse(var2);
   }

   public List<Integer> getIntList(@NotNull String var1, @Nullable List<Integer> var2) {
      return (List)this.getOptionalIntList(var1).orElse(var2);
   }

   public List<Integer> getIntList(@NotNull Route var1) {
      return (List)this.getOptionalIntList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getIntList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Integer> getIntList(@NotNull String var1) {
      return (List)this.getOptionalIntList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getIntList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<BigInteger>> getOptionalBigIntList(@NotNull Route var1) {
      return ListConversions.toBigIntList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<BigInteger>> getOptionalBigIntList(@NotNull String var1) {
      return ListConversions.toBigIntList(this.getList((String)var1, (List)null));
   }

   public List<BigInteger> getBigIntList(@NotNull Route var1, @Nullable List<BigInteger> var2) {
      return (List)this.getOptionalBigIntList(var1).orElse(var2);
   }

   public List<BigInteger> getBigIntList(@NotNull String var1, @Nullable List<BigInteger> var2) {
      return (List)this.getOptionalBigIntList(var1).orElse(var2);
   }

   public List<BigInteger> getBigIntList(@NotNull Route var1) {
      return (List)this.getOptionalBigIntList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBigIntList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<BigInteger> getBigIntList(@NotNull String var1) {
      return (List)this.getOptionalBigIntList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getBigIntList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Byte>> getOptionalByteList(@NotNull Route var1) {
      return ListConversions.toByteList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Byte>> getOptionalByteList(@NotNull String var1) {
      return ListConversions.toByteList(this.getList((String)var1, (List)null));
   }

   public List<Byte> getByteList(@NotNull Route var1, @Nullable List<Byte> var2) {
      return (List)this.getOptionalByteList(var1).orElse(var2);
   }

   public List<Byte> getByteList(@NotNull String var1, @Nullable List<Byte> var2) {
      return (List)this.getOptionalByteList(var1).orElse(var2);
   }

   public List<Byte> getByteList(@NotNull Route var1) {
      return (List)this.getOptionalByteList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getByteList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Byte> getByteList(@NotNull String var1) {
      return (List)this.getOptionalByteList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getByteList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Long>> getOptionalLongList(@NotNull Route var1) {
      return ListConversions.toLongList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Long>> getOptionalLongList(@NotNull String var1) {
      return ListConversions.toLongList(this.getList((String)var1, (List)null));
   }

   public List<Long> getLongList(@NotNull Route var1, @Nullable List<Long> var2) {
      return (List)this.getOptionalLongList(var1).orElse(var2);
   }

   public List<Long> getLongList(@NotNull String var1, @Nullable List<Long> var2) {
      return (List)this.getOptionalLongList(var1).orElse(var2);
   }

   public List<Long> getLongList(@NotNull Route var1) {
      return (List)this.getOptionalLongList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getLongList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Long> getLongList(@NotNull String var1) {
      return (List)this.getOptionalLongList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getLongList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Double>> getOptionalDoubleList(@NotNull Route var1) {
      return ListConversions.toDoubleList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Double>> getOptionalDoubleList(@NotNull String var1) {
      return ListConversions.toDoubleList(this.getList((String)var1, (List)null));
   }

   public List<Double> getDoubleList(@NotNull Route var1, @Nullable List<Double> var2) {
      return (List)this.getOptionalDoubleList(var1).orElse(var2);
   }

   public List<Double> getDoubleList(@NotNull String var1, @Nullable List<Double> var2) {
      return (List)this.getOptionalDoubleList(var1).orElse(var2);
   }

   public List<Double> getDoubleList(@NotNull Route var1) {
      return (List)this.getOptionalDoubleList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getDoubleList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Double> getDoubleList(@NotNull String var1) {
      return (List)this.getOptionalDoubleList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getDoubleList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Float>> getOptionalFloatList(@NotNull Route var1) {
      return ListConversions.toFloatList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Float>> getOptionalFloatList(@NotNull String var1) {
      return ListConversions.toFloatList(this.getList((String)var1, (List)null));
   }

   public List<Float> getFloatList(@NotNull Route var1, @Nullable List<Float> var2) {
      return (List)this.getOptionalFloatList(var1).orElse(var2);
   }

   public List<Float> getFloatList(@NotNull String var1, @Nullable List<Float> var2) {
      return (List)this.getOptionalFloatList(var1).orElse(var2);
   }

   public List<Float> getFloatList(@NotNull Route var1) {
      return (List)this.getOptionalFloatList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getFloatList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Float> getFloatList(@NotNull String var1) {
      return (List)this.getOptionalFloatList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getFloatList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Short>> getOptionalShortList(@NotNull Route var1) {
      return ListConversions.toShortList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Short>> getOptionalShortList(@NotNull String var1) {
      return ListConversions.toShortList(this.getList((String)var1, (List)null));
   }

   public List<Short> getShortList(@NotNull Route var1, @Nullable List<Short> var2) {
      return (List)this.getOptionalShortList(var1).orElse(var2);
   }

   public List<Short> getShortList(@NotNull String var1, @Nullable List<Short> var2) {
      return (List)this.getOptionalShortList(var1).orElse(var2);
   }

   public List<Short> getShortList(@NotNull Route var1) {
      return (List)this.getOptionalShortList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getShortList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Short> getShortList(@NotNull String var1) {
      return (List)this.getOptionalShortList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getShortList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public Optional<List<Map<?, ?>>> getOptionalMapList(@NotNull Route var1) {
      return ListConversions.toMapList(this.getList((Route)var1, (List)null));
   }

   public Optional<List<Map<?, ?>>> getOptionalMapList(@NotNull String var1) {
      return ListConversions.toMapList(this.getList((String)var1, (List)null));
   }

   public List<Map<?, ?>> getMapList(@NotNull Route var1, @Nullable List<Map<?, ?>> var2) {
      return (List)this.getOptionalMapList(var1).orElse(var2);
   }

   public List<Map<?, ?>> getMapList(@NotNull String var1, @Nullable List<Map<?, ?>> var2) {
      return (List)this.getOptionalMapList(var1).orElse(var2);
   }

   public List<Map<?, ?>> getMapList(@NotNull Route var1) {
      return (List)this.getOptionalMapList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getMapList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   public List<Map<?, ?>> getMapList(@NotNull String var1) {
      return (List)this.getOptionalMapList(var1).orElseGet(() -> {
         return this.canUseDefaults() ? this.defaults.getMapList(var1) : this.root.getGeneralSettings().getDefaultList();
      });
   }

   private static class BlockReference {
      @NotNull
      private final Section parent;
      @NotNull
      private final Object key;

      private BlockReference(@NotNull Section var1, @NotNull Object var2) {
         this.parent = var1;
         this.key = var2;
      }

      // $FF: synthetic method
      BlockReference(Section var1, Object var2, Object var3) {
         this(var1, var2);
      }
   }
}
