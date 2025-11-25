package com.seunome.mestredosfx;

import com.seunome.mestredosfx.commands.AdminCommand;
import com.seunome.mestredosfx.commands.AdminTabComplete;
import com.seunome.mestredosfx.commands.EfeitosCommand;
import com.seunome.mestredosfx.database.SQLiteManager;
import com.seunome.mestredosfx.listeners.PhysicalItemListener;
import com.seunome.mestredosfx.managers.EffectApplier;
import com.seunome.mestredosfx.managers.GlowManager;
import com.seunome.mestredosfx.managers.ParticleManager;
import com.seunome.mestredosfx.hooks.ItemsAdderHook;
import org.bukkit.plugin.java.JavaPlugin;

public final class MestreDosEfeitos extends JavaPlugin {

    private static MestreDosEfeitos instance;
    
    private SQLiteManager sqliteManager;
    private GlowManager glowManager;
    private ParticleManager particleManager;
    private EffectApplier effectApplier;
    private ItemsAdderHook itemsAdderHook;
    
    // Menus (instanciados uma vez para evitar listeners duplicados)
    private com.seunome.mestredosfx.menus.MainMenu mainMenu;
    private com.seunome.mestredosfx.menus.GlowMenu glowMenu;
    private com.seunome.mestredosfx.menus.ParticleMenu particleMenu;
    private com.seunome.mestredosfx.config.ConfigManager configManager;

    public static MestreDosEfeitos getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        
        // Salvar configurações padrão
        saveDefaultConfig();
        saveResource("glows.yml", false);
        saveResource("particles.yml", false);
        
        // Inicializar ConfigManager
        this.configManager = new com.seunome.mestredosfx.config.ConfigManager(this);
        
        // Verificar ItemsAdder
        if (!getServer().getPluginManager().isPluginEnabled("ItemsAdder")) {
            getLogger().severe("ItemsAdder não foi encontrado! Desabilitando plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        
        // Inicializar banco de dados
        this.sqliteManager = new SQLiteManager(this);
        sqliteManager.initializeDatabase();
        
        // Inicializar ItemsAdder Hook
        this.itemsAdderHook = new ItemsAdderHook(this);
        
        // Inicializar Managers
        this.glowManager = new GlowManager(this);
        this.particleManager = new ParticleManager(this);
        this.effectApplier = new EffectApplier(this);
        
        // Inicializar Menus (apenas uma vez para evitar listeners duplicados)
        this.mainMenu = new com.seunome.mestredosfx.menus.MainMenu(this);
        this.glowMenu = new com.seunome.mestredosfx.menus.GlowMenu(this);
        this.particleMenu = new com.seunome.mestredosfx.menus.ParticleMenu(this);
        
        // Registrar listeners
        getServer().getPluginManager().registerEvents(new PhysicalItemListener(this), this);
        
        // Registrar comandos
        getCommand("efeitos").setExecutor(new EfeitosCommand(this));
        getCommand("efeitos").setTabCompleter(new EfeitosCommand(this));
        
        getCommand("meffeitos").setExecutor(new AdminCommand(this));
        getCommand("meffeitos").setTabCompleter(new AdminTabComplete());
        
        getLogger().info("Mestre Dos Efeitos v2.0 habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        // Desabilitar todos os efeitos ativos
        if (effectApplier != null) {
            effectApplier.shutdown();
        }
        
        // Fechar banco de dados
        if (sqliteManager != null) {
            sqliteManager.closeConnection();
        }
        
        instance = null;
        getLogger().info("Mestre Dos Efeitos desabilitado.");
    }

    public SQLiteManager getSQLiteManager() {
        return sqliteManager;
    }

    public GlowManager getGlowManager() {
        return glowManager;
    }

    public ParticleManager getParticleManager() {
        return particleManager;
    }

    public EffectApplier getEffectApplier() {
        return effectApplier;
    }

    public ItemsAdderHook getItemsAdderHook() {
        return itemsAdderHook;
    }

    public com.seunome.mestredosfx.menus.MainMenu getMainMenu() {
        return mainMenu;
    }

    public com.seunome.mestredosfx.menus.GlowMenu getGlowMenu() {
        return glowMenu;
    }

    public com.seunome.mestredosfx.menus.ParticleMenu getParticleMenu() {
        return particleMenu;
    }

    public com.seunome.mestredosfx.config.ConfigManager getConfigManager() {
        return configManager;
    }
}

