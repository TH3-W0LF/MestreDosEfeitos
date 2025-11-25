package xshyo.us.theglow.libs.theAPI.libs.universalScheduler;

import org.bukkit.plugin.Plugin;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.bukkitScheduler.BukkitScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.foliaScheduler.FoliaScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.paperScheduler.PaperScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.utils.JavaUtil;

public class UniversalScheduler {
   public static final boolean isFolia = JavaUtil.classExists("io.papermc.paper.threadedregions.RegionizedServer");
   public static final boolean isExpandedSchedulingAvailable = JavaUtil.classExists("io.papermc.paper.threadedregions.scheduler.ScheduledTask");

   public static TaskScheduler getScheduler(Plugin var0) {
      return (TaskScheduler)(isFolia ? new FoliaScheduler(var0) : (isExpandedSchedulingAvailable ? new PaperScheduler(var0) : new BukkitScheduler(var0)));
   }
}
