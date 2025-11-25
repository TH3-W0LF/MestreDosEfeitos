package xshyo.us.theglow.B.A.A;

import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.Conversable;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.StringPrompt;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xshyo.us.theglow.TheGlow;
import xshyo.us.theglow.data.PlayerGlowData;
import xshyo.us.theglow.libs.theAPI.utilities.Utils;

public class A extends StringPrompt {
   public Prompt acceptInput(ConversationContext var1, String var2) {
      Conversable var3 = var1.getForWhom();
      Player var4 = (Player)var3;
      Map var5 = var1.getAllSessionData();
      TheGlow var6 = (TheGlow)Objects.requireNonNull(var1.getPlugin());
      String var7 = (String)var5.get("targetName");
      if (var2 != null && !var2.isEmpty()) {
         if (var2.equalsIgnoreCase("no")) {
            xshyo.us.theglow.B.A.A(var4, "MESSAGES.COMMANDS.REMOVE_CONFIRM.CANCEL");
            return END_OF_CONVERSATION;
         } else if (var2.equalsIgnoreCase("yes")) {
            var6.getDatabase().A(var7).thenAccept((var3x) -> {
               if (var3x == null) {
                  xshyo.us.theglow.B.A.A((CommandSender)var4, "MESSAGES.COMMANDS.REMOVEALL_INVALID_DATA", var7);
               } else {
                  Player var4x = Bukkit.getPlayer(var7);
                  if (var4x != null && var4x.isOnline() && var6.getGlowManager().B(var4x)) {
                     var6.getGlowManager().A(var4x);
                  }

                  var6.getDatabase().A.put(var3x.getUuid(), new PlayerGlowData(var3x.getUuid(), var3x.getName()));
                  var6.getDatabase().C(var3x.getUuid());
                  xshyo.us.theglow.B.A.A((CommandSender)var4, "MESSAGES.COMMANDS.REMOVEALL", var7);
               }
            }).exceptionally((var0) -> {
               var0.printStackTrace();
               return null;
            });
            return END_OF_CONVERSATION;
         } else {
            return this;
         }
      } else {
         return this;
      }
   }

   @NotNull
   public String getPromptText(@NotNull ConversationContext var1) {
      Map var2 = var1.getAllSessionData();
      String var3 = (String)var2.get("targetName");
      return Utils.translate(TheGlow.getInstance().getLang().getString("MESSAGES.COMMANDS.REMOVE_CONFIRM.USAGE").replace("{1}", var3));
   }
}
