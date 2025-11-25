package com.seunome.mestredosfx.config;

import com.seunome.mestredosfx.MestreDosEfeitos;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ConfigManager {

    private final MestreDosEfeitos plugin;
    private FileConfiguration glowsConfig;
    private FileConfiguration particlesConfig;

    public ConfigManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        loadConfigs();
    }

    public void loadConfigs() {
        loadGlowsConfig();
        loadParticlesConfig();
    }

    private void loadGlowsConfig() {
        File glowsFile = new File(plugin.getDataFolder(), "glows.yml");
        if (!glowsFile.exists()) {
            plugin.saveResource("glows.yml", false);
        }
        this.glowsConfig = YamlConfiguration.loadConfiguration(glowsFile);
        
        InputStream defaultStream = plugin.getResource("glows.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            glowsConfig.setDefaults(defaultConfig);
        }
    }

    private void loadParticlesConfig() {
        File particlesFile = new File(plugin.getDataFolder(), "particles.yml");
        if (!particlesFile.exists()) {
            plugin.saveResource("particles.yml", false);
        }
        this.particlesConfig = YamlConfiguration.loadConfiguration(particlesFile);
        
        InputStream defaultStream = plugin.getResource("particles.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            particlesConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getGlowsConfig() {
        return glowsConfig;
    }

    public FileConfiguration getParticlesConfig() {
        return particlesConfig;
    }

    public void reloadConfigs() {
        loadConfigs();
    }
}

