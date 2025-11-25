package me.eplugins.eglow;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.Objects;
import lombok.Generated;
import me.eplugins.eglow.addon.GSitAddon;
import me.eplugins.eglow.addon.LuckPermsAddon;
import me.eplugins.eglow.addon.PlaceholderAPIAddon;
import me.eplugins.eglow.addon.VaultAddon;
import me.eplugins.eglow.addon.VelocitabAddon;
import me.eplugins.eglow.addon.citizens.CitizensAddon;
import me.eplugins.eglow.addon.internal.AdvancedGlowVisibilityAddon;
import me.eplugins.eglow.addon.internal.TablistFormattingAddon;
import me.eplugins.eglow.addon.tab.TABAddon;
import me.eplugins.eglow.api.EGlowAPI;
import me.eplugins.eglow.command.EGlowCommand;
import me.eplugins.eglow.config.EGlowCustomEffectsConfig;
import me.eplugins.eglow.config.EGlowMainConfig;
import me.eplugins.eglow.config.EGlowMessageConfig;
import me.eplugins.eglow.custommenu.CustomMenus;
import me.eplugins.eglow.data.DataManager;
import me.eplugins.eglow.database.EGlowPlayerdataManager;
import me.eplugins.eglow.event.EGlowEventListener;
import me.eplugins.eglow.util.DebugUtil;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.ProtocolVersion;
import me.eplugins.eglow.util.packets.nms.NMSHook;
import me.eplugins.eglow.util.text.ChatUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EGlow extends JavaPlugin {
   private static EGlow instance;
   private static EGlowAPI api;
   private boolean upToDate = true;
   private boolean isMMSupported = false;
   private String latestVersion = "";
   private boolean beta = false;
   private AdvancedGlowVisibilityAddon advancedGlowVisibilityAddon;
   private TablistFormattingAddon tablistFormattingAddon;
   private CustomMenus customMenus;
   private CitizensAddon citizensAddon;
   private TABAddon tabAddon;
   private LuckPermsAddon lpAddon;
   private VaultAddon vaultAddon;
   private GSitAddon gsitAddon;
   private VelocitabAddon velocitabAddon;

   public void onEnable() {
      instance = this;
      api = new EGlowAPI();
      if (this.versionIsCompactible()) {
         ProtocolVersion.SERVER_VERSION = ProtocolVersion.fromServerString(Bukkit.getBukkitVersion().split("-")[0]);
         NMSHook.initialize();
         this.loadConfigs();
         DataManager.initialize();
         this.registerEventsAndCommands();
         NMSHook.scheduleTask(true, this::checkForUpdates);
         this.runAddonHooks();
         this.runPlayerCheckOnEnable();
         if (EGlowMainConfig.MainConfig.ENABLE_BETA.getBoolean()) {
            this.setBeta(true);
         }

         if (this.isBeta()) {
            this.setCustomMenus(new CustomMenus());
         }
      } else {
         ChatUtil.sendToConsole("Disabling eGlow! Your server version is not compatible! (" + DebugUtil.getServerVersion() + ")", false);
         this.getServer().getPluginManager().disablePlugin(this);
      }

   }

   public void onDisable() {
      if (this.getAdvancedGlowVisibilityAddon() != null) {
         this.getAdvancedGlowVisibilityAddon().shutdown();
      }

      if (this.getLpAddon() != null) {
         this.getLpAddon().unload();
      }

      this.getServer().getMessenger().unregisterOutgoingPluginChannel(this);
      this.getServer().getMessenger().unregisterIncomingPluginChannel(this);
      this.runPlayerCheckOnDisable();
   }

   private boolean versionIsCompactible() {
      return !DebugUtil.getServerVersion().equals("v_1_9_R1") && DebugUtil.getMainVersion() >= 9 && DebugUtil.getMainVersion() <= 21;
   }

   private void loadConfigs() {
      EGlowMainConfig.initialize();
      EGlowMessageConfig.initialize();
      EGlowCustomEffectsConfig.initialize();
      EGlowPlayerdataManager.initialize();
   }

   private void registerEventsAndCommands() {
      ((PluginCommand)Objects.requireNonNull(this.getCommand("eglow"))).setExecutor(new EGlowCommand());
      new EGlowEventListener();
   }

   private void runAddonHooks() {
      NMSHook.scheduleTask(false, () -> {
         if (EGlowMainConfig.MainConfig.ADVANCED_GLOW_VISIBILITY_ENABLE.getBoolean() && this.getAdvancedGlowVisibilityAddon() == null) {
            this.setAdvancedGlowVisibilityAddon(new AdvancedGlowVisibilityAddon());
         }

         if (Dependency.PLACEHOLDER_API.isLoaded()) {
            new PlaceholderAPIAddon();
         }

         if (Dependency.VAULT.isLoaded()) {
            this.setVaultAddon(new VaultAddon(getInstance()));
         }

         if (Dependency.CITIZENS.isLoaded() && this.getCitizensAddon() == null) {
            this.setCitizensAddon(new CitizensAddon());
         }

         if (Dependency.GSIT.isLoaded() && Integer.parseInt(Dependency.GSIT.getVersion().replaceAll("[^\\d]", "")) >= 200) {
            this.setGsitAddon(new GSitAddon(getInstance()));
         }

         this.setTabAddon(new TABAddon(getInstance()));
         if (Dependency.LUCKPERMS.isLoaded()) {
            this.setLpAddon(new LuckPermsAddon(getInstance()));
         }

         if (EGlowMainConfig.MainConfig.ADVANCED_VELOCITAB_MESSAGING.getBoolean() && this.getVelocitabAddon() == null) {
            this.setVelocitabAddon(new VelocitabAddon(getInstance()));
         }

         this.setTablistFormattingAddon(new TablistFormattingAddon(getInstance()));
      });
   }

   private void runPlayerCheckOnEnable() {
      if (!this.getServer().getOnlinePlayers().isEmpty()) {
         Iterator var1 = this.getServer().getOnlinePlayers().iterator();

         while(var1.hasNext()) {
            Player player = (Player)var1.next();
            if (DataManager.getEGlowPlayer(player) == null) {
               EGlowEventListener.PlayerConnect(player, player.getUniqueId());
            }
         }
      }

   }

   private void runPlayerCheckOnDisable() {
      if (!this.getServer().getOnlinePlayers().isEmpty()) {
         Iterator var1 = this.getServer().getOnlinePlayers().iterator();

         while(var1.hasNext()) {
            Player player = (Player)var1.next();
            if (DataManager.getEGlowPlayer(player) != null) {
               EGlowEventListener.PlayerServerShutDown(player);
            }
         }
      }

   }

   private void checkForUpdates() {
      try {
         URL url = new URL("https://raw.githubusercontent.com/SlyOtters/EGlowTracker/main/Version.txt");
         String currentVersion = getInstance().getDescription().getVersion();
         String latestVersion = (new BufferedReader(new InputStreamReader(url.openConnection().getInputStream()))).readLine();
         this.setLatestVersion(latestVersion);
         if (!currentVersion.contains("PRE") && !currentVersion.contains("SNAPSHOT")) {
            if (!latestVersion.contains(currentVersion)) {
               this.setUpToDate(false);
            }
         } else {
            String betaVersion = currentVersion.split("-")[0];
            this.setUpToDate(!betaVersion.equals(latestVersion));
         }
      } catch (Exception var5) {
      }

   }

   public static EGlow getInstance() {
      return instance;
   }

   public static EGlowAPI getAPI() {
      return api;
   }

   @Generated
   public boolean isUpToDate() {
      return this.upToDate;
   }

   @Generated
   public boolean isMMSupported() {
      return this.isMMSupported;
   }

   @Generated
   public String getLatestVersion() {
      return this.latestVersion;
   }

   @Generated
   public boolean isBeta() {
      return this.beta;
   }

   @Generated
   public AdvancedGlowVisibilityAddon getAdvancedGlowVisibilityAddon() {
      return this.advancedGlowVisibilityAddon;
   }

   @Generated
   public TablistFormattingAddon getTablistFormattingAddon() {
      return this.tablistFormattingAddon;
   }

   @Generated
   public CustomMenus getCustomMenus() {
      return this.customMenus;
   }

   @Generated
   public CitizensAddon getCitizensAddon() {
      return this.citizensAddon;
   }

   @Generated
   public TABAddon getTabAddon() {
      return this.tabAddon;
   }

   @Generated
   public LuckPermsAddon getLpAddon() {
      return this.lpAddon;
   }

   @Generated
   public VaultAddon getVaultAddon() {
      return this.vaultAddon;
   }

   @Generated
   public GSitAddon getGsitAddon() {
      return this.gsitAddon;
   }

   @Generated
   public VelocitabAddon getVelocitabAddon() {
      return this.velocitabAddon;
   }

   @Generated
   public void setUpToDate(boolean upToDate) {
      this.upToDate = upToDate;
   }

   @Generated
   public void setMMSupported(boolean isMMSupported) {
      this.isMMSupported = isMMSupported;
   }

   @Generated
   public void setLatestVersion(String latestVersion) {
      this.latestVersion = latestVersion;
   }

   @Generated
   public void setBeta(boolean beta) {
      this.beta = beta;
   }

   @Generated
   public void setAdvancedGlowVisibilityAddon(AdvancedGlowVisibilityAddon advancedGlowVisibilityAddon) {
      this.advancedGlowVisibilityAddon = advancedGlowVisibilityAddon;
   }

   @Generated
   public void setTablistFormattingAddon(TablistFormattingAddon tablistFormattingAddon) {
      this.tablistFormattingAddon = tablistFormattingAddon;
   }

   @Generated
   public void setCustomMenus(CustomMenus customMenus) {
      this.customMenus = customMenus;
   }

   @Generated
   public void setCitizensAddon(CitizensAddon citizensAddon) {
      this.citizensAddon = citizensAddon;
   }

   @Generated
   public void setTabAddon(TABAddon tabAddon) {
      this.tabAddon = tabAddon;
   }

   @Generated
   public void setLpAddon(LuckPermsAddon lpAddon) {
      this.lpAddon = lpAddon;
   }

   @Generated
   public void setVaultAddon(VaultAddon vaultAddon) {
      this.vaultAddon = vaultAddon;
   }

   @Generated
   public void setGsitAddon(GSitAddon gsitAddon) {
      this.gsitAddon = gsitAddon;
   }

   @Generated
   public void setVelocitabAddon(VelocitabAddon velocitabAddon) {
      this.velocitabAddon = velocitabAddon;
   }
}
