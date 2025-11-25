package xshyo.us.theglow.libs.theAPI.actions.handler;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;

public class SoundActionHandler implements ActionHandler {
   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         String[] var4 = var2.split(";");
         String var5;
         float var6;
         float var7;
         if (var4.length >= 1) {
            var5 = var4[0];
            if (var4.length >= 2) {
               var6 = Float.parseFloat(var4[1]);
            } else {
               var6 = 1.0F;
            }

            if (var4.length >= 3) {
               var7 = Float.parseFloat(var4[2]);
            } else {
               var7 = 1.0F;
            }
         } else {
            var6 = 1.0F;
            var7 = 1.0F;
            var5 = "BLOCK_NOTE_BLOCK_HARP";
         }

         if (var3 > 0) {
            TheAPI.getInstance().getScheduler().runTaskLater(() -> {
               var1.playSound(var1.getLocation(), Sound.valueOf(var5), var7, var6);
            }, (long)var3);
         } else {
            TheAPI.getInstance().getScheduler().runTask(() -> {
               var1.playSound(var1.getLocation(), Sound.valueOf(var5), var7, var6);
            });
         }

      }
   }
}
