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

import java.util.*;

public class GlowManager {

    private final MestreDosEfeitos plugin;
    private final PlayerEffectDAO dao;
    private final Map<UUID, String> activeGlows;
    private final Map<String, ChatColor> glowColors;
    private final Scoreboard mainScoreboard;
    private List<ChatColor> rainbowColors; // Lista de cores para rainbow glow
    
    // NOVO SISTEMA: Efeitos encapsulados
    private final Map<String, GlowEffect> glowEffects; // Mapa de glowId -> GlowEffect
    private final Map<UUID, String> playerTeamNames; // Cache de nomes de teams por player
    private final TeamNameGenerator teamNameGenerator; // Gerador robusto de nomes de teams
    
    // DEPRECATED (mantido para compatibilidade durante transição)
    private final Map<UUID, Integer> rainbowColorIndex; // Índice da cor atual para cada player
    private final Map<UUID, Long> playerJoinTime; // Tempo de join para verificar se é jogador novo
    private int rainbowTaskId = -1;

    public GlowManager(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.dao = new PlayerEffectDAO(plugin);
        this.activeGlows = new HashMap<>();
        this.mainScoreboard = plugin.getServer().getScoreboardManager().getMainScoreboard();
        this.glowColors = new HashMap<>();
        this.glowEffects = new HashMap<>();
        this.playerTeamNames = new HashMap<>();
        this.teamNameGenerator = new TeamNameGenerator();
        this.rainbowColorIndex = new HashMap<>(); // DEPRECATED
        this.playerJoinTime = new HashMap<>();
        
        initializeGlowColors();
        initializeRainbowColors();
        initializeGlowEffects(); // NOVO: Inicializar efeitos encapsulados
        loadActiveGlows();
        // startRainbowTask(); // DEPRECATED: Agora gerenciado pelo GlowEffect
    }
    
    /**
     * Inicializa os efeitos encapsulados
     * Baseado no sistema do TheGlow: ScheduledExecutorService + Iterator
     */
    private void initializeGlowEffects() {
        // Inicializar efeito Rainbow
        double intervalSeconds = plugin.getConfigManager().getGlowsConfig()
            .getDouble("settings.rainbow.change-interval", 3.0);
        
        GlowEffect rainbowEffect = new GlowEffect(
            plugin,
            "rainbow",
            "Rainbow",
            intervalSeconds, // Delay em segundos (será convertido para milissegundos internamente)
            rainbowColors.toArray(new ChatColor[0])
        );
        
        // Configurar callback para atualização de cor do rainbow
        rainbowEffect.setColorUpdateCallback((player, color) -> {
            updateRainbowColor(player, color);
        });
        
        glowEffects.put("rainbow", rainbowEffect);
        
        // Inicializar efeitos estáticos (cores sólidas)
        for (Map.Entry<String, ChatColor> entry : glowColors.entrySet()) {
            String glowId = entry.getKey();
            ChatColor color = entry.getValue();
            
            // Pular rainbow (já inicializado)
            if ("rainbow".equals(glowId) || color == null) {
                continue;
            }
            
            // Efeito estático (uma cor apenas)
            GlowEffect effect = new GlowEffect(plugin, glowId, glowId, color);
            
            // Configurar callback para atualização de cor
            effect.setColorUpdateCallback((player, color1) -> {
                applyGlowWithColor(player, color1);
            });
            
            glowEffects.put(glowId, effect);
        }
        
        // Inicializar efeitos custom (Natal, Halloween, etc) se configurados
        initializeCustomEffects();
    }
    
