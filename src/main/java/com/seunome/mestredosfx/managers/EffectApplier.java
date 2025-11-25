package com.seunome.mestredosfx.managers;

import com.seunome.mestredosfx.MestreDosEfeitos;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EffectApplier implements Listener {

    private final GlowManager glowManager;
    private final ParticleManager particleManager;

    public EffectApplier(MestreDosEfeitos plugin) {
        this.glowManager = plugin.getGlowManager();
        this.particleManager = plugin.getParticleManager();
        
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Aplicar efeitos salvos
        glowManager.onPlayerJoin(player);
        particleManager.onPlayerJoin(player);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Limpar efeitos visuais (dados já estão salvos no banco)
        glowManager.onPlayerQuit(player);
        particleManager.onPlayerQuit(player);
    }

    public void shutdown() {
        // Parar todas as partículas ativas
        particleManager.shutdown();
    }
}

