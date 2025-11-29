package com.seunome.mestredosfx.database;

import com.seunome.mestredosfx.MestreDosEfeitos;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLiteManager {

    private final MestreDosEfeitos plugin;
    private Connection connection;
    private String dbPath;

    public SQLiteManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        this.dbPath = new File(dataFolder, "data.db").getAbsolutePath();
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao conectar ao banco de dados SQLite", e);
        }
        return connection;
    }

    public void initializeDatabase() {
        try (Connection conn = getConnection()) {
            if (conn == null) {
                plugin.getLogger().severe("Não foi possível conectar ao banco de dados!");
                return;
            }

            // Criar tabela player_effects
            String createPlayerEffects = """
                CREATE TABLE IF NOT EXISTS player_effects (
                  uuid TEXT PRIMARY KEY,
                  glow TEXT,
                  particle TEXT,
                  nick_color_unlocked INTEGER DEFAULT 0,
                  nick_color_enabled INTEGER DEFAULT 0
                );
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(createPlayerEffects)) {
                stmt.executeUpdate();
            }

            // Garantir colunas extras (em caso de versões antigas do banco)
            addColumnIfMissing(conn, """
                ALTER TABLE player_effects ADD COLUMN nick_color_unlocked INTEGER DEFAULT 0
            """);
            addColumnIfMissing(conn, """
                ALTER TABLE player_effects ADD COLUMN nick_color_enabled INTEGER DEFAULT 0
            """);

            // Criar tabela unlocked
            String createUnlocked = """
                CREATE TABLE IF NOT EXISTS unlocked (
                  uuid TEXT,
                  type TEXT,
                  effect_id TEXT,
                  PRIMARY KEY(uuid, type, effect_id)
                );
                """;
            
            try (PreparedStatement stmt = conn.prepareStatement(createUnlocked)) {
                stmt.executeUpdate();
            }

            plugin.getLogger().info("Banco de dados inicializado com sucesso!");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao inicializar banco de dados", e);
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao fechar conexão do banco de dados", e);
        }
    }

    private void addColumnIfMissing(Connection conn, String alterTableSql) {
        try (PreparedStatement stmt = conn.prepareStatement(alterTableSql)) {
            stmt.executeUpdate();
        } catch (SQLException ignored) {
            // Coluna já existe - ignorar erro
        }
    }
}

