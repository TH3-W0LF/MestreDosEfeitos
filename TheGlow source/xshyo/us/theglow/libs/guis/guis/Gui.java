package xshyo.us.theglow.libs.guis.guis;

import java.util.Set;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.builder.gui.PaginatedBuilder;
import xshyo.us.theglow.libs.guis.builder.gui.ScrollingBuilder;
import xshyo.us.theglow.libs.guis.builder.gui.SimpleBuilder;
import xshyo.us.theglow.libs.guis.builder.gui.StorageBuilder;
import xshyo.us.theglow.libs.guis.components.GuiType;
import xshyo.us.theglow.libs.guis.components.InteractionModifier;
import xshyo.us.theglow.libs.guis.components.ScrollType;

public class Gui extends BaseGui {
   public Gui(int var1, @NotNull String var2, @NotNull Set<InteractionModifier> var3) {
      super(var1, var2, var3);
   }

   public Gui(@NotNull GuiType var1, @NotNull String var2, @NotNull Set<InteractionModifier> var3) {
      super(var1, var2, var3);
   }

   @Deprecated
   public Gui(int var1, @NotNull String var2) {
      super(var1, var2);
   }

   @Deprecated
   public Gui(@NotNull String var1) {
      super(1, var1);
   }

   @Deprecated
   public Gui(@NotNull GuiType var1, @NotNull String var2) {
      super(var1, var2);
   }

   @NotNull
   @Contract("_ -> new")
   public static SimpleBuilder gui(@NotNull GuiType var0) {
      return new SimpleBuilder(var0);
   }

   @NotNull
   @Contract(" -> new")
   public static SimpleBuilder gui() {
      return gui(GuiType.CHEST);
   }

   @NotNull
   @Contract(" -> new")
   public static StorageBuilder storage() {
      return new StorageBuilder();
   }

   @NotNull
   @Contract(" -> new")
   public static PaginatedBuilder paginated() {
      return new PaginatedBuilder();
   }

   @NotNull
   @Contract("_ -> new")
   public static ScrollingBuilder scrolling(@NotNull ScrollType var0) {
      return new ScrollingBuilder(var0);
   }

   @NotNull
   @Contract(" -> new")
   public static ScrollingBuilder scrolling() {
      return scrolling(ScrollType.VERTICAL);
   }
}
