package me.eplugins.eglow.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.util.enums.EnumUtil;
import me.eplugins.eglow.util.packets.PacketUtil;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.chat.EnumChatFormat;
import me.eplugins.eglow.util.text.ChatUtil;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPC.Metadata;
import net.citizensnpcs.trait.ScoreboardTrait;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class EGlowPlayer {
   private final EnumUtil.EntityType entityType;
   private NPC citizensNPC;
   private Player player;
   private String displayName;
   private UUID uuid;
   private String teamName = "";
   private ProtocolVersion version;
   private ChatColor activeColor;
   private boolean glowStatus;
   private boolean fakeGlowStatus;
   private EGlowEffect glowEffect;
   private EGlowEffect forcedEffect;
   private boolean glowOnJoin;
   private boolean activeOnQuit;
   private boolean saveData;
   private EnumUtil.GlowDisableReason glowDisableReason;
   private EnumUtil.GlowVisibility glowVisibility;
   private EnumUtil.GlowTargetMode glowTargetMode;
   private List<Player> customTargetList;
   private final String sortingOrder;

   public EGlowPlayer(Player player) {
      this.version = ProtocolVersion.SERVER_VERSION;
      this.activeColor = ChatColor.RESET;
      this.glowStatus = false;
      this.fakeGlowStatus = false;
      this.saveData = false;
      this.glowDisableReason = EnumUtil.GlowDisableReason.NONE;
      this.glowTargetMode = EnumUtil.GlowTargetMode.ALL;
      this.customTargetList = new ArrayList();
      this.sortingOrder = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      this.entityType = EnumUtil.EntityType.PLAYER;
      this.player = player;
      this.displayName = player.getName();
      this.uuid = player.getUniqueId();
      this.teamName = this.getTeamName();
      this.customTargetList = new ArrayList(Collections.singletonList(player));
      this.version = ProtocolVersion.getPlayerVersion(this);
      if (this.version.getNetworkId() <= 110 && !this.version.getFriendlyName().equals("1.9.4")) {
         this.glowVisibility = EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT;
      } else {
         this.glowVisibility = EnumUtil.GlowVisibility.ALL;
      }

      this.setupForceGlows();
   }

   public EGlowPlayer(NPC npc) {
      this.version = ProtocolVersion.SERVER_VERSION;
      this.activeColor = ChatColor.RESET;
      this.glowStatus = false;
      this.fakeGlowStatus = false;
      this.saveData = false;
      this.glowDisableReason = EnumUtil.GlowDisableReason.NONE;
      this.glowTargetMode = EnumUtil.GlowTargetMode.ALL;
      this.customTargetList = new ArrayList();
      this.sortingOrder = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
      this.entityType = EnumUtil.EntityType.CITIZENNPC;
      this.citizensNPC = npc;
      this.displayName = npc.getName();
   }

   public void setGlowing(boolean status, boolean fake) {
      if (fake || status != this.getGlowStatus()) {
         this.setGlowStatus(status);
         this.setFakeGlowStatus(fake);
         switch(this.getEntityType()) {
         case PLAYER:
            PacketUtil.updateGlowing(this, status);
            break;
         case CITIZENNPC:
            try {
               this.getCitizensNPC().data().setPersistent(Metadata.GLOWING, status);
            } catch (Exception var4) {
               ChatUtil.sendToConsole("&cYour Citizens version is outdated please use it's latest version", true);
            }
         }

      }
   }

   public void setColor(ChatColor color, boolean status, boolean fake) {
      if (this.skipSaveData()) {
         this.setSaveData(true);
      }

      this.setFakeGlowStatus(fake);
      if (color.equals(ChatColor.RESET)) {
         this.setGlowing(false, fake);
         if (EGlow.getInstance().getVelocitabAddon() != null && this.getEntityType().equals(EnumUtil.EntityType.PLAYER) && !fake) {
            EGlow.getInstance().getVelocitabAddon().VelocitabUpdateRequest(this.getPlayer(), color);
         }
      } else {
         this.setGlowing(status, fake);
         if (this.getActiveColor() != null && this.getActiveColor().equals(color)) {
            return;
         }

         this.setGlowStatus(status);
         this.setActiveColor(color);
         switch(this.getEntityType()) {
         case PLAYER:
            if (EGlow.getInstance().getVelocitabAddon() != null) {
               EGlow.getInstance().getVelocitabAddon().VelocitabUpdateRequest(this.getPlayer(), color);
            }

            PacketUtil.updateScoreboardTeam(DataManager.getEGlowPlayer(this.getPlayer()), this.getTeamName(), (EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagPrefix(this) : "") + color, EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagSuffix(this) : "", EnumChatFormat.valueOf(color.name()));
            break;
         case CITIZENNPC:
            if (!fake && this.getCitizensNPC().isSpawned()) {
               ((ScoreboardTrait)this.getCitizensNPC().getOrAddTrait(ScoreboardTrait.class)).setColor(color);
            }
         }
      }

      if (!this.getEntityType().equals(EnumUtil.EntityType.CITIZENNPC)) {
         this.updatePlayerTabname();
         DataManager.sendAPIEvent(this, fake);
      }
   }

   public boolean isSameGlow(EGlowEffect newGlowEffect) {
      return this.getGlowStatus() && this.getGlowEffect() != null && newGlowEffect.equals(this.getGlowEffect());
   }

   public void activateGlow() {
      if (this.getGlowEffect() != null) {
         this.setGlowStatus(true);
         this.activateGlow(this.getGlowEffect());
      } else {
         this.setGlowStatus(false);
      }

   }

   public void activateGlow(EGlowEffect newGlowEffect) {
      this.disableGlow(true);
      this.setGlowEffect(newGlowEffect);
      newGlowEffect.activateForEntity(this.getEntity());
      this.setGlowing(true, false);
   }

   public void disableGlow(boolean hardReset) {
      if (this.isGlowing()) {
         if (this.getGlowEffect() != null) {
            this.getGlowEffect().deactivateForEntity(this.getEntity());
         }

         if (hardReset) {
            this.setGlowEffect(DataManager.getEGlowEffect("none"));
         }

         this.setActiveColor(ChatColor.RESET);
         this.setGlowing(false, false);
         if (this.getPlayer() != null) {
            PacketUtil.updateScoreboardTeam(DataManager.getEGlowPlayer(this.getPlayer()), this.getTeamName(), EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagPrefix(this) : "", EGlow.getInstance().getVaultAddon() != null ? EGlow.getInstance().getVaultAddon().getPlayerTagSuffix(this) : "", EnumChatFormat.RESET);
            DataManager.sendAPIEvent(this, false);
            this.updatePlayerTabname();
         }

         if (this.getCitizensNPC() != null) {
            ((ScoreboardTrait)this.getCitizensNPC().getOrAddTrait(ScoreboardTrait.class)).setColor(ChatColor.RESET);
         }
      }

   }

   public void updatePlayerTabname() {
      if (EGlowMainConfig.MainConfig.FORMATTING_TABLIST_ENABLE.getBoolean()) {
         if (this.getPlayer() != null) {
            if (!EGlow.getInstance().getTablistFormattingAddon().isUsingPAPIPlaceholder()) {
               EGlow.getInstance().getTablistFormattingAddon().updateTablistFormat(this);
            }

         }
      }
   }

   public boolean isGlowing() {
      return this.getGlowStatus() || this.isFakeGlowStatus();
   }

   public String getTeamName() {
      if (!this.teamName.isEmpty()) {
         return this.teamName;
      } else {
         String teamname = this.getObvuscatedTeamName();
         Iterator var2 = DataManager.getEGlowPlayers().iterator();

         while(var2.hasNext()) {
            EGlowPlayer eGlowPlayer = (EGlowPlayer)var2.next();
            if (eGlowPlayer.getTeamName().equals(teamname)) {
               int lastnumber = Integer.parseInt(teamname.substring(teamname.length() - 1));
               if (lastnumber < 9) {
                  ++lastnumber;
               } else {
                  --lastnumber;
               }

               teamname = teamname.substring(0, 14) + lastnumber;
            }
         }

         return teamname;
      }
   }

   private String getObvuscatedTeamName() {
      String playerName = this.getDisplayName().replace("_", "0");
      StringBuilder obvuscatedTeamname = new StringBuilder();
      if (playerName.length() > 8) {
         playerName = playerName.substring(0, 8);
      }

      for(int i = 0; i < playerName.length(); ++i) {
         int number = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".indexOf(playerName.charAt(i)) + 1;
         if (number < 10) {
            obvuscatedTeamname.append("0");
         }

         obvuscatedTeamname.append(number);
      }

      if (obvuscatedTeamname.length() > 15) {
         obvuscatedTeamname = new StringBuilder(obvuscatedTeamname.substring(0, 15));
      }

      String teamname = obvuscatedTeamname.toString();
      this.setTeamName(teamname);
      return obvuscatedTeamname.toString();
   }

   public Object getEntity() {
      return this.player != null ? this.player : this.citizensNPC;
   }

   public boolean hasPermission(String permission) {
      return this.getPlayer().hasPermission(permission) || this.getPlayer().hasPermission("eglow.effect.*") || this.getPlayer().isOp();
   }

   public void setupForceGlows() {
      if (EGlowMainConfig.MainConfig.SETTINGS_JOIN_FORCE_GLOWS_ENABLE.getBoolean() && this.getPlayer() != null && (!this.isInBlockedWorld() || !EGlowMainConfig.MainConfig.SETTINGS_JOIN_FORCE_GLOWS_BYPASS_BLOCKED_WORLDS.getBoolean())) {
         Iterator var1 = EGlowMainConfig.MainConfig.SETTINGS_JOIN_FORCE_GLOWS_LIST.getConfigSection().iterator();

         String permission;
         do {
            if (!var1.hasNext()) {
               return;
            }

            permission = (String)var1.next();
         } while(!this.getPlayer().hasPermission("eglow.force." + permission.toLowerCase()));

         EGlowEffect effect = DataManager.getEGlowEffect(EGlowMainConfig.MainConfig.SETTINGS_JOIN_FORCE_GLOWS_LIST.getString(permission));
         this.setForcedEffect(effect);
         this.setGlowEffect(effect);
      }
   }

   public boolean isForcedGlow(EGlowEffect effect) {
      return this.getForcedEffect() == null ? false : effect.getName().equals(this.getForcedEffect().getName());
   }

   public boolean hasNoForceGlow() {
      return this.getForcedEffect() == null;
   }

   public boolean isInBlockedWorld() {
      if (!EGlowMainConfig.MainConfig.WORLD_ENABLE.getBoolean()) {
         return false;
      } else {
         EnumUtil.GlowWorldAction action;
         try {
            action = EnumUtil.GlowWorldAction.valueOf(EGlowMainConfig.MainConfig.WORLD_ACTION.getString().toUpperCase() + "ED");
         } catch (IllegalArgumentException var3) {
            action = EnumUtil.GlowWorldAction.UNKNOWN;
         }

         List<String> worldList = EGlowMainConfig.MainConfig.WORLD_LIST.getStringList();
         switch(action) {
         case BLOCKED:
            if (worldList.contains(this.getPlayer().getWorld().getName().toLowerCase())) {
               return true;
            }
            break;
         case ALLOWED:
            if (!worldList.contains(this.getPlayer().getWorld().getName().toLowerCase())) {
               return true;
            }
            break;
         case UNKNOWN:
            return false;
         }

         return false;
      }
   }

   public boolean isInvisible() {
      return !EGlowMainConfig.MainConfig.SETTINGS_DISABLE_GLOW_WHEN_INVISIBLE.getBoolean() ? false : this.getPlayer().hasPotionEffect(PotionEffectType.INVISIBILITY);
   }

   public boolean getGlowStatus() {
      return this.glowStatus;
   }

   public void setForcedGlowDisableReason(EnumUtil.GlowDisableReason reason) {
      if (reason.equals(EnumUtil.GlowDisableReason.ANIMATION)) {
         reason = EnumUtil.GlowDisableReason.NONE;
      }

      this.glowDisableReason = reason;
   }

   public EnumUtil.GlowDisableReason setGlowDisableReason(EnumUtil.GlowDisableReason reason) {
      switch(reason) {
      case NONE:
         if (!this.glowDisableReason.equals(reason)) {
            if (this.isInBlockedWorld()) {
               this.glowDisableReason = EnumUtil.GlowDisableReason.BLOCKEDWORLD;
               return this.getGlowDisableReason();
            } else if (this.isInvisible()) {
               this.glowDisableReason = EnumUtil.GlowDisableReason.INVISIBLE;
               return this.getGlowDisableReason();
            }
         }
      case BLOCKEDWORLD:
      case INVISIBLE:
      case ANIMATION:
      default:
         this.glowDisableReason = reason;
         return this.getGlowDisableReason();
      }
   }

   public void setGlowVisibility(EnumUtil.GlowVisibility visibility) {
      if (!this.glowVisibility.equals(EnumUtil.GlowVisibility.UNSUPPORTEDCLIENT)) {
         this.glowVisibility = visibility;
         if (this.skipSaveData()) {
            this.setSaveData(true);
         }
      }

   }

   public void setGlowTargetMode(EnumUtil.GlowTargetMode glowTarget) {
      if (glowTarget != this.glowTargetMode) {
         this.glowTargetMode = glowTarget;
         PacketUtil.updateGlowTarget(this);
      }

   }

   public List<Player> getGlowTargets() {
      return this.customTargetList;
   }

   public void addGlowTarget(Player player) {
      if (!this.getGlowTargets().contains(player)) {
         this.getGlowTargets().add(player);
         PacketUtil.glowTargetChange(this, player, true);
      }

      if (!this.getGlowTargets().contains(this.getPlayer())) {
         this.getGlowTargets().add(this.getPlayer());
      }

      if (this.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.ALL)) {
         this.setGlowTargetMode(EnumUtil.GlowTargetMode.CUSTOM);
      }

   }

   public void removeGlowTarget(Player player) {
      if (this.getGlowTargets().contains(player)) {
         PacketUtil.glowTargetChange(this, player, false);
      }

      this.getGlowTargets().remove(player);
      if (this.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.CUSTOM) && this.getGlowTargets().isEmpty()) {
         this.setGlowTargetMode(EnumUtil.GlowTargetMode.ALL);
      }

   }

   public void setGlowTargets(List<Player> targets) {
      if (targets == null) {
         this.getGlowTargets().clear();
         this.getGlowTargets().add(this.getPlayer());
      } else {
         if (!targets.contains(this.getPlayer())) {
            targets.add(this.getPlayer());
         }

         this.setCustomTargetList(targets);
      }

      if (this.getGlowTargetMode().equals(EnumUtil.GlowTargetMode.ALL)) {
         this.setGlowTargetMode(EnumUtil.GlowTargetMode.CUSTOM);
      } else {
         Iterator var2 = ((List)Objects.requireNonNull(targets, "Can't loop over 'null'")).iterator();

         while(var2.hasNext()) {
            Player player = (Player)var2.next();
            PacketUtil.glowTargetChange(this, player, true);
         }
      }

   }

   public void resetGlowTargets() {
      this.getGlowTargets().clear();
      this.setGlowTargetMode(EnumUtil.GlowTargetMode.ALL);
   }

   public void setGlowOnJoin(boolean status) {
      if (this.glowOnJoin != status && this.skipSaveData()) {
         this.setSaveData(true);
      }

      this.glowOnJoin = status;
   }

   public boolean skipSaveData() {
      return !this.saveData;
   }

   public void setDataFromLastGlow(String lastGlow) {
      EGlowEffect effect = DataManager.getEGlowEffect(lastGlow);
      if (this.hasNoForceGlow()) {
         this.setGlowEffect(effect);
      }

   }

   public String getLastGlowName() {
      return this.getGlowEffect() != null ? this.getGlowEffect().getDisplayName() : EGlowMessageConfig.Message.COLOR.get("none");
   }

   public String getLastGlow() {
      return this.getGlowEffect() != null ? this.getGlowEffect().getName() : "none";
   }

   @Generated
   public EnumUtil.EntityType getEntityType() {
      return this.entityType;
   }

   @Generated
   public NPC getCitizensNPC() {
      return this.citizensNPC;
   }

   @Generated
   public Player getPlayer() {
      return this.player;
   }

   @Generated
   public String getDisplayName() {
      return this.displayName;
   }

   @Generated
   public UUID getUuid() {
      return this.uuid;
   }

   @Generated
   public ProtocolVersion getVersion() {
      return this.version;
   }

   @Generated
   public ChatColor getActiveColor() {
      return this.activeColor;
   }

   @Generated
   public boolean isFakeGlowStatus() {
      return this.fakeGlowStatus;
   }

   @Generated
   public EGlowEffect getGlowEffect() {
      return this.glowEffect;
   }

   @Generated
   public EGlowEffect getForcedEffect() {
      return this.forcedEffect;
   }

   @Generated
   public boolean isGlowOnJoin() {
      return this.glowOnJoin;
   }

   @Generated
   public boolean isActiveOnQuit() {
      return this.activeOnQuit;
   }

   @Generated
   public boolean isSaveData() {
      return this.saveData;
   }

   @Generated
   public EnumUtil.GlowDisableReason getGlowDisableReason() {
      return this.glowDisableReason;
   }

   @Generated
   public EnumUtil.GlowVisibility getGlowVisibility() {
      return this.glowVisibility;
   }

   @Generated
   public EnumUtil.GlowTargetMode getGlowTargetMode() {
      return this.glowTargetMode;
   }

   @Generated
   public List<Player> getCustomTargetList() {
      return this.customTargetList;
   }

   @Generated
   public String getSortingOrder() {
      Objects.requireNonNull(this);
      return "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
   }

   @Generated
   public void setCitizensNPC(NPC citizensNPC) {
      this.citizensNPC = citizensNPC;
   }

   @Generated
   public void setPlayer(Player player) {
      this.player = player;
   }

   @Generated
   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   @Generated
   public void setUuid(UUID uuid) {
      this.uuid = uuid;
   }

   @Generated
   public void setTeamName(String teamName) {
      this.teamName = teamName;
   }

   @Generated
   public void setVersion(ProtocolVersion version) {
      this.version = version;
   }

   @Generated
   public void setActiveColor(ChatColor activeColor) {
      this.activeColor = activeColor;
   }

   @Generated
   public void setGlowStatus(boolean glowStatus) {
      this.glowStatus = glowStatus;
   }

   @Generated
   public void setFakeGlowStatus(boolean fakeGlowStatus) {
      this.fakeGlowStatus = fakeGlowStatus;
   }

   @Generated
   public void setGlowEffect(EGlowEffect glowEffect) {
      this.glowEffect = glowEffect;
   }

   @Generated
   public void setForcedEffect(EGlowEffect forcedEffect) {
      this.forcedEffect = forcedEffect;
   }

   @Generated
   public void setActiveOnQuit(boolean activeOnQuit) {
      this.activeOnQuit = activeOnQuit;
   }

   @Generated
   public void setSaveData(boolean saveData) {
      this.saveData = saveData;
   }

   @Generated
   public void setCustomTargetList(List<Player> customTargetList) {
      this.customTargetList = customTargetList;
   }
}
