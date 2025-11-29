package com.seunome.mestredosfx.managers;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.managers.glow.GlowEffect;
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
    private final Map<UUID, Boolean> nickColorCache = new ConcurrentHashMap<>();

    private final Map<String, GlowEffect> glowEffects;
    private final Map<UUID, String> playerTeamNames;
    private final TeamNameGenerator teamNameGenerator;

    private final Map<UUID, Long> playerJoinTime;

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

        initializeGlowColors();
        initializeRainbowColors();
        initializeGlowEffects();
        loadActiveGlows();
    }

    // --- GERENCIAMENTO DE CACHE DE PREFERÊNCIAS ---

    public void loadPlayerPreferences(Player player) {
        if (player == null) {
            return;
        }
        boolean unlocked = dao.hasUnlockedNickColor(player.getUniqueId());
        boolean enabled = dao.isNickColorEnabled(player.getUniqueId());
        nickColorCache.put(player.getUniqueId(), unlocked && enabled);
    }

    public void updateNickColorPreference(Player player, boolean enabled) {
        if (player == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        boolean unlocked = dao.hasUnlockedNickColor(uuid);
        nickColorCache.put(uuid, unlocked && enabled);

        if (hasGlow(player)) {
            refreshActiveGlow(player);
        }
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
        loadPlayerPreferences(player);

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
        nickColorCache.remove(uuid);
    }

    // --- LÓGICA DE APLICAÇÃO ---

    private void applyGlowWithColor(Player player, ChatColor color) {
        if (color == null || player == null || !player.isOnline()) {
            return;
        }

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

            boolean allowColoredNick = player.hasPermission("mestredosfx.namecolor");
            if (!allowColoredNick) {
                allowColoredNick = nickColorCache.getOrDefault(player.getUniqueId(), false);
            }
            team.setPrefix(allowColoredNick ? "" : ChatColor.WHITE.toString());

            if (!team.hasEntry(cleanName)) {
                team.addEntry(cleanName);
            }

            if (!player.isGlowing()) {
                player.setGlowing(true);
            }
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
            loadPlayerPreferences(player);
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
        removeGlowEffect(player);
    }

    private void removeGlowEffect(Player player) {
        if (player == null) {
            return;
        }
        try {
            player.setGlowing(false);
            String cleanName = ChatColor.stripColor(player.getName());
            if (cleanName == null || cleanName.isEmpty()) {
                return;
            }
            String teamName = playerTeamNames.get(player.getUniqueId());
            if (teamName != null) {
                Team team = mainScoreboard.getTeam(teamName);
                if (team != null && team.hasEntry(cleanName)) {
                    team.removeEntry(cleanName);
                }
            }
        } catch (Exception ignored) {
        }
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
        nickColorCache.clear();
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
