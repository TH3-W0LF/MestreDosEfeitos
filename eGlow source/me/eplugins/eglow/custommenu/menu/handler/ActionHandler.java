package me.eplugins.eglow.custommenu.menu.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.config.ConfigStorage;
import me.eplugins.eglow.custommenu.config.EGlowCustomMenuConfig;
import me.eplugins.eglow.custommenu.menu.Menu;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.chat.IChatBaseComponent;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.InventoryHolder;

public class ActionHandler {
   private static final Map<String, BiConsumer<Player, String>> ACTIONS = new HashMap();

   public static boolean handleActionCommands(ActionDataStorage actionDataStorage) {
      List<String> actionCommands = actionDataStorage.getActionCommands();
      Player player = actionDataStorage.getPlayer();
      Map<String, String> parsedArgs = actionDataStorage.getArguments();
      boolean returnValue = false;
      if (actionCommands != null && !actionCommands.isEmpty()) {
         Iterator var5 = actionCommands.iterator();

         while(true) {
            while(true) {
               String raw;
               int endIndex;
               do {
                  do {
                     if (!var5.hasNext()) {
                        return returnValue;
                     }

                     raw = (String)var5.next();
                     endIndex = raw.indexOf("]") + 1;
                  } while(endIndex <= 1);
               } while(raw.length() < endIndex);

               String key = raw.substring(0, endIndex).toLowerCase();
               String args = raw.substring(endIndex).replaceFirst("^\\s+", "");
               if (!key.equals("[placeholder]") && args.contains("%")) {
                  args = TextUtil.translateText(args, player, false, true, false, parsedArgs);
               }

               if (key.startsWith("[mini")) {
                  args = TextUtil.translateText(args, player, false, true, true, parsedArgs);
               }

               if (key.startsWith("[openguimenu]")) {
                  if (EGlowCustomMenuConfig.isReloading()) {
                     TextUtil.sendToPlayer(player, EGlowMessageConfig.Message.CUSTOM_MENU_RELOAD_BLOCKED.get());
                     continue;
                  }

                  ConfigStorage configStorage = null;
                  String menuName = actionDataStorage.getMenuName();
                  args = args.trim();
                  if (menuName.contains("--")) {
                     menuName = menuName.split("--")[0] + "--" + args;
                     configStorage = EGlowCustomMenuConfig.getConfigStorageFromFileName(menuName);
                  }

                  if (configStorage == null) {
                     configStorage = EGlowCustomMenuConfig.getConfigStorageFromFileName(args);
                  }

                  if (configStorage != null) {
                     player.closeInventory();
                     (new Menu(player, configStorage.getFileName(), configStorage.getConfig(), parsedArgs)).openInventory();
                     returnValue = true;
                  }
               }

               if (key.startsWith("[refresh]")) {
                  InventoryHolder holder = player.getOpenInventory().getTopInventory().getHolder();
                  if (holder instanceof Menu) {
                     Menu menu = (Menu)holder;
                     menu.setMenuItems();
                     menu.getMenuMetadata().getOwner().updateInventory();
                  }
               }

               if (key.startsWith("[close]")) {
                  player.closeInventory();
                  returnValue = true;
               } else {
                  BiConsumer<Player, String> action = (BiConsumer)ACTIONS.get(key);
                  if (action != null) {
                     try {
                        action.accept(player, args);
                     } catch (Exception var12) {
                        TextUtil.sendException("Failed to execute action: " + key, var12);
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   private static void register(String key, BiConsumer<Player, String> action) {
      ACTIONS.put(key.toLowerCase(), action);
   }

   private static void playSound(Player player, boolean worldOnly, boolean global, String soundArgs) {
      String[] args = soundArgs.split(" ");
      if (args.length >= 3) {
         try {
            String soundKey = args[0].toLowerCase();
            float volume = Float.parseFloat(args[1]);
            float pitch = Float.parseFloat(args[2]);
            Sound sound = DebugUtil.getMainVersion() > 20 ? (Sound)Registry.SOUNDS.get(NamespacedKey.minecraft(soundKey)) : Sound.valueOf(soundKey.toUpperCase());
            if (sound == null) {
               return;
            }

            if (!worldOnly && !global) {
               player.playSound(player.getLocation(), sound, volume, pitch);
            } else {
               Iterator var9;
               Player p;
               if (worldOnly) {
                  var9 = player.getWorld().getPlayers().iterator();

                  while(var9.hasNext()) {
                     p = (Player)var9.next();
                     p.playSound(p.getLocation(), sound, volume, pitch);
                  }
               } else {
                  var9 = Bukkit.getOnlinePlayers().iterator();

                  while(var9.hasNext()) {
                     p = (Player)var9.next();
                     p.playSound(p.getLocation(), sound, volume, pitch);
                  }
               }
            }
         } catch (Exception var11) {
            ChatUtil.printException("Failed to get Sound based on argument: " + soundArgs, var11);
         }

      }
   }

   public static void runCommandsFromConfig(YamlConfiguration config, String menuName, String path, Player player, Map<String, String> parsedArgs) {
      Object raw = config.get(path, "");
      List<String> commands = new ArrayList();
      if (raw instanceof String) {
         String command = (String)raw;
         if (!command.isEmpty()) {
            commands.add(command);
         }
      } else if (raw instanceof List) {
         Iterator var10 = ((List)raw).iterator();

         while(var10.hasNext()) {
            Object object = var10.next();
            if (object instanceof String) {
               String command = (String)object;
               commands.add(command);
            }
         }
      }

      handleActionCommands(new ActionDataStorage(commands, menuName, player, parsedArgs));
   }

   private static EGlowEffect switchEffectSpeed(EGlowEffect eGlowEffect) {
      String effectName = eGlowEffect.getName();
      return effectName.contains("slow") ? DataManager.getEGlowEffect(effectName.replace("slow", "fast")) : DataManager.getEGlowEffect(effectName.replace("fast", "slow"));
   }

   static {
      register("[player]", (player, args) -> {
         player.performCommand(args.trim().replace("/", ""));
      });
      register("[console]", (player, args) -> {
         Bukkit.dispatchCommand(Bukkit.getConsoleSender(), args.trim().replace("/", ""));
      });
      register("[commandevent]", (player, args) -> {
         Bukkit.getPluginManager().callEvent(new PlayerCommandPreprocessEvent(player, !args.trim().startsWith("/") ? "/" + args.trim() : args.trim()));
      });
      register("[placeholder]", (player, args) -> {
         TextUtil.translateText(args, player, false, true, false, (Map)null);
      });
      register("[message]", (player, args) -> {
         ChatUtil.sendPlainMsg(player, args, false);
      });
      register("[broadcast]", (player, args) -> {
         Bukkit.broadcastMessage(ChatUtil.translateColors(args));
      });
      register("[minimessage]", (player, args) -> {
         ChatUtil.sendPlainMsg(player, args, false);
      });
      register("[minibroadcast]", (player, args) -> {
         Bukkit.broadcastMessage(args);
      });
      register("[connect]", (player, args) -> {
         EGlow.getInstance().getCustomMenus().sendPlayerToServer(player, args);
      });
      register("[json]", (player, args) -> {
         ChatUtil.sendPlainMsg(player, IChatBaseComponent.optimizedComponent(args).toString(), false);
      });
      register("[jsonbroadcast]", (player, args) -> {
         Bukkit.broadcastMessage(IChatBaseComponent.optimizedComponent(args).toString());
      });
      register("[broadcastsound]", (player, args) -> {
         playSound(player, false, true, args);
      });
      register("[broadcastsoundworld]", (player, args) -> {
         playSound(player, true, false, args);
      });
      register("[sound]", (player, args) -> {
         playSound(player, false, false, args);
      });
      register("[chat]", (player, args) -> {
         player.chat(ChatUtil.translateColors(args));
      });
      register("[takemoney]", (player, args) -> {
         try {
            double amount = Double.parseDouble(args);
            if (EGlow.getInstance().getVaultAddon() != null) {
               EGlow.getInstance().getVaultAddon().withdrawBalance(player, amount);
            }
         } catch (NumberFormatException var4) {
         }

      });
      register("[givemoney]", (player, args) -> {
         try {
            double amount = Double.parseDouble(args);
            if (EGlow.getInstance().getVaultAddon() != null) {
               EGlow.getInstance().getVaultAddon().depositBalance(player, amount);
            }
         } catch (NumberFormatException var4) {
         }

      });
      register("[takeexp]", (player, args) -> {
         try {
            boolean level = args.toLowerCase().contains("l");
            int amount = Integer.parseInt(args.toLowerCase().replace("l", ""));
            if (level) {
               player.giveExpLevels(-amount);
            } else {
               player.giveExp(-amount);
            }
         } catch (NumberFormatException var4) {
         }

      });
      register("[giveexp]", (player, args) -> {
         try {
            boolean level = args.toLowerCase().contains("l");
            int amount = Integer.parseInt(args.toLowerCase().replace("l", ""));
            if (level) {
               player.giveExpLevels(amount);
            } else {
               player.giveExp(amount);
            }
         } catch (NumberFormatException var4) {
         }

      });
      register("[givepermission]", (player, args) -> {
         String perm = args.contains(".") && !args.contains(" ") ? args.toLowerCase() : "";
         if (!perm.isEmpty()) {
            if (EGlow.getInstance().getLpAddon() != null) {
               EGlow.getInstance().getLpAddon().givePermission(player, perm);
            } else if (EGlow.getInstance().getVaultAddon() != null) {
               EGlow.getInstance().getVaultAddon().givePermission(player, perm);
            }
         }

      });
      register("[takepermission]", (player, args) -> {
         String perm = args.contains(".") && !args.contains(" ") ? args.toLowerCase() : "";
         if (!perm.isEmpty()) {
            if (EGlow.getInstance().getLpAddon() != null) {
               EGlow.getInstance().getLpAddon().removePermission(player, perm);
            } else if (EGlow.getInstance().getVaultAddon() != null) {
               EGlow.getInstance().getVaultAddon().removePermission(player, perm);
            }
         }

      });
      register("[eglow_activate_effect]", (player, args) -> {
         String effectName = args.replace(" ", "");
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
         EGlowEffect eGlowEffect = DataManager.getEGlowEffect(effectName);
         if (eGlowPlayer != null && eGlowEffect != null) {
            eGlowPlayer.setGlowEffect(eGlowEffect);
         }
      });
      register("[eglow_switch_speed]", (player, args) -> {
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
         if (eGlowPlayer.isGlowing()) {
            EGlowEffect newEGlowEffect = switchEffectSpeed(eGlowPlayer.getGlowEffect());
            if (!eGlowPlayer.getGlowEffect().equals(newEGlowEffect)) {
               eGlowPlayer.activateGlow(newEGlowEffect);
            }
         }

      });
      register("[eglow_set_visibility]", (player, args) -> {
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
         EnumUtil.GlowVisibility glowVisibility = EnumUtil.GlowVisibility.valueOf(args.toUpperCase());
      });
      register("[eglow_cycle_visibility]", (player, args) -> {
         EGlowPlayer eGlowPlayer = DataManager.getEGlowPlayer(player);
         switch(eGlowPlayer.getGlowVisibility()) {
         case ALL:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OTHER);
            break;
         case OTHER:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.OWN);
            break;
         case OWN:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.NONE);
            break;
         case NONE:
            eGlowPlayer.setGlowVisibility(EnumUtil.GlowVisibility.ALL);
         }

      });
   }
}