    /**
     * Inicializa efeitos custom (Natal, Halloween, etc) do YAML
     * Permite definir listas de cores que alternam automaticamente
     */
    private void initializeCustomEffects() {
        org.bukkit.configuration.file.FileConfiguration config = plugin.getConfigManager().getGlowsConfig();
        
        // Verificar se há seção de custom effects
        if (!config.contains("custom-effects")) {
            return;
        }
        
        org.bukkit.configuration.ConfigurationSection customSection = config.getConfigurationSection("custom-effects");
        if (customSection == null) {
            return;
        }
        
        for (String effectId : customSection.getKeys(false)) {
            try {
                // Obter lista de cores do YAML
                List<String> colorNames = config.getStringList("custom-effects." + effectId + ".colors");
                if (colorNames == null || colorNames.isEmpty()) {
                    continue;
                }
                
                // Converter nomes de cores para ChatColor
                List<ChatColor> effectColors = new ArrayList<>();
                for (String colorName : colorNames) {
                    try {
                        ChatColor color = ChatColor.valueOf(colorName.toUpperCase().replace(" ", "_"));
                        effectColors.add(color);
                    } catch (IllegalArgumentException e) {
                        plugin.getLogger().warning("Cor inválida no efeito custom '" + effectId + "': " + colorName);
                    }
                }
                
                if (effectColors.isEmpty()) {
                    continue;
                }
                
                // Obter delay (em segundos)
                double delaySeconds = config.getDouble("custom-effects." + effectId + ".delay", 1.0);
                
                // Obter display name
                String displayName = config.getString("custom-effects." + effectId + ".display-name", effectId);
                
                // Criar efeito custom
                GlowEffect customEffect = new GlowEffect(
                    plugin,
                    effectId,
                    displayName,
                    delaySeconds,
                    effectColors
                );
                
                // Configurar callback para atualização de cor
                customEffect.setColorUpdateCallback((player, color) -> {
                    applyGlowWithColor(player, color);
                });
                
                glowEffects.put(effectId, customEffect);
                
                // Adicionar ao mapa de cores para compatibilidade
                glowColors.put(effectId, null); // null indica que é efeito custom
                
                plugin.getLogger().info("Efeito custom carregado: " + effectId + " com " + effectColors.size() + " cores");
            } catch (Exception e) {
                plugin.getLogger().warning("Erro ao carregar efeito custom '" + effectId + "': " + e.getMessage());
            }
        }
    }
    
    /**
     * Atualiza a cor do rainbow para um player
     * Chamado pelo callback do GlowEffect
     */
    private void updateRainbowColor(Player player, ChatColor color) {
        if (player == null || !player.isOnline() || color == null) {
            return;
        }
        
        try {
            // Garantir que está usando o scoreboard principal
            if (player.getScoreboard() != mainScoreboard) {
                player.setScoreboard(mainScoreboard);
            }
            
            String teamName = getPlayerTeamName(player);
            if (teamName == null || teamName.isEmpty()) {
                return;
            }
            
            Team team = mainScoreboard.getTeam(teamName);
            if (team == null) {
                // Se o team não existe, criar
                applyGlowWithColor(player, color);
                return;
            }
            
            String cleanName = ChatColor.stripColor(player.getName());
            if (cleanName == null || cleanName.isEmpty()) {
                return;
            }
            
            // Se o player ainda não está no team (ex: após desligar e religar), recriar
            if (!team.hasEntry(cleanName)) {
                applyGlowWithColor(player, color);
                return;
            }
            
            // APENAS atualizar a cor do time existente (sem remover/readicionar player)
            team.setColor(color);
            
            // CRÍTICO: Notificar TODOS os players sobre a mudança de cor do rainbow
            // Isso garante que todos os clientes recebam a atualização e evita DCs
            notifyAllPlayersOfTeamChange(player, teamName, color);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao atualizar cor do rainbow em " + player.getName() + ": " + e.getMessage());
        }
    }
    
    private void initializeRainbowColors() {
        // Lista de cores para o rainbow glow
        // ORDEM EXATA DO THEGLOW (menus.yml linha 146-166) para compatibilidade
        rainbowColors = new ArrayList<>();
        // Paleta clássica do arco-íris: Red, Orange, Yellow, Green, Blue, Indigo, Violet
        rainbowColors.add(ChatColor.RED);          // Red
        rainbowColors.add(ChatColor.GOLD);         // Orange
        rainbowColors.add(ChatColor.YELLOW);       // Yellow
        rainbowColors.add(ChatColor.GREEN);        // Green
        rainbowColors.add(ChatColor.BLUE);         // Blue
        rainbowColors.add(ChatColor.DARK_BLUE);    // Indigo
        rainbowColors.add(ChatColor.LIGHT_PURPLE); // Violet
    }

    private void initializeGlowColors() {
        // Todas as 16 cores do ChatColor do Minecraft
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
        
        // Aliases comuns
        glowColors.put("cyan", ChatColor.AQUA);
        glowColors.put("purple", ChatColor.LIGHT_PURPLE);
        glowColors.put("pink", ChatColor.LIGHT_PURPLE);
        glowColors.put("orange", ChatColor.GOLD);
        
        // Rainbow glow (especial, será tratado diferente)
        glowColors.put("rainbow", null); // null indica que é rainbow
    }
    
