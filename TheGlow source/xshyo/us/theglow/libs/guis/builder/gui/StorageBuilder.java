package xshyo.us.theglow.libs.guis.builder.gui;

import java.util.function.Consumer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.libs.guis.components.util.Legacy;
import xshyo.us.theglow.libs.guis.guis.StorageGui;

public final class StorageBuilder extends BaseGuiBuilder<StorageGui, StorageBuilder> {
   @NotNull
   @Contract(" -> new")
   public StorageGui create() {
      StorageGui var1 = new StorageGui(this.getRows(), Legacy.SERIALIZER.serialize(this.getTitle()), this.getModifiers());
      Consumer var2 = this.getConsumer();
      if (var2 != null) {
         var2.accept(var1);
      }

      return var1;
   }
}
