package xshyo.us.theglow.B.A.C;

import java.util.List;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.config.block.implementation.Section;

public class D extends xshyo.us.theglow.B.A.A {
   private final TheGlow I = TheGlow.getInstance();
   private final String J;

   public ItemStack B(Player var1) {
      Section var2 = this.I.getMenus().getSection(this.J);
      return this.A(var2, var1);
   }

   public void A(Player var1, int var2, ClickType var3, int var4) {
      List var5 = this.I.getMenus().getStringList(this.J + ".actions");
      if (var5 != null && !var5.isEmpty()) {
         this.A(var5, var1);
      }
   }

   private void A(List<String> var1, Player var2) {
      this.I.getActionExecutor().finalExecuteActions(var2, var1);
   }

   public D(String var1) {
      this.J = var1;
   }
}
