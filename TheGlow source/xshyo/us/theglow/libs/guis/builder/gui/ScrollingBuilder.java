package xshyo.us.theglow.libs.guis.builder.gui;

import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.ScrollType;
import xshyo.us.theglow.libs.guis.components.util.Legacy;
import xshyo.us.theglow.libs.guis.guis.ScrollingGui;

public final class ScrollingBuilder extends BaseGuiBuilder<ScrollingGui, ScrollingBuilder> {
   private ScrollType scrollType;
   private int pageSize = 0;

   public ScrollingBuilder(@NotNull ScrollType var1) {
      this.scrollType = var1;
   }

   @NotNull
   @Contract("_ -> this")
   public ScrollingBuilder scrollType(@NotNull ScrollType var1) {
      this.scrollType = var1;
      return this;
   }

   @NotNull
   @Contract("_ -> this")
   public ScrollingBuilder pageSize(int var1) {
      this.pageSize = var1;
      return this;
   }

   @NotNull
   @Contract(" -> new")
   public ScrollingGui create() {
      ScrollingGui var1 = new ScrollingGui(this.getRows(), this.pageSize, Legacy.SERIALIZER.serialize(this.getTitle()), this.scrollType, this.getModifiers());
      Consumer var2 = this.getConsumer();
      if (var2 != null) {
         var2.accept(var1);
      }

      return var1;
   }
}
