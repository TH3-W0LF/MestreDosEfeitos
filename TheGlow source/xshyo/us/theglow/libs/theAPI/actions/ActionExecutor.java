package xshyo.us.theglow.libs.theAPI.actions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import xshyo.us.theglow.libs.theAPI.TheAPI;
import xshyo.us.theglow.libs.theAPI.actions.handler.ActionBarActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.BroadcastActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.CloseActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.CommandActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.ConsoleLogActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.FireworkActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.MessageActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.NegativePermissionActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.OPCommandActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.PermissionActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.PlayerChatActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.SoundActionHandler;
import xshyo.us.theglow.libs.theAPI.actions.handler.TitleActionHandler;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class ActionExecutor {
   private final Map<String, ActionHandler> actionHandlers = new HashMap();
   private final Map<UUID, Set<BukkitTask>> pendingTasks = new HashMap();

   public ActionExecutor() {
      this.actionHandlers.put("[command]", new CommandActionHandler());
      this.actionHandlers.put("[player]", new PlayerChatActionHandler());
      this.actionHandlers.put("[message]", new MessageActionHandler());
      this.actionHandlers.put("[sound]", new SoundActionHandler());
      this.actionHandlers.put("[broadcast]", new BroadcastActionHandler());
      this.actionHandlers.put("[log]", new ConsoleLogActionHandler());
      this.actionHandlers.put("[title]", new TitleActionHandler());
      this.actionHandlers.put("[effect]", new TitleActionHandler());
      this.actionHandlers.put("[close]", new CloseActionHandler());
      this.actionHandlers.put("[actionbar]", new ActionBarActionHandler());
      this.actionHandlers.put("[firework]", new FireworkActionHandler());
      this.actionHandlers.put("[permission]", new PermissionActionHandler());
      this.actionHandlers.put("[!permission]", new NegativePermissionActionHandler());
      this.actionHandlers.put("[opcommand]", new OPCommandActionHandler());
   }

   public void finalExecuteActions(Player var1, List<String> var2) {
      Iterator var3 = var2.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();
         var4 = var4.trim();
         String[] var5 = var4.split("\\s+", 2);
         String var6 = var5[0].toLowerCase();
         String var7 = var5.length > 1 ? var5[1] : "";
         if (var6.startsWith("[chance=")) {
            if (Utils.shouldExecuteAction(var6)) {
               this.executeAction(var1, var7, new HashMap());
            }
         } else {
            this.executeAction(var1, var4, new HashMap());
         }
      }

   }

   public void finalExecuteActions(Player var1, List<String> var2, Map<String, String> var3) {
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var5 = var5.trim();
         String[] var6 = var5.split("\\s+", 2);
         String var7 = var6[0].toLowerCase();
         String var8 = var6.length > 1 ? var6[1] : "";
         if (var7.startsWith("[chance=")) {
            if (Utils.shouldExecuteAction(var7)) {
               this.executeAction(var1, var8, var3);
            }
         } else {
            this.executeAction(var1, var5, var3);
         }
      }

   }

   private void executeAction(Player var1, String var2, Map<String, String> var3) {
      String[] var4 = var2.split("\\s+", 2);
      String var5 = var4[0].toLowerCase();
      String var6 = var4.length > 1 ? var4[1].replace("{player}", var1.getName()) : "";
      var6 = Utils.setPAPI(var1, var6);

      Entry var8;
      for(Iterator var7 = var3.entrySet().iterator(); var7.hasNext(); var6 = var6.replace((CharSequence)var8.getKey(), (CharSequence)var8.getValue())) {
         var8 = (Entry)var7.next();
      }

      int var13 = 0;
      int var9;
      int var10;
      String var14;
      if (var6.contains("<delay=") && var6.contains(">")) {
         var9 = var6.indexOf("<delay=") + "<delay=".length();
         var10 = var6.indexOf(">", var9);
         if (var9 != -1 && var10 != -1) {
            try {
               var13 = Integer.parseInt(var6.substring(var9, var10));
            } catch (NumberFormatException var12) {
               Bukkit.getLogger().log(Level.WARNING, "Actions Incorrect delay format. Using default delay.");
            }
         }

         var14 = var6.replace(var6.substring(var9 - "<delay=".length(), var10 + 1), "");
      } else {
         var14 = var6;
      }

      if (var14.contains("<center>") && var14.contains("</center>")) {
         var9 = var14.indexOf("<center>") + "<center>".length();
         var10 = var14.indexOf("</center>", var9);
         if (var9 != -1 && var10 != -1) {
            String var11 = var14.substring(var9, var10);
            var11 = this.centerText(var11);
            var14 = var14.replace("<center>" + var14.substring(var9, var10) + "</center>", var11);
         }
      }

      ActionHandler var15 = (ActionHandler)this.actionHandlers.get(var5);
      if (var15 != null) {
         if (var13 > 0) {
            BukkitTask var16 = Bukkit.getScheduler().runTaskLater(TheAPI.getInstance(), () -> {
               var15.execute(var1, var14, 0);
            }, (long)var13);
            ((Set)this.pendingTasks.computeIfAbsent(var1.getUniqueId(), (var0) -> {
               return new HashSet();
            })).add(var16);
         } else {
            var15.execute(var1, var14, 0);
         }
      } else {
         Bukkit.getLogger().log(Level.WARNING, "Actions Formato incorrecto: " + var5);
      }

   }

   public void cancelPendingTasks(UUID var1) {
      Set var2 = (Set)this.pendingTasks.get(var1);
      if (var2 != null) {
         var2.forEach(BukkitTask::cancel);
         var2.clear();
         this.pendingTasks.remove(var1);
      }

   }

   private String centerText(String var1) {
      byte var2 = 80;
      int var3 = var1.replaceAll("§[0-9a-fk-or]", "").length();
      int var4 = (var2 - var3) / 2;
      StringBuilder var5 = new StringBuilder();

      for(int var6 = 0; var6 < var4; ++var6) {
         var5.append(" ");
      }

      var5.append(var1);
      return var5.toString();
   }

   public Map<String, ActionHandler> getActionHandlers() {
      return this.actionHandlers;
   }

   public Map<UUID, Set<BukkitTask>> getPendingTasks() {
      return this.pendingTasks;
   }
}
