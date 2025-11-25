package xshyo.us.theglow;

import java.util.List;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import xshyo.us.theglow.C.A.C;
import xshyo.us.theglow.C.A.D;
import xshyo.us.theglow.C.A.E;
import xshyo.us.theglow.C.A.F;
import xshyo.us.theglow.C.A.G;
import xshyo.us.theglow.C.A.H;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;
import xshyo.us.theglow.libs.theAPI.commands.CommandProvider;

public class B implements CommandProvider {
   public void registerArguments(List<CommandArg> var1) {
      var1.add(new E());
      var1.add(new H());
      var1.add(new D());
      var1.add(new C());
      var1.add(new F());
      var1.add(new G());
      var1.add(new xshyo.us.theglow.C.A.B());
      var1.add(new xshyo.us.theglow.C.A.A());
      var1.addAll(TheGlow.getInstance().getCommandArgs());
   }

   public void handleEmptyCommand(CommandSender var1, String var2) {
      if (var1 instanceof ConsoleCommandSender) {
         var1.sendMessage(ChatColor.RED + "You can only use /" + var2 + " reload or help from console.");
      } else {
         (new E()).executeArgument(var1, new String[0]);
      }

   }

   public void handleInvalidSender(CommandSender var1, String var2, String var3) {
      var1.sendMessage(ChatColor.RED + "Only players can use /" + var2 + " " + var3);
   }

   public void handleUnknownCommand(CommandSender var1, String var2) {
      xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.USAGE");
   }
}
