package xshyo.us.theglow.C.A;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class C implements CommandArg {
   private static final String E = "theglow.toggle";
   private final TheGlow D = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("toggle");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.toggle");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (!xshyo.us.theglow.B.A.A(var1, "theglow.toggle")) {
         return true;
      } else {
         Player var3;
         if (var2.length > 1) {
            var3 = this.D.getServer().getPlayer(var2[1]);
            if (var3 == null) {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.PLAYER_NOT_FOUND");
               return true;
            }
         } else {
            if (!(var1 instanceof Player)) {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.TOGGLE_NO_PLAYER");
               return true;
            }

            var3 = (Player)var1;
         }

         CurrentGlow var4 = this.D.getDatabase().B(var3.getUniqueId()).getCurrentGlow();
         if (var4 != null && !var4.getGlowName().isEmpty()) {
            if (var4.getEnable() && this.D.getGlowManager().B(var3)) {
               this.D.getGlowManager().A(var3);
               var4.setEnable(false);
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.TOGGLE", "&cOFF");
            } else {
               this.D.getGlowManager().A(var3, var4.getColorList(), 20L, false);
               var4.setEnable(true);
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.TOGGLE", "&aON");
            }

            this.D.getDatabase().C(var3.getUniqueId());
            return true;
         } else {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.TOGGLE_EMPTY");
            return true;
         }
      }
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      return null;
   }
}
