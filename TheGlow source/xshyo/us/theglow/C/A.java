package xshyo.us.theglow.C;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.enums.Filter;
import xshyo.us.theglow.enums.Order;
import xshyo.us.theglow.libs.theAPI.commands.AbstractCommand;

public class A extends AbstractCommand {
   private final TheGlow A = TheGlow.getInstance();

   public A(String var1, String var2, String var3, List<String> var4) {
      super(var1, var2, var3, var4);
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      if (!(var1 instanceof Player)) {
         xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SHORTENED_OPEN_CONSOLE");
         return true;
      } else {
         Player var5 = (Player)var1;
         boolean var6 = this.A.getConf().getBoolean("config.command.shortened-open-command.need-permissions");
         String var7 = this.A.getConf().getString("config.command.shortened-open-command.permission");
         if (var6 && !xshyo.us.theglow.B.A.A(var5, var7)) {
            return true;
         } else {
            PlayerGlowData var8 = this.A.getDatabase().B(var5.getUniqueId());
            (new xshyo.us.theglow.A.A(var5, Order.ALL, Filter.ALL, var8)).A(1);
            xshyo.us.theglow.B.A.A(var1, "MESSAGES.COMMANDS.SHORTENED_OPEN");
            return true;
         }
      }
   }

   public List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, String[] var4) {
      ArrayList var5 = new ArrayList();
      if (var4.length == 1) {
         byte var6 = 1;
         byte var7 = 10;

         for(int var8 = var6; var8 <= var7; ++var8) {
            var5.add(Integer.toString(var8));
         }
      }

      return var5;
   }
}
