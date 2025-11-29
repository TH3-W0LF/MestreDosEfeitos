package com.seunome.mestredosfx.database;

import com.seunome.mestredosfx.MestreDosEfeitos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class PlayerEffectDAO {

    private final MestreDosEfeitos plugin;
    private final SQLiteManager sqliteManager;
    private static final String COLUMN_NICK_COLOR_UNLOCKED = "nick_color_unlocked";
    private static final String COLUMN_NICK_COLOR_ENABLED = "nick_color_enabled";

    public PlayerEffectDAO(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.sqliteManager = plugin.getSQLiteManager();
    }

    public String getActiveGlow(UUID uuid) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT glow FROM player_effects WHERE uuid = ?")) {
            
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("glow");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao buscar glow ativo", e);
        }
        return null;
    }

    public String getActiveParticle(UUID uuid) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT particle FROM player_effects WHERE uuid = ?")) {
            
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("particle");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao buscar partícula ativa", e);
        }
        return null;
    }

    public void setActiveGlow(UUID uuid, String glowId) {
        try (Connection conn = sqliteManager.getConnection()) {
            // Verificar se já existe registro
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT uuid FROM player_effects WHERE uuid = ?")) {
                checkStmt.setString(1, uuid.toString());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Atualizar
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE player_effects SET glow = ? WHERE uuid = ?")) {
                        updateStmt.setString(1, glowId);
                        updateStmt.setString(2, uuid.toString());
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Inserir
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO player_effects (uuid, glow, particle) VALUES (?, ?, ?)")) {
                        insertStmt.setString(1, uuid.toString());
                        insertStmt.setString(2, glowId);
                        insertStmt.setString(3, null);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao salvar glow ativo", e);
        }
    }

    public void setActiveParticle(UUID uuid, String particleId) {
        try (Connection conn = sqliteManager.getConnection()) {
            // Verificar se já existe registro
            try (PreparedStatement checkStmt = conn.prepareStatement("SELECT uuid FROM player_effects WHERE uuid = ?")) {
                checkStmt.setString(1, uuid.toString());
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    // Atualizar
                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE player_effects SET particle = ? WHERE uuid = ?")) {
                        updateStmt.setString(1, particleId);
                        updateStmt.setString(2, uuid.toString());
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Inserir
                    try (PreparedStatement insertStmt = conn.prepareStatement(
                            "INSERT INTO player_effects (uuid, glow, particle) VALUES (?, ?, ?)")) {
                        insertStmt.setString(1, uuid.toString());
                        insertStmt.setString(2, null);
                        insertStmt.setString(3, particleId);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao salvar partícula ativa", e);
        }
    }

    public void removeActiveGlow(UUID uuid) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE player_effects SET glow = ? WHERE uuid = ?")) {
            
            stmt.setString(1, null);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao remover glow ativo", e);
        }
    }

    public void removeActiveParticle(UUID uuid) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE player_effects SET particle = ? WHERE uuid = ?")) {
            
            stmt.setString(1, null);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao remover partícula ativa", e);
        }
    }

    public void unlock(UUID uuid, String type, String effectId) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT OR IGNORE INTO unlocked (uuid, type, effect_id) VALUES (?, ?, ?)")) {
            
            stmt.setString(1, uuid.toString());
            stmt.setString(2, type);
            stmt.setString(3, effectId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao desbloquear efeito", e);
        }
    }

    public boolean hasUnlocked(UUID uuid, String type, String effectId) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT 1 FROM unlocked WHERE uuid = ? AND type = ? AND effect_id = ?")) {
            
            stmt.setString(1, uuid.toString());
            stmt.setString(2, type);
            stmt.setString(3, effectId);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao verificar se efeito está desbloqueado", e);
        }
        return false;
    }

    public void removeUnlock(UUID uuid, String type, String effectId) {
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "DELETE FROM unlocked WHERE uuid = ? AND type = ? AND effect_id = ?")) {
            
            stmt.setString(1, uuid.toString());
            stmt.setString(2, type);
            stmt.setString(3, effectId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao remover desbloqueio", e);
        }
    }

    public void unlockAll(UUID uuid) {
        // Desbloquear todos os glows e partículas disponíveis dinamicamente
        try {
            // Desbloquear todos os glows do GlowManager (incluindo aliases e rainbow)
            java.util.List<String> allGlows = plugin.getGlowManager().getAllGlowIds();
            for (String glow : allGlows) {
                unlock(uuid, "glow", glow);
            }
            
            // Desbloquear todas as partículas do ParticleManager
            java.util.List<String> allParticles = plugin.getParticleManager().getAllParticleIds();
            for (String particle : allParticles) {
                unlock(uuid, "particle", particle);
            }

            // Desbloquear cor do nick também
            unlockNickColor(uuid);
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao desbloquear todos os efeitos", e);
        }
    }

    public void resetPlayer(UUID uuid) {
        try (Connection conn = sqliteManager.getConnection()) {
            // Remover efeitos ativos
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM player_effects WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();
            }
            
            // Remover todos os desbloqueios
            try (PreparedStatement stmt = conn.prepareStatement("DELETE FROM unlocked WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao resetar jogador", e);
        }
    }

    public boolean hasUnlockedNickColor(UUID uuid) {
        return getBooleanColumn(uuid, COLUMN_NICK_COLOR_UNLOCKED);
    }

    public boolean isNickColorEnabled(UUID uuid) {
        return getBooleanColumn(uuid, COLUMN_NICK_COLOR_ENABLED);
    }

    public void setNickColorEnabled(UUID uuid, boolean enabled) {
        updateBooleanColumn(uuid, COLUMN_NICK_COLOR_ENABLED, enabled);
    }

    public void unlockNickColor(UUID uuid) {
        updateBooleanColumn(uuid, COLUMN_NICK_COLOR_UNLOCKED, true);
    }

    private boolean getBooleanColumn(UUID uuid, String column) {
        String sql = "SELECT " + column + " FROM player_effects WHERE uuid = ?";
        try (Connection conn = sqliteManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(column) == 1;
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao ler coluna " + column + " do jogador", e);
        }
        return false;
    }

    private void updateBooleanColumn(UUID uuid, String column, boolean value) {
        String sql = "UPDATE player_effects SET " + column + " = ? WHERE uuid = ?";
        try (Connection conn = sqliteManager.getConnection()) {
            ensurePlayerRow(conn, uuid);
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, value ? 1 : 0);
                stmt.setString(2, uuid.toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Erro ao atualizar coluna " + column + " do jogador", e);
        }
    }

    private void ensurePlayerRow(Connection conn, UUID uuid) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "INSERT OR IGNORE INTO player_effects (uuid) VALUES (?)")) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        }
    }
}

