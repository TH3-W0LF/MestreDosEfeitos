package me.eplugins.eglow.command.subcommands;

import java.util.Iterator;
import java.util.Objects;
import me.eplugins.eglow.command.SubCommand;
import me.eplugins.eglow.config.EGlowCustomEffectsConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowEffect;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.command.CommandSender;

public class ListCommand extends SubCommand {
   public String getName() {
      return "list";
   }

   public String getPermission() {
      return "eglow.command.list";
   }

   public String[] getSyntax() {
      return new String[]{"/eGlow list"};
   }

   public boolean isPlayerCmd() {
      return false;
   }

   public void perform(CommandSender sender, EGlowPlayer eGlowPlayer, String[] args) {
      ChatUtil.sendPlainMsg(sender, "&m        &r &fColors & effects for &eeGlow&f: &m          ", false);
      ChatUtil.sendPlainMsg(sender, "&fColors:", false);
      ChatUtil.sendPlainMsg(sender, "&cred&f, &4darkred&f, &6gold&f,", false);
      ChatUtil.sendPlainMsg(sender, "&eyellow&f, &agreen&f, &2darkgreen&f,", false);
      ChatUtil.sendPlainMsg(sender, "&baqua&f, &3darkaqua&f, &9blue&f,", false);
      ChatUtil.sendPlainMsg(sender, "&1darkblue&f, &5purple&f, &dpink&f,", false);
      ChatUtil.sendPlainMsg(sender, "&fwhite&f, &7gray&f, &8darkgray&f,", false);
      ChatUtil.sendPlainMsg(sender, "&0black&f.", false);
      ChatUtil.sendPlainMsg(sender, "&eoff&f, &edisable&f or &enone &fwill stop the glow.", false);
      ChatUtil.sendPlainMsg(sender, "&fEffects:", false);
      ChatUtil.sendPlainMsg(sender, this.getEffectName("rainbowslow") + ", " + this.getEffectName("rainbowfast"), false);
      ChatUtil.sendPlainMsg(sender, "&fCustom effects:", false);
      StringBuilder text = new StringBuilder();
      int i = 1;

      for(Iterator var6 = EGlowCustomEffectsConfig.Effect.GET_ALL_EFFECTS.get().iterator(); var6.hasNext(); ++i) {
         String effect = (String)var6.next();
         text.append(this.getEffectName(effect)).append(", ");
         if (i == 3) {
            ChatUtil.sendPlainMsg(sender, text.toString(), false);
            text = new StringBuilder();
            i = 0;
         }
      }

      if (text.length() > 0) {
         ChatUtil.sendPlainMsg(sender, text.toString(), false);
      }

      ChatUtil.sendPlainMsg(sender, "&f&m                                                       ", false);
   }

   private String getEffectName(String effect) {
      return "&e" + effect + " &f(" + ((EGlowEffect)Objects.requireNonNull(DataManager.getEGlowEffect(effect), "Unable to retrieve effect from given name")).getDisplayName() + "&f)";
   }
}
