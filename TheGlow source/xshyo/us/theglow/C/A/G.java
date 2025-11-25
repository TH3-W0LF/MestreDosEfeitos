package xshyo.us.theglow.C.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class G implements CommandArg {
   private static final String M = "theglow.removeall";
   private final TheGlow L = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("removeall");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.removeall");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (this.isPlayer(var1)) {
         if (!xshyo.us.theglow.B.A.A(var1, "theglow.removeall")) {
            return true;
         }

         if (var2.length < 2) {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.REMOVEALL_USAGE");
            return false;
         }

         xshyo.us.theglow.B.A.A(this.L, (Player)var1, new xshyo.us.theglow.B.A.A.A(), Map.of("targetName", var2[1]));
      } else {
         if (var2.length < 2) {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.REMOVEALL_USAGE");
            return false;
         }

         String var3 = var2[1];
         this.L.getDatabase().A(var3).thenAccept((var3x) -> {
            if (var3x == null) {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.REMOVEALL_INVALID_DATA", var3);
            } else {
               Player var4 = Bukkit.getPlayer(var3);
               if (var4 != null && var4.isOnline() && this.L.getGlowManager().B(var4)) {
                  this.L.getGlowManager().A(var4);
               }

               this.L.getDatabase().A.put(var3x.getUuid(), new PlayerGlowData(var3x.getUuid(), var3x.getName()));
               this.L.getDatabase().C(var3x.getUuid());
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.REMOVEALL", var3);
            }
         }).exceptionally((var0) -> {
            var0.printStackTrace();
            return null;
         });
      }

      return false;
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      ArrayList var4 = new ArrayList();
      Iterator var5 = Bukkit.getOnlinePlayers().iterator();

      while(var5.hasNext()) {
         Player var6 = (Player)var5.next();
         var4.add(var6.getName());
      }

      return (List)StringUtil.copyPartialMatches(var3[1], var4, new ArrayList());
   }
}