    private void startRainbowTask() {
        // Obter intervalo do config (em segundos)
        double intervalSeconds = plugin.getConfigManager().getGlowsConfig()
            .getDouble("settings.rainbow.change-interval", 3.0);
        
        // Converter para ticks (1 segundo = 20 ticks)
        // Mínimo: 3 ticks (0.15 segundos) para evitar spam de pacotes e desconexões
        long intervalTicks = Math.max(3L, (long) (intervalSeconds * 20));
        
        // Cancelar task anterior se existir
        if (rainbowTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(rainbowTaskId);
        }
        
        // Criar task que atualiza rainbow glows
        rainbowTaskId = plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            updateRainbowGlows();
        }, intervalTicks, intervalTicks);
    }
    
    private void updateRainbowGlows() {
        // Atualizar cor de todos os players com rainbow glow de forma segura
        // IMPORTANTE: NÃO remover/readicionar players aos times, apenas atualizar a cor
        for (Map.Entry<UUID, String> entry : new HashMap<>(activeGlows).entrySet()) {
            if ("rainbow".equals(entry.getValue())) {
                Player player = plugin.getServer().getPlayer(entry.getKey());
                
                // Verificações de segurança antes de aplicar
                if (player == null || !player.isOnline()) {
                    continue;
                }
                
                // Verificar se o jogador já passou do período inicial de login (1 segundo)
                Long joinTime = playerJoinTime.get(entry.getKey());
                if (joinTime != null && (System.currentTimeMillis() - joinTime) < 1000) {
                    continue; // Pular jogadores que acabaram de entrar
                }
                
                // Verificar se o jogador tem scoreboard válido
                try {
                    Scoreboard playerScoreboard = player.getScoreboard();
                    if (playerScoreboard == null) {
                        continue; // Jogador não tem scoreboard ainda
                    }
                    
                    // APENAS atualizar a cor do time existente (sem remover/readicionar player)
                    updateRainbowGlowColor(player);
                } catch (Exception e) {
                    // Erro silencioso - não derruba a task nem outros players
                    plugin.getLogger().warning("Erro ao atualizar rainbow glow em " + player.getName() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Aplica o rainbow glow inicialmente (primeira vez que é ativado)
     * Cria o time e adiciona o player uma única vez
     * CRÍTICO: Define a cor inicial para que outros jogadores vejam corretamente
     */
    private void applyRainbowGlow(Player player) {
        // Verificações de segurança
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Garantir que está usando o scoreboard principal (CRÍTICO para outros jogadores verem)
        try {
            player.setScoreboard(mainScoreboard);
        } catch (Exception e) {
            return;
        }
        
        // Resetar glow antes de aplicar rainbow para evitar conflitos
        resetGlowForPlayer(player);
        
        // Obter índice inicial (primeira cor do rainbow)
        int currentIndex = 0;
        if (currentIndex >= rainbowColors.size()) {
            currentIndex = 0;
        }
        
        ChatColor color = rainbowColors.get(currentIndex);
        if (color == null) {
            // Fallback para primeira cor válida
            if (!rainbowColors.isEmpty()) {
                color = rainbowColors.get(0);
            } else {
                return;
            }
        }
        
        // Aplicar glow inicial com a primeira cor (cria o time e adiciona o player)
        // Isso garante que a cor inicial seja definida corretamente
        applyGlowWithColor(player, color);
        
        // Inicializar índice para próxima cor no loop
        rainbowColorIndex.put(player.getUniqueId(), 1);
    }
    
    /**
     * Atualiza APENAS a cor do rainbow glow sem remover/readicionar o player
     * Usado no loop periódico para mudar apenas a propriedade de cor
     * CRÍTICO: Não remove/readiciona players para evitar spam de pacotes e desconexões
     */
    private void updateRainbowGlowColor(Player player) {
        // Verificações de segurança
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Garantir que está usando o scoreboard principal
        try {
            player.setScoreboard(mainScoreboard);
        } catch (Exception e) {
            return;
        }
        
        // Obter nome do Team do jogador (UUID-based)
        String teamName = getPlayerTeamName(player);
        if (teamName == null || teamName.isEmpty()) {
            return;
        }
        
        // Obter o Team existente do jogador no scoreboard principal
        Team team = mainScoreboard.getTeam(teamName);
        if (team == null) {
            // Se o time não existe, o player ainda não foi adicionado ao time
            // Isso não deveria acontecer no loop, mas se acontecer, apenas pular
            // O time será criado na próxima ativação manual ou no applyRainbowGlow inicial
            return;
        }
        
        // Verificar se o player realmente está no team (safety check)
        String cleanName = ChatColor.stripColor(player.getName());
        if (!team.hasEntry(cleanName)) {
            // Player não está no time, recriar (não deveria acontecer, mas seguro)
            int currentIndex = rainbowColorIndex.getOrDefault(player.getUniqueId(), 0);
            ChatColor color = rainbowColors.get(currentIndex);
            if (color != null) {
                applyGlowWithColor(player, color);
                rainbowColorIndex.put(player.getUniqueId(), (currentIndex + 1) % rainbowColors.size());
            }
            return;
        }
        
        // Obter índice atual e próxima cor
        int currentIndex = rainbowColorIndex.getOrDefault(player.getUniqueId(), 0);
        if (currentIndex >= rainbowColors.size() || currentIndex < 0) {
            currentIndex = 0;
        }
        
        ChatColor nextColor = rainbowColors.get(currentIndex);
        if (nextColor == null) {
            return;
        }
        
        // Calcular próximo índice antes do try-catch
        int nextIndex = (currentIndex + 1) % rainbowColors.size();
        
        // APENAS atualizar a cor do time existente (SEM remover/readicionar player)
        // Esta é a única operação permitida no loop para evitar spam de pacotes
        // CRÍTICO: Atualizar a cor para que outros jogadores vejam a mudança
        try {
            // Garantir que o time está no scoreboard principal (para visibilidade global)
            if (team.getScoreboard() != mainScoreboard) {
                // Se o time estiver em outro scoreboard, recriar no principal
                // Isso não deveria acontecer, mas é uma segurança extra
                applyGlowWithColor(player, nextColor);
                rainbowColorIndex.put(player.getUniqueId(), nextIndex);
                return;
            }
            
            // Atualizar a cor do time existente
            team.setColor(nextColor);
            
            // CRÍTICO: Notificar TODOS os players sobre a mudança de cor do rainbow
            // Isso garante que todos os clientes recebam a atualização e evita DCs
            notifyAllPlayersOfTeamChange(player, teamName, nextColor);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao atualizar cor do rainbow em " + player.getName() + ": " + e.getMessage());
            return;
        }
        
        // Avançar para próxima cor (ciclar)
        rainbowColorIndex.put(player.getUniqueId(), nextIndex);
    }
    
    /**
     * Gera um nome de Team seguro baseado no nome do jogador
     * Usa o TeamNameGenerator robusto que detecta e resolve conflitos
     */
    private String getPlayerTeamName(Player player) {
        if (player == null) {
            return null;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Verificar se já temos o nome gerado em cache
        if (playerTeamNames.containsKey(uuid)) {
            return playerTeamNames.get(uuid);
        }
        
        // Obter todos os nomes de teams existentes (do scoreboard e do cache)
        Set<String> existingTeamNames = new HashSet<>();
        
        // Adicionar teams do scoreboard
        for (Team team : mainScoreboard.getTeams()) {
            if (team != null && team.getName() != null) {
                existingTeamNames.add(team.getName());
            }
        }
        
        // Adicionar teams do cache
        existingTeamNames.addAll(playerTeamNames.values());
        existingTeamNames.addAll(teamNameGenerator.getUsedTeamNames());
        
        // Gerar novo nome usando o gerador robusto
        String teamName = teamNameGenerator.generateTeamName(player, existingTeamNames);
        
        if (teamName != null) {
            // Registrar nome gerado
            playerTeamNames.put(uuid, teamName);
            teamNameGenerator.registerTeamName(teamName);
        }
        
        return teamName;
    }
    
    private void applyGlowWithColor(Player player, ChatColor color) {
        if (color == null || player == null || !player.isOnline()) {
            return;
        }
        
        try {
            // Verificar se player tem scoreboard válido
            if (player.getScoreboard() == null) {
                return;
            }
            
            // Garantir que o player está usando o scoreboard principal
            player.setScoreboard(mainScoreboard);
            
            // Usar nome limpo do player (sem cores) para adicionar ao team
            String cleanName = ChatColor.stripColor(player.getName());
            if (cleanName == null || cleanName.isEmpty()) {
                return;
            }
            
            // Gerar nome do Team baseado no UUID do jogador (SEGURO)
            String teamName = getPlayerTeamName(player);
            if (teamName == null || teamName.isEmpty()) {
                return;
            }
            
            // Limpar qualquer team anterior primeiro (remover de todos os teams)
            for (Team team : mainScoreboard.getTeams()) {
                if (team != null && team.hasEntry(cleanName)) {
                    team.removeEntry(cleanName);
                }
            }
            
            // Obter ou criar Team único para este jogador
            Team team = mainScoreboard.getTeam(teamName);
            
            if (team == null) {
                // Criar novo time
                team = mainScoreboard.registerNewTeam(teamName);
                if (team != null) {
                    // CRÍTICO: Definir cor IMEDIATAMENTE ao criar o time
                    // Isso garante que outros jogadores vejam a cor correta desde o início
                    team.setColor(color);
                }
            } else {
                // Atualizar cor do Team existente
                // IMPORTANTE: Sempre atualizar a cor para garantir que está visível para outros
                team.setColor(color);
            }
            
            if (team != null) {
                // Adicionar player ao team usando o NOME do jogador (não o UUID)
                // O nome do Team é UUID, mas a entry é o nome do jogador
                if (!team.hasEntry(cleanName)) {
                    team.addEntry(cleanName);
                }
                
                // Garantir que a cor está definida (fallback de segurança)
                team.setColor(color);
            }
            
            // ATIVAR O GLOW
            player.setGlowing(true);
            
            // CRÍTICO: Notificar TODOS os players online sobre a mudança do team
            // Isso garante que todos os clientes recebam a atualização do scoreboard
            notifyAllPlayersOfTeamChange(player, teamName, color);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao aplicar glow com cor em " + (player != null ? player.getName() : "null") + ": " + e.getMessage());
        }
    }
    
    /**
     * Notifica TODOS os players online sobre mudanças em um team
     * CRÍTICO: Isso evita DCs porque todos os clientes recebem a atualização
     * Baseado no sistema do eGlow que envia packets para todos os players
     */
    private void notifyAllPlayersOfTeamChange(Player changedPlayer, String teamName, ChatColor color) {
        if (changedPlayer == null || teamName == null || teamName.isEmpty()) {
            return;
        }
        
        // Garantir que TODOS os players estão usando o scoreboard principal
        // Isso força uma sincronização do scoreboard em todos os clientes
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer == null || !onlinePlayer.isOnline()) {
                continue;
            }
            
            try {
                // Apenas forçar se estiverem em outro scoreboard
                if (onlinePlayer.getScoreboard() != mainScoreboard) {
                    onlinePlayer.setScoreboard(mainScoreboard);
                }
            } catch (Exception e) {
                // Erro silencioso - não derruba outros players
                plugin.getLogger().warning("Erro ao notificar player " + onlinePlayer.getName() + " sobre mudança de team: " + e.getMessage());
            }
        }
    }
    
    /**
     * Notifica TODOS os players sobre a remoção de um player de um team
     * CRÍTICO: Isso evita DCs quando um player remove o glow
     */
    private void notifyAllPlayersOfTeamRemoval(String teamName) {
        if (teamName == null || teamName.isEmpty()) {
            return;
        }
        
        // Garantir que TODOS os players estão usando o scoreboard principal
        // Isso força uma sincronização do scoreboard em todos os clientes
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (onlinePlayer == null || !onlinePlayer.isOnline()) {
                continue;
            }
            
            try {
                if (onlinePlayer.getScoreboard() != mainScoreboard) {
                    onlinePlayer.setScoreboard(mainScoreboard);
                }
            } catch (Exception e) {
                // Erro silencioso - não derruba outros players
                plugin.getLogger().warning("Erro ao notificar player " + onlinePlayer.getName() + " sobre remoção de team: " + e.getMessage());
            }
        }
    }

    private void loadActiveGlows() {
        // Carregar glows ativos do banco de dados para jogadores online
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            String glowId = dao.getActiveGlow(player.getUniqueId());
            if (glowId != null && !glowId.isEmpty()) {
                activeGlows.put(player.getUniqueId(), glowId);
                applyGlow(player, glowId);
            }
        }
    }

    public List<String> getAllGlowIds() {
        // Retornar todos os IDs: cores estáticas + rainbow + custom effects
        Set<String> allIds = new HashSet<>(glowColors.keySet());
        allIds.addAll(glowEffects.keySet()); // Incluir custom effects que podem não estar em glowColors
        return new ArrayList<>(allIds);
    }

    public boolean hasGlow(Player player) {
        return activeGlows.containsKey(player.getUniqueId());
    }

    public String getActiveGlow(Player player) {
        return activeGlows.get(player.getUniqueId());
    }

    public void setGlow(Player player, String glowId) {
        if (glowId == null || glowId.isEmpty()) {
            removeGlow(player);
            return;
        }

        UUID uuid = player.getUniqueId();
        String previousGlowId = activeGlows.get(uuid);
        
        // CRÍTICO: Desativar TODOS os efeitos anteriores ANTES de aplicar o novo
        // Isso garante que apenas um glow por player está ativo e evita bugs de múltiplos glows
        deactivateAllEffectsForPlayer(player);
        
        // Enviar mensagem se havia um glow anterior ativo
        if (previousGlowId != null && !previousGlowId.equals(glowId)) {
            String previousGlowName = getGlowDisplayName(previousGlowId);
            if (previousGlowName != null && !previousGlowName.isEmpty()) {
                PlayerUtils.sendMessage(player, "<gray>Brilho de <yellow>" + previousGlowName + "</yellow> desativado.</gray>");
            }
        }
        
        // Resetar visual do glow (remover de teams) antes de aplicar novo
        resetGlowForPlayer(player);

        // Atualizar glow ativo
        activeGlows.put(uuid, glowId);
        dao.setActiveGlow(uuid, glowId);
        
        // Aplicar novo glow
        applyGlow(player, glowId);
    }
    
    /**
     * Obtém o nome de exibição de um glow
     */
    private String getGlowDisplayName(String glowId) {
        if (glowId == null || glowId.isEmpty()) {
            return null;
        }
        
        // Verificar se é um efeito custom ou rainbow
        GlowEffect effect = glowEffects.get(glowId);
        if (effect != null) {
            return effect.getDisplayName();
        }
        
        // Tentar obter do YAML
        try {
            org.bukkit.configuration.file.FileConfiguration config = plugin.getConfigManager().getGlowsConfig();
            String displayName = config.getString("glows." + glowId + ".display_name");
            if (displayName != null && !displayName.isEmpty()) {
                // Remover tags de cor para obter nome limpo
                return ChatColor.stripColor(displayName.replaceAll("<[^>]+>", "").replaceAll("&[0-9a-fk-or]", ""));
            }
        } catch (Exception e) {
            // Ignorar erro
        }
        
        // Fallback: capitalizar primeira letra
        return glowId.substring(0, 1).toUpperCase() + glowId.substring(1).toLowerCase();
    }

    public void removeGlow(Player player) {
        if (player == null) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // CRÍTICO: Desativar TODOS os efeitos do player usando método centralizado
        deactivateAllEffectsForPlayer(player);
        
        // Remover do mapa de glows ativos
        activeGlows.remove(uuid);
        dao.removeActiveGlow(uuid);
        
        // Remover visual do glow (scoreboard/teams)
        removeGlowEffect(player);
    }

    private void applyGlow(Player player, String glowId) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // CRÍTICO: Garantir que NENHUM outro efeito está ativo para este player
        // Desativar TODOS os efeitos antes de aplicar o novo (usando método centralizado)
        deactivateAllEffectsForPlayer(player);
        
        // Usar novo sistema de efeitos encapsulados
        GlowEffect effect = glowEffects.get(glowId);
        
        if (effect != null) {
            // Resetar visual do glow (remover de teams) antes de aplicar novo
            resetGlowForPlayer(player);
            
            // Limpar dados do rainbow antigo (se existir)
            rainbowColorIndex.remove(uuid);
            
            // Ativar efeito para este player
            effect.activateForEntity(player);
            
            // Aplicar cor inicial será feito pelo delay de 20 ticks no GlowEffect
            // Não aplicar aqui para evitar conflitos com o delay
        } else {
            // Se o efeito não foi encontrado, logar para debug
            plugin.getLogger().warning("GlowEffect não encontrado para ID: " + glowId + ". Efeitos disponíveis: " + glowEffects.keySet());
            
            // Fallback para sistema antigo (compatibilidade)
            // NOTA: O rainbow DEVE estar no glowEffects, se não estiver, há um problema de inicialização
            if ("rainbow".equals(glowId)) {
                plugin.getLogger().severe("ERRO CRÍTICO: Rainbow não encontrado no glowEffects! Verificar inicialização.");
                // Tentar recriar o rainbow effect
                double intervalSeconds = plugin.getConfigManager().getGlowsConfig()
                    .getDouble("settings.rainbow.change-interval", 3.0);
                
                GlowEffect rainbowEffect = new GlowEffect(
                    plugin,
                    "rainbow",
                    "Rainbow",
                    intervalSeconds,
                    rainbowColors.toArray(new ChatColor[0])
                );
                
                rainbowEffect.setColorUpdateCallback((p, c) -> {
                    updateRainbowColor(p, c);
                });
                
                glowEffects.put("rainbow", rainbowEffect);
                
                // Tentar aplicar novamente
                resetGlowForPlayer(player);
                rainbowColorIndex.remove(uuid);
                rainbowEffect.activateForEntity(player);
                return;
            }
            
            ChatColor color = glowColors.get(glowId);
            if (color == null) {
                plugin.getLogger().warning("Cor não encontrada para glow ID: " + glowId);
                return;
            }
            
            // Resetar glow e aplicar cor estática
            resetGlowForPlayer(player);
            rainbowColorIndex.remove(uuid);
            applyGlowWithColor(player, color);
        }
    }
    
    /**
     * Desativa TODOS os efeitos de glow para um player
     * CRÍTICO: Garante que nenhum efeito fica ativo, evitando múltiplos glows simultâneos
     */
    private void deactivateAllEffectsForPlayer(Player player) {
        if (player == null) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Iterar sobre TODOS os efeitos e desativar se estiverem ativos para este player
        for (Map.Entry<String, GlowEffect> entry : glowEffects.entrySet()) {
            GlowEffect effect = entry.getValue();
            if (effect != null && effect.isEntityActive(uuid)) {
                effect.deactivateForEntity(player);
            }
        }
        
        // Limpar dados do rainbow
        rainbowColorIndex.remove(uuid);
    }
    
    /**
     * Reseta o glow do jogador, removendo de todos os teams de forma segura
     * CRÍTICO: Este método apenas remove o visual (scoreboard), NÃO desativa os efeitos
     * Para desativar efeitos, use deactivateAllEffectsForPlayer() ou removeGlow()
     */
    private void resetGlowForPlayer(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        try {
            // Desativar glow visual primeiro
            player.setGlowing(false);
            
            // Usar nome limpo do player
            String cleanName = ChatColor.stripColor(player.getName());
            if (cleanName == null || cleanName.isEmpty()) {
                return;
            }
            
            // Obter nome do Team do jogador (UUID-based) sem gerar novos nomes
            String playerTeamName = playerTeamNames.get(player.getUniqueId());
            if (playerTeamName == null || playerTeamName.isEmpty()) {
                // Fallback: descobrir pelo scoreboard
                for (Team team : mainScoreboard.getTeams()) {
                    if (team != null && team.hasEntry(cleanName)) {
                        playerTeamName = team.getName();
                        break;
                    }
                }
            }
            
            // Verificar se tem scoreboard válido
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard == null) {
                scoreboard = mainScoreboard;
            }
            
            // Garantir que está no scoreboard principal (CRÍTICO para outros jogadores verem)
            player.setScoreboard(mainScoreboard);
            
            // Remover do Team específico do jogador no scoreboard principal
            // IMPORTANTE: NUNCA usar team.unregister() durante o gameplay - causa DC em outros players
            if (playerTeamName != null) {
                Team playerTeam = mainScoreboard.getTeam(playerTeamName);
                if (playerTeam != null && playerTeam.hasEntry(cleanName)) {
                    playerTeam.removeEntry(cleanName);
                    // NÃO remover o time (unregister) - deixar vazio é seguro
                }
            }
            
            // Remover de todos os outros teams do scoreboard principal (limpeza geral)
            // CRÍTICO: Isso garante que o player não está em múltiplos teams
            for (Team team : mainScoreboard.getTeams()) {
                if (team != null && team.hasEntry(cleanName)) {
                    team.removeEntry(cleanName);
                    // NÃO remover o time (unregister) - deixar vazio é seguro
                }
            }
            
            // CRÍTICO: Notificar TODOS os players sobre a remoção/reset
            // Isso garante que todos os clientes recebam a atualização e evita DCs
            if (playerTeamName != null) {
                notifyAllPlayersOfTeamRemoval(playerTeamName);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao resetar glow de " + (player != null ? player.getName() : "null") + ": " + e.getMessage());
        }
    }

    private void removeGlowEffect(Player player) {
        removeGlowEffect(player, true);
    }
    
    private void removeGlowEffect(Player player, boolean notifyPlayers) {
        if (player == null) {
            return;
        }
        
        try {
            try {
                player.setGlowing(false);
            } catch (Exception ignored) {
                // Jogador pode já ter desconectado
            }
            
            // Usar nome limpo do player (sem cores)
            String cleanName = ChatColor.stripColor(player.getName());
            if (cleanName == null || cleanName.isEmpty()) {
                return;
            }
            
            // Obter nome do Team do jogador (UUID-based) sem gerar novos nomes
            String playerTeamName = playerTeamNames.get(player.getUniqueId());
            if (playerTeamName == null || playerTeamName.isEmpty()) {
                for (Team team : mainScoreboard.getTeams()) {
                    if (team != null && team.hasEntry(cleanName)) {
                        playerTeamName = team.getName();
                        break;
                    }
                }
            }
            
            Scoreboard scoreboard = mainScoreboard;

            // Remover do Team específico do jogador
            // CRÍTICO: NUNCA usar team.unregister() durante o gameplay - causa DC em outros players
            if (playerTeamName != null) {
                try {
                    Team playerTeam = scoreboard.getTeam(playerTeamName);
                    if (playerTeam != null && playerTeam.hasEntry(cleanName)) {
                        playerTeam.removeEntry(cleanName);
                        // NÃO remover o time (unregister) - deixar vazio é seguro e não causa DC
                    }
                } catch (Exception e) {
                    // Erro silencioso
                }
            }
            
            // Limpar também de todos os teams restantes (limpeza geral de segurança)
            // CRÍTICO: NUNCA usar team.unregister() durante o gameplay
            for (Team team : scoreboard.getTeams()) {
                if (team != null && team.hasEntry(cleanName)) {
                    try {
                        team.removeEntry(cleanName);
                        // NÃO remover o time (unregister) - deixar vazio é seguro
                    } catch (Exception e) {
                        // Erro silencioso
                    }
                }
            }
            
            // CRÍTICO: Notificar TODOS os players online sobre a remoção
            // Isso garante que todos os clientes recebam a atualização e evita DCs
            if (notifyPlayers && playerTeamName != null) {
                notifyAllPlayersOfTeamRemoval(playerTeamName);
            }
        } catch (Exception e) {
            // Erro geral silencioso - não crashar o evento de quit
            plugin.getLogger().warning("Erro ao remover glow effect de " + (player != null ? player.getName() : "null") + ": " + e.getMessage());
        }
    }

    public void onPlayerJoin(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Registrar tempo de join para verificação posterior
        playerJoinTime.put(player.getUniqueId(), System.currentTimeMillis());
        
        // CRÍTICO: Delay de 2 ticks (como mencionado na análise do eGlow) para evitar DC no join
        // Isso evita conflitos de pacotes durante o handshake do login
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            // Verificar se player ainda está online após o delay
            if (player == null || !player.isOnline()) {
                return;
            }
            
            String glowId = dao.getActiveGlow(player.getUniqueId());
            if (glowId != null && !glowId.isEmpty()) {
                activeGlows.put(player.getUniqueId(), glowId);
                
                // Aplicar glow usando novo sistema (já tem delay interno se necessário)
                applyGlowOnJoin(player, glowId);
            }
        }, 2L); // Delay de 2 ticks (0.1 segundos) - conforme análise do eGlow
    }
    
    private void applyGlowOnJoin(Player player, String glowId) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        // Usar novo sistema de efeitos encapsulados
        GlowEffect effect = glowEffects.get(glowId);
        
        if (effect != null) {
            // Resetar glow anterior primeiro
            resetGlowForPlayer(player);
            
            // Ativar efeito para este player
            effect.activateForEntity(player);
            
            // Aplicar cor inicial imediatamente
            ChatColor initialColor = effect.getCurrentColor(player);
            if (initialColor != null && initialColor != ChatColor.RESET) {
                applyGlowWithColor(player, initialColor);
            }
        } else {
            // Fallback para sistema antigo (compatibilidade)
            if ("rainbow".equals(glowId)) {
                applyRainbowGlow(player);
                return;
            }
            
            ChatColor color = glowColors.get(glowId);
            if (color == null) {
                return;
            }
            
            rainbowColorIndex.remove(player.getUniqueId());
            applyGlowWithColor(player, color);
        }
    }

    public void onPlayerQuit(Player player) {
        if (player == null) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        String glowId = activeGlows.get(uuid);
        
        // Desativar efeito usando novo sistema
        if (glowId != null) {
            GlowEffect effect = glowEffects.get(glowId);
            if (effect != null) {
                effect.deactivateForEntity(player);
            }
        }
        
        // Não removemos do mapa, pois queremos manter os dados
        // Apenas removemos o efeito visual de forma segura
        try {
            removeGlowEffect(player, false);
        } catch (Exception e) {
            plugin.getLogger().warning("Erro ao remover glow no quit de " + player.getName() + ": " + e.getMessage());
        }
        
        // Limpar cache de team name
        String teamName = playerTeamNames.remove(uuid);
        if (teamName != null) {
            teamNameGenerator.unregisterTeamName(teamName);
        }
        
        // Limpar dados do rainbow (DEPRECATED)
        rainbowColorIndex.remove(uuid);
        playerJoinTime.remove(uuid);
    }
    
    public void reload() {
        // Recarregar efeitos com novas configurações
        for (GlowEffect effect : glowEffects.values()) {
            if (effect != null) {
                effect.reloadEffect();
            }
        }
        
        // DEPRECATED: Manter compatibilidade durante transição
        startRainbowTask();
    }

    public boolean isValidGlowId(String glowId) {
        // Rainbow é válido mesmo que seja null no mapa
        if ("rainbow".equals(glowId)) {
            return true;
        }
        return glowColors.containsKey(glowId);
    }
    
    public void shutdown() {
        // Método para limpeza ao desabilitar plugin
        
        // Remover todos os efeitos (cancela tasks e faz shutdown dos schedulers)
        for (GlowEffect effect : glowEffects.values()) {
            if (effect != null) {
                effect.shutdown(); // Shutdown do ScheduledExecutorService
            }
        }
        
        // Limpar tasks antigas (DEPRECATED)
        if (rainbowTaskId != -1) {
            plugin.getServer().getScheduler().cancelTask(rainbowTaskId);
            rainbowTaskId = -1;
        }
        
        // Limpar dados
        rainbowColorIndex.clear();
        activeGlows.clear();
        playerJoinTime.clear();
        playerTeamNames.clear();
        teamNameGenerator.clear();
        glowEffects.clear();
    }
}

