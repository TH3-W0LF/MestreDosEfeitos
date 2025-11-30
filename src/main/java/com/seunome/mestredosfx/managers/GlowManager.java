package com.seunome.mestredosfx.managers;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.managers.glow.GlowEffect;
import com.seunome.mestredosfx.managers.glow.GlowPacketManager;
import com.seunome.mestredosfx.managers.glow.TeamNameGenerator;
import com.seunome.mestredosfx.utils.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class GlowManager {

    private final MestreDosEfeitos plugin;
    private final PlayerEffectDAO dao;
    private final Map<UUID, String> activeGlows;
    private final Map<String, ChatColor> glowColors;
    private final Scoreboard mainScoreboard;
    private List<ChatColor> rainbowColors;

    private final Map<String, GlowEffect> glowEffects;
    private final Map<UUID, String> playerTeamNames;
    private final TeamNameGenerator teamNameGenerator;
    private GlowPacketManager packetManager;

    private final Map<UUID, Long> playerJoinTime;
    
    // Mapa para rastrear se é a primeira aplicação de glow (para usar Mode 0 ou Mode 2)
    private final Map<UUID, Boolean> firstGlowApplication = new ConcurrentHashMap<>();

    public GlowManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.dao = new PlayerEffectDAO(plugin);
        this.activeGlows = new ConcurrentHashMap<>();
        this.mainScoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        this.glowColors = new HashMap<>();
        this.glowEffects = new ConcurrentHashMap<>();
        this.playerTeamNames = new ConcurrentHashMap<>();
        this.teamNameGenerator = new TeamNameGenerator();
        this.playerJoinTime = new ConcurrentHashMap<>();
        
        // Inicializar GlowPacketManager (ProtocolLib)
        try {
            this.packetManager = new GlowPacketManager(plugin);
        } catch (Exception e) {
            plugin.getLogger().warning("ProtocolLib não disponível! O sistema de Glow via ProtocolLib não funcionará: " + e.getMessage());
            this.packetManager = null;
        }

        initializeGlowColors();
        initializeRainbowColors();
        initializeGlowEffects();
        loadActiveGlows();
    }

    // --- EVENTOS DE JOIN / QUIT ---

    public void onPlayerJoin(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }

        try {
            player.setScoreboard(mainScoreboard);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao setar scoreboard no join: " + e.getMessage());
        }

        playerJoinTime.put(player.getUniqueId(), System.currentTimeMillis());

        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (player == null || !player.isOnline()) {
                return;
            }
            String glowId = dao.getActiveGlow(player.getUniqueId());
            if (glowId != null && !glowId.isEmpty()) {
                activeGlows.put(player.getUniqueId(), glowId);
                applyGlowOnJoin(player, glowId);
            }
        }, 5L);
    }

    public void onPlayerQuit(Player player) {
        if (player == null) {
            return;
        }

        UUID uuid = player.getUniqueId();
        String glowId = activeGlows.get(uuid);

        if (glowId != null) {
            GlowEffect effect = glowEffects.get(glowId);
            if (effect != null) {
                effect.deactivateForEntity(player);
            }
        }

        activeGlows.remove(uuid);
        String teamName = playerTeamNames.remove(uuid);
        if (teamName != null) {
            teamNameGenerator.unregisterTeamName(teamName);
        }

        playerJoinTime.remove(uuid);
        firstGlowApplication.remove(uuid);
        
        // Limpar no GlowPacketManager (enviar removeGlow para todos os observadores)
        if (packetManager != null) {
            Player playerQuitting = plugin.getServer().getPlayer(uuid);
            if (playerQuitting != null && playerQuitting.isOnline()) {
                for (Player observer : plugin.getServer().getOnlinePlayers()) {
                    if (observer != null && observer.isOnline() && observer.canSee(playerQuitting)) {
                        try {
                            packetManager.removeGlow(playerQuitting, observer);
                        } catch (Exception ignored) {}
                    }
                }
                // Limpar o rastreamento da entidade
                packetManager.clearPlayer(playerQuitting);
            }
        }
    }

    // --- LÓGICA DE APLICAÇÃO (PROTOCOLLIB) ---

    private void applyGlowWithColor(Player player, ChatColor color) {
        if (color == null || player == null || !player.isOnline()) {
            return;
        }

        // Se ProtocolLib não estiver disponível, usar fallback (Bukkit API antiga)
        if (packetManager == null) {
            applyGlowWithColorFallback(player, color);
            return;
        }

        try {
            UUID playerUuid = player.getUniqueId();
            
            // SEMPRE manter a cor original do nick (simplificado - sem toggle)
            boolean keepOriginalColor = true;
            
            // Verificar se o glow já existe
            boolean hasActiveGlow = packetManager.hasGlow(player);
            
            // Enviar pacotes para todos os observadores
            for (Player observer : plugin.getServer().getOnlinePlayers()) {
                if (observer == null || !observer.isOnline() || !observer.canSee(player)) {
                    continue;
                }
                
                try {
                    if (hasActiveGlow) {
                        // Glow já existe: usar refreshGlow (Mode 2 - Update) para evitar flicker
                        packetManager.refreshGlow(player, observer, color, keepOriginalColor);
                    } else {
                        // Glow não existe: criar novo (Mode 0 - Create)
                        packetManager.setGlow(player, observer, color, keepOriginalColor);
                    }
                } catch (Exception e) {
                    plugin.debug("Erro ao enviar pacote para " + observer.getName(), e);
                }
            }
            
            // Marcar como primeira aplicação após criar o glow (apenas uma vez, fora do loop)
            if (!hasActiveGlow) {
                firstGlowApplication.put(playerUuid, true);
            }
            
        } catch (Exception e) {
            plugin.debug("Erro ao aplicar glow com ProtocolLib para " + player.getName(), e);
        }
    }
    
    /**
     * Fallback para quando ProtocolLib não está disponível (usar API antiga do Bukkit)
     */
    private void applyGlowWithColorFallback(Player player, ChatColor color) {
        try {
            if (player.getScoreboard() != mainScoreboard) {
                player.setScoreboard(mainScoreboard);
            }

            String cleanName = ChatColor.stripColor(player.getName());
            if (cleanName == null || cleanName.isEmpty()) {
                return;
            }

            String teamName = getPlayerTeamName(player);
            if (teamName == null || teamName.isEmpty()) {
                return;
            }

            Team team = mainScoreboard.getTeam(teamName);
            if (team == null) {
                team = mainScoreboard.registerNewTeam(teamName);
            }

            team.setColor(color);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

            // Sempre manter o prefixo branco (simplificado - sem toggle)
            team.setPrefix(ChatColor.WHITE.toString());

            if (!team.hasEntry(cleanName)) {
                team.addEntry(cleanName);
            }

            // NÃO usar player.setGlowing() - remover linha antiga
        } catch (Exception ignored) {
        }
    }

    private void initializeGlowEffects() {
        double intervalSeconds = plugin.getConfigManager().getGlowsConfig()
            .getDouble("settings.rainbow.change-interval", 3.0);

        GlowEffect rainbowEffect = new GlowEffect(
            plugin,
            "rainbow",
            "Rainbow",
            intervalSeconds,
            rainbowColors.toArray(new ChatColor[0])
        );
        rainbowEffect.setColorUpdateCallback(this::applyGlowWithColor);
        glowEffects.put("rainbow", rainbowEffect);

        for (Map.Entry<String, ChatColor> entry : glowColors.entrySet()) {
            String glowId = entry.getKey();
            ChatColor color = entry.getValue();
            if ("rainbow".equals(glowId) || color == null) {
                continue;
            }

            GlowEffect effect = new GlowEffect(plugin, glowId, glowId, color);
            effect.setColorUpdateCallback(this::applyGlowWithColor);
            glowEffects.put(glowId, effect);
        }

        initializeCustomEffects();
    }

    private void initializeCustomEffects() {
        org.bukkit.configuration.file.FileConfiguration config = plugin.getConfigManager().getGlowsConfig();
        if (!config.contains("custom-effects")) {
            return;
        }

        org.bukkit.configuration.ConfigurationSection customSection = config.getConfigurationSection("custom-effects");
        if (customSection == null) {
            return;
        }

        for (String effectId : customSection.getKeys(false)) {
            try {
                List<String> colorNames = config.getStringList("custom-effects." + effectId + ".colors");
                if (colorNames == null || colorNames.isEmpty()) {
                    continue;
                }

                List<ChatColor> effectColors = new ArrayList<>();
                for (String colorName : colorNames) {
                    try {
                        effectColors.add(ChatColor.valueOf(colorName.toUpperCase().replace(" ", "_")));
                    } catch (Exception ignored) {
                    }
                }

                if (effectColors.isEmpty()) {
                    continue;
                }

                double delaySeconds = config.getDouble("custom-effects." + effectId + ".delay", 1.0);
                String displayName = config.getString("custom-effects." + effectId + ".display-name", effectId);

                GlowEffect customEffect = new GlowEffect(
                    plugin,
                    effectId,
                    displayName,
                    delaySeconds,
                    effectColors
                );
                customEffect.setColorUpdateCallback(this::applyGlowWithColor);
                glowEffects.put(effectId, customEffect);
                glowColors.put(effectId, null);
            } catch (Exception e) {
                plugin.getLogger().warning("Erro ao carregar efeito custom '" + effectId + "': " + e.getMessage());
            }
        }
    }

    private String getPlayerTeamName(Player player) {
        if (player == null) {
            return null;
        }
        UUID uuid = player.getUniqueId();
        if (playerTeamNames.containsKey(uuid)) {
            return playerTeamNames.get(uuid);
        }

        Set<String> existing = new HashSet<>();
        for (Team team : mainScoreboard.getTeams()) {
            existing.add(team.getName());
        }
        existing.addAll(playerTeamNames.values());
        existing.addAll(teamNameGenerator.getUsedTeamNames());

        String name = teamNameGenerator.generateTeamName(player, existing);
        if (name != null) {
            playerTeamNames.put(uuid, name);
            teamNameGenerator.registerTeamName(name);
        }
        return name;
    }

    private void initializeRainbowColors() {
        rainbowColors = new ArrayList<>();
        rainbowColors.add(ChatColor.RED);
        rainbowColors.add(ChatColor.GOLD);
        rainbowColors.add(ChatColor.YELLOW);
        rainbowColors.add(ChatColor.GREEN);
        rainbowColors.add(ChatColor.BLUE);
        rainbowColors.add(ChatColor.DARK_BLUE);
        rainbowColors.add(ChatColor.LIGHT_PURPLE);
    }

    private void initializeGlowColors() {
        glowColors.put("black", ChatColor.BLACK);
        glowColors.put("dark_blue", ChatColor.DARK_BLUE);
        glowColors.put("dark_green", ChatColor.DARK_GREEN);
        glowColors.put("dark_aqua", ChatColor.DARK_AQUA);
        glowColors.put("dark_red", ChatColor.DARK_RED);
        glowColors.put("dark_purple", ChatColor.DARK_PURPLE);
        glowColors.put("gold", ChatColor.GOLD);
        glowColors.put("gray", ChatColor.GRAY);
        glowColors.put("dark_gray", ChatColor.DARK_GRAY);
        glowColors.put("blue", ChatColor.BLUE);
        glowColors.put("green", ChatColor.GREEN);
        glowColors.put("aqua", ChatColor.AQUA);
        glowColors.put("red", ChatColor.RED);
        glowColors.put("light_purple", ChatColor.LIGHT_PURPLE);
        glowColors.put("yellow", ChatColor.YELLOW);
        glowColors.put("white", ChatColor.WHITE);
        glowColors.put("cyan", ChatColor.AQUA);
        glowColors.put("purple", ChatColor.LIGHT_PURPLE);
        glowColors.put("pink", ChatColor.LIGHT_PURPLE);
        glowColors.put("orange", ChatColor.GOLD);
        glowColors.put("rainbow", null);
    }

    private void loadActiveGlows() {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            String glowId = dao.getActiveGlow(player.getUniqueId());
            if (glowId != null && !glowId.isEmpty()) {
                activeGlows.put(player.getUniqueId(), glowId);
                applyGlow(player, glowId);
            }
        }
    }

    public List<String> getAllGlowIds() {
        Set<String> allIds = new HashSet<>(glowColors.keySet());
        allIds.addAll(glowEffects.keySet());
        return new ArrayList<>(allIds);
    }

    public boolean hasGlow(Player player) {
        if (player == null) {
            return false;
        }
        return activeGlows.containsKey(player.getUniqueId());
    }

    public String getActiveGlow(Player player) {
        if (player == null) {
            return null;
        }
        return activeGlows.get(player.getUniqueId());
    }

    public void setGlow(Player player, String glowId) {
        if (player == null) {
            return;
        }

        if (glowId == null || glowId.isEmpty()) {
            removeGlow(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        String previousGlowId = activeGlows.get(uuid);
        if (previousGlowId != null && !previousGlowId.equals(glowId)) {
            String previousGlowName = getGlowDisplayName(previousGlowId);
            if (previousGlowName != null) {
                PlayerUtils.sendMessage(player, "<gray>Brilho de <yellow>" + previousGlowName + "</yellow> desativado.</gray>");
            }
        }

        deactivateAllEffectsForPlayer(player);
        resetGlowForPlayer(player);

        activeGlows.put(uuid, glowId);
        dao.setActiveGlow(uuid, glowId);
        applyGlow(player, glowId);
    }

    public void refreshActiveGlow(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        String glowId = activeGlows.get(player.getUniqueId());
        if (glowId != null && !glowId.isEmpty()) {
            applyGlow(player, glowId);
        } else {
            resetGlowForPlayer(player);
        }
    }

    public void removeGlow(Player player) {
        if (player == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        deactivateAllEffectsForPlayer(player);
        activeGlows.remove(uuid);
        dao.removeActiveGlow(uuid);
        removeGlowEffect(player);
    }

    private void applyGlow(Player player, String glowId) {
        if (player == null || !player.isOnline()) {
            return;
        }

        deactivateAllEffectsForPlayer(player);
        GlowEffect effect = glowEffects.get(glowId);
        if (effect != null) {
            resetGlowForPlayer(player);
            effect.activateForEntity(player);
        } else {
            ChatColor color = glowColors.get(glowId);
            if (color != null) {
                applyGlowWithColor(player, color);
            }
        }
    }

    private void applyGlowOnJoin(Player player, String glowId) {
        applyGlow(player, glowId);
    }

    private void deactivateAllEffectsForPlayer(Player player) {
        if (player == null) {
            return;
        }
        glowEffects.values().forEach(effect -> effect.deactivateForEntity(player));
    }

    private void resetGlowForPlayer(Player player) {
        if (player == null) {
            return;
        }
        
        UUID playerUuid = player.getUniqueId();
        
        // Remover glow existente
        removeGlowEffect(player);
        
        // Resetar flag de primeira aplicação para que o próximo glow crie um novo time
        firstGlowApplication.remove(playerUuid);
        
        // Limpar no packet manager (já foi feito acima no loop de observadores)
    }

    private void removeGlowEffect(Player player) {
        if (player == null) {
            return;
        }
        
        UUID playerUuid = player.getUniqueId();
        
        // Remover glow usando ProtocolLib se disponível
        if (packetManager != null) {
            // Enviar pacotes de remoção para todos os observadores
            for (Player observer : plugin.getServer().getOnlinePlayers()) {
                if (observer != null && observer.isOnline() && observer.canSee(player)) {
                    try {
                        packetManager.removeGlow(player, observer);
                    } catch (Exception ignored) {}
                }
            }
        } else {
            // Fallback: remover do scoreboard (sem enviar packets)
            try {
                String cleanName = ChatColor.stripColor(player.getName());
                if (cleanName == null || cleanName.isEmpty()) {
                    return;
                }
                String teamName = playerTeamNames.get(playerUuid);
                if (teamName != null) {
                    Team team = mainScoreboard.getTeam(teamName);
                    if (team != null && team.hasEntry(cleanName)) {
                        team.removeEntry(cleanName);
                    }
                }
            } catch (Exception ignored) {
            }
        }
        
        // Limpar flag de primeira aplicação
        firstGlowApplication.remove(playerUuid);
    }

    public void reload() {
        glowEffects.values().forEach(GlowEffect::reloadEffect);
    }

    public boolean isValidGlowId(String glowId) {
        return "rainbow".equals(glowId) || glowColors.containsKey(glowId);
    }

    public void shutdown() {
        glowEffects.values().forEach(GlowEffect::shutdown);
        activeGlows.clear();
        playerTeamNames.clear();
        teamNameGenerator.clear();
        firstGlowApplication.clear();

        // Desregistrar o listener do ProtocolLib
        if (packetManager != null) {
            packetManager.shutdown();
        }
    }

    private String getGlowDisplayName(String glowId) {
        if (glowId == null || glowId.isEmpty()) {
            return null;
        }

        GlowEffect effect = glowEffects.get(glowId);
        if (effect != null) {
            return effect.getDisplayName();
        }

        try {
            org.bukkit.configuration.file.FileConfiguration config = plugin.getConfigManager().getGlowsConfig();
            String displayName = config.getString("glows." + glowId + ".display_name");
            if (displayName != null && !displayName.isEmpty()) {
                return ChatColor.stripColor(displayName.replaceAll("<[^>]+>", "").replaceAll("&[0-9a-fk-or]", ""));
            }
        } catch (Exception ignored) {
        }

        return glowId.substring(0, 1).toUpperCase() + glowId.substring(1).toLowerCase();
    }
}
