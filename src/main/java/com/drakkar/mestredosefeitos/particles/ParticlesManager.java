package com.drakkar.mestredosefeitos.particles;

import com.drakkar.mestredosefeitos.MestreDosEfeitos;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ParticlesManager {

    private final MestreDosEfeitos plugin;
    private final Map<UUID, Helix> helixMap = new ConcurrentHashMap<>();
    private ParticleMenu menu;
    private ParticleJoinListener joinListener;
    private BukkitTask helixTask;

    public ParticlesManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (menu != null) {
            menu.unregister();
        }
        if (joinListener != null) {
            joinListener.unregister();
        }
        if (helixTask != null) {
            helixTask.cancel();
        }

        menu = new ParticleMenu(plugin, this);
        joinListener = new ParticleJoinListener(plugin, this);
        helixTask = new HelixTask(plugin, helixMap).runTaskTimer(plugin, 0L, 2L);
    }

    public void shutdown() {
        if (menu != null) {
            menu.unregister();
        }
        if (joinListener != null) {
            joinListener.unregister();
        }
        if (helixTask != null) {
            helixTask.cancel();
        }
        helixMap.clear();
    }

    public void openMenu(Player player) {
        if (menu == null) {
            FileConfiguration config = plugin.getParticlesConfig();
            player.sendMessage(MiniMessage.miniMessage()
                    .deserialize(config.getString("messages.menu-not-loaded", "<red>Menu indisponível.</red>")));
            return;
        }
        menu.open(player);
    }

    boolean applyEffect(Player player, ParticleEffectType effect) {
        Helix current = helixMap.get(player.getUniqueId());
        if (current != null && current.getParticle() == effect.getParticle()) {
            return false;
        }
        helixMap.put(player.getUniqueId(), new Helix(player, effect.getParticle()));
        return true;
    }

    boolean removeEffect(Player player) {
        return helixMap.remove(player.getUniqueId()) != null;
    }
}

