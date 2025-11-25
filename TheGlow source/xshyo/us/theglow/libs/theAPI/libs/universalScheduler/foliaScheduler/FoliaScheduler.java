package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.foliaScheduler;

import io.papermc.paper.threadedregions.scheduler.AsyncScheduler;
import io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler;
import io.papermc.paper.threadedregions.scheduler.RegionScheduler;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.tasks.MyScheduledTask;

public class FoliaScheduler implements TaskScheduler {
   final Plugin plugin;
   private final RegionScheduler regionScheduler = Bukkit.getServer().getRegionScheduler();
   private final GlobalRegionScheduler globalRegionScheduler = Bukkit.getServer().getGlobalRegionScheduler();
   private final AsyncScheduler asyncScheduler = Bukkit.getServer().getAsyncScheduler();

   public FoliaScheduler(Plugin var1) {
      this.plugin = var1;
   }

   public boolean isGlobalThread() {
      return Bukkit.getServer().isGlobalTickThread();
   }

   public boolean isTickThread() {
      return Bukkit.getServer().isPrimaryThread();
   }

   public boolean isEntityThread(Entity var1) {
      return Bukkit.getServer().isOwnedByCurrentRegion(var1);
   }

   public boolean isRegionThread(Location var1) {
      return Bukkit.getServer().isOwnedByCurrentRegion(var1);
   }

   public MyScheduledTask runTask(Runnable var1) {
      return new FoliaScheduledTask(this.globalRegionScheduler.run(this.plugin, (var1x) -> {
         var1.run();
      }));
   }

   public MyScheduledTask runTaskLater(Runnable var1, long var2) {
      return (MyScheduledTask)(var2 <= 0L ? this.runTask(var1) : new FoliaScheduledTask(this.globalRegionScheduler.runDelayed(this.plugin, (var1x) -> {
         var1.run();
      }, var2)));
   }

   public MyScheduledTask runTaskTimer(Runnable var1, long var2, long var4) {
      var2 = this.getOneIfNotPositive(var2);
      return new FoliaScheduledTask(this.globalRegionScheduler.runAtFixedRate(this.plugin, (var1x) -> {
         var1.run();
      }, var2, var4));
   }

   public MyScheduledTask runTask(Plugin var1, Runnable var2) {
      return new FoliaScheduledTask(this.globalRegionScheduler.run(var1, (var1x) -> {
         var2.run();
      }));
   }

   public MyScheduledTask runTaskLater(Plugin var1, Runnable var2, long var3) {
      return (MyScheduledTask)(var3 <= 0L ? this.runTask(var1, var2) : new FoliaScheduledTask(this.globalRegionScheduler.runDelayed(var1, (var1x) -> {
         var2.run();
      }, var3)));
   }

   public MyScheduledTask runTaskTimer(Plugin var1, Runnable var2, long var3, long var5) {
      var3 = this.getOneIfNotPositive(var3);
      return new FoliaScheduledTask(this.globalRegionScheduler.runAtFixedRate(var1, (var1x) -> {
         var2.run();
      }, var3, var5));
   }

   public MyScheduledTask runTask(Location var1, Runnable var2) {
      return new FoliaScheduledTask(this.regionScheduler.run(this.plugin, var1, (var1x) -> {
         var2.run();
      }));
   }

   public MyScheduledTask runTaskLater(Location var1, Runnable var2, long var3) {
      return (MyScheduledTask)(var3 <= 0L ? this.runTask(var2) : new FoliaScheduledTask(this.regionScheduler.runDelayed(this.plugin, var1, (var1x) -> {
         var2.run();
      }, var3)));
   }

   public MyScheduledTask runTaskTimer(Location var1, Runnable var2, long var3, long var5) {
      var3 = this.getOneIfNotPositive(var3);
      return new FoliaScheduledTask(this.regionScheduler.runAtFixedRate(this.plugin, var1, (var1x) -> {
         var2.run();
      }, var3, var5));
   }

   public MyScheduledTask runTask(Entity var1, Runnable var2) {
      return new FoliaScheduledTask(var1.getScheduler().run(this.plugin, (var1x) -> {
         var2.run();
      }, (Runnable)null));
   }

   public MyScheduledTask runTaskLater(Entity var1, Runnable var2, long var3) {
      return (MyScheduledTask)(var3 <= 0L ? this.runTask(var1, var2) : new FoliaScheduledTask(var1.getScheduler().runDelayed(this.plugin, (var1x) -> {
         var2.run();
      }, (Runnable)null, var3)));
   }

   public MyScheduledTask runTaskTimer(Entity var1, Runnable var2, long var3, long var5) {
      var3 = this.getOneIfNotPositive(var3);
      return new FoliaScheduledTask(var1.getScheduler().runAtFixedRate(this.plugin, (var1x) -> {
         var2.run();
      }, (Runnable)null, var3, var5));
   }

   public MyScheduledTask runTaskAsynchronously(Runnable var1) {
      return new FoliaScheduledTask(this.asyncScheduler.runNow(this.plugin, (var1x) -> {
         var1.run();
      }));
   }

   public MyScheduledTask runTaskLaterAsynchronously(Runnable var1, long var2) {
      var2 = this.getOneIfNotPositive(var2);
      return new FoliaScheduledTask(this.asyncScheduler.runDelayed(this.plugin, (var1x) -> {
         var1.run();
      }, var2 * 50L, TimeUnit.MILLISECONDS));
   }

   public MyScheduledTask runTaskTimerAsynchronously(Runnable var1, long var2, long var4) {
      return new FoliaScheduledTask(this.asyncScheduler.runAtFixedRate(this.plugin, (var1x) -> {
         var1.run();
      }, var2 * 50L, var4 * 50L, TimeUnit.MILLISECONDS));
   }

   public MyScheduledTask runTaskAsynchronously(Plugin var1, Runnable var2) {
      return new FoliaScheduledTask(this.asyncScheduler.runNow(var1, (var1x) -> {
         var2.run();
      }));
   }

   public MyScheduledTask runTaskLaterAsynchronously(Plugin var1, Runnable var2, long var3) {
      var3 = this.getOneIfNotPositive(var3);
      return new FoliaScheduledTask(this.asyncScheduler.runDelayed(var1, (var1x) -> {
         var2.run();
      }, var3 * 50L, TimeUnit.MILLISECONDS));
   }

   public MyScheduledTask runTaskTimerAsynchronously(Plugin var1, Runnable var2, long var3, long var5) {
      var3 = this.getOneIfNotPositive(var3);
      return new FoliaScheduledTask(this.asyncScheduler.runAtFixedRate(var1, (var1x) -> {
         var2.run();
      }, var3 * 50L, var5 * 50L, TimeUnit.MILLISECONDS));
   }

   public void execute(Runnable var1) {
      this.globalRegionScheduler.execute(this.plugin, var1);
   }

   public void execute(Location var1, Runnable var2) {
      this.regionScheduler.execute(this.plugin, var1, var2);
   }

   public void execute(Entity var1, Runnable var2) {
      var1.getScheduler().execute(this.plugin, var2, (Runnable)null, 1L);
   }

   public void cancelTasks() {
      this.globalRegionScheduler.cancelTasks(this.plugin);
      this.asyncScheduler.cancelTasks(this.plugin);
   }

   public void cancelTasks(Plugin var1) {
      this.globalRegionScheduler.cancelTasks(var1);
      this.asyncScheduler.cancelTasks(var1);
   }

   private long getOneIfNotPositive(long var1) {
      return var1 <= 0L ? 1L : var1;
   }
}
