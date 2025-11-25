package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.foliaScheduler;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask.ExecutionState;
import org.bukkit.plugin.Plugin;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.tasks.MyScheduledTask;

public class FoliaScheduledTask implements MyScheduledTask {
   private final ScheduledTask task;

   public FoliaScheduledTask(ScheduledTask var1) {
      this.task = var1;
   }

   public void cancel() {
      this.task.cancel();
   }

   public boolean isCancelled() {
      return this.task.isCancelled();
   }

   public Plugin getOwningPlugin() {
      return this.task.getOwningPlugin();
   }

   public boolean isCurrentlyRunning() {
      ExecutionState var1 = this.task.getExecutionState();
      return var1 == ExecutionState.RUNNING || var1 == ExecutionState.CANCELLED_RUNNING;
   }

   public boolean isRepeatingTask() {
      return this.task.isRepeatingTask();
   }
}
