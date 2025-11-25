package xshyo.us.theglow;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import xshyo.us.theglow.D.D;
import xshyo.us.theglow.E.C;
import xshyo.us.theglow.libs.bstats.bukkit.Metrics;
import xshyo.us.theglow.libs.config.YamlDocument;
import xshyo.us.theglow.libs.config.dvs.versioning.BasicVersioning;
import xshyo.us.theglow.libs.config.settings.dumper.DumperSettings;
import xshyo.us.theglow.libs.config.settings.general.GeneralSettings;
import xshyo.us.theglow.libs.config.settings.loader.LoaderSettings;
import xshyo.us.theglow.libs.config.settings.updater.UpdaterSettings;
import xshyo.us.theglow.libs.kyori.adventure.platform.bukkit.BukkitAudiences;
import xshyo.us.theglow.libs.theAPI.commands.AbstractCommand;
import xshyo.us.theglow.libs.theAPI.commands.CommandArg;
import xshyo.us.theglow.libs.theAPI.commands.DynamicCommand;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public final class TheGlow extends A implements Listener {
   private BukkitAudiences adventure;
   private static TheGlow instance;
   private YamlDocument lang;
   private YamlDocument conf;
   private YamlDocument menus;
   private final List<CommandArg> commandArgs;
   private final ListeningExecutorService executor;
   private xshyo.us.theglow.I.A database;
   private C glowManager;
   private xshyo.us.theglow.E.A glowLoad;
   private boolean updateAvailable;
   private String newUpdateVersion;
   private String UpdateDescription;
   private xshyo.us.theglow.J.A pluginIntegrationManager;
   private final List<String> fixedRegisteredCommands = new ArrayList();
   private static final DecimalFormat NUMBER_FORMAT_NANO = new DecimalFormat("0.00");

   public TheGlow() {
      instance = this;
      this.executor = MoreExecutors.listeningDecorator(new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue(1024)));
      this.commandArgs = new ArrayList();
   }

   public void load() {
   }

   public void start() {
      long var1 = System.nanoTime();
      PluginManager var3 = Bukkit.getServer().getPluginManager();
      this.pluginIntegrationManager = new xshyo.us.theglow.J.A();
      AbstractCommand.enable();
      var3.registerEvents(this, this);
      this.Hooks();
      this.setupMetrics();
      String var4 = this.getConf().getString("config.storage-method");
      if (var4.equalsIgnoreCase("mysql")) {
         this.database = new xshyo.us.theglow.H.B();
      } else {
         this.database = new xshyo.us.theglow.H.A();
      }

      this.database.B();
      this.glowManager = new C();
      this.glowLoad = new xshyo.us.theglow.E.A();
      this.glowLoad.B();
      String var5 = Bukkit.getVersion();
      if (var5.contains("1.16.5")) {
         this.adventure = null;
      } else {
         this.adventure = BukkitAudiences.create(this);
      }

      if (this.getConf().getBoolean("config.update-checker")) {
         xshyo.us.theglow.D.C var6 = new xshyo.us.theglow.D.C(this);
         var6.getReleaseInfo("name", (var2) -> {
            String var3 = this.getDescription().getVersion();
            this.newUpdateVersion = var2;
            if (!var3.equals(this.newUpdateVersion) && !this.getDescription().getVersion().contains("DEV")) {
               Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[TheGlow] An update for &aTheGlow (" + this.newUpdateVersion + ") &fis available at:"));
               Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&f[TheGlow] &7https://discord.com/invite/s3Qu3Taz2y"));
               this.updateAvailable = true;
               var6.getReleaseInfo("body", (var1) -> {
                  this.UpdateDescription = var1;
               });
            }

         });
      }

      this.logPluginEnabled(var1);
   }

   public void setupListener() {
      PluginManager var1 = Bukkit.getServer().getPluginManager();
      var1.registerEvents(new D(), this);
      var1.registerEvents(new xshyo.us.theglow.D.C(this), this);
      Plugin var2 = var1.getPlugin("LibsDisguises");
      if (var2 != null && var2.isEnabled()) {
         var1.registerEvents(new xshyo.us.theglow.D.B(), this);
      }

      Plugin var3 = var1.getPlugin("GSit");
      if (var3 != null && var3.isEnabled()) {
         var1.registerEvents(new xshyo.us.theglow.D.A(), this);
      }

   }

   public void setupFiles() {
      this.getLogger().log(Level.INFO, "Registering files...");

      try {
         HashSet var1 = new HashSet();
         var1.add("config.glow.auto-glow-on-join.groups");
         this.conf = YamlDocument.create(new File(this.getDataFolder(), "config.yml"), this.getResource("config.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().addIgnoredRoutes("3", var1, '.').setKeepAll(true).setVersioning(new BasicVersioning("file-version")).build());
         this.lang = YamlDocument.create(new File(this.getDataFolder(), "messages.yml"), this.getResource("messages.yml"), GeneralSettings.DEFAULT, LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().setVersioning(new BasicVersioning("file-version")).build());
         HashSet var2 = new HashSet();
         var2.add("inventories.glowing.glows.colors");
         var2.add("inventories.glowing.custom-items");
         this.menus = YamlDocument.create(new File(this.getDataFolder(), "menus.yml"), this.getResource("menus.yml"), GeneralSettings.builder().setUseDefaults(false).build(), LoaderSettings.builder().setAutoUpdate(true).build(), DumperSettings.DEFAULT, UpdaterSettings.builder().addIgnoredRoutes("2", var2, '.').setKeepAll(true).setVersioning(new BasicVersioning("file-version")).build());
      } catch (IOException var3) {
         var3.printStackTrace();
      }

   }

   public void setupActions() {
      this.getLogger().log(Level.INFO, "Registering Actions...");
      this.getActionExecutor().getActionHandlers().put("[minimessage]", new xshyo.us.theglow.F.B());
      this.getActionExecutor().getActionHandlers().put("[minibroadcast]", new xshyo.us.theglow.F.A());
   }

   private void logPluginEnabled(long var1) {
      Bukkit.getConsoleSender().sendMessage(Utils.translate("&2[TheGlow] Server version: " + Bukkit.getServer().getVersion() + " " + Bukkit.getServer().getBukkitVersion()));
      Bukkit.getConsoleSender().sendMessage(Utils.translate("&2[TheGlow] Done and enabled in %time%ms".replace("%time%", nanosToMillis(System.nanoTime() - var1))));
   }

   public static String nanosToMillis(long var0) {
      return NUMBER_FORMAT_NANO.format((double)var0 / 1000000.0D);
   }

   public void setupCommands() {
      this.reloadDynamicCommands();
   }

   public void reloadDynamicCommands() {
      this.getLogger().info("Reloading dynamic commands...");
      if (!this.fixedRegisteredCommands.isEmpty()) {
         this.getLogger().info("Removing previously registered commands: " + this.fixedRegisteredCommands);
         Iterator var1 = this.fixedRegisteredCommands.iterator();

         while(var1.hasNext()) {
            String var2 = (String)var1.next();
            AbstractCommand.removeCommand(var2);
         }

         this.fixedRegisteredCommands.clear();
      } else {
         this.getLogger().info("No previously registered commands found.");
      }

      this.registerCommand("config.command.shortened-open-command", xshyo.us.theglow.C.A.class, "Open glow menu", "Use /glow");
      if (this.conf.getBoolean("config.command.default.enabled")) {
         String var3 = this.conf.getString("config.command.default.name");
         List var4 = this.conf.getStringList("config.command.default.aliases");
         this.getLogger().info("Registering main command: " + var3 + ", aliases: " + var4);
         (new DynamicCommand(var3, "/" + var3 + " <arg>", "Main glow command", var4, new B())).register();
         this.fixedRegisteredCommands.add(var3.toLowerCase());
      }

   }

   private void registerCommand(String var1, Class<?> var2, String var3, String var4) {
      if (this.conf.getBoolean(var1 + ".enabled")) {
         String var5 = this.conf.getString(var1 + ".name");
         List var6 = this.conf.getStringList(var1 + ".aliases");
         this.getLogger().info("Registering command: " + var5 + ", aliases: " + var6);

         try {
            Constructor var7 = var2.getConstructor(String.class, String.class, String.class, List.class);
            AbstractCommand var8 = (AbstractCommand)var7.newInstance(var5, var3, var4, var6);
            var8.register();
            this.fixedRegisteredCommands.add(var5.toLowerCase());
         } catch (Exception var9) {
            this.getLogger().log(Level.SEVERE, "Failed to register command: " + var5 + " from class " + var2.getSimpleName(), var9);
         }

      }
   }

   public void Hooks() {
      this.getLogger().log(Level.INFO, "Registering Hooks...");
      PluginManager var1 = Bukkit.getServer().getPluginManager();
      Plugin var2 = var1.getPlugin("PlaceholderAPI");
      if (var2 != null && var2.isEnabled()) {
         (new xshyo.us.theglow.G.A()).register();
         this.getLogger().log(Level.INFO, "Hooked onto PlaceholderAPI");
      } else {
         this.getLogger().log(Level.WARNING, "PlaceholderAPI not found! Not enabling placeholders! Download and install it from https://www.spigotmc.org/resources/6245/");
      }

   }

   public void reload() {
      try {
         this.lang.reload();
         this.conf.reload();
         this.menus.reload();
      } catch (IOException var2) {
         throw new RuntimeException(var2);
      }

      this.reloadDynamicCommands();
      this.glowLoad.B();
   }

   public void stop() {
      this.getScheduler().cancelTasks(this);
      if (this.glowManager != null) {
         this.glowManager.C();
      }

      if (this.adventure != null) {
         this.adventure.close();
         this.adventure = null;
      }

   }

   private void setupMetrics() {
      this.getLogger().log(Level.INFO, "Registering Metrics...");
      short var1 = 23345;
      new Metrics(this, var1);
   }

   @NonNull
   public BukkitAudiences adventure() {
      if (this.adventure == null) {
         throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
      } else {
         return this.adventure;
      }
   }

   public BukkitAudiences getAdventure() {
      return this.adventure;
   }

   public YamlDocument getLang() {
      return this.lang;
   }

   public YamlDocument getConf() {
      return this.conf;
   }

   public YamlDocument getMenus() {
      return this.menus;
   }

   public List<CommandArg> getCommandArgs() {
      return this.commandArgs;
   }

   public ListeningExecutorService getExecutor() {
      return this.executor;
   }

   public xshyo.us.theglow.I.A getDatabase() {
      return this.database;
   }

   public C getGlowManager() {
      return this.glowManager;
   }

   public xshyo.us.theglow.E.A getGlowLoad() {
      return this.glowLoad;
   }

   public boolean isUpdateAvailable() {
      return this.updateAvailable;
   }

   public String getNewUpdateVersion() {
      return this.newUpdateVersion;
   }

   public String getUpdateDescription() {
      return this.UpdateDescription;
   }

   public xshyo.us.theglow.J.A getPluginIntegrationManager() {
      return this.pluginIntegrationManager;
   }

   public List<String> getFixedRegisteredCommands() {
      return this.fixedRegisteredCommands;
   }

   public static TheGlow getInstance() {
      return instance;
   }
}
