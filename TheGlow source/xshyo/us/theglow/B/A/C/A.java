package xshyo.us.theglow.B.A.C;

import java.util.Collections;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.config.block.implementation.Section;

public class A extends xshyo.us.theglow.B.A.A {
   private final TheGlow A = TheGlow.getInstance();

   public ItemStack B(Player var1) {
      Section var2 = this.A.getMenus().getSection("inventories.glowing.items.unEquip");
      return this.A(var2, var1);
   }

   public void A(Player var1, int var2, ClickType var3, int var4) {
      PlayerGlowData var5 = this.A.getDatabase().B(var1.getUniqueId());
      CurrentGlow var6 = var5 != null ? var5.getCurrentGlow() : null;
      if (var6 != null) {
         if (var6.getGlowName().isEmpty()) {
            xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.UN_EQUIP_EMPTY");
         } else {
            var6.setGlowName("");
            var6.setColorList(Collections.emptyList());
            this.A.getDatabase().C(var5.getUuid()).thenRun(() -> {
               Bukkit.getScheduler().runTask(this.A, () -> {
                  this.A.getGlowManager().A(var1);
                  xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.UN_EQUIP");
               });
            });
            this.A(var1);
         }
      }
   }
}
