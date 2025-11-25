package xshyo.us.theglow.C.A;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.GlowCacheData;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class D implements CommandArg {
   private static final String F = "theglow.set";
   private final TheGlow G = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("set");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.set");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (!xshyo.us.theglow.B.A.A(var1, "theglow.set")) {
         return true;
      } else if (var2.length < 3) {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET_USAGE");
         return true;
      } else {
         Player var3 = Bukkit.getPlayer(var2[1]);
         if (var3 == null) {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET_INVALID_PLAYER", var2[1]);
            return true;
         } else {
            String var4 = var2[2];
            if (var4.equalsIgnoreCase("none")) {
               this.G.getGlowManager().A(var3);
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET_NONE", var3.getName());
               return true;
            } else {
               GlowCacheData var5 = (GlowCacheData)this.G.getGlowLoad().C().get(var4);
               HashMap var6 = null;
               if (var5 != null) {
                  var6 = new HashMap();
                  var6.put("patterns", var5.getPatterns());
               }

               if (var6 == null) {
                  xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET_VALID_COLORS", var4);
                  return true;
               } else {
                  List var7 = (List)var6.get("patterns");
                  if (var7.stream().anyMatch((var0) -> {
                     return !xshyo.us.theglow.B.A.A(var0);
                  })) {
                     xshyo.us.theglow.B.A.A(var1, "MESSAGES.GUI.INVALID_COLOR");
                     return true;
                  } else {
                     PlayerGlowData var8 = this.G.getDatabase().B(var3.getUniqueId());
                     if (var8 == null) {
                        return true;
                     } else {
                        CurrentGlow var9 = var8.getCurrentGlow();
                        if (var9 == null) {
                           return true;
                        } else if (var9.getGlowName().equals(var4)) {
                           xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET_ALREADY", var3.getName(), var4);
                           return true;
                        } else {
                           var9.setColorList(var7);
                           var9.setGlowName(var4);
                           var9.setEnable(true);
                           this.G.getDatabase().C(var8.getUuid()).thenRun(() -> {
                              Bukkit.getScheduler().runTask(this.G, () -> {
                                 this.G.getGlowManager().A(var3);
                                 this.G.getGlowManager().A(var3, var7, 20L, false);
                                 xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SET", var3.getName(), var4);
                              });
                           });
                           return true;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      ArrayList var4 = new ArrayList();
      if (var3.length == 2) {
         List var5 = (List)Bukkit.getOnlinePlayers().stream().map(Player::getName).collect(Collectors.toList());
         StringUtil.copyPartialMatches(var3[1], var5, var4);
      }

      if (var3.length == 3) {
         ArrayList var6 = new ArrayList(this.G.getGlowLoad().C().keySet());
         StringUtil.copyPartialMatches(var3[2], var6, var4);
      }

      Collections.sort(var4);
      return var4;
   }
}
