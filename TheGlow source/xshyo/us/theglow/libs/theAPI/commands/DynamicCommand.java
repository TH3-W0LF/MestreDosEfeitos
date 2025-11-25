package xshyo.us.theglow.libs.theAPI.commands;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class DynamicCommand extends AbstractCommand {
   private final CommandProvider provider;

   public DynamicCommand(String var1, String var2, String var3, List<String> var4, CommandProvider var5) {
      super(var1, var2, var3, var4);
      this.provider = var5;
   }

   private List<CommandArg> getCurrentArguments() {
      ArrayList var1 = new ArrayList();
      this.provider.registerArguments(var1);
      return var1;
   }

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      List var5 = this.getCurrentArguments();
      if (var4.length == 1) {
         ArrayList var10 = new ArrayList();
         Iterator var11 = var5.iterator();

         while(var11.hasNext()) {
            CommandArg var8;
            boolean var12;
            label28: {
               var8 = (CommandArg)var11.next();
               if (var8.getPermissionsToExecute() != null) {
                  Stream var10000 = var8.getPermissionsToExecute().stream();
                  Objects.requireNonNull(var1);
                  if (!var10000.anyMatch(var1::hasPermission)) {
                     var12 = false;
                     break label28;
                  }
               }

               var12 = true;
            }

            boolean var9 = var12;
            if (var9) {
               var10.addAll(var8.getNames());
            }
         }

         return (List)StringUtil.copyPartialMatches(var4[0], var10, new ArrayList());
      } else {
         if (var4.length >= 2) {
            Iterator var6 = var5.iterator();

            while(var6.hasNext()) {
               CommandArg var7 = (CommandArg)var6.next();
               if (var7.getNames().contains(var4[0])) {
                  return var7.tabComplete(var1, var3, var4);
               }
            }
         }

         return null;
      }
   }

   public boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4) {
      List var5 = this.getCurrentArguments();
      if (var4.length == 0) {
         this.provider.handleEmptyCommand(var1, var2.getName());
         return true;
      } else {
         Iterator var6 = var5.iterator();

         CommandArg var7;
         do {
            if (!var6.hasNext()) {
               this.provider.handleUnknownCommand(var1, var4[0]);
               return true;
            }

            var7 = (CommandArg)var6.next();
         } while(!var7.getNames().contains(var4[0]));

         if (!var7.allowNonPlayersToExecute() && !(var1 instanceof Player)) {
            this.provider.handleInvalidSender(var1, var2.getName(), var4[0]);
            return true;
         } else {
            return var7.executeArgument(var1, var4);
         }
      }
   }

   @Deprecated
   public List<CommandArg> getArguments() {
      return this.getCurrentArguments();
   }

   public void addArgument(CommandArg var1) {
   }

   public CommandProvider getProvider() {
      return this.provider;
   }
}
