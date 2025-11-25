package com.drakkar.mestredosefeitos.glow;

import com.drakkar.mestredosefeitos.MestreDosEfeitos;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class GlowManager implements Listener {

    private static final String TEAM_PREFIX = "mdf_glow_";

    private final MestreDosEfeitos plugin;
    private final NamespacedKey selectedKey;
    private final NamespacedKey maxLevelKey;
    private final NamespacedKey unlockAllKey;
    private final NamespacedKey menuColorKey;
    private final Scoreboard scoreboard;

    private List<GlowColor> colors = new ArrayList<>();
    private GlowMenu menu;

    public GlowManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.selectedKey = new NamespacedKey(plugin, "glow_selected");
        this.maxLevelKey = new NamespacedKey(plugin, "glow_max_level");
        this.unlockAllKey = new NamespacedKey(plugin, "glow_all_unlocked");
        this.menuColorKey = new NamespacedKey(plugin, "glow_menu_color");
        ScoreboardManager manager = Bukkit.getScoreboardManager();
        this.scoreboard = manager != null ? manager.getMainScoreboard() : null;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        reload();
    }

    public void reload() {
        loadColorsFromConfig();
        if (menu != null) {
            menu.unregister();
        }
        menu = new GlowMenu(plugin, this, menuColorKey);
        Bukkit.getOnlinePlayers().forEach(this::applyStoredGlow);
    }

    public void shutdown() {
        HandlerList.unregisterAll(this);
        if (menu != null) {
            menu.unregister();
        }
        Bukkit.getOnlinePlayers().forEach(this::removeFromTeams);
    }

    private void loadColorsFromConfig() {
        FileConfiguration config = plugin.getGlowsConfig();
        ConfigurationSection section = config.getConfigurationSection("colors");
        if (section == null) {
            colors = Collections.emptyList();
            return;
        }

        int step = config.getInt("settings.unlock-step", 100);
        List<GlowColor> loaded = new ArrayList<>();
        int index = 0;
        for (String id : section.getKeys(false)) {
            ConfigurationSection colorSection = section.getConfigurationSection(id);
            if (colorSection == null) continue;
            GlowColor color = GlowColor.fromConfig(
                    id,
                    colorSection,
                    step * index
            );
            loaded.add(color);
            index++;
        }
        colors = loaded;
    }

    List<GlowColor> getColors() {
        return colors;
    }

    public GlowColor getColorById(String id) {
        for (GlowColor color : colors) {
            if (color.getId().equalsIgnoreCase(id)) {
                return color;
            }
        }
        return null;
    }

    public NamespacedKey getMenuColorKey() {
        return menuColorKey;
    }

    public void openMenu(Player player) {
        if (menu == null) {
            sendMessage(player, "not-available", "<red>Menu de glow indisponível.</red>");
            return;
        }
        menu.open(player);
    }

    public boolean applyGlow(Player player, GlowColor color, boolean notify) {
        if (color == null) {
            sendMessage(player, "not-available", "<red>Glow indisponível.</red>");
            return false;
        }

        if (!isUnlocked(player, color)) {
            sendMessage(player, "color-locked", "<red>Glow bloqueado! Precisa de nível <level>.</red>",
                    "<level>", String.valueOf(color.getRequiredLevel()));
            return false;
        }

        removeFromTeams(player);
        Team team = getOrCreateTeam(color);
        if (team != null) {
            team.addEntry(player.getName());
        }
        player.setGlowing(true);
        storeSelectedColor(player, color.getId());

        if (notify) {
            String formatted = MiniMessage.miniMessage().serialize(color.displayNameComponent());
            sendMessage(player, "color-selected", "<green>Glow selecionado: <color></green>",
                    "<color>", formatted);
        }
        return true;
    }

    public void disableGlow(Player player, boolean notify) {
        removeFromTeams(player);
        player.setGlowing(false);
        storeSelectedColor(player, null);
        if (notify) {
            sendMessage(player, "glow-disabled", "<yellow>Glow desativado.</yellow>");
        }
    }

    private void removeFromTeams(Player player) {
        if (scoreboard == null) {
            return;
        }
        for (Team team : scoreboard.getTeams()) {
            if (team.getName().startsWith(TEAM_PREFIX)) {
                team.removeEntry(player.getName());
            }
        }
    }

    private Team getOrCreateTeam(GlowColor color) {
        if (scoreboard == null) {
            return null;
        }
        String name = (TEAM_PREFIX + color.getId()).toLowerCase(Locale.ROOT);
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
            team.setCanSeeFriendlyInvisibles(true);
            team.setOption(Team.Option.COLLISION_RULE, Team.OptionStatus.NEVER);
        }
        team.setColor(color.getChatColor());
        return team;
    }

    public boolean isUnlocked(Player player, GlowColor color) {
        if (color == null) {
            return false;
        }
        if (hasUnlockedAll(player)) {
            return true;
        }
        return getMaxLevel(player) >= color.getRequiredLevel();
    }

    public int getMaxLevel(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.getOrDefault(maxLevelKey, PersistentDataType.INTEGER, 0);
    }

    public void setMaxLevel(Player player, int level) {
        player.getPersistentDataContainer().set(maxLevelKey, PersistentDataType.INTEGER, level);
    }

    public boolean hasUnlockedAll(Player player) {
        return player.getPersistentDataContainer().getOrDefault(unlockAllKey, PersistentDataType.BOOLEAN, false);
    }

    public void unlockAllColors(Player player, boolean notify) {
        player.getPersistentDataContainer().set(unlockAllKey, PersistentDataType.BOOLEAN, true);
        if (notify) {
            sendMessage(player, "unlocked-all", "<gold>Todas as cores foram desbloqueadas!</gold>");
        }
    }

    private void storeSelectedColor(Player player, String id) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        if (id == null || id.isEmpty()) {
            container.remove(selectedKey);
        } else {
            container.set(selectedKey, PersistentDataType.STRING, id.toLowerCase(Locale.ROOT));
        }
    }

    public String getStoredColor(Player player) {
        return player.getPersistentDataContainer().get(selectedKey, PersistentDataType.STRING);
    }

    public void applyStoredGlow(Player player) {
        String stored = getStoredColor(player);
        if (stored == null) {
            return;
        }
        GlowColor color = getColorById(stored);
        if (color == null) {
            disableGlow(player, false);
            return;
        }
        if (!isUnlocked(player, color)) {
            disableGlow(player, false);
            return;
        }
        applyGlow(player, color, false);
    }

    public void handleLevelProgress(Player player, int newLevel) {
        if (player == null) {
            return;
        }
        int currentMax = getMaxLevel(player);
        if (newLevel <= currentMax) {
            return;
        }
        setMaxLevel(player, newLevel);
        List<GlowColor> newlyUnlocked = colors.stream()
                .filter(color -> currentMax < color.getRequiredLevel() && newLevel >= color.getRequiredLevel())
                .collect(Collectors.toList());
        if (!newlyUnlocked.isEmpty()) {
            for (GlowColor color : newlyUnlocked) {
                String formatted = MiniMessage.miniMessage().serialize(color.displayNameComponent());
                sendMessage(player, "unlocked-new-color", "<green>Nova cor desbloqueada: <color></green>",
                        "<color>", formatted);
            }
        }
    }

    private void sendMessage(Player player, String key, String fallback, String... replacements) {
        FileConfiguration config = plugin.getGlowsConfig();
        String raw = Objects.requireNonNullElse(config.getString("messages." + key), fallback);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            raw = raw.replace(replacements[i], replacements[i + 1]);
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize(raw));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> applyStoredGlow(event.getPlayer()), 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        removeFromTeams(event.getPlayer());
    }
}

