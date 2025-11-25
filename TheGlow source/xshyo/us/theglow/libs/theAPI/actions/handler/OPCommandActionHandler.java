package xshyo.us.theglow.libs.theAPI.actions.handler;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.ActionHandler;

public class OPCommandActionHandler implements ActionHandler {
   private static final Logger LOGGER = TheAPI.getInstance().getLogger();

   public void execute(Player var1, String var2, int var3) {
      if (var1 != null && var1.isOnline()) {
         if (var3 > 0) {
            TheAPI.getInstance().getScheduler().runTaskLater(() -> {
               this.executeAsOP(var1, var2);
            }, (long)var3);
         } else {
            TheAPI.getInstance().getScheduler().runTask(() -> {
               this.executeAsOP(var1, var2);
            });
         }

      }
   }

   private void executeAsOP(Player var1, String var2) {
      boolean var3 = var1.isOp();
      LOGGER.warning("⚠️ The plugin is temporarily granting OP to the player " + var1.getName() + ". This action is NOT recommended unless absolutely necessary.");

      try {
         if (!var3) {
            LOGGER.info("➕ Granting temporary OP to player: " + var1.getName());
            var1.setOp(true);
         }

         LOGGER.info("\ud83c\udfc3 Executing command as OP: /" + var2 + " for " + var1.getName());
         var1.performCommand(var2);
      } catch (Exception var8) {
         LOGGER.log(Level.SEVERE, "❌ Failed to execute command as OP for " + var1.getName(), var8);
      } finally {
         if (!var3) {
            var1.setOp(false);
            LOGGER.info("➖ Revoking temporary OP from player: " + var1.getName());
         }

      }

   }
}
