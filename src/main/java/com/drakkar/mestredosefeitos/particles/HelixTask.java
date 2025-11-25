package com.drakkar.mestredosefeitos.particles;

import com.drakkar.mestredosefeitos.MestreDosEfeitos;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

class HelixTask extends BukkitRunnable {

    private final MestreDosEfeitos plugin;
    private final Map<UUID, Helix> helixMap;

    HelixTask(MestreDosEfeitos plugin, Map<UUID, Helix> helixMap) {
        this.plugin = plugin;
        this.helixMap = helixMap;
    }

    @Override
    public void run() {
        Iterator<Map.Entry<UUID, Helix>> iterator = helixMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Helix> entry = iterator.next();
            Helix helix = entry.getValue();
            if (helix == null) {
                iterator.remove();
                continue;
            }

            Player player = helix.getPlayer();
            if (player == null || !player.isOnline()) {
                iterator.remove();
                continue;
            }

            try {
                helix.update();
            } catch (Exception ex) {
                plugin.getLogger().warning("Erro ao atualizar partículas de " + player.getName() + ": " + ex.getMessage());
                iterator.remove();
            }
        }
    }
}

