package me.eplugins.eglow.addon;

import me.eplugins.eglow.EGlow;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class AbstractAddonBase {
   protected final EGlow eGlowInstance;

   public AbstractAddonBase(EGlow eGlowInstance) {
      this.eGlowInstance = eGlowInstance;
      if (this instanceof Listener) {
         Bukkit.getPluginManager().registerEvents((Listener)this, this.getEGlowInstance());
      }

   }

   public EGlow getEGlowInstance() {
      return this.eGlowInstance;
   }
}
