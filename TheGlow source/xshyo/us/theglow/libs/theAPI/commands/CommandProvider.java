package xshyo.us.theglow.libs.theAPI.commands;

import java.util.List;
import org.bukkit.command.CommandSender;

public interface CommandProvider {
   void registerArguments(List<CommandArg> var1);

   void handleEmptyCommand(CommandSender var1, String var2);

   void handleInvalidSender(CommandSender var1, String var2, String var3);

   void handleUnknownCommand(CommandSender var1, String var2);
}
