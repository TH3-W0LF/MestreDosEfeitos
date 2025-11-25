package xshyo.us.theglow.C.A;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class E implements CommandArg {
   private static final String I = "theglow.help";
   private final TheGlow H = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("help");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.help");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (this.isPlayer(var1)) {
         if (!xshyo.us.theglow.B.A.A(var1, "theglow.help")) {
            return true;
         }

         if (var2.length < 2) {
            TextComponent var5 = this.A(1);
            if (var5 != null) {
               var1.spigot().sendMessage(var5);
            }

            return true;
         }

         if (this.isNumber(var2[1])) {
            int var3 = Integer.parseInt(var2[1]);
            if (var3 <= 0) {
               xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.HELP_USAGE");
               return true;
            }

            TextComponent var4 = this.A(var3);
            if (var4 == null) {
               return true;
            }

            var1.spigot().sendMessage(var4);
         } else {
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.HELP_USAGE");
         }
      } else {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.HELP_CONSOLE");
      }

      return true;
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      return null;
   }

   private TextComponent A(int var1) {
      List var2 = this.H.getLang().getStringList("MESSAGES.COMMANDS.HELP");
      int var3 = (int)Math.ceil((double)var2.size() / 10.0D);
      if (var1 <= var3 && var1 > 0) {
         String var4 = this.H.getConf().getString("config.command.default.name");
         String var5 = this.H.getConf().getString("config.command.shortened-open-command.name");
         int var6 = (var1 - 1) * 10;
         int var7 = Math.min(var6 + 10, var2.size());
         TextComponent var8 = new TextComponent("");
         PluginDescriptionFile var9 = this.H.getDescription();
         String var10 = var9.getVersion();
         TextComponent var11 = new TextComponent("======= Help Page " + var1 + " / " + var3 + " =======\n The Glow " + var10 + "\n \n");
         var11.setColor(ChatColor.GOLD.asBungee());
         var8.addExtra(var11);

         TextComponent var14;
         for(int var12 = var6; var12 < var7; ++var12) {
            String var13 = (String)var2.get(var12);
            var13 = var13.replace("{cmd}", var4);
            var13 = var13.replace("{shortenedcmd}", var5);
            var14 = new TextComponent(Utils.translate(var13) + "\n");
            var14.setColor(ChatColor.WHITE.asBungee());
            var8.addExtra(var14);
         }

         TextComponent var15 = new TextComponent("");
         TextComponent var16;
         if (var1 > 1) {
            var16 = new TextComponent("<< Previous ");
            var16.setColor(ChatColor.RED.asBungee());
            var16.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + var4 + " help " + (var1 - 1)));
            var15.addExtra(var16);
         }

         if (var1 < var3) {
            var16 = new TextComponent("Next >>");
            var16.setColor(ChatColor.GREEN.asBungee());
            var16.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/" + var4 + " help " + (var1 + 1)));
            if (var15.getExtra() != null && !var15.getExtra().isEmpty()) {
               var14 = new TextComponent(" ");
               var15.addExtra(var14);
            }

            var15.addExtra(var16);
         }

         if (var15.getExtra() != null && !var15.getExtra().isEmpty()) {
            var8.addExtra("\n");
            var8.addExtra(var15);
         }

         return var8;
      } else {
         return null;
      }
   }

   public boolean isPlayer(CommandSender var1) {
      return var1 instanceof Player;
   }

   public boolean isNumber(String var1) {
      Pattern var2 = Pattern.compile("-?\\d+(\\.\\d+)?");
      return var2.matcher(var1).matches();
   }
}
