package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.schedulers;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.tasks.MyScheduledTask;

public interface TaskScheduler {
   boolean isGlobalThread();

   default boolean isTickThread() {
      return Bukkit.getServer().isPrimaryThread();
   }

   boolean isEntityThread(Entity var1);

   boolean isRegionThread(Location var1);

   MyScheduledTask runTask(Runnable var1);

   MyScheduledTask runTaskLater(Runnable var1, long var2);

   MyScheduledTask runTaskTimer(Runnable var1, long var2, long var4);

   @Deprecated
   default MyScheduledTask runTask(Plugin var1, Runnable var2) {
      return this.runTask(var2);
   }

   @Deprecated
   default MyScheduledTask runTaskLater(Plugin var1, Runnable var2, long var3) {
      return this.runTaskLater(var2, var3);
   }

   @Deprecated
   default MyScheduledTask runTaskTimer(Plugin var1, Runnable var2, long var3, long var5) {
      return this.runTaskTimer(var2, var3, var5);
   }

   default MyScheduledTask runTask(Location var1, Runnable var2) {
      return this.runTask(var2);
   }

   default MyScheduledTask runTaskLater(Location var1, Runnable var2, long var3) {
      return this.runTaskLater(var2, var3);
   }

   default MyScheduledTask runTaskTimer(Location var1, Runnable var2, long var3, long var5) {
      return this.runTaskTimer(var2, var3, var5);
   }

   @Deprecated
   default MyScheduledTask scheduleSyncDelayedTask(Runnable var1, long var2) {
      return this.runTaskLater(var1, var2);
   }

   @Deprecated
   default MyScheduledTask scheduleSyncDelayedTask(Runnable var1) {
      return this.runTask(var1);
   }

   @Deprecated
   default MyScheduledTask scheduleSyncRepeatingTask(Runnable var1, long var2, long var4) {
      return this.runTaskTimer(var1, var2, var4);
   }

   default MyScheduledTask runTask(Entity var1, Runnable var2) {
      return this.runTask(var2);
   }

   default MyScheduledTask runTaskLater(Entity var1, Runnable var2, long var3) {
      return this.runTaskLater(var2, var3);
   }

   default MyScheduledTask runTaskTimer(Entity var1, Runnable var2, long var3, long var5) {
      return this.runTaskTimer(var2, var3, var5);
   }

   MyScheduledTask runTaskAsynchronously(Runnable var1);

   MyScheduledTask runTaskLaterAsynchronously(Runnable var1, long var2);

   MyScheduledTask runTaskTimerAsynchronously(Runnable var1, long var2, long var4);

   @Deprecated
   default MyScheduledTask runTaskAsynchronously(Plugin var1, Runnable var2) {
      return this.runTaskAsynchronously(var2);
   }

   @Deprecated
   default MyScheduledTask runTaskLaterAsynchronously(Plugin var1, Runnable var2, long var3) {
      return this.runTaskLaterAsynchronously(var2, var3);
   }

   @Deprecated
   default MyScheduledTask runTaskTimerAsynchronously(Plugin var1, Runnable var2, long var3, long var5) {
      return this.runTaskTimerAsynchronously(var2, var3, var5);
   }

   default <T> Future<T> callSyncMethod(Callable<T> var1) {
      CompletableFuture var2 = new CompletableFuture();
      this.execute(() -> {
         try {
            var2.complete(var1.call());
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      });
      return var2;
   }

   void execute(Runnable var1);

   default void execute(Location var1, Runnable var2) {
      this.execute(var2);
   }

   default void execute(Entity var1, Runnable var2) {
      this.execute(var2);
   }

   void cancelTasks();

   void cancelTasks(Plugin var1);
}
