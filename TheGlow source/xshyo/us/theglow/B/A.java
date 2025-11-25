package xshyo.us.theglow.B;

import com.google.common.primitives.Ints;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public final class A {
   public static boolean A(String var0) {
      try {
         ChatColor.valueOf(var0.toUpperCase());
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   public static boolean A(CommandSender var0, String var1) {
      if (!var0.isOp() && !var0.hasPermission(var1) && !var0.hasPermission("theglow.*")) {
         if (TheGlow.getInstance().getLang().contains("MESSAGES.COMMANDS.NOPERMS")) {
            var0.sendMessage(Utils.translate(TheGlow.getInstance().getLang().getString("MESSAGES.COMMANDS.NOPERMS").replace("{1}", var0.getName()).replace("{2}", var1)));
         } else {
            var0.sendMessage(Utils.translate("&cYou don't have permission to execute this command. Required: " + var1));
            Bukkit.getLogger().warning("Path 'MESSAGES.COMMANDS.NOPERMS' was not found in the configuration file.");
         }

         if (TheGlow.getInstance().getLang().contains("MESSAGES.COMMANDS.NOPERMS_LOGGER")) {
            Bukkit.getLogger().info(Utils.translate(TheGlow.getInstance().getLang().getString("MESSAGES.COMMANDS.NOPERMS_LOGGER").replace("{1}", var0.getName()).replace("{2}", var1)));
         } else {
            Bukkit.getLogger().info("Player " + var0.getName() + " tried to use a command without having the permission: " + var1);
            Bukkit.getLogger().warning("Path 'MESSAGES.COMMANDS.NOPERMS_LOGGER' was not found in the configuration file.");
         }

         return false;
      } else {
         return true;
      }
   }

   public static void A(Player var0, String var1, Object... var2) {
      String var3 = TheGlow.getInstance().getLang().getString(var1);
      if (var3 == null) {
         var0.sendRawMessage(Utils.translate("&cError: Message with key '" + var1 + "' was not found in the configuration."));
         Bukkit.getLogger().warning("Message with key '" + var1 + "' was not found in the configuration.");
      } else if (!var3.isEmpty()) {
         String var5;
         if (var2.length > 0) {
            for(int var4 = 0; var4 < var2.length; ++var4) {
               var5 = "{" + (var4 + 1) + "}";
               var3 = var3.replace(var5, var2[var4].toString());
            }
         }

         String var6 = null;
         if (TheGlow.getInstance().getConf().contains("config.command.default.name")) {
            var6 = TheGlow.getInstance().getConf().getString("config.command.default.name");
         } else {
            var6 = "glow";
            Bukkit.getLogger().warning("Path 'config.command.default.name' was not found in the configuration.");
         }

         var5 = null;
         if (TheGlow.getInstance().getConf().contains("config.command.shortened-open-command.name")) {
            var5 = TheGlow.getInstance().getConf().getString("config.command.shortened-open-command.name");
         } else {
            var5 = "g";
            Bukkit.getLogger().warning("Path 'config.command.shortened-open-command.name' was not found in the configuration.");
         }

         var3 = var3.replace("{cmd}", var6);
         var3 = var3.replace("{shortenedcmd}", var5);
         var0.sendRawMessage(Utils.translate(var3));
      }
   }

   public static void A(CommandSender var0, String var1, Object... var2) {
      String var3 = TheGlow.getInstance().getLang().getString(var1);
      if (var3 == null) {
         var0.sendMessage(Utils.translate("&cError: Message with key '" + var1 + "' was not found in the configuration."));
         Bukkit.getLogger().warning("Message with key '" + var1 + "' was not found in the configuration.");
      } else if (!var3.isEmpty()) {
         String var5;
         if (var2.length > 0) {
            for(int var4 = 0; var4 < var2.length; ++var4) {
               var5 = "{" + (var4 + 1) + "}";
               var3 = var3.replace(var5, var2[var4].toString());
            }
         }

         String var6 = null;
         if (TheGlow.getInstance().getConf().contains("config.command.default.name")) {
            var6 = TheGlow.getInstance().getConf().getString("config.command.default.name");
         } else {
            var6 = "glow";
            Bukkit.getLogger().warning("Path 'config.command.default.name' was not found in the configuration.");
         }

         var5 = null;
         if (TheGlow.getInstance().getConf().contains("config.command.shortened-open-command.name")) {
            var5 = TheGlow.getInstance().getConf().getString("config.command.shortened-open-command.name");
         } else {
            var5 = "g";
            Bukkit.getLogger().warning("Path 'config.command.shortened-open-command.name' was not found in the configuration.");
         }

         var3 = var3.replace("{cmd}", var6);
         var3 = var3.replace("{shortenedcmd}", var5);
         var0.sendMessage(Utils.translate(var3));
      }
   }

   public static ChatColor A(Player var0) {
      Scoreboard var1 = var0.getScoreboard();
      Iterator var2 = var1.getTeams().iterator();

      Team var3;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         var3 = (Team)var2.next();
      } while(!var3.hasEntry(var0.getName()) || !var3.getName().startsWith("TG_"));

      String var4 = var3.getName().replace("TG_", "");

      try {
         return ChatColor.valueOf(var4);
      } catch (IllegalArgumentException var6) {
         return null;
      }
   }

   public static void A(TheGlow var0, Player var1, StringPrompt var2, Map<Object, Object> var3) {
      Conversation var4 = (new ConversationFactory(var0)).withFirstPrompt(var2).withInitialSessionData(var3).withLocalEcho(TheGlow.getInstance().getConf().getBoolean("config.prompt.localEcho")).withTimeout(TheGlow.getInstance().getConf().getInt("config.prompt.timeout")).buildConversation(var1);
      var4.begin();
   }

   public static int A() {
      Matcher var0 = Pattern.compile("(?<version>\\d+\\.\\d+)(?<patch>\\.\\d+)?").matcher(Bukkit.getBukkitVersion());
      StringBuilder var1 = new StringBuilder();
      if (var0.find()) {
         var1.append(var0.group("version").replace(".", ""));
         String var2 = var0.group("patch");
         if (var2 == null) {
            var1.append("0");
         } else {
            var1.append(var2.replace(".", ""));
         }
      }

      Integer var3 = Ints.tryParse(var1.toString());
      if (var3 == null) {
         throw new RuntimeException("Could not retrieve server version!");
      } else {
         return var3;
      }
   }

   private A() {
      throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
   }
}
