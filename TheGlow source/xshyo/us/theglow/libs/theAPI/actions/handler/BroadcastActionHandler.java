package xshyo.us.theglow.libs.theAPI.actions.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class BroadcastActionHandler implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         if (var3 > 0) {
            TheAPI.getInstance().getScheduler().runTaskLater(() -> {
               Bukkit.broadcastMessage(Utils.translate(var2));
            }, (long)var3);
         } else {
            TheAPI.getInstance().getScheduler().runTask(() -> {
               Bukkit.broadcastMessage(Utils.translate(var2));
            });
         }

      }
   }
}
