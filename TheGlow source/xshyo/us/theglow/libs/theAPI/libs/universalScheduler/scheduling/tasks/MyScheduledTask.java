package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.tasks;

import org.bukkit.plugin.Plugin;

public interface MyScheduledTask {
   void cancel();

   boolean isCancelled();

   Plugin getOwningPlugin();

   boolean isCurrentlyRunning();

   boolean isRepeatingTask();
}
