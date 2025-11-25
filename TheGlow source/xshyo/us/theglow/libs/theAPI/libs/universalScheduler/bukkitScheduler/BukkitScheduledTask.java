package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.bukkitScheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.tasks.MyScheduledTask;

public class BukkitScheduledTask implements MyScheduledTask {
   BukkitTask task;
   boolean isRepeating;

   public BukkitScheduledTask(BukkitTask var1) {
      this.task = var1;
      this.isRepeating = false;
   }

   public BukkitScheduledTask(BukkitTask var1, boolean var2) {
      this.task = var1;
      this.isRepeating = var2;
   }

   public void cancel() {
      this.task.cancel();
   }

   public boolean isCancelled() {
      return this.task.isCancelled();
   }

   public Plugin getOwningPlugin() {
      return this.task.getOwner();
   }

   public boolean isCurrentlyRunning() {
      return Bukkit.getServer().getScheduler().isCurrentlyRunning(this.task.getTaskId());
   }

   public boolean isRepeatingTask() {
      return this.isRepeating;
   }
}
