package xshyo.us.theglow.libs.theAPI;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.plugin.java.JavaPlugin;
import xshyo.us.theglow.libs.theAPI.actions.ActionExecutor;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.UniversalScheduler;
import xshyo.us.theglow.libs.theAPI.libs.universalScheduler.scheduling.schedulers.TaskScheduler;
import xshyo.us.theglow.libs.theAPI.requirements.RequirementManager;

public abstract class TheAPI extends JavaPlugin {
   private static TheAPI instance;
   private TaskScheduler scheduler;
   private final List<CommandArg> commandArgs;
   private final ActionExecutor actionExecutor;
   private RequirementManager requirementManager;

   public abstract void load();

   public abstract void start();

   public abstract void stop();

   public abstract void setupListener();

   public abstract void setupCommands();

   public abstract void setupFiles();

   public abstract void setupActions();

   public TheAPI() {
      instance = this;
      this.commandArgs = new ArrayList();
      this.actionExecutor = new ActionExecutor();
   }

   public void onLoad() {
      this.load();
   }

   public void onEnable() {
      this.scheduler = UniversalScheduler.getScheduler(this);
      this.setupFiles();
      this.setupListener();
      this.setupCommands();
      this.setupActions();
      this.requirementManager = new RequirementManager();
      this.start();
   }

   public void onDisable() {
      this.stop();
   }

   public TaskScheduler getScheduler() {
      return this.scheduler;
   }

   public List<CommandArg> getCommandArgs() {
      return this.commandArgs;
   }

   public ActionExecutor getActionExecutor() {
      return this.actionExecutor;
   }

   public RequirementManager getRequirementManager() {
      return this.requirementManager;
   }

   public static TheAPI getInstance() {
      return instance;
   }
}
