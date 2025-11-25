package xshyo.us.theglow.libs.guis.builder.gui;

import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.GuiType;
import xshyo.us.theglow.libs.guis.components.util.Legacy;
import xshyo.us.theglow.libs.guis.guis.Gui;

public final class SimpleBuilder extends BaseGuiBuilder<Gui, SimpleBuilder> {
   private GuiType guiType;

   public SimpleBuilder(@NotNull GuiType var1) {
      this.guiType = var1;
   }

   @NotNull
   @Contract("_ -> this")
   public SimpleBuilder type(@NotNull GuiType var1) {
      this.guiType = var1;
      return this;
   }

   @NotNull
   @Contract(" -> new")
   public Gui create() {
      String var2 = Legacy.SERIALIZER.serialize(this.getTitle());
      Gui var1;
      if (this.guiType != null && this.guiType != GuiType.CHEST) {
         var1 = new Gui(this.guiType, var2, this.getModifiers());
      } else {
         var1 = new Gui(this.getRows(), var2, this.getModifiers());
      }

      Consumer var3 = this.getConsumer();
      if (var3 != null) {
         var3.accept(var1);
      }

      return var1;
   }
}
