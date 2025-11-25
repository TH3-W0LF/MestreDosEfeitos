package xshyo.us.theglow.libs.guis.builder.gui;

import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.util.Legacy;
import xshyo.us.theglow.libs.guis.guis.PaginatedGui;

public class PaginatedBuilder extends BaseGuiBuilder<PaginatedGui, PaginatedBuilder> {
   private int pageSize = 0;

   @NotNull
   @Contract("_ -> this")
   public PaginatedBuilder pageSize(int var1) {
      this.pageSize = var1;
      return this;
   }

   @NotNull
   @Contract(" -> new")
   public PaginatedGui create() {
      PaginatedGui var1 = new PaginatedGui(this.getRows(), this.pageSize, Legacy.SERIALIZER.serialize(this.getTitle()), this.getModifiers());
      Consumer var2 = this.getConsumer();
      if (var2 != null) {
         var2.accept(var1);
      }

      return var1;
   }
}
