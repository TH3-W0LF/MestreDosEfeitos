package me.eplugins.eglow.addon.citizens;

import lombok.Generated;
import me.eplugins.eglow.data.EGlowPlayer;
import me.eplugins.eglow.util.text.ChatUtil;
import net.citizensnpcs.api.persistence.Persist;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.util.DataKey;

public class EGlowCitizensTrait extends Trait {
   EGlowPlayer eGlowNPC = null;
   @Persist("LastEffect")
   String lastEffect = "none";

   public EGlowCitizensTrait() {
      super("eGlow");
   }

   public void load(DataKey key) {
      this.lastEffect = key.getString("LastEffect", "none");
   }

   public void save(DataKey key) {
      if (this.getEGlowNPC() != null) {
         this.lastEffect = this.getEGlowNPC().isGlowing() ? this.getEGlowNPC().getGlowEffect().getName() : "none";
         key.setString("LastEffect", this.lastEffect);
      }

   }

   public void onSpawn() {
      if (this.getEGlowNPC() == null) {
         this.eGlowNPC = new EGlowPlayer(this.npc);
      }

      this.getEGlowNPC().disableGlow(true);
      this.getEGlowNPC().setDataFromLastGlow(this.getLastEffect());

      try {
         if (!((EGlowCitizensTrait)this.npc.getOrAddTrait(EGlowCitizensTrait.class)).getLastEffect().equals("none")) {
            this.getEGlowNPC().activateGlow();
         }
      } catch (NoSuchMethodError var2) {
         ChatUtil.sendToConsole("&cYour Citizens version is outdated please update it", true);
      }

   }

   @Generated
   public EGlowPlayer getEGlowNPC() {
      return this.eGlowNPC;
   }

   @Generated
   public String getLastEffect() {
      return this.lastEffect;
   }
}
