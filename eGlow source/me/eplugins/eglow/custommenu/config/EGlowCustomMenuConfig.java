package me.eplugins.eglow.custommenu.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.custommenu.command.CommandRegistry;
import me.eplugins.eglow.custommenu.util.TextUtil;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class EGlowCustomMenuConfig {
   private static boolean reloading = false;
   private static List<ConfigStorage> configStorage = new ArrayList();

   public static void initialize() {
      try {
         File customMenuFolder = new File(EGlow.getInstance().getDataFolder(), "custom_menus");
         File defaultCustomMenuFile = new File(customMenuFolder.getPath(), "main-menu.yml");
         File defaultCustomEffectsMenuFile = new File(customMenuFolder.getPath(), "custom-effects.yml");
         if (!customMenuFolder.exists()) {
            TextUtil.sendToConsole("&4custom-menus folder not found&f! &eCreating&f...");
            customMenuFolder.mkdirs();
            if (!defaultCustomMenuFile.exists()) {
               saveResource(EGlow.getInstance().getResource("main-menu.yml"), defaultCustomMenuFile);
            }

            if (!defaultCustomEffectsMenuFile.exists()) {
               saveResource(EGlow.getInstance().getResource("custom-effects.yml"), defaultCustomEffectsMenuFile);
            }
         }

         TextUtil.sendToConsole("&aLoading custom menu configs&f.");
         File[] files = customMenuFolder.listFiles();
         if (files == null) {
            TextUtil.sendToConsole("&cNo valid custom menu configs found&f!");
            return;
         }

         File[] var4 = files;
         int var5 = files.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            File file = var4[var6];
            Path path = file.toPath();
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            ConfigStorage configStorageInstance;
            if (attributes.isDirectory()) {
               File[] subDirectoryFiles = file.listFiles();
               if (subDirectoryFiles == null) {
                  TextUtil.sendToConsole("&cNo valid custom menu configs found in subfolder: &e" + file.getName() + " &f!");
               } else {
                  File[] var12 = subDirectoryFiles;
                  int var13 = subDirectoryFiles.length;

                  for(int var14 = 0; var14 < var13; ++var14) {
                     File subDirectoryFile = var12[var14];
                     configStorageInstance = getConfigStorageInstance(subDirectoryFile, subDirectoryFile.getName());
                     if (configStorageInstance != null) {
                        getConfigStorage().add(configStorageInstance);
                     }
                  }
               }
            } else {
               configStorageInstance = getConfigStorageInstance(file, "");
               if (configStorageInstance != null) {
                  getConfigStorage().add(configStorageInstance);
               }
            }
         }

         Iterator var17 = getConfigStorage().iterator();

         while(var17.hasNext()) {
            ConfigStorage configStorageInstance = (ConfigStorage)var17.next();
            if (configStorageInstance.isRegisterCommand()) {
               CommandRegistry.registerCommand(configStorageInstance.getMainCommand(), configStorageInstance.getAliases());
            }
         }

         TextUtil.sendToConsole("&aSuccessfully loaded " + getConfigStorage().size() + " custom menu configs&f.");
      } catch (Exception var16) {
         TextUtil.sendException("Failed to initialize custom menu config", var16);
      }

   }

   public static void reloadConfig() {
      try {
         setReloading(true);
         File customMenuFolder = new File(EGlow.getInstance().getDataFolder(), "custom_menus");
         List<ConfigStorage> newConfigStorage = new ArrayList();
         File[] files = customMenuFolder.listFiles();
         if (files == null) {
            TextUtil.sendToConsole("&cNo valid custom menu configs found&f!");
            return;
         }

         File[] var3 = files;
         int var4 = files.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];
            Path path = file.toPath();
            BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class, LinkOption.NOFOLLOW_LINKS);
            ConfigStorage configStorageInstance;
            if (attributes.isDirectory()) {
               File[] subDirectoryFiles = file.listFiles();
               if (subDirectoryFiles == null) {
                  TextUtil.sendToConsole("&cNo valid custom menu configs found in subfolder: &e" + file.getName() + " &f!");
               } else {
                  File[] var11 = subDirectoryFiles;
                  int var12 = subDirectoryFiles.length;

                  for(int var13 = 0; var13 < var12; ++var13) {
                     File subDirectoryFile = var11[var13];
                     configStorageInstance = getConfigStorageInstance(subDirectoryFile, subDirectoryFile.getName());
                     if (configStorageInstance != null) {
                        newConfigStorage.add(configStorageInstance);
                     }
                  }
               }
            } else {
               configStorageInstance = getConfigStorageInstance(file, "");
               if (configStorageInstance != null) {
                  newConfigStorage.add(configStorageInstance);
               }
            }
         }

         setConfigStorage(newConfigStorage);
         setReloading(false);
         TextUtil.sendToConsole("&aSuccessfully loaded " + getConfigStorage().size() + " custom menu configs&f.");
      } catch (Exception var15) {
         TextUtil.sendException("Failed to initialize custom menu config", var15);
      }

   }

   private static ConfigStorage getConfigStorageInstance(File file, String subFolder) {
      String safeSubFolderName = subFolder.trim().replaceAll("\\s+", "");
      String fileNamePrefix = safeSubFolderName.isEmpty() ? "" : safeSubFolderName + "--";
      String fileName = fileNamePrefix + file.getName();
      String filePath = file.getPath();
      String mainCommand = "";
      List<String> aliases = new ArrayList();
      if (fileName.endsWith(".yml")) {
         fileName = fileName.substring(0, fileName.length() - 4);
         YamlConfiguration config = new YamlConfiguration();

         try {
            config.load(file);
         } catch (InvalidConfigurationException | IOException var11) {
            TextUtil.sendException("&cFailed to load file: &e" + fileName + " &cat location: " + filePath, var11);
            return null;
         }

         List<String> openCommands = getMenuCommands(config);
         if (!openCommands.isEmpty()) {
            mainCommand = ((String)openCommands.get(0)).toLowerCase();

            for(int i = 1; i < openCommands.size(); ++i) {
               aliases.add(((String)openCommands.get(i)).toLowerCase());
            }
         }

         return new ConfigStorage(fileName, filePath, config, mainCommand, aliases, config.getBoolean("register_command"));
      } else {
         return null;
      }
   }

   public static ConfigStorage getConfigStorageFromCommand(String command) {
      Iterator var1 = getConfigStorage().iterator();

      ConfigStorage configStorageInstance;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         configStorageInstance = (ConfigStorage)var1.next();
      } while(!configStorageInstance.matchesCommandOrAlias(command));

      return configStorageInstance;
   }

   public static ConfigStorage getConfigStorageFromFileName(String fileName) {
      Iterator var1 = getConfigStorage().iterator();

      ConfigStorage configStorageInstance;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         configStorageInstance = (ConfigStorage)var1.next();
      } while(!configStorageInstance.matchesFileName(fileName));

      return configStorageInstance;
   }

   private static void setReloading(boolean state) {
      reloading = state;
   }

   private static List<ConfigStorage> getConfigStorage() {
      return configStorage;
   }

   private static void setConfigStorage(List<ConfigStorage> newConfigStorage) {
      configStorage = newConfigStorage;
   }

   private static List<String> getMenuCommands(YamlConfiguration config) {
      List<String> commands = new ArrayList();
      if (config.isSet("open_command")) {
         Object raw = config.get("open_command", "");
         if (raw instanceof String) {
            String command = (String)raw;
            if (!command.isEmpty()) {
               commands.add(command.toLowerCase());
            }
         } else if (raw instanceof List) {
            Iterator var6 = ((List)raw).iterator();

            while(var6.hasNext()) {
               Object object = var6.next();
               if (object instanceof String) {
                  String command = (String)object;
                  if (!command.isEmpty()) {
                     commands.add(command.toLowerCase());
                  }
               }
            }
         }
      }

      return commands;
   }

   private static void saveResource(InputStream in, File fileLocation) {
      try {
         int lastIndex = fileLocation.getPath().lastIndexOf(47);
         File outDir = new File(EGlow.getInstance().getDataFolder(), fileLocation.getPath().substring(0, Math.max(lastIndex, 0)));
         if (!outDir.exists()) {
            outDir.mkdirs();
         }

         OutputStream out = Files.newOutputStream(fileLocation.toPath());
         byte[] buf = new byte[1024];

         int len;
         while((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
         }

         out.close();
         in.close();
      } catch (Exception var7) {
         TextUtil.sendException("Failed to write default menu config file", var7);
      }

   }

   @Generated
   public static boolean isReloading() {
      return reloading;
   }
}
