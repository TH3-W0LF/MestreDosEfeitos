package me.eplugins.eglow.command.subcommands;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.config.ConfigStorage;
import me.eplugins.eglow.custommenu.config.EGlowCustomMenuConfig;
import me.eplugins.eglow.custommenu.menu.Menu;
import me.eplugins.eglow.custommenu.util.TextUtil;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.menu.menus.EGlowMainMenu;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GUICommand extends SubCommand {
   public String getName() {
      return "gui";
   }

   public String getPermission() {
      return "eglow.command.gui";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow"};
   }

   public boolean isPlayerCmd() {
      return true;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      if (EGlowMainConfig.MainConfig.SETTINGS_GUI_DISABLE_GUI.getBoolean()) {
         ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.GUI_DISABLED.get(), true);
      } else {
         switch(eGlowPlayer.getGlowDisableReason()) {
         case BLOCKEDWORLD:
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.WORLD_BLOCKED.get(), true);
            return;
         case INVISIBLE:
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.INVISIBILITY_BLOCKED.get(), true);
            return;
         case ANIMATION:
            ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.ANIMATION_BLOCKED.get(), true);
            return;
         default:
            if (eGlowPlayer.getGlowVisibility().equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) {
               ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.UNSUPPORTED_GLOW.get(), true);
            }

            if (this.getInstance().isBeta()) {
               if (EGlowCustomMenuConfig.isReloading()) {
                  TextUtil.sendToPlayer((Player)sender, EGlowMessageConfig.Message.CUSTOM_MENU_RELOAD_BLOCKED.get());
                  return;
               }

               ConfigStorage configStorage = EGlowCustomMenuConfig.getConfigStorageFromFileName("main-menu");
               if (configStorage == null) {
                  InputStream inputStream = EGlow.getInstance().getResource("main-menu.yml");
                  if (inputStream == null) {
                     throw new IllegalArgumentException("Default main file is missing from the JAR! This shouldn't be possible with an unmodified version of eGlow!");
                  }

                  InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                  (new Menu(eGlowPlayer.getPlayer(), "main-menu", YamlConfiguration.loadConfiguration(reader), (Map)null)).openInventory();
               } else {
                  (new Menu(eGlowPlayer.getPlayer(), "main-menu", configStorage.getConfig(), (Map)null)).openInventory();
               }
            } else {
               (new EGlowMainMenu(eGlowPlayer)).openInventory();
            }

         }
      }
   }
}
