package xshyo.us.theglow.libs.theAPI.actions.handler;

import java.util.Objects;
import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.schedulers.TaskScheduler;

public class CloseActionHandler implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         TaskScheduler var10000;
         if (var3 > 0) {
            var10000 = TheAPI.getInstance().getScheduler();
            Objects.requireNonNull(var1);
            var10000.runTaskLater(var1::closeInventory, (long)var3);
         } else {
            var10000 = TheAPI.getInstance().getScheduler();
            Objects.requireNonNull(var1);
            var10000.runTask(var1::closeInventory);
         }

      }
   }
}
