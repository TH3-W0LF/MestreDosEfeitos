package me.eplugins.eglow.custommenu.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.custommenu.config.ConfigStorage;
import me.eplugins.eglow.custommenu.config.EGlowCustomMenuConfig;
import me.eplugins.eglow.custommenu.menu.handler.ActionDataStorage;
import me.eplugins.eglow.custommenu.menu.handler.ActionHandler;
import me.eplugins.eglow.custommenu.menu.handler.Pair;
import me.eplugins.eglow.custommenu.menu.handler.RequirementHandler;
import me.eplugins.eglow.custommenu.menu.manager.MenuManager;
import me.eplugins.eglow.custommenu.menu.manager.item.ItemBuilder;
import me.eplugins.eglow.custommenu.menu.manager.item.helper.MenuHelper;
import me.eplugins.eglow.custommenu.menu.manager.item.util.MenuItemDataStorage;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class Menu extends MenuManager implements InventoryHolder {
   protected final ConcurrentHashMap<Integer, String> updateItems = new ConcurrentHashMap();
   protected final ConcurrentHashMap<Integer, String> itemSlot = new ConcurrentHashMap();
   protected final Map<String, String> args;
   protected final MenuManager.MenuMetadata menuMetadata;
   protected final YamlConfiguration config;
   protected final String menuName;
   protected Inventory inventory;
   protected boolean update;

   public Menu(Player player, String menuName, YamlConfiguration config, Map<String, String> args) {
      this.menuMetadata = this.getMenuMetadata(player);
      this.menuName = menuName;
      this.config = config;
      this.args = args;
   }

   public void handleMenu(InventoryClickEvent event) {
      int clickedSlot = event.getSlot();
      if (this.itemSlot.containsKey(clickedSlot)) {
         String itemName = (String)this.getItemSlot().get(clickedSlot);
         Player player = (Player)event.getWhoClicked();
         List<String> actions = new ArrayList();
         if (!RequirementHandler.failedRequirements(this.config, player, this.menuName, "items." + itemName + ".click_requirement", this.getArgs())) {
            actions.addAll(this.config.getStringList("items." + itemName + ".click_commands"));
         }

         switch(event.getClick()) {
         case LEFT:
            if (!RequirementHandler.failedRequirements(this.config, player, this.menuName, "items." + itemName + ".left_click_requirement", this.getArgs())) {
               actions.addAll(this.getConfig().getStringList("items." + itemName + ".left_click_commands"));
            }
            break;
         case SHIFT_LEFT:
            if (!RequirementHandler.failedRequirements(this.config, player, this.menuName, "items." + itemName + ".shift_left_click_requirement", this.getArgs())) {
               actions.addAll(this.getConfig().getStringList("items." + itemName + ".shift_left_click_commands"));
            }
            break;
         case RIGHT:
            if (!RequirementHandler.failedRequirements(this.config, player, this.menuName, "items." + itemName + ".right_click_requirement", this.getArgs())) {
               actions.addAll(this.getConfig().getStringList("items." + itemName + ".right_click_commands"));
            }
            break;
         case SHIFT_RIGHT:
            if (!RequirementHandler.failedRequirements(this.config, player, this.menuName, "items." + itemName + ".shift_right_click_requirement", this.getArgs())) {
               actions.addAll(this.getConfig().getStringList("items." + itemName + ".shift_right_click_commands"));
            }
         }

         if (!ActionHandler.handleActionCommands(new ActionDataStorage(actions, this.getMenuName(), player, this.getArgs()))) {
            ;
         }
      }
   }

   public void setMenuItems() {
      ConfigurationSection itemsSection = this.config.getConfigurationSection("items");
      if (itemsSection != null) {
         Player player = this.getMenuMetadata().getOwner();
         Map<Integer, Pair<Integer, String>> slotToItem = new HashMap();
         if (itemsSection.getKeys(false).isEmpty()) {
            ChatUtil.sendToConsole("&f[&eeGlow-CustomMenu&f] &eitems &csection has been configured for menu: &e" + this.getMenuName() + " &cbut doesn't contain any items&f!", false);
         } else {
            ConfigStorage configStorage = EGlowCustomMenuConfig.getConfigStorageFromFileName(this.getMenuName());
            if (configStorage != null) {
               Iterator var5 = itemsSection.getKeys(false).iterator();

               label76:
               while(true) {
                  String itemName;
                  MenuItemDataStorage menuItemDataStorage;
                  int priority;
                  do {
                     ConfigurationSection itemSection;
                     do {
                        if (!var5.hasNext()) {
                           var5 = slotToItem.entrySet().iterator();

                           while(var5.hasNext()) {
                              Entry<Integer, Pair<Integer, String>> entry = (Entry)var5.next();
                              int slot = (Integer)entry.getKey();
                              String itemName = (String)((Pair)entry.getValue()).getRight();
                              ConfigurationSection itemSection = itemsSection.getConfigurationSection(itemName);
                              if (itemSection != null && slot <= this.getInventory().getSize() - 1) {
                                 ItemStack item = (new ItemBuilder(new MenuItemDataStorage(itemSection, configStorage.getFilePath(), player, this.getArgs()))).build();
                                 if (item != null) {
                                    if (itemsSection.getBoolean(itemName + ".update", false)) {
                                       this.getUpdateItems().put(slot, itemName);
                                    }

                                    if (!this.getItemSlot().containsKey(slot)) {
                                       this.getItemSlot().put(slot, itemName);
                                    } else {
                                       this.getItemSlot().replace(slot, itemName);
                                    }

                                    this.getInventory().setItem(slot, item);
                                 }
                              }
                           }

                           return;
                        }

                        itemName = (String)var5.next();
                        itemSection = itemsSection.getConfigurationSection(itemName);
                     } while(itemSection == null);

                     menuItemDataStorage = new MenuItemDataStorage(itemSection, configStorage.getFilePath(), player, this.getArgs());
                     priority = MenuHelper.getPriority(menuItemDataStorage);
                  } while(RequirementHandler.failedRequirements(this.config, player, this.menuName, "items." + itemName + ".view_requirement", this.getArgs()));

                  List<Integer> slots = MenuHelper.getSlots(menuItemDataStorage);
                  Iterator var11 = slots.iterator();

                  while(true) {
                     int slot;
                     do {
                        if (!var11.hasNext()) {
                           continue label76;
                        }

                        slot = (Integer)var11.next();
                     } while(slotToItem.containsKey(slot) && priority >= (Integer)((Pair)slotToItem.get(slot)).getLeft());

                     slotToItem.put(slot, Pair.of(priority, itemName));
                  }
               }
            }
         }
      }
   }

   public void refresh() {
      int refreshInterval = this.config.getInt("update_interval", -1);
      if (refreshInterval == -1) {
         refreshInterval = 20;
         this.update = false;
      } else {
         refreshInterval *= 20;
         this.update = true;
      }

      (new BukkitRunnable() {
         public void run() {
            Iterator var1 = Menu.this.getUpdateItems().entrySet().iterator();

            while(var1.hasNext()) {
               Entry<Integer, String> entry = (Entry)var1.next();
               int slot = (Integer)entry.getKey();
               ItemStack item = Menu.this.getInventory().getItem(slot);
               ConfigStorage configStorage = EGlowCustomMenuConfig.getConfigStorageFromFileName(Menu.this.getMenuName());
               if (configStorage == null) {
                  return;
               }

               ConfigurationSection itemSection = configStorage.getConfig().getConfigurationSection("items." + (String)entry.getValue());
               Menu.this.getInventory().setItem(slot, (new ItemBuilder(new MenuItemDataStorage(itemSection, configStorage.getFilePath(), Menu.this.getMenuMetadata().getOwner(), Menu.this.getArgs()))).addItemStack(item).update());
            }

            (new BukkitRunnable() {
               public void run() {
                  Menu.this.getMenuMetadata().getOwner().updateInventory();
               }
            }).runTask(EGlow.getInstance());
            if (!Menu.this.update) {
               this.cancel();
            }

         }
      }).runTaskTimerAsynchronously(EGlow.getInstance(), (long)refreshInterval, (long)refreshInterval);
   }

   public void openInventory() {
      InventoryType inventoryType = InventoryType.valueOf(TextUtil.translateText(this.getConfig().getString("inventory_type", "CHEST"), this.getMenuMetadata().getOwner(), false, false, false, (Map)null));
      String menuTitle = TextUtil.translateText(this.getConfig().getString("menu_title", ""), this.getMenuMetadata().getOwner(), true, true, false, this.getArgs());
      if (inventoryType.equals(InventoryType.CHEST)) {
         if (!menuTitle.isEmpty()) {
            this.inventory = Bukkit.createInventory(this, this.getConfig().getInt("size", 54), menuTitle);
         } else {
            this.inventory = Bukkit.createInventory(this, this.getConfig().getInt("size", 54));
         }
      } else if (!menuTitle.isEmpty()) {
         this.inventory = Bukkit.createInventory(this, inventoryType, menuTitle);
      } else {
         this.inventory = Bukkit.createInventory(this, inventoryType);
      }

      (new BukkitRunnable() {
         public void run() {
            Menu.this.setMenuItems();
            (new BukkitRunnable() {
               public void run() {
                  Menu.this.getMenuMetadata().getOwner().openInventory(Menu.this.getInventory());
                  Menu.this.refresh();
               }
            }).runTask(EGlow.getInstance());
         }
      }).runTaskAsynchronously(EGlow.getInstance());
   }

   public void closeInventory() {
      this.update = false;
      ActionHandler.runCommandsFromConfig(this.config, this.getMenuName(), "close_commands", this.getMenuMetadata().getOwner(), this.getArgs());
   }

   public Inventory getInventory() {
      return this.inventory;
   }

   @Generated
   public ConcurrentHashMap<Integer, String> getUpdateItems() {
      return this.updateItems;
   }

   @Generated
   public ConcurrentHashMap<Integer, String> getItemSlot() {
      return this.itemSlot;
   }

   @Generated
   public Map<String, String> getArgs() {
      return this.args;
   }

   @Generated
   public MenuManager.MenuMetadata getMenuMetadata() {
      return this.menuMetadata;
   }

   @Generated
   public YamlConfiguration getConfig() {
      return this.config;
   }

   @Generated
   public String getMenuName() {
      return this.menuName;
   }

   @Generated
   public boolean isUpdate() {
      return this.update;
   }
}
