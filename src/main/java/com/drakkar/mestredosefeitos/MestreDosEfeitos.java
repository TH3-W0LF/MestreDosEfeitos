package com.drakkar.mestredosefeitos;

import com.drakkar.mestredosefeitos.glow.GlowManager;
import com.drakkar.mestredosefeitos.particles.ParticlesManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class MestreDosEfeitos extends JavaPlugin {

    private static MestreDosEfeitos instance;
    private FileConfiguration particlesConfig;
    private FileConfiguration glowsConfig;
    
    private ParticlesManager particlesManager;
    private GlowManager glowManager;

    public static MestreDosEfeitos getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        saveDefaultConfig();
        loadConfigs();
        
        this.particlesManager = new ParticlesManager(this);
        this.glowManager = new GlowManager(this);
        
        getCommand("efeitos").setExecutor(new EfeitosCommand(this));
        getCommand("efeitos").setTabCompleter(new EfeitosCommand(this));
        
        getLogger().info("Mestre Dos Efeitos habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (particlesManager != null) {
            particlesManager.shutdown();
        }
        if (glowManager != null) {
            glowManager.shutdown();
        }
        
        instance = null;
        getLogger().info("Mestre Dos Efeitos desabilitado.");
    }

    public void loadConfigs() {
        loadParticlesConfig();
        loadGlowsConfig();
        
        if (particlesManager != null) {
            particlesManager.reload();
        }
        if (glowManager != null) {
            glowManager.reload();
        }
    }

    private void loadParticlesConfig() {
        File particlesFile = new File(getDataFolder(), "particles.yml");
        if (!particlesFile.exists()) {
            saveResource("particles.yml", false);
        }
        this.particlesConfig = YamlConfiguration.loadConfiguration(particlesFile);
        
        InputStream defaultStream = getResource("particles.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            particlesConfig.setDefaults(defaultConfig);
        }
    }

    private void loadGlowsConfig() {
        File glowsFile = new File(getDataFolder(), "glows.yml");
        if (!glowsFile.exists()) {
            saveResource("glows.yml", false);
        }
        this.glowsConfig = YamlConfiguration.loadConfiguration(glowsFile);
        
        InputStream defaultStream = getResource("glows.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            glowsConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getParticlesConfig() {
        return particlesConfig;
    }

    public FileConfiguration getGlowsConfig() {
        return glowsConfig;
    }

    public ParticlesManager getParticlesManager() {
        return particlesManager;
    }

    public GlowManager getGlowManager() {
        return glowManager;
    }
}

