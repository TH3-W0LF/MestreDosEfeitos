package com.drakkar.mestredosefeitos.particles;

import org.bukkit.Material;
import org.bukkit.Particle;

public enum ParticleEffectType {

    HELIX("Estrela", Particle.ENCHANT, Material.NETHER_STAR),
    SPELL("Magia", Particle.WITCH, Material.BLAZE_POWDER),
    FLAME("Chamas", Particle.FLAME, Material.BLAZE_ROD),
    HEART("Coracoes", Particle.HEART, Material.RED_DYE),
    CLOUD("Nuvem", Particle.CLOUD, Material.WHITE_WOOL),
    SMOKE("Fumaca", Particle.SMOKE, Material.COAL),
    DAMAGE_INDICATOR("Indicador de Dano", Particle.DAMAGE_INDICATOR, Material.REDSTONE),
    WATER("Gotas de Agua", Particle.DRIPPING_WATER, Material.WATER_BUCKET),
    LAVA("Gotas de Lava", Particle.DRIPPING_LAVA, Material.LAVA_BUCKET),
    CRIT("Critico", Particle.CRIT, Material.IRON_SWORD),
    SOUL("Alma", Particle.SOUL, Material.SOUL_SOIL),
    NOTE("Notas Musicais", Particle.NOTE, Material.NOTE_BLOCK),
    SLIME("Slime", Particle.ITEM_SLIME, Material.SLIME_BLOCK),
    SNOW("Flocos de Neve", Particle.SNOWFLAKE, Material.SNOWBALL),
    DRIP_LAVA("Lava Pingando", Particle.DRIPPING_LAVA, Material.MAGMA_BLOCK),
    SPARK("Centelhas", Particle.FIREWORK, Material.FIREWORK_STAR),
    DRAGON_BREATH("Sopro de Dragao", Particle.DRAGON_BREATH, Material.DRAGON_BREATH),
    END_ROD("Varinha Magica", Particle.END_ROD, Material.END_ROD),
    TOTEM("Totem Mistico", Particle.ENCHANTED_HIT, Material.TOTEM_OF_UNDYING),
    PORTAL("Portal", Particle.PORTAL, Material.ENDER_PEARL),
    SONIC_BOOM("Explosao Sonica", Particle.SONIC_BOOM, Material.FIREWORK_ROCKET),
    FALLING_LAVA("Lava Caindo", Particle.FALLING_LAVA, Material.LAVA_BUCKET),
    FALLING_WATER("Agua Caindo", Particle.FALLING_WATER, Material.WATER_BUCKET),
    SNOW_SHOVEL("Nevasca", Particle.ITEM_SNOWBALL, Material.SNOWBALL),
    COMPOSTER("Compostagem", Particle.COMPOSTER, Material.COMPOSTER),
    ANGRY_VILLAGER("Aldeao Zangado", Particle.ANGRY_VILLAGER, Material.VILLAGER_SPAWN_EGG),
    HAPPY_VILLAGER("Aldeao Feliz", Particle.HAPPY_VILLAGER, Material.EMERALD),
    EXPLOSION("Explosao", Particle.EXPLOSION, Material.TNT),
    BUBBLE("Bolhas", Particle.BUBBLE, Material.PRISMARINE_CRYSTALS),
    SPLASH("Respingo", Particle.SPLASH, Material.WATER_BUCKET),
    FISHING("Pesca", Particle.FISHING, Material.FISHING_ROD),
    LARGE_SMOKE("Fumaca Grande", Particle.LARGE_SMOKE, Material.COAL_BLOCK),
    INSTANT_EFFECT("Efeito Instantaneo", Particle.INSTANT_EFFECT, Material.POTION),
    MYCELIUM("Micelio", Particle.MYCELIUM, Material.MYCELIUM),
    BLOCK("Bloco", Particle.BLOCK, Material.STONE),
    RAIN("Chuva", Particle.RAIN, Material.WATER_BUCKET),
    DUST_PLUME("Pluma de Poeira", Particle.DUST_PLUME, Material.SAND),
    SWEEP_ATTACK("Ataque em Area", Particle.SWEEP_ATTACK, Material.IRON_SWORD),
    SCULK_SOUL("Alma Sculk", Particle.SCULK_SOUL, Material.SCULK_CATALYST),
    SPIT("Cuspe", Particle.SPIT, Material.SNOWBALL),
    SQUID_INK("Tinta de Lula", Particle.SQUID_INK, Material.INK_SAC),
    BUBBLE_POP("Estouro de Bolha", Particle.BUBBLE_POP, Material.PRISMARINE_CRYSTALS),
    CURRENT_DOWN("Corrente para Baixo", Particle.CURRENT_DOWN, Material.WATER_BUCKET),
    BUBBLE_COLUMN_UP("Coluna de Bolhas", Particle.BUBBLE_COLUMN_UP, Material.PRISMARINE_CRYSTALS),
    NAUTILUS("Nautilus", Particle.NAUTILUS, Material.NAUTILUS_SHELL),
    DOLPHIN("Golfinho", Particle.DOLPHIN, Material.PRISMARINE_CRYSTALS),
    SNEEZE("Espirro", Particle.SNEEZE, Material.SUGAR),
    CAMPFIRE_COSY_SMOKE("Fumaca Aconchegante", Particle.CAMPFIRE_COSY_SMOKE, Material.CAMPFIRE),
    CAMPFIRE_SIGNAL_SMOKE("Fumaca de Sinal", Particle.CAMPFIRE_SIGNAL_SMOKE, Material.CAMPFIRE),
    FLASH("Flash", Particle.FLASH, Material.GLOWSTONE),
    LANDING_LAVA("Lava Aterrissando", Particle.LANDING_LAVA, Material.LAVA_BUCKET),
    DRIPPING_HONEY("Mel Gotejando", Particle.DRIPPING_HONEY, Material.HONEY_BLOCK),
    FALLING_HONEY("Mel Caindo", Particle.FALLING_HONEY, Material.HONEY_BLOCK),
    LANDING_HONEY("Mel Aterrissando", Particle.LANDING_HONEY, Material.HONEY_BLOCK),
    FALLING_NECTAR("Nectar Caindo", Particle.FALLING_NECTAR, Material.HONEY_BOTTLE),
    SOUL_FIRE_FLAME("Chama de Alma", Particle.SOUL_FIRE_FLAME, Material.SOUL_LANTERN),
    ASH("Cinzas", Particle.ASH, Material.BLACK_CONCRETE_POWDER),
    CRIMSON_SPORE("Esporo Carmesim", Particle.CRIMSON_SPORE, Material.CRIMSON_FUNGUS),
    WARPED_SPORE("Esporo Deformado", Particle.WARPED_SPORE, Material.WARPED_FUNGUS),
    DRIPPING_OBSIDIAN_TEAR("Lagrima de Obsidiana Gotejando", Particle.DRIPPING_OBSIDIAN_TEAR, Material.OBSIDIAN),
    FALLING_OBSIDIAN_TEAR("Lagrima de Obsidiana Caindo", Particle.FALLING_OBSIDIAN_TEAR, Material.OBSIDIAN),
    LANDING_OBSIDIAN_TEAR("Lagrima de Obsidiana Aterrissando", Particle.LANDING_OBSIDIAN_TEAR, Material.OBSIDIAN),
    REVERSE_PORTAL("Portal Reverso", Particle.REVERSE_PORTAL, Material.ENDER_PEARL),
    WHITE_ASH("Cinzas Brancas", Particle.WHITE_ASH, Material.WHITE_CONCRETE_POWDER),
    EGG_CRACK("Quebra de Ovo", Particle.EGG_CRACK, Material.EGG),
    GLOWSTONE_DUST("Brilho", Particle.GLOW, Material.GLOWSTONE_DUST),
    FALLING_SPORE_BLOSSOM("Flor de Esporo Caindo", Particle.FALLING_SPORE_BLOSSOM, Material.SPORE_BLOSSOM),
    SPORE_BLOSSOM_AIR("Flor de Esporo no Ar", Particle.SPORE_BLOSSOM_AIR, Material.SPORE_BLOSSOM);

    private final String displayName;
    private final Particle particle;
    private final Material icon;

    ParticleEffectType(String displayName, Particle particle, Material icon) {
        this.displayName = displayName;
        this.particle = particle;
        this.icon = icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Particle getParticle() {
        return particle;
    }

    public Material getIcon() {
        return icon;
    }
}

