package xshyo.us.theglow.C.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class A implements CommandArg {
   private static final String Q = "theglow.info";
   private final TheGlow P = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("info");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.info");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (var2.length < 2) {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.INFO_USAGE");
         return true;
      } else if (!xshyo.us.theglow.B.A.A(var1, "theglow.info")) {
         return true;
      } else {
         String var3 = var2[1];
         this.P.getDatabase().A(var3).thenAccept((var2x) -> {
            if (var2x == null) {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.INFO_DATA_NOT_FOUND", var3);
            } else {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.INFO", var3, var2x.getCurrentGlow().getGlowName(), var2x.getCurrentGlow().getColorList(), var2x.getCurrentGlow().getEnable(), var2x.getMenuData().getOrder(), var2x.getMenuData().getFilter());
            }
         }).exceptionally((var0) -> {
            var0.printStackTrace();
            return null;
         });
         return true;
      }
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      ArrayList var4 = new ArrayList();
      if (var3.length != 2) {
         return (List)StringUtil.copyPartialMatches(var3[var3.length - 1], var4, new ArrayList());
      } else {
         Iterator var5 = Bukkit.getOnlinePlayers().iterator();

         while(var5.hasNext()) {
            Player var6 = (Player)var5.next();
            var4.add(var6.getName());
         }

         return (List)StringUtil.copyPartialMatches(var3[1], var4, new ArrayList());
      }
   }
}
