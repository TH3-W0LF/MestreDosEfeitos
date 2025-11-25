package xshyo.us.theglow.libs.theAPI.commands;

import java.util.List;
import java.util.regex.Pattern;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public interface CommandArg {
   List<String> getNames();

   boolean allowNonPlayersToExecute();

   List<String> getPermissionsToExecute();

   boolean executeArgument(CommandSender var1, String[] var2);

   List<String> tabComplete(CommandSender var1, String var2, String[] var3);

   default boolean isPlayer(CommandSender var1) {
      return var1 instanceof Player;
   }

   default boolean isNumber(String var1) {
      Pattern var2 = Pattern.compile("-?\\d+(\\.\\d+)?");
      return var2.matcher(var1).matches();
   }
}
