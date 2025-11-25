package me.eplugins.eglow.custommenu.event;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.command.CommandStorageHelper;
import me.eplugins.eglow.custommenu.config.ConfigStorage;
import me.eplugins.eglow.custommenu.config.EGlowCustomMenuConfig;
import me.eplugins.eglow.custommenu.menu.Menu;
import me.eplugins.eglow.custommenu.menu.handler.ActionHandler;
import me.eplugins.eglow.custommenu.menu.handler.RequirementHandler;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class EGlowCustomMenuListener implements Listener {
   public EGlowCustomMenuListener() {
      EGlow.getInstance().getServer().getPluginManager().registerEvents(this, EGlow.getInstance());
   }

   @EventHandler
   public void onPreCommandUsage(PlayerCommandPreprocessEvent event) {
      CommandStorageHelper.CommandStorage parsed = CommandStorageHelper.parse(event.getMessage());
      Map<String, String> mappedArgs = null;
      if (parsed != null) {
         Player player = event.getPlayer();
         String command = parsed.getCommand();
         String[] args = parsed.getArgs();
         if (EGlowCustomMenuConfig.isReloading()) {
            TextUtil.sendToPlayer(player, EGlowMessageConfig.Message.CUSTOM_MENU_RELOAD_BLOCKED.get());
         } else {
            ConfigStorage configStorage = EGlowCustomMenuConfig.getConfigStorageFromCommand(command);
            if (configStorage != null) {
               YamlConfiguration config = configStorage.getConfig();
               String menuName = configStorage.getFileName();
               if (!config.getStringList("args").isEmpty()) {
                  Map<String, Integer> argsCount = new LinkedHashMap();
                  Iterator var11 = config.getStringList("args").iterator();

                  while(var11.hasNext()) {
                     String key = (String)var11.next();
                     String[] split = key.split(":");
                     int count = 1;
                     argsCount.put("{" + split[0] + "}", split.length > 1 ? Integer.parseInt(split[1]) : count);
                  }

                  mappedArgs = CommandStorageHelper.mapArgs(argsCount, args);
                  if (!CommandStorageHelper.argsMatch(argsCount, args)) {
                     String argsUsage = config.getString("args_usage_message", "");
                     if (!argsUsage.isEmpty()) {
                        TextUtil.sendToPlayerWithoutPrefix(player, argsUsage);
                     }

                     event.setCancelled(true);
                     return;
                  }
               }

               if (RequirementHandler.failedRequirements(config, player, menuName, "open_requirements", mappedArgs)) {
                  event.setCancelled(true);
               } else {
                  ActionHandler.runCommandsFromConfig(config, menuName, "open_commands", player, mappedArgs);
                  (new Menu(player, configStorage.getFileName(), config, mappedArgs)).openInventory();
                  event.setCancelled(true);
               }
            }
         }
      }
   }

   @EventHandler
   public void onMenuClick(InventoryClickEvent event) {
      InventoryHolder holder = event.getInventory().getHolder();
      if (holder != null) {
         if (holder instanceof Menu) {
            event.setCancelled(true);
            Inventory bottomInventory = NMSHook.getBottomInventory(event);
            if (bottomInventory == null || bottomInventory.equals(event.getClickedInventory()) || event.getCurrentItem() == null) {
               return;
            }

            Menu menu = (Menu)holder;
            menu.handleMenu(event);
         }

      }
   }

   @EventHandler
   public void onMenuClose(InventoryCloseEvent event) {
      InventoryHolder holder = event.getInventory().getHolder();
      if (holder != null) {
         if (holder instanceof Menu) {
            Menu menu = (Menu)holder;
            menu.closeInventory();
         }

      }
   }
}
