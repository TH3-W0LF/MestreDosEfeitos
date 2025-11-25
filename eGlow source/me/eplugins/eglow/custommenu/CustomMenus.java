package me.eplugins.eglow.custommenu;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.Generated;
import me.eplugins.eglow.EGlow;
import me.eplugins.eglow.custommenu.addon.HeadDatabaseAddon;
import me.eplugins.eglow.custommenu.addon.ItemsAdderAddon;
import me.eplugins.eglow.custommenu.addon.MiniMessagesAddon;
import me.eplugins.eglow.custommenu.addon.OraxenAddon;
import me.eplugins.eglow.custommenu.addon.PAPIAddon;
import me.eplugins.eglow.custommenu.config.EGlowCustomMenuConfig;
import me.eplugins.eglow.custommenu.event.EGlowCustomMenuListener;
import me.eplugins.eglow.custommenu.menu.manager.item.helper.version.VersionHelper;
import me.eplugins.eglow.util.enums.Dependency;
import me.eplugins.eglow.util.packets.nms.NMSStorage;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class CustomMenus implements PluginMessageListener {
   private HeadDatabaseAddon hdbAddon;
   private ItemsAdderAddon itemsAdderAddon;
   private OraxenAddon oraxenAddon;
   private MiniMessagesAddon mmAddon;
   private PAPIAddon papiAddon;

   public CustomMenus() {
      if (EGlow.getInstance().isBeta()) {
         EGlow.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(EGlow.getInstance(), "BungeeCord");
         EGlow.getInstance().getServer().getMessenger().registerIncomingPluginChannel(EGlow.getInstance(), "BungeeCord", this);
         EGlowCustomMenuConfig.initialize();
         new EGlowCustomMenuListener();
         VersionHelper.initialize();
         if (Dependency.HEADDATABASE.isLoaded()) {
            this.setHdbAddon(new HeadDatabaseAddon());
         }

         if (Dependency.ITEMSADDER.isLoaded()) {
            this.setItemsAdderAddon(new ItemsAdderAddon());
         }

         if (Dependency.ORAXEN.isLoaded()) {
            this.setOraxenAddon(new OraxenAddon());
         }

         if (Dependency.PLACEHOLDER_API.isLoaded()) {
            this.setPapiAddon(new PAPIAddon());
         }

         if (NMSStorage.classExists("net.kyori.adventure.text.minimessage.MiniMessage")) {
            this.setMmAddon(new MiniMessagesAddon());
         }
      }

   }

   public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
   }

   public void sendPlayerToServer(Player player, String name) {
      ByteArrayDataOutput out = ByteStreams.newDataOutput();
      out.writeUTF("Connect");
      out.writeUTF(name);
      player.sendPluginMessage(EGlow.getInstance(), "BungeeCord", out.toByteArray());
   }

   @Generated
   public HeadDatabaseAddon getHdbAddon() {
      return this.hdbAddon;
   }

   @Generated
   public ItemsAdderAddon getItemsAdderAddon() {
      return this.itemsAdderAddon;
   }

   @Generated
   public OraxenAddon getOraxenAddon() {
      return this.oraxenAddon;
   }

   @Generated
   public MiniMessagesAddon getMmAddon() {
      return this.mmAddon;
   }

   @Generated
   public PAPIAddon getPapiAddon() {
      return this.papiAddon;
   }

   @Generated
   public void setHdbAddon(HeadDatabaseAddon hdbAddon) {
      this.hdbAddon = hdbAddon;
   }

   @Generated
   public void setItemsAdderAddon(ItemsAdderAddon itemsAdderAddon) {
      this.itemsAdderAddon = itemsAdderAddon;
   }

   @Generated
   public void setOraxenAddon(OraxenAddon oraxenAddon) {
      this.oraxenAddon = oraxenAddon;
   }

   @Generated
   public void setMmAddon(MiniMessagesAddon mmAddon) {
      this.mmAddon = mmAddon;
   }

   @Generated
   public void setPapiAddon(PAPIAddon papiAddon) {
      this.papiAddon = papiAddon;
   }
}
