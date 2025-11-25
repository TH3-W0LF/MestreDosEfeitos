package xshyo.us.theglow.C.A;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;

public class H implements CommandArg {
   private static final String O = "theglow.reload";
   private final TheGlow N = TheGlow.getInstance();

   public List<String> getNames() {
      return Collections.singletonList("reload");
   }

   public boolean allowNonPlayersToExecute() {
      return true;
   }

   public List<String> getPermissionsToExecute() {
      return Arrays.asList("theglow.reload");
   }

   public boolean executeArgument(CommandSender var1, String[] var2) {
      if (!xshyo.us.theglow.B.A.A(var1, "theglow.reload")) {
         return true;
      } else {
         this.N.reload();
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.RELOAD");
         return false;
      }
   }

   public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
      return null;
   }
}
