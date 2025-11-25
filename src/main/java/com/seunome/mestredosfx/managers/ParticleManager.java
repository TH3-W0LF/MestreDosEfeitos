package com.seunome.mestredosfx.managers;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ParticleManager {

    private final MestreDosEfeitos plugin;
    private final PlayerEffectDAO dao;
    private final Map<UUID, String> activeParticles;
    private final Map<UUID, BukkitRunnable> particleTasks;
    private final Map<String, ParticleInfo> particleTypes;

    public ParticleManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.dao = new PlayerEffectDAO(plugin);
        this.activeParticles = new HashMap<>();
        this.particleTasks = new HashMap<>();
        this.particleTypes = new HashMap<>();
        
        initializeParticleTypes();
        loadActiveParticles();
    }

    private void initializeParticleTypes() {
        // Definir todos os tipos de partículas disponíveis (baseado na source original)
        particleTypes.put("helix", new ParticleInfo(Particle.ENCHANT, 0.3, 0.5, 0.3));
        particleTypes.put("spell", new ParticleInfo(Particle.WITCH, 0.3, 0.5, 0.3));
        particleTypes.put("flame", new ParticleInfo(Particle.FLAME, 0.3, 0.5, 0.3));
        particleTypes.put("fire", new ParticleInfo(Particle.FLAME, 0.3, 0.5, 0.3));
        particleTypes.put("heart", new ParticleInfo(Particle.HEART, 0.5, 1.0, 0.5));
        particleTypes.put("cloud", new ParticleInfo(Particle.CLOUD, 0.5, 0.8, 0.5));
        particleTypes.put("smoke", new ParticleInfo(Particle.SMOKE, 0.3, 0.5, 0.3));
        particleTypes.put("damage_indicator", new ParticleInfo(Particle.DAMAGE_INDICATOR, 0.3, 0.5, 0.3));
        particleTypes.put("water", new ParticleInfo(Particle.DRIPPING_WATER, 0.3, 0.5, 0.3));
        particleTypes.put("lava", new ParticleInfo(Particle.DRIPPING_LAVA, 0.3, 0.5, 0.3));
        particleTypes.put("crit", new ParticleInfo(Particle.CRIT, 0.3, 0.5, 0.3));
        particleTypes.put("soul", new ParticleInfo(Particle.SOUL, 0.3, 0.5, 0.3));
        particleTypes.put("note", new ParticleInfo(Particle.NOTE, 0.3, 0.5, 0.3));
        particleTypes.put("slime", new ParticleInfo(Particle.ITEM_SLIME, 0.3, 0.5, 0.3));
        particleTypes.put("snow", new ParticleInfo(Particle.SNOWFLAKE, 0.3, 0.5, 0.3));
        particleTypes.put("drip_lava", new ParticleInfo(Particle.DRIPPING_LAVA, 0.3, 0.5, 0.3));
        particleTypes.put("spark", new ParticleInfo(Particle.FIREWORK, 0.4, 0.3, 0.4));
        particleTypes.put("dragon_breath", new ParticleInfo(Particle.DRAGON_BREATH, 0.3, 0.5, 0.3));
        particleTypes.put("end_rod", new ParticleInfo(Particle.END_ROD, 0.3, 0.5, 0.3));
        particleTypes.put("totem", new ParticleInfo(Particle.ENCHANTED_HIT, 0.4, 0.6, 0.4));
        particleTypes.put("portal", new ParticleInfo(Particle.PORTAL, 0.4, 0.4, 0.4));
        particleTypes.put("sonic_boom", new ParticleInfo(Particle.SONIC_BOOM, 0.3, 0.5, 0.3));
        particleTypes.put("falling_lava", new ParticleInfo(Particle.FALLING_LAVA, 0.3, 0.5, 0.3));
        particleTypes.put("falling_water", new ParticleInfo(Particle.FALLING_WATER, 0.3, 0.5, 0.3));
        particleTypes.put("snow_shovel", new ParticleInfo(Particle.ITEM_SNOWBALL, 0.3, 0.5, 0.3));
        particleTypes.put("composter", new ParticleInfo(Particle.COMPOSTER, 0.3, 0.5, 0.3));
        particleTypes.put("angry_villager", new ParticleInfo(Particle.ANGRY_VILLAGER, 0.3, 0.5, 0.3));
        particleTypes.put("happy_villager", new ParticleInfo(Particle.HAPPY_VILLAGER, 0.3, 0.5, 0.3));
        particleTypes.put("explosion", new ParticleInfo(Particle.EXPLOSION, 0.3, 0.5, 0.3));
        particleTypes.put("bubble", new ParticleInfo(Particle.BUBBLE, 0.3, 0.5, 0.3));
        particleTypes.put("splash", new ParticleInfo(Particle.SPLASH, 0.3, 0.5, 0.3));
        particleTypes.put("fishing", new ParticleInfo(Particle.FISHING, 0.3, 0.5, 0.3));
        particleTypes.put("large_smoke", new ParticleInfo(Particle.LARGE_SMOKE, 0.3, 0.5, 0.3));
        particleTypes.put("instant_effect", new ParticleInfo(Particle.INSTANT_EFFECT, 0.3, 0.5, 0.3));
        particleTypes.put("mycelium", new ParticleInfo(Particle.MYCELIUM, 0.3, 0.5, 0.3));
        particleTypes.put("block", new ParticleInfo(Particle.BLOCK, 0.3, 0.5, 0.3));
        particleTypes.put("rain", new ParticleInfo(Particle.RAIN, 0.3, 0.5, 0.3));
        particleTypes.put("dust_plume", new ParticleInfo(Particle.DUST_PLUME, 0.3, 0.5, 0.3));
        particleTypes.put("sweep_attack", new ParticleInfo(Particle.SWEEP_ATTACK, 0.3, 0.5, 0.3));
        particleTypes.put("sculk_soul", new ParticleInfo(Particle.SCULK_SOUL, 0.3, 0.5, 0.3));
        particleTypes.put("spit", new ParticleInfo(Particle.SPIT, 0.3, 0.5, 0.3));
        particleTypes.put("squid_ink", new ParticleInfo(Particle.SQUID_INK, 0.3, 0.5, 0.3));
        particleTypes.put("bubble_pop", new ParticleInfo(Particle.BUBBLE_POP, 0.3, 0.5, 0.3));
        particleTypes.put("current_down", new ParticleInfo(Particle.CURRENT_DOWN, 0.3, 0.5, 0.3));
        particleTypes.put("bubble_column_up", new ParticleInfo(Particle.BUBBLE_COLUMN_UP, 0.3, 0.5, 0.3));
        particleTypes.put("nautilus", new ParticleInfo(Particle.NAUTILUS, 0.3, 0.5, 0.3));
        particleTypes.put("dolphin", new ParticleInfo(Particle.DOLPHIN, 0.3, 0.5, 0.3));
        particleTypes.put("sneeze", new ParticleInfo(Particle.SNEEZE, 0.3, 0.5, 0.3));
        particleTypes.put("campfire_cosy_smoke", new ParticleInfo(Particle.CAMPFIRE_COSY_SMOKE, 0.3, 0.5, 0.3));
        particleTypes.put("campfire_signal_smoke", new ParticleInfo(Particle.CAMPFIRE_SIGNAL_SMOKE, 0.3, 0.5, 0.3));
        particleTypes.put("flash", new ParticleInfo(Particle.FLASH, 0.3, 0.5, 0.3));
        particleTypes.put("landing_lava", new ParticleInfo(Particle.LANDING_LAVA, 0.3, 0.5, 0.3));
        particleTypes.put("dripping_honey", new ParticleInfo(Particle.DRIPPING_HONEY, 0.3, 0.5, 0.3));
        particleTypes.put("falling_honey", new ParticleInfo(Particle.FALLING_HONEY, 0.3, 0.5, 0.3));
        particleTypes.put("landing_honey", new ParticleInfo(Particle.LANDING_HONEY, 0.3, 0.5, 0.3));
        particleTypes.put("falling_nectar", new ParticleInfo(Particle.FALLING_NECTAR, 0.3, 0.5, 0.3));
        particleTypes.put("soul_fire_flame", new ParticleInfo(Particle.SOUL_FIRE_FLAME, 0.3, 0.5, 0.3));
        particleTypes.put("ash", new ParticleInfo(Particle.ASH, 0.3, 0.5, 0.3));
        particleTypes.put("crimson_spore", new ParticleInfo(Particle.CRIMSON_SPORE, 0.3, 0.5, 0.3));
        particleTypes.put("warped_spore", new ParticleInfo(Particle.WARPED_SPORE, 0.3, 0.5, 0.3));
        particleTypes.put("dripping_obsidian_tear", new ParticleInfo(Particle.DRIPPING_OBSIDIAN_TEAR, 0.3, 0.5, 0.3));
        particleTypes.put("falling_obsidian_tear", new ParticleInfo(Particle.FALLING_OBSIDIAN_TEAR, 0.3, 0.5, 0.3));
        particleTypes.put("landing_obsidian_tear", new ParticleInfo(Particle.LANDING_OBSIDIAN_TEAR, 0.3, 0.5, 0.3));
        particleTypes.put("reverse_portal", new ParticleInfo(Particle.REVERSE_PORTAL, 0.3, 0.5, 0.3));
        particleTypes.put("white_ash", new ParticleInfo(Particle.WHITE_ASH, 0.3, 0.5, 0.3));
        particleTypes.put("egg_crack", new ParticleInfo(Particle.EGG_CRACK, 0.3, 0.5, 0.3));
        particleTypes.put("glowstone_dust", new ParticleInfo(Particle.GLOW, 0.3, 0.5, 0.3));
        particleTypes.put("falling_spore_blossom", new ParticleInfo(Particle.FALLING_SPORE_BLOSSOM, 0.3, 0.5, 0.3));
        particleTypes.put("spore_blossom_air", new ParticleInfo(Particle.SPORE_BLOSSOM_AIR, 0.3, 0.5, 0.3));
        particleTypes.put("magic", new ParticleInfo(Particle.ENCHANT, 0.5, 0.5, 0.5));
        particleTypes.put("rainbow", new ParticleInfo(Particle.DUST, 0.3, 0.5, 0.3));
    }

    private void loadActiveParticles() {
        // Carregar partículas ativas do banco de dados para jogadores online
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            String particleId = dao.getActiveParticle(player.getUniqueId());
            if (particleId != null && !particleId.isEmpty()) {
                activeParticles.put(player.getUniqueId(), particleId);
                startParticleEffect(player, particleId);
            }
        }
    }

    public List<String> getAllParticleIds() {
        return new ArrayList<>(particleTypes.keySet());
    }

    public boolean hasParticle(Player player) {
        return activeParticles.containsKey(player.getUniqueId());
    }

    public String getActiveParticle(Player player) {
        return activeParticles.get(player.getUniqueId());
    }

    public void setParticle(Player player, String particleId) {
        // Remover partícula atual se houver
        removeParticle(player);

        if (particleId == null || particleId.isEmpty()) {
            return;
        }

        activeParticles.put(player.getUniqueId(), particleId);
        dao.setActiveParticle(player.getUniqueId(), particleId);
        startParticleEffect(player, particleId);
    }

    public void removeParticle(Player player) {
        activeParticles.remove(player.getUniqueId());
        dao.removeActiveParticle(player.getUniqueId());
        stopParticleEffect(player);
    }

    private void startParticleEffect(Player player, String particleId) {
        // Parar efeito anterior se houver
        stopParticleEffect(player);

        ParticleInfo info = particleTypes.get(particleId);
        if (info == null) {
            return;
        }

        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    cancel();
                    particleTasks.remove(player.getUniqueId());
                    return;
                }

                Location loc = player.getLocation();
                
                if (particleId.equals("rainbow")) {
                    // Efeito rainbow especial com cores variadas
                    spawnRainbowParticles(loc);
                } else {
                    // Efeito padrão
                    player.getWorld().spawnParticle(
                        info.particle,
                        loc.clone().add(0, 0.5, 0),
                        5,
                        info.offsetX,
                        info.offsetY,
                        info.offsetZ,
                        0.1
                    );
                }
            }
        };

        task.runTaskTimer(plugin, 0L, 5L); // A cada 5 ticks (0.25 segundos)
        particleTasks.put(player.getUniqueId(), task);
    }

    private void spawnRainbowParticles(Location loc) {
        org.bukkit.Particle.DustOptions[] colors = {
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 1.0f),
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.ORANGE, 1.0f),
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.YELLOW, 1.0f),
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.LIME, 1.0f),
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.BLUE, 1.0f),
            new org.bukkit.Particle.DustOptions(org.bukkit.Color.PURPLE, 1.0f)
        };
        
        int colorIndex = (int) (System.currentTimeMillis() / 500) % colors.length;
        loc.getWorld().spawnParticle(
            Particle.DUST,
            loc.clone().add(0, 0.5, 0),
            5,
            0.3, 0.5, 0.3,
            colors[colorIndex]
        );
    }

    private void stopParticleEffect(Player player) {
        BukkitRunnable task = particleTasks.remove(player.getUniqueId());
        if (task != null) {
            task.cancel();
        }
    }

    public void onPlayerJoin(Player player) {
        String particleId = dao.getActiveParticle(player.getUniqueId());
        if (particleId != null && !particleId.isEmpty()) {
            activeParticles.put(player.getUniqueId(), particleId);
            startParticleEffect(player, particleId);
        }
    }

    public void onPlayerQuit(Player player) {
        stopParticleEffect(player);
    }

    public void shutdown() {
        for (BukkitRunnable task : particleTasks.values()) {
            task.cancel();
        }
        particleTasks.clear();
    }

    public boolean isValidParticleId(String particleId) {
        return particleTypes.containsKey(particleId);
    }

    private static class ParticleInfo {
        final Particle particle;
        final double offsetX;
        final double offsetY;
        final double offsetZ;

        ParticleInfo(Particle particle, double offsetX, double offsetY, double offsetZ) {
            this.particle = particle;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.offsetZ = offsetZ;
        }
    }
}

