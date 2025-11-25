package xshyo.us.theglow.C.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class F implements CommandArg {
   private static final String J = "theglow.unset";
   private final TheGlow K = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("unset");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.unset");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (!xshyo.us.theglow.B.A.A(var1, "theglow.unset")) {
         return true;
      } else if (var2.length < 2) {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET_USAGE");
         return true;
      } else {
         Player var3 = Bukkit.getPlayer(var2[1]);
         if (var3 == null) {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.UNSET_INVALID_PLAYER", var2[1]);
            return true;
         } else {
            if (this.K.getGlowManager().B(var3)) {
               PlayerGlowData var4 = this.K.getDatabase().B(var3.getUniqueId());
               if (var4 == null) {
                  return true;
               }

               CurrentGlow var5 = var4.getCurrentGlow();
               if (var5 == null) {
                  return true;
               }

               var5.setGlowName("");
               var5.setColorList(Collections.emptyList());
               var5.setEnable(true);
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.UNSET", var3.getName());
               this.K.getDatabase().C(var4.getUuid()).thenRun(() -> {
                  Bukkit.getScheduler().runTask(this.K, () -> {
                     this.K.getGlowManager().A(var3);
                  });
               });
            } else {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.UNSET_NO_GLOW", var3.getName());
            }

            return true;
         }
      }
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      ArrayList var4 = new ArrayList();
      if (var3.length == 2) {
         List var5 = (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
         StringUtil.copyPartialMatches(var3[1], var5, var4);
      }

      Collections.sort(var4);
      return var4;
   }
}
