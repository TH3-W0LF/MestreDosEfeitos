package xshyo.us.theglow.C.A;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class B implements CommandArg {
   private static final String C = "theglow.openmenu";
   private final TheGlow A = TheGlow.getInstance();
   private static final List<String> B = Arrays.asList("glow");

   public List<String> getNames() {
      return Collections.singletonList("openmenu");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.openmenu");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (!xshyo.us.theglow.B.A.A(var1, "theglow.openmenu")) {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.NOPERMS");
         return true;
      } else if (var2.length < 3) {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.OPEN_MENU_USAGE");
         return true;
      } else {
         String var3 = var2[1];
         String var4 = var2[2];
         Player var5 = Bukkit.getPlayer(var3);
         if (var5 != null && var5.isOnline()) {
            if (!B.contains(var4.toLowerCase())) {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.OPEN_MENU_NOT_FOUND", var4);
               return true;
            } else {
               this.A(var5, var4);
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.OPEN_MENU", var4, var3);
               return true;
            }
         } else {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.OPEN_MENU_TARGET_INVALID");
            return true;
         }
      }
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      if (var3.length == 2) {
         return (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).filter((var1x) -> {
            return var1x.toLowerCase().startsWith(var3[1].toLowerCase());
         }).collect(Collectors.toList());
      } else {
         return var3.length == 3 ? (List)B.stream().filter((var1x) -> {
            return var1x.toLowerCase().startsWith(var3[2].toLowerCase());
         }).collect(Collectors.toList()) : Collections.emptyList();
      }
   }

   private void A(Player var1, String var2) {
      PlayerGlowData var3 = this.A.getDatabase().B(var1.getUniqueId());
      if (var3 != null) {
         String var4 = var2.toLowerCase();
         byte var5 = -1;
         switch(var4.hashCode()) {
         case 3175821:
            if (var4.equals("glow")) {
               var5 = 0;
            }
         default:
            switch(var5) {
            case 0:
               (new xshyo.us.theglow.A.A(var1, var3.getMenuData().getOrder(), var3.getMenuData().getFilter(), var3)).A(1);
            default:
            }
         }
      }
   }
}
