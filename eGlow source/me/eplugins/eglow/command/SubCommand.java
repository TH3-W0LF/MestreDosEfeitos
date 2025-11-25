package me.eplugins.eglow.command;

import java.util.HashSet;
import java.util.Set;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.addon.citizens.EGlowCitizensTrait;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.text.ChatUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class SubCommand {
   public abstract String getName();

   public abstract String getPermission();

   public abstract String[] getSyntax();

   public abstract boolean isPlayerCmd();

   public abstract void perform(CommandSender var1, EGlowPlayer var2, String[] var3);

   public void sendSyntax(CommandSender sender) {
      this.sendSyntax(sender, this.getSyntax());
   }

   private void sendSyntax(CommandSender sender, String[] syntaxMessages) {
      ChatUtil.sendPlainMsg(sender, "", true);
      String[] var3 = syntaxMessages;
      int var4 = syntaxMessages.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String message = var3[var5];
         ChatUtil.sendPlainMsg(sender, EGlowMessageConfig.Message.INCORRECT_USAGE.get(message), false);
      }

   }

   public Set<EGlowPlayer> getTarget(CommandSender sender, String[] args) {
      Set<EGlowPlayer> results = new HashSet();
      if (args.length >= 2) {
         if (args[1].toLowerCase().contains("npc:")) {
            if (this.getInstance().getCitizensAddon() == null) {
               return results;
            }

            String argument = args[1].toLowerCase().replace("npc:", "");
            NPC npc = null;

            try {
               if (!argument.equals("s") && !argument.equals("sel") && !argument.equals("selected")) {
                  npc = CitizensAPI.getNPCRegistry().getById(Integer.parseInt(argument));
               } else {
                  npc = CitizensAPI.getDefaultNPCSelector().getSelected(sender);
               }
            } catch (NullPointerException var7) {
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.CITIZENS_NPC_NOT_FOUND.get(), true);
            }

            if (npc == null || !npc.isSpawned()) {
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.CITIZENS_NPC_NOT_FOUND.get(), true);
               return results;
            }

            try {
               if (!this.getInstance().getCitizensAddon().traitCheck(npc)) {
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.PREFIX.get() + "&cYour Citizens plugin is outdated&f!", true);
                  return results;
               }

               results.add(((EGlowCitizensTrait)npc.getOrAddTrait(EGlowCitizensTrait.class)).getEGlowNPC());
               return results;
            } catch (NoSuchMethodError var8) {
               ChatUtil.sendToConsole("&cYour Citizens plugin is outdated&f!", true);
            }
         } else {
            if (args[1].equalsIgnoreCase("*") || args[1].equalsIgnoreCase("all")) {
               results.addAll(DataManager.getEGlowPlayers());
               return results;
            }

            Player player = Bukkit.getPlayer(args[1].toLowerCase());
            if (player == null) {
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.PLAYER_NOT_FOUND.get(), true);
               return results;
            }

            EGlowPlayer eGlowTarget = DataManager.getEGlowPlayer(player);
            if (eGlowTarget == null) {
               ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.PLAYER_NOT_FOUND.get(), true);
               return results;
            }

            if (args.length >= 3 && !args[2].equalsIgnoreCase("glowonjoin")) {
               switch(eGlowTarget.getGlowDisableReason()) {
               case BLOCKEDWORLD:
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_PLAYER_IN_DISABLED_WORLD.get(eGlowTarget), true);
                  return results;
               case INVISIBLE:
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_PLAYER_INVISIBLE.get(eGlowTarget), true);
                  return results;
               case ANIMATION:
                  ChatUtil.sendMsg(sender, EGlowMessageConfig.Message.OTHER_PLAYER_ANIMATION.get(eGlowTarget), true);
                  return results;
               }
            }

            results.add(eGlowTarget);
         }
      }

      return results;
   }

   public EGlow getInstance() {
      return EGlow.getInstance();
   }
}
