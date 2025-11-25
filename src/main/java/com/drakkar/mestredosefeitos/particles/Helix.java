package com.drakkar.mestredosefeitos.particles;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public class Helix {

    private final Player player;
    private final Particle particle;
    private double time;
    private static final double RADIUS = 1.0;
    private static final double HEIGHT = 2.0;
    private static final int ROTATIONS = 2;
    private static final double VIEW_DISTANCE = 16.0;

    public Helix(Player player, Particle particle) {
        this.player = player;
        this.particle = particle;
    }

    public Player getPlayer() {
        return player;
    }

    public Particle getParticle() {
        return particle;
    }

    public void update() {
        if (player == null || !player.isOnline()) {
            return;
        }

        time += Math.PI / 16;
        double angle = time;
        double x = RADIUS * Math.cos(angle);
        double z = RADIUS * Math.sin(angle);
        double y = (HEIGHT / (2 * Math.PI * ROTATIONS)) * time;

        Location loc = player.getLocation().clone().add(x, y, z);

        player.getWorld().getPlayers().forEach(nearby -> {
            if (nearby.getLocation().distanceSquared(player.getLocation()) <= VIEW_DISTANCE * VIEW_DISTANCE) {
                nearby.spawnParticle(particle, loc, 1, 0, 0, 0, 0);
            }
        });

        double fullRotation = 2 * Math.PI * ROTATIONS;
        if (time > fullRotation) {
            time -= fullRotation;
        }
    }
}

