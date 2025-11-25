package xshyo.us.theglow.libs.theAPI.actions.handler;

import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class TitleActionHandler implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         String[] var4 = var2.split(";");
         String var5 = "";
         String var6 = "";
         int var7;
         int var8;
         int var9;
         if (var4.length >= 1) {
            var5 = var4[0];
            if (var4.length >= 2) {
               var6 = var4[1];
            }

            if (var4.length >= 3) {
               var7 = Integer.parseInt(var4[2]);
            } else {
               var7 = 10;
            }

            if (var4.length >= 4) {
               var8 = Integer.parseInt(var4[3]);
            } else {
               var8 = 20;
            }

            if (var4.length >= 5) {
               var9 = Integer.parseInt(var4[4]);
            } else {
               var9 = 10;
            }
         } else {
            var9 = 10;
            var8 = 20;
            var7 = 10;
         }

         if (var3 > 0) {
            TheAPI.getInstance().getScheduler().runTaskLater(() -> {
               var1.sendTitle(Utils.translate(var5), Utils.translate(var6), var7, var8, var9);
            }, (long)var3);
         } else {
            TheAPI.getInstance().getScheduler().runTask(() -> {
               var1.sendTitle(Utils.translate(var5), Utils.translate(var6), var7, var8, var9);
            });
         }

      }
   }
}
