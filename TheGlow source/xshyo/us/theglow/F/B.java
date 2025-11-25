package xshyo.us.theglow.F;

import org.bukkit.entity.Player;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.kyori.adventure.text.minimessage.MiniMessage;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;

public class B implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var3 > 0) {
         TheAPI.getInstance().getScheduler().runTaskLater(() -> {
            TheGlow.getInstance().adventure().player(var1).sendMessage(MiniMessage.miniMessage().deserialize(var2));
         }, (long)var3);
      } else {
         TheAPI.getInstance().getScheduler().runTask(() -> {
            TheGlow.getInstance().adventure().player(var1).sendMessage(MiniMessage.miniMessage().deserialize(var2));
         });
      }

   }
}
