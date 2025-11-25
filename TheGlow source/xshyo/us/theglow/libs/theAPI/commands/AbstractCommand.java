package xshyo.us.theglow.libs.theAPI.commands;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {
   protected final String command;
   protected final String description;
   protected final List<String> alias;
   protected final String usage;
   protected final String permMessage;
   protected final Plugin plugin;
   protected String permission;
   protected static CommandMap cmap;
   private static final Map<String, Field> fieldCache = new ConcurrentHashMap();
   private static final Map<String, Method> methodCache = new ConcurrentHashMap();
   private static final Map<String, Class<?>> classCache = new ConcurrentHashMap();
   private static String serverVersion = null;
   private static int majorVersion = 0;
   private static int minorVersion = 0;
   private static boolean isPaper = false;
   private static boolean isSpigot = false;
   private static final CommandMap commandMap;
   private static final Field knownCommands;
   private static boolean loaded;
   private static final List<AbstractCommand> list;

   public AbstractCommand(String var1) {
      this(var1, (String)null, (String)null, (String)null, (List)null, (Plugin)null);
   }

   public AbstractCommand(String var1, String var2) {
      this(var1, var2, (String)null, (String)null, (List)null, (Plugin)null);
   }

   public AbstractCommand(String var1, String var2, String var3) {
      this(var1, var2, var3, (String)null, (List)null, (Plugin)null);
   }

   public AbstractCommand(String var1, String var2, String var3, String var4) {
      this(var1, var2, var3, var4, (List)null, (Plugin)null);
   }

   public AbstractCommand(String var1, String var2, String var3, List<String> var4) {
      this(var1, var2, var3, (String)null, var4, (Plugin)null);
   }

   public AbstractCommand(String var1, String var2, String var3, String var4, List<String> var5) {
      this(var1, var2, var3, var4, var5, (Plugin)null);
   }

   public AbstractCommand(String var1, String var2, String var3, String var4, List<String> var5, Plugin var6) {
      this.command = var1.toLowerCase();
      this.usage = var2;
      this.description = var3;
      this.permMessage = var4;
      this.alias = var5;
      this.plugin = var6;
   }

   private static void detectServerInfo() {
      try {
         String var0 = Bukkit.getServer().getClass().getPackage().getName();
         Pattern var1 = Pattern.compile("v(\\d+)_(\\d+)_R\\d+");
         Matcher var2 = var1.matcher(var0);
         if (var2.find()) {
            majorVersion = Integer.parseInt(var2.group(1));
            minorVersion = Integer.parseInt(var2.group(2));
            serverVersion = majorVersion + "." + minorVersion;
         } else {
            String var3 = Bukkit.getBukkitVersion();
            if (var3.contains("1.")) {
               String[] var4 = var3.split("\\.");
               if (var4.length >= 2) {
                  majorVersion = Integer.parseInt(var4[0]);
                  minorVersion = Integer.parseInt(var4[1].split("-")[0]);
                  serverVersion = majorVersion + "." + minorVersion;
               }
            }
         }

         isPaper = checkPaperServer();
         isSpigot = checkSpigotServer();
      } catch (Exception var5) {
         majorVersion = 1;
         minorVersion = 20;
         serverVersion = "1.20";
      }

   }

   public static void enable() {
      loaded = true;
      Iterator var0 = list.iterator();

      while(var0.hasNext()) {
         AbstractCommand var1 = (AbstractCommand)var0.next();
         var1.register();
      }

      list.clear();
      scheduleCommandSync();
   }

   public static void removePluginCommands(Plugin var0) {
      try {
         Map var1 = (Map)knownCommands.get(commandMap);
         if (var1 == null) {
            return;
         }

         HashSet var2 = new HashSet();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            Command var5 = (Command)var4.getValue();
            if (var5 instanceof PluginIdentifiableCommand) {
               PluginIdentifiableCommand var6 = (PluginIdentifiableCommand)var5;
               if (var6.getPlugin() == var0) {
                  var2.add((String)var4.getKey());
               }
            } else if (isPluginCommand(var5, var0)) {
               var2.add((String)var4.getKey());
            }
         }

         safeRemoveCommands(var1, var2);
         knownCommands.set(commandMap, var1);
         scheduleCommandSync();
      } catch (Exception var7) {
         var7.printStackTrace();
      }

   }

   public void register() {
      if (!loaded) {
         list.add(this);
      } else {
         this.reg();
      }

   }

   protected void reg() {
      try {
         this.removeExistingCommand(this.command);
         AbstractCommand.ReflectCommand var1 = this.createReflectCommand();
         this.configureCommand(var1);
         String var2 = this.plugin == null ? "minecraft" : this.plugin.getName();
         boolean var3 = commandMap.register(var2, var1);
         if (var3) {
            var1.setExecutor(this);
            scheduleCommandSync();
         } else {
            System.err.println("[AbstractCommand] Failed to register command: " + this.command);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
      }

   }

   private AbstractCommand.ReflectCommand createReflectCommand() {
      if (this.plugin != null) {
         try {
            return new AbstractCommand.PluginReflectCommand(this.command, this.plugin);
         } catch (Exception var2) {
            return new AbstractCommand.ReflectCommand(this.command);
         }
      } else {
         return new AbstractCommand.ReflectCommand(this.command);
      }
   }

   private void configureCommand(AbstractCommand.ReflectCommand var1) {
      try {
         if (this.alias != null && !this.alias.isEmpty()) {
            var1.setAliases(new ArrayList(this.alias));
         }

         if (this.description != null) {
            var1.setDescription(this.description);
         }

         if (this.usage != null) {
            var1.setUsage(this.usage);
         }

         if (this.permMessage != null) {
            var1.setPermissionMessage(this.permMessage);
         }

         if (this.permission != null) {
            var1.setPermission(this.permission);
         }
      } catch (Exception var3) {
      }

   }

   private void removeExistingCommand(String var1) {
      try {
         Map var2 = (Map)knownCommands.get(commandMap);
         if (var2 != null) {
            HashSet var3 = new HashSet();
            String var4 = this.plugin != null ? this.plugin.getName().toLowerCase() + ":" : "";
            Iterator var5 = var2.keySet().iterator();

            while(true) {
               String var6;
               do {
                  if (!var5.hasNext()) {
                     Command var9 = (Command)var2.get(var1);
                     if (var9 != null && var9.getAliases() != null) {
                        Iterator var10 = var9.getAliases().iterator();

                        while(var10.hasNext()) {
                           String var7 = (String)var10.next();
                           var3.add(var7);
                           var3.add(var4 + var7);
                           var3.add("minecraft:" + var7);
                        }
                     }

                     safeRemoveCommands(var2, var3);
                     return;
                  }

                  var6 = (String)var5.next();
               } while(!var6.equals(var1) && !var6.equals(var1.toLowerCase()) && !var6.equals(var4 + var1) && !var6.equals("minecraft:" + var1));

               var3.add(var6);
            }
         }
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   private static void safeRemoveCommands(Map<String, Command> var0, Set<String> var1) {
      try {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            String var8 = (String)var2.next();

            try {
               var0.remove(var8);
            } catch (UnsupportedOperationException var6) {
               break;
            }
         }
      } catch (Exception var7) {
         try {
            Map var3 = createNewCommandMap(var0, var1);
            var0.clear();
            var0.putAll(var3);
         } catch (Exception var5) {
            forceRemoveCommands(var0, var1);
         }
      }

   }

   private static Map<String, Command> createNewCommandMap(Map<String, Command> var0, Set<String> var1) {
      HashMap var2 = new HashMap();
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         if (!var1.contains(var4.getKey())) {
            var2.put((String)var4.getKey(), (Command)var4.getValue());
         }
      }

      return var2;
   }

   private static void forceRemoveCommands(Map<String, Command> var0, Set<String> var1) {
      try {
         Field[] var2 = var0.getClass().getDeclaredFields();
         Field[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field var6 = var3[var5];
            if (Map.class.isAssignableFrom(var6.getType())) {
               var6.setAccessible(true);
               Map var7 = (Map)var6.get(var0);
               if (var7 != null && var7 != var0) {
                  Iterator var8 = var1.iterator();

                  while(var8.hasNext()) {
                     String var9 = (String)var8.next();

                     try {
                        var7.remove(var9);
                     } catch (Exception var11) {
                     }
                  }

                  return;
               }
            }
         }
      } catch (Exception var12) {
         System.err.println("[AbstractCommand] Could not remove commands: " + String.valueOf(var1));
      }

   }

   private static void scheduleCommandSync() {
      Plugin var0 = getAnyPlugin();
      if (var0 != null) {
         Bukkit.getScheduler().runTaskLater(var0, () -> {
            syncCommandsToAllPlayers();
         }, 1L);
      } else {
         syncCommandsToAllPlayers();
      }

   }

   private static void syncCommandsToAllPlayers() {
      if (!Bukkit.getOnlinePlayers().isEmpty()) {
         try {
            Iterator var0 = Bukkit.getOnlinePlayers().iterator();

            while(var0.hasNext()) {
               Player var1 = (Player)var0.next();
               syncCommandsToPlayer(var1);
            }
         } catch (Exception var2) {
         }

      }
   }

   private static void syncCommandsToPlayer(Player var0) {
      if (!tryModernSync(var0)) {
         if (!isPaper || !tryPaperSync(var0)) {
            if (!isSpigot || !trySpigotSync(var0)) {
               tryLegacySync(var0);
            }
         }
      }
   }

   private static boolean tryModernSync(Player var0) {
      try {
         Method var1 = var0.getClass().getMethod("updateCommands");
         var1.invoke(var0);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   private static boolean tryPaperSync(Player var0) {
      Method var2;
      try {
         Class var1 = Class.forName("com.destroystokyo.paper.entity.CraftPlayer");
         if (var1.isInstance(var0)) {
            var2 = var1.getMethod("updateCommands");
            var2.invoke(var0);
            return true;
         } else {
            return false;
         }
      } catch (Exception var4) {
         try {
            var2 = var0.getClass().getMethod("updateCommands");
            var2.invoke(var0);
            return true;
         } catch (Exception var3) {
            return false;
         }
      }
   }

   private static boolean trySpigotSync(Player var0) {
      try {
         Method var1 = var0.getClass().getMethod("sendCommands");
         var1.invoke(var0);
         return true;
      } catch (Exception var2) {
         return false;
      }
   }

   private static void tryLegacySync(Player var0) {
      try {
         if (majorVersion >= 1 && minorVersion >= 13) {
            try {
               var0.updateCommands();
            } catch (Exception var2) {
            }
         }
      } catch (Exception var3) {
      }

   }

   protected static Field getKnownCommands() {
      String var0 = "knownCommands";
      if (fieldCache.containsKey(var0)) {
         return (Field)fieldCache.get(var0);
      } else {
         try {
            Class var1 = commandMap.getClass();
            HashSet var2 = new HashSet(Arrays.asList("MockCommandMap", "CraftCommandMap", "FakeSimpleCommandMap", "PaperCommandMap", "SpigotCommandMap", "SimpleCommandMap"));
            if (var2.contains(var1.getSimpleName())) {
               var1 = var1.getSuperclass();
            }

            Field var3 = findKnownCommandsField(var1);
            if (var3 != null) {
               fieldCache.put(var0, var3);
            }

            return var3;
         } catch (Exception var4) {
            var4.printStackTrace();
            return null;
         }
      }
   }

   private static Field findKnownCommandsField(Class<?> var0) {
      String[] var1 = new String[]{"knownCommands", "commands", "commandMap"};
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];

         try {
            Field var6 = var0.getDeclaredField(var5);
            var6.setAccessible(true);
            if (Map.class.isAssignableFrom(var6.getType())) {
               return var6;
            }
         } catch (Exception var7) {
         }
      }

      return var0.getSuperclass() != null ? findKnownCommandsField(var0.getSuperclass()) : null;
   }

   protected static CommandMap getCommandMap() {
      if (cmap == null) {
         try {
            if (tryGetCommandMapModern()) {
               return cmap;
            }

            if (tryGetCommandMapByField()) {
               return cmap;
            }

            if (tryGetCommandMapByMethod()) {
               return cmap;
            }
         } catch (Exception var1) {
            var1.printStackTrace();
         }
      }

      return cmap;
   }

   private static boolean tryGetCommandMapModern() {
      try {
         Method var0 = Bukkit.getServer().getClass().getMethod("getCommandMap");
         cmap = (CommandMap)var0.invoke(Bukkit.getServer());
         return cmap != null;
      } catch (Exception var1) {
         return false;
      }
   }

   private static boolean tryGetCommandMapByField() {
      try {
         String[] var0 = new String[]{"commandMap", "cmap", "map"};
         Class var1 = Bukkit.getServer().getClass();
         String[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];

            try {
               Field var6 = var1.getDeclaredField(var5);
               var6.setAccessible(true);
               Object var7 = var6.get(Bukkit.getServer());
               if (var7 instanceof CommandMap) {
                  cmap = (CommandMap)var7;
                  return true;
               }
            } catch (Exception var8) {
            }
         }

         return false;
      } catch (Exception var9) {
         return false;
      }
   }

   private static boolean tryGetCommandMapByMethod() {
      try {
         String[] var0 = new String[]{"getCommandMap", "getCmdMap", "getMap"};
         Class var1 = Bukkit.getServer().getClass();
         String[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];

            try {
               Method var6 = var1.getDeclaredMethod(var5);
               var6.setAccessible(true);
               Object var7 = var6.invoke(Bukkit.getServer());
               if (var7 instanceof CommandMap) {
                  cmap = (CommandMap)var7;
                  return true;
               }
            } catch (Exception var8) {
            }
         }

         return false;
      } catch (Exception var9) {
         return false;
      }
   }

   public static void removeCommand(String var0) {
      try {
         Map var1 = (Map)knownCommands.get(commandMap);
         if (var1 != null) {
            HashSet var2 = new HashSet();
            Iterator var3 = var1.keySet().iterator();

            while(true) {
               String var4;
               do {
                  if (!var3.hasNext()) {
                     safeRemoveCommands(var1, var2);
                     knownCommands.set(commandMap, var1);
                     scheduleCommandSync();
                     return;
                  }

                  var4 = (String)var3.next();
               } while(!var4.equals(var0) && !var4.endsWith(":" + var0) && !var4.equals(var0.toLowerCase()));

               var2.add(var4);
            }
         }
      } catch (Exception var5) {
         var5.printStackTrace();
      }
   }

   private static boolean isPluginCommand(Command var0, Plugin var1) {
      try {
         String var2 = var0.toString().toLowerCase();
         String var3 = var1.getName().toLowerCase();
         return var2.contains(var3) || var0.getName().startsWith(var3 + ":");
      } catch (Exception var4) {
         return false;
      }
   }

   private static boolean checkPaperServer() {
      try {
         Class.forName("com.destroystokyo.paper.PaperConfig");
         return true;
      } catch (ClassNotFoundException var3) {
         try {
            Class.forName("io.papermc.paper.configuration.Configuration");
            return true;
         } catch (ClassNotFoundException var2) {
            return false;
         }
      }
   }

   private static boolean checkSpigotServer() {
      try {
         Class.forName("org.spigotmc.SpigotConfig");
         return true;
      } catch (ClassNotFoundException var1) {
         return false;
      }
   }

   private static Plugin getAnyPlugin() {
      try {
         Plugin[] var0 = Bukkit.getPluginManager().getPlugins();
         Plugin[] var1 = var0;
         int var2 = var0.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Plugin var4 = var1[var3];
            if (var4.isEnabled()) {
               return var4;
            }
         }

         return var0.length > 0 ? var0[0] : null;
      } catch (Exception var5) {
         return null;
      }
   }

   public static void removeCommandOfClass(Class<? extends Command> var0) {
      try {
         Map var1 = (Map)knownCommands.get(commandMap);
         if (var1 == null) {
            return;
         }

         HashSet var2 = new HashSet();
         Iterator var3 = var1.entrySet().iterator();

         while(var3.hasNext()) {
            Entry var4 = (Entry)var3.next();
            if (var0.isAssignableFrom(((Command)var4.getValue()).getClass())) {
               var2.add((String)var4.getKey());
            }
         }

         safeRemoveCommands(var1, var2);
         knownCommands.set(commandMap, var1);
         scheduleCommandSync();
      } catch (Exception var5) {
         var5.printStackTrace();
      }

   }

   public boolean isPlayer(CommandSender var1) {
      return var1 instanceof Player;
   }

   public boolean isAuthorized(CommandSender var1, String var2) {
      return var1.hasPermission(var2);
   }

   public boolean isAuthorized(Player var1, String var2) {
      return var1.hasPermission(var2);
   }

   public boolean isAuthorized(CommandSender var1, Permission var2) {
      return var1.hasPermission(var2);
   }

   public boolean isAuthorized(Player var1, Permission var2) {
      return var1.hasPermission(var2);
   }

   public abstract boolean onCommand(CommandSender var1, Command var2, String var3, String[] var4);

   public List<String> onTabComplete(CommandSender var1, Command var2, String var3, String[] var4) {
      return null;
   }

   public static String getServerVersion() {
      return serverVersion != null ? serverVersion : "Unknown";
   }

   public static boolean isVersionSupported(int var0, int var1) {
      return majorVersion > var0 || majorVersion == var0 && minorVersion >= var1;
   }

   public static boolean isPaperServer() {
      return isPaper;
   }

   public static boolean isSpigotServer() {
      return isSpigot;
   }

   public String getCommand() {
      return this.command;
   }

   static {
      detectServerInfo();
      commandMap = getCommandMap();
      knownCommands = getKnownCommands();
      loaded = false;
      list = new ArrayList();
   }

   private class ReflectCommand extends Command {
      private AbstractCommand exe = null;

      protected ReflectCommand(String var2) {
         super(var2);
      }

      public void setExecutor(AbstractCommand var1) {
         this.exe = var1;
      }

      public boolean execute(CommandSender var1, String var2, String[] var3) {
         return this.exe != null ? this.exe.onCommand(var1, this, var2, var3) : false;
      }

      public List<String> tabComplete(CommandSender var1, String var2, String[] var3) {
         if (this.exe != null) {
            List var4 = this.exe.onTabComplete(var1, this, var2, var3);
            if (var4 != null) {
               return var4;
            }
         }

         return super.tabComplete(var1, var2, var3);
      }
   }

   private class PluginReflectCommand extends AbstractCommand.ReflectCommand implements PluginIdentifiableCommand {
      protected Plugin plugin;

      protected PluginReflectCommand(String var2, Plugin var3) {
         super(var2);
         this.plugin = var3;
      }

      public Plugin getPlugin() {
         return this.plugin;
      }
   }
}
