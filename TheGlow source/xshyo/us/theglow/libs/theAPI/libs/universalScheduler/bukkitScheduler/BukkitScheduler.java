package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.bukkitScheduler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.tasks.MyScheduledTask;

public class BukkitScheduler implements TaskScheduler {
   final Plugin plugin;

   public BukkitScheduler(Plugin var1) {
      this.plugin = var1;
   }

   public boolean isGlobalThread() {
      return Bukkit.getServer().isPrimaryThread();
   }

   public boolean isEntityThread(Entity var1) {
      return Bukkit.getServer().isPrimaryThread();
   }

   public boolean isRegionThread(Location var1) {
      return Bukkit.getServer().isPrimaryThread();
   }

   public MyScheduledTask runTask(Runnable var1) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTask(this.plugin, var1));
   }

   public MyScheduledTask runTaskLater(Runnable var1, long var2) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(this.plugin, var1, var2));
   }

   public MyScheduledTask runTaskTimer(Runnable var1, long var2, long var4) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimer(this.plugin, var1, var2, var4));
   }

   public MyScheduledTask runTaskAsynchronously(Runnable var1) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskAsynchronously(this.plugin, var1));
   }

   public MyScheduledTask runTaskLaterAsynchronously(Runnable var1, long var2) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, var1, var2));
   }

   public MyScheduledTask runTaskTimerAsynchronously(Runnable var1, long var2, long var4) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimerAsynchronously(this.plugin, var1, var2, var4));
   }

   public MyScheduledTask runTask(Plugin var1, Runnable var2) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTask(var1, var2));
   }

   public MyScheduledTask runTaskLater(Plugin var1, Runnable var2, long var3) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLater(var1, var2, var3));
   }

   public MyScheduledTask runTaskTimer(Plugin var1, Runnable var2, long var3, long var5) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimer(var1, var2, var3, var5));
   }

   public MyScheduledTask runTaskAsynchronously(Plugin var1, Runnable var2) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskAsynchronously(var1, var2));
   }

   public MyScheduledTask runTaskLaterAsynchronously(Plugin var1, Runnable var2, long var3) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskLaterAsynchronously(var1, var2, var3));
   }

   public MyScheduledTask runTaskTimerAsynchronously(Plugin var1, Runnable var2, long var3, long var5) {
      return new BukkitScheduledTask(Bukkit.getScheduler().runTaskTimerAsynchronously(var1, var2, var3, var5));
   }

   public void execute(Runnable var1) {
      Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, var1);
   }

   public void cancelTasks() {
      Bukkit.getScheduler().cancelTasks(this.plugin);
   }

   public void cancelTasks(Plugin var1) {
      Bukkit.getScheduler().cancelTasks(var1);
   }
}
