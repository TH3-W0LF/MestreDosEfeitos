package me.eplugins.eglow.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.command.subcommands.EffectCommand;
import me.eplugins.eglow.command.subcommands.GUICommand;
import me.eplugins.eglow.command.subcommands.HelpCommand;
import me.eplugins.eglow.command.subcommands.ListCommand;
import me.eplugins.eglow.command.subcommands.ToggleCommand;
import me.eplugins.eglow.command.subcommands.ToggleGlowOnJoinCommand;
import me.eplugins.eglow.command.subcommands.VisibilityCommand;
import me.eplugins.eglow.command.subcommands.admin.DebugCommand;
import me.eplugins.eglow.command.subcommands.admin.InfoCommand;
import me.eplugins.eglow.command.subcommands.admin.ReloadCommand;
import me.eplugins.eglow.command.subcommands.admin.SetCommand;
import me.eplugins.eglow.command.subcommands.admin.UnsetCommand;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.event.EGlowEventListener;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class EGlowCommand implements CommandExecutor, TabExecutor {
   private final ArrayList<String> colors = new ArrayList(Arrays.asList("red", "darkred", "gold", "yellow", "green", "darkgreen", "aqua", "darkaqua", "blue", "darkblue", "purple", "pink", "white", "gray", "darkgray", "black", "none"));
   private final Set<SubCommand> subcmds = new HashSet();

   public EGlowCommand() {
      NMSHook.registerCommandAlias();
      this.subcmds.add(new GUICommand());
      this.subcmds.add(new HelpCommand());
      this.subcmds.add(new ListCommand());
      this.subcmds.add(new ToggleCommand());
      this.subcmds.add(new ToggleGlowOnJoinCommand());
      this.subcmds.add(new EffectCommand());
      this.subcmds.add(new VisibilityCommand());
      this.subcmds.add(new SetCommand());
      this.subcmds.add(new UnsetCommand());
      this.subcmds.add(new ReloadCommand());
      this.subcmds.add(new DebugCommand());
      this.subcmds.add(new InfoCommand());
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (command.getName().equalsIgnoreCase("eglow") || EGlowMainConfig.MainConfig.COMMAND_ALIAS_ENABLE.getBoolean() && command.getName().equalsIgnoreCase(EGlowMainConfig.MainConfig.COMMAND_ALIAS.getString())) {
         SubCommand subCommand = null;
         EGlowPlayer eGlowPlayer = null;
         String[] argsCopy = (String[])args.clone();
         if (args.length == 0) {
            args = new String[]{"gui"};
         }

         if (DataManager.isValidEffect(args[0], true) || args[0].equalsIgnoreCase("blink") || DataManager.isValidEffect(args[0], false) || args[0].toLowerCase().replace("off", "none").replace("disable", "none").equalsIgnoreCase("none")) {
            args = new String[]{"effect"};
         }

         Iterator var8 = this.getSubCommands().iterator();

         while(var8.hasNext()) {
            SubCommand subCmd = (SubCommand)var8.next();
            if (args[0].equalsIgnoreCase(subCmd.getName())) {
               subCommand = subCmd;
               break;
            }
         }

         if (subCommand == null) {
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.COMMAND_LIST.get(), true);
            return true;
         }

         if (sender instanceof ConsoleCommandSender && subCommand.isPlayerCmd()) {
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.PLAYER_ONLY.get(), true);
            return true;
         }

         if (!subCommand.getPermission().isEmpty() && !sender.hasPermission(subCommand.getPermission())) {
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.NO_PERMISSION.get(), true);
            return true;
         }

         if (sender instanceof Player) {
            eGlowPlayer = DataManager.getEGlowPlayer((Player)sender);
            if (eGlowPlayer == null) {
               EGlowEventListener.PlayerConnect((Player)sender, ((Player)sender).getUniqueId());
               eGlowPlayer = DataManager.getEGlowPlayer((Player)sender);
            }
         }

         subCommand.perform(sender, eGlowPlayer, argsCopy);
      }

      return true;
   }

   public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
      if (sender == null) {
         return null;
      } else if ((!(sender instanceof Player) || !cmd.getName().equalsIgnoreCase("eglow")) && (!EGlowMainConfig.MainConfig.COMMAND_ALIAS_ENABLE.getBoolean() || !cmd.getName().equalsIgnoreCase(EGlowMainConfig.MainConfig.COMMAND_ALIAS.getString()))) {
         return null;
      } else {
         ArrayList<String> suggestions = new ArrayList();
         ArrayList<String> finalSuggestions = new ArrayList();
         String var7;
         byte var8;
         Iterator var9;
         String color;
         Iterator var11;
         EGlowEffect effect;
         String name;
         switch(args.length) {
         case 1:
            if (sender.hasPermission("eglow.command.help")) {
               suggestions.add("help");
            }

            if (sender.hasPermission("eglow.command.list")) {
               suggestions.add("list");
            }

            if (sender.hasPermission("eglow.command.toggle")) {
               suggestions.add("toggle");
            }

            if (sender.hasPermission("eglow.command.toggleglowonjoin")) {
               suggestions.add("toggleglowonjoin");
            }

            if (sender.hasPermission("eglow.command.visibility")) {
               suggestions.add("visibility");
            }

            suggestions.add("blink");
            var11 = DataManager.getEGlowEffects().iterator();

            while(var11.hasNext()) {
               effect = (EGlowEffect)var11.next();
               name = effect.getName().replace("slow", "").replace("fast", "");
               if (!name.contains("blink") && sender.hasPermission(effect.getPermissionNode())) {
                  suggestions.add(name);
               }

               if (name.equals("none")) {
                  suggestions.add("off");
                  suggestions.add("disable");
               }
            }

            var11 = DataManager.getCustomEffects().iterator();

            while(var11.hasNext()) {
               effect = (EGlowEffect)var11.next();
               if (sender.hasPermission(effect.getPermissionNode())) {
                  suggestions.add(effect.getName());
               }
            }

            if (sender.hasPermission("eglow.command.set")) {
               suggestions.add("set");
            }

            if (sender.hasPermission("eglow.command.unset")) {
               suggestions.add("unset");
            }

            if (sender.hasPermission("eglow.command.debug")) {
               suggestions.add("debug");
            }

            if (sender.hasPermission("eglow.command.info")) {
               suggestions.add("info");
            }

            if (sender.hasPermission("eglow.command.reload")) {
               suggestions.add("reload");
            }

            StringUtil.copyPartialMatches(args[0], suggestions, finalSuggestions);
            break;
         case 2:
            var7 = args[0].toLowerCase();
            var8 = -1;
            switch(var7.hashCode()) {
            case 113762:
               if (var7.equals("set")) {
                  var8 = 3;
               }
               break;
            case 93826908:
               if (var7.equals("blink")) {
                  var8 = 1;
               }
               break;
            case 95458899:
               if (var7.equals("debug")) {
                  var8 = 5;
               }
               break;
            case 111442729:
               if (var7.equals("unset")) {
                  var8 = 4;
               }
               break;
            case 951590323:
               if (var7.equals("convert")) {
                  var8 = 2;
               }
               break;
            case 1941332754:
               if (var7.equals("visibility")) {
                  var8 = 0;
               }
            }

            Player p;
            label269:
            switch(var8) {
            case 0:
               suggestions = new ArrayList(Arrays.asList("all", "other", "own", "none"));
               break;
            case 1:
               var9 = this.colors.iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     break label269;
                  }

                  color = (String)var9.next();
                  if (!color.equals("none") && sender.hasPermission("eglow.blink." + color)) {
                     suggestions.add(color);
                  }
               }
            case 2:
               if (!sender.hasPermission("eglow.command.convert")) {
                  break;
               }

               suggestions = new ArrayList(Collections.singletonList("stop"));
               int i = 1;

               while(true) {
                  if (i > 10) {
                     break label269;
                  }

                  suggestions.add(String.valueOf(i));
                  ++i;
               }
            case 3:
               if (!sender.hasPermission("eglow.command.set")) {
                  break;
               }

               if (EGlow.getInstance().getCitizensAddon() != null) {
                  suggestions = new ArrayList(Arrays.asList("npc:ID", "npc:s", "npc:sel", "npc:selected"));
               }

               suggestions.add("*");
               var9 = Bukkit.getServer().getOnlinePlayers().iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     break label269;
                  }

                  p = (Player)var9.next();
                  suggestions.add(p.getName());
               }
            case 4:
               if (!sender.hasPermission("eglow.command.unset")) {
                  break;
               }

               if (EGlow.getInstance().getCitizensAddon() != null) {
                  suggestions = new ArrayList(Arrays.asList("npc:ID", "npc:s", "npc:sel", "npc:selected"));
               }

               suggestions.add("*");
               var9 = Bukkit.getServer().getOnlinePlayers().iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     break label269;
                  }

                  p = (Player)var9.next();
                  suggestions.add(p.getName());
               }
            case 5:
               var9 = Bukkit.getServer().getOnlinePlayers().iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     break label269;
                  }

                  p = (Player)var9.next();
                  suggestions.add(p.getName());
               }
            default:
               if (DataManager.isValidEffect(args[0], false)) {
                  suggestions = new ArrayList(Arrays.asList("slow", "fast"));
               }
            }

            StringUtil.copyPartialMatches(args[1], suggestions, finalSuggestions);
            break;
         case 3:
            if (this.colors.contains(args[1].toLowerCase())) {
               suggestions = new ArrayList(Arrays.asList("slow", "fast"));
            }

            if (args[0].equalsIgnoreCase("set") && sender.hasPermission("eglow.command.set") && Bukkit.getPlayer(args[1]) != null || EGlow.getInstance().getCitizensAddon() != null && args[1].toLowerCase().contains("npc:")) {
               var11 = DataManager.getEGlowEffects().iterator();

               while(var11.hasNext()) {
                  effect = (EGlowEffect)var11.next();
                  name = effect.getName().replace("slow", "").replace("fast", "");
                  if (!name.contains("blink")) {
                     suggestions.add(name);
                  }
               }

               var11 = DataManager.getCustomEffects().iterator();

               while(var11.hasNext()) {
                  effect = (EGlowEffect)var11.next();
                  suggestions.add(effect.getName());
               }

               suggestions.add("blink");
               suggestions.add("off");
               suggestions.add("disable");
               if (Bukkit.getPlayer(args[1]) != null) {
                  suggestions.add("glowonjoin");
               }
            }

            StringUtil.copyPartialMatches(args[2], suggestions, finalSuggestions);
            break;
         case 4:
            var7 = args[2].toLowerCase();
            var8 = -1;
            switch(var7.hashCode()) {
            case 93826908:
               if (var7.equals("blink")) {
                  var8 = 1;
               }
               break;
            case 1002257238:
               if (var7.equals("glowonjoin")) {
                  var8 = 0;
               }
            }

            label215:
            switch(var8) {
            case 0:
               if (sender.hasPermission("eglow.command.set")) {
                  suggestions = new ArrayList(Arrays.asList("true", "false"));
               }
               break;
            case 1:
               if (!sender.hasPermission("eglow.command.set")) {
                  break;
               }

               var9 = this.colors.iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     break label215;
                  }

                  color = (String)var9.next();
                  if (!color.equals("none")) {
                     suggestions.add(color);
                  }
               }
            default:
               if (DataManager.isValidEffect(args[2], false)) {
                  suggestions = new ArrayList(Arrays.asList("slow", "fast"));
               }
            }

            StringUtil.copyPartialMatches(args[3], suggestions, finalSuggestions);
            break;
         case 5:
            if (sender.hasPermission("eglow.command.set") && this.colors.contains(args[3].toLowerCase())) {
               suggestions = new ArrayList(Arrays.asList("slow", "fast"));
            }

            StringUtil.copyPartialMatches(args[3], suggestions, finalSuggestions);
            break;
         default:
            return suggestions;
         }

         return !finalSuggestions.isEmpty() ? finalSuggestions : suggestions;
      }
   }

   public Set<SubCommand> getSubCommands() {
      return this.subcmds;
   }
}
