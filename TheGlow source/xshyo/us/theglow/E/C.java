package xshyo.us.theglow.E;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.CurrentGlow;
import xshyo.us.theglow.data.PlayerGlowData;

public class C {
   private final TheGlow C = TheGlow.getInstance();
   private final Scoreboard B = Bukkit.getScoreboardManager().getNewScoreboard();
   private final ScheduledExecutorService D = Executors.newScheduledThreadPool(1);
   private final Map<UUID, ScheduledFuture<?>> A = new HashMap();
   private final Map<UUID, Iterator<ChatColor>> E = new HashMap();

   public void A(Player var1, List<String> var2, long var3, boolean var5) {
      if (var2 != null && !var2.isEmpty()) {
         List var6 = this.A(var2);
         if (var6.isEmpty()) {
            var1.sendMessage(ChatColor.RED + "There are no valid colors to apply.");
         } else {
            boolean var7 = this.C.getConf().getStringList("config.glow.disabled-worlds").contains(var1.getWorld().getName());
            if (var7) {
               this.A(var1);
            } else {
               this.C.getScheduler().runTaskLater(() -> {
                  if (!var5 && this.C.getConf().getBoolean("config.glow.check-invisibility") && var1.hasPotionEffect(PotionEffectType.INVISIBILITY)) {
                     xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.INVISIBILITY");
                  } else if (!var5 && this.C.getConf().getBoolean("config.glow.check-gamemode") && var1.getGameMode().equals(GameMode.SPECTATOR)) {
                     xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.GAMEMODE");
                  } else if (!var5 && this.C.getConf().getBoolean("config.glow.check-disguise") && this.C.getPluginIntegrationManager().A(var1)) {
                     xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.DISGUISE");
                  } else if (!var5 && this.C.getConf().getBoolean("config.glow.check-gsit") && this.C.getPluginIntegrationManager().C(var1)) {
                     xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.GSIT");
                  } else {
                     PlayerGlowData var6x = this.C.getDatabase().B(var1.getUniqueId());
                     if (var6x != null) {
                        CurrentGlow var7 = var6x.getCurrentGlow();
                        if (var7 != null && !var7.getEnable()) {
                           xshyo.us.theglow.B.A.A((CommandSender)var1, "MESSAGES.GUI.OFF");
                        } else {
                           if (var6.size() == 1) {
                              this.A(var1, (ChatColor)var6.get(0));
                           } else {
                              this.A(var1, var6, var3);
                           }

                        }
                     }
                  }
               }, 20L);
            }

         }
      }
   }

   private void A(Player var1, List<ChatColor> var2, long var3) {
      this.C(var1);
      ScheduledFuture var5 = this.D.scheduleAtFixedRate(() -> {
         if (!var1.isOnline()) {
            this.E.remove(var1.getUniqueId());
            this.C(var1);
         } else {
            Iterator var3 = (Iterator)this.E.get(var1.getUniqueId());
            if (var3 == null || !var3.hasNext()) {
               var3 = var2.iterator();
               this.E.put(var1.getUniqueId(), var3);
            }

            ChatColor var4 = (ChatColor)var3.next();
            this.A(var1, var4);
         }
      }, 0L, var3 * 50L, TimeUnit.MILLISECONDS);
      this.A.put(var1.getUniqueId(), var5);
      this.E.put(var1.getUniqueId(), var2.iterator());
   }

   private void A(Player var1, ChatColor var2) {
      String var3 = this.A(var2);
      Team var4 = this.B.getTeam(var3);
      if (var4 == null) {
         var4 = this.B(var2);
      }

      var4.addEntry(var1.getName());
      var1.setScoreboard(this.B);
      var1.setGlowing(true);
      this.C.getPluginIntegrationManager().A(var1, var2);
   }

   private String A(ChatColor var1) {
      return "TG_" + var1.name();
   }

   private Team B(ChatColor var1) {
      String var2 = this.A(var1);
      Team var3 = this.B.registerNewTeam(var2);
      var3.setColor(var1);
      var3.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
      var3.setOption(Option.COLLISION_RULE, OptionStatus.NEVER);
      return var3;
   }

   public void A(Player var1) {
      this.C(var1);
      Iterator var2 = this.B.getTeams().iterator();

      while(var2.hasNext()) {
         Team var3 = (Team)var2.next();
         if (var3.getName().startsWith("TG_") && var3.hasEntry(var1.getName())) {
            var3.removeEntry(var1.getName());
         }
      }

      var1.setGlowing(false);
      this.C.getPluginIntegrationManager().B(var1);
   }

   private void C(Player var1) {
      ScheduledFuture var2 = (ScheduledFuture)this.A.remove(var1.getUniqueId());
      if (var2 != null) {
         var2.cancel(false);
      }

      this.E.remove(var1.getUniqueId());
   }

   public void B() {
      Iterator var1 = this.B.getTeams().iterator();

      while(var1.hasNext()) {
         Team var2 = (Team)var1.next();
         if (var2.getName().startsWith("TG_")) {
            var2.unregister();
         }
      }

      var1 = this.A.values().iterator();

      while(var1.hasNext()) {
         ScheduledFuture var3 = (ScheduledFuture)var1.next();
         var3.cancel(false);
      }

      this.A.clear();
      this.E.clear();
   }

   private List<ChatColor> A(List<String> var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         String var4 = (String)var3.next();

         try {
            ChatColor var5 = ChatColor.valueOf(var4.toUpperCase());
            var2.add(var5);
         } catch (IllegalArgumentException var6) {
         }
      }

      return var2;
   }

   public boolean B(Player var1) {
      Iterator var2 = this.B.getTeams().iterator();

      Team var3;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         var3 = (Team)var2.next();
      } while(!var3.getName().startsWith("TG_") || !var3.hasEntry(var1.getName()));

      return true;
   }

   public void C() {
      this.D.shutdown();

      try {
         if (!this.D.awaitTermination(5L, TimeUnit.SECONDS)) {
            this.D.shutdownNow();
         }
      } catch (InterruptedException var2) {
         this.D.shutdownNow();
      }

   }

   public Map<UUID, ScheduledFuture<?>> A() {
      return this.A;
   }
}
