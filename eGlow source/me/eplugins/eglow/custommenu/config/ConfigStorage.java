package me.eplugins.eglow.custommenu.config;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import lombok.Generated;
import org.bukkit.configuration.file.YamlConfiguration;

public class ConfigStorage {
   private final YamlConfiguration config;
   private final String fileName;
   private final String filePath;
   private final String mainCommand;
   private final Set<String> aliases;
   private final boolean registerCommand;

   public ConfigStorage(String fileName, String filePath, YamlConfiguration config, String mainCommand, List<String> aliases, boolean registerCommand) {
      this.config = config;
      this.fileName = fileName.toLowerCase();
      this.filePath = filePath;
      this.mainCommand = mainCommand.toLowerCase();
      this.aliases = new HashSet();
      this.registerCommand = registerCommand;
      Iterator var7 = aliases.iterator();

      while(var7.hasNext()) {
         String alias = (String)var7.next();
         this.aliases.add(alias.toLowerCase());
      }

   }

   public boolean matchesFileName(String fileName) {
      return this.fileName.equals(fileName.toLowerCase());
   }

   public boolean matchesCommandOrAlias(String command) {
      String safeCommand = command.toLowerCase();
      return this.mainCommand.equals(safeCommand) || this.aliases.contains(safeCommand);
   }

   @Generated
   public YamlConfiguration getConfig() {
      return this.config;
   }

   @Generated
   public String getFileName() {
      return this.fileName;
   }

   @Generated
   public String getFilePath() {
      return this.filePath;
   }

   @Generated
   public String getMainCommand() {
      return this.mainCommand;
   }

   @Generated
   public Set<String> getAliases() {
      return this.aliases;
   }

   @Generated
   public boolean isRegisterCommand() {
      return this.registerCommand;
   }
}
