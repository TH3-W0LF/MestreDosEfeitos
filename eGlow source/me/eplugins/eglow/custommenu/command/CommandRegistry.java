package me.eplugins.eglow.custommenu.command;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

public class CommandRegistry {
   private static final Map<String, PluginCommand> registeredCommands = new HashMap();

   public static void registerCommand(String name, Set<String> aliases) {
      if (!registeredCommands.containsKey(name)) {
         PluginCommand command = createPluginCommand(name, EGlow.getInstance());
         if (command != null) {
            aliases.remove(name);
            if (!aliases.isEmpty()) {
               command.setAliases(new ArrayList(aliases));
            }

            CommandExecutor commandExecutor = (sender, command1, label, args) -> {
               return true;
            };
            command.setExecutor(commandExecutor);

            try {
               CommandMap commandMap = getCommandMap();
               if (commandMap.register(name, command)) {
                  registeredCommands.put(name, command);
               }
            } catch (Exception var5) {
               ChatUtil.printException("Failed to register command", var5);
            }

         }
      }
   }

   private static PluginCommand createPluginCommand(String name, Plugin plugin) {
      try {
         Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
         constructor.setAccessible(true);
         return (PluginCommand)constructor.newInstance(name, plugin);
      } catch (Exception var3) {
         ChatUtil.printException("Failed to create PluginCommand instance for command registering", var3);
         return null;
      }
   }

   private static CommandMap getCommandMap() throws Exception {
      return (CommandMap)Bukkit.getServer().getClass().getMethod("getCommandMap").invoke(Bukkit.getServer());
   }
}
