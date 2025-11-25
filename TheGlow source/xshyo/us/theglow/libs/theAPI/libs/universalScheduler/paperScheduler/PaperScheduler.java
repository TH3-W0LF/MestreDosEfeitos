package xshyo.us.theglow.libs.theAPI.libs.universalScheduler.paperScheduler;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.foliaScheduler.FoliaScheduler;

public class PaperScheduler extends FoliaScheduler {
   public PaperScheduler(Plugin var1) {
      super(var1);
   }

   public boolean isGlobalThread() {
      return Bukkit.getServer().isPrimaryThread();
   }
}
