package com.seunome.mestredosfx.managers.glow;

import com.seunome.mestredosfx.MestreDosEfeitos;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Classe base para efeitos de glow
 * Gerencia seu próprio ciclo de vida e tasks
 * Baseado no sistema do TheGlow (ScheduledExecutorService + Iterator)
 */
public class GlowEffect {
    
    private final MestreDosEfeitos plugin;
    private final String effectId;
    private final String displayName;
    private final List<ChatColor> colors;
    private final long delayMillis; // delay em milissegundos
    
    // ScheduledExecutorService para gerenciar tasks (assíncrono, thread-safe)
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    
    // Mapa de tasks ativas: UUID -> ScheduledFuture
    private final Map<UUID, ScheduledFuture<?>> activeTasks = new ConcurrentHashMap<>();
    
    // Mapa de delays iniciais pendentes (BukkitTask) para cancelar corretamente
    private final Map<UUID, BukkitTask> pendingInitialTasks = new ConcurrentHashMap<>();
    
    // Mapa de índices de cor atuais (garante restart limpo)
    private final Map<UUID, Integer> colorIndices = new ConcurrentHashMap<>();
    
    /**
     * Construtor para efeito com múltiplas cores (ex: rainbow, custom)
     * @param delaySeconds Delay em segundos (será convertido para milissegundos)
     */
    public GlowEffect(MestreDosEfeitos plugin, String effectId, String displayName, 
                     double delaySeconds, ChatColor... colors) {
        this.plugin = plugin;
        this.effectId = effectId;
        this.displayName = displayName;
        // Converter segundos para milissegundos (TheGlow usa * 50, mas vamos usar * 1000 para mais precisão)
        this.delayMillis = Math.max(50L, (long) (delaySeconds * 1000)); // Mínimo 50ms
        this.colors = new ArrayList<>(Arrays.asList(colors));
    }
    
    /**
     * Construtor para efeito com lista de cores
     * @param delaySeconds Delay em segundos (será convertido para milissegundos)
     */
    public GlowEffect(MestreDosEfeitos plugin, String effectId, String displayName, 
                     double delaySeconds, List<ChatColor> colors) {
        this.plugin = plugin;
        this.effectId = effectId;
        this.displayName = displayName;
        this.delayMillis = Math.max(50L, (long) (delaySeconds * 1000)); // Mínimo 50ms
        this.colors = new ArrayList<>(colors);
    }
    
    /**
     * Construtor para efeito estático (uma cor apenas)
     */
    public GlowEffect(MestreDosEfeitos plugin, String effectId, String displayName, ChatColor color) {
        this.plugin = plugin;
        this.effectId = effectId;
        this.displayName = displayName;
        this.delayMillis = 0; // Sem delay para efeito estático
        this.colors = Collections.singletonList(color);
    }
    
    /**
     * Ativa o efeito para uma entidade (player)
     * Baseado no sistema do TheGlow: usa ScheduledExecutorService + Iterator
     */
    public void activateForEntity(Player player) {
        if (player == null || !player.isOnline()) {
            return;
        }
        
        UUID uuid = player.getUniqueId();
        
        // Se é efeito estático (uma cor apenas), aplicar imediatamente
        if (colors.size() == 1 || delayMillis == 0) {
            ChatColor color = colors.get(0);
            if (onColorUpdate != null) {
                // Aplicar de forma síncrona (thread-safe)
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    onColorUpdate.accept(player, color);
                });
            }
            return;
        }
        
        // Cancelar task anterior (incluindo delays pendentes)
        cancelTaskForPlayer(uuid);
        
        // Resetar índice para garantir que o efeito sempre comece do início
        colorIndices.put(uuid, 0);
        
        // CRÍTICO: Aplicar cor inicial com delay de 20 ticks (1 segundo) como TheGlow
        // Guardamos a task para poder cancelar se necessário
        BukkitTask initialTask = plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            pendingInitialTasks.remove(uuid);
            
            if (!player.isOnline()) {
                cancelTaskForPlayer(uuid);
                return;
            }
            
            // Aplicar primeira cor imediatamente após o delay
            ChatColor firstColor = getNextColor(uuid);
            if (firstColor != null && onColorUpdate != null) {
                onColorUpdate.accept(player, firstColor);
            }
            
            // Iniciar task periódica APÓS aplicar primeira cor
            // Isso garante que a primeira cor seja aplicada antes de começar a alternar
            ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
                // Verificar se player ainda está online
                if (!player.isOnline()) {
                    cancelTaskForPlayer(uuid);
                    return;
                }
                
                // Obter próxima cor
                ChatColor nextColor = getNextColor(uuid);
                if (nextColor == null) {
                    return;
                }
                
                // Aplicar cor de forma SÍNCRONA (thread-safe) - CRÍTICO para evitar DC
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (player.isOnline() && onColorUpdate != null) {
                        onColorUpdate.accept(player, nextColor);
                    }
                });
                
            }, delayMillis, delayMillis, TimeUnit.MILLISECONDS);
            
            activeTasks.put(uuid, task);
        }, 20L); // Delay de 20 ticks (1 segundo) como TheGlow
        
        pendingInitialTasks.put(uuid, initialTask);
    }
    
    /**
     * Desativa o efeito para uma entidade
     */
    public void deactivateForEntity(Player player) {
        if (player == null) {
            return;
        }
        
        cancelTaskForPlayer(player.getUniqueId());
    }
    
    /**
     * Cancela a task de um player específico
     */
    private void cancelTaskForPlayer(UUID uuid) {
        ScheduledFuture<?> task = activeTasks.remove(uuid);
        if (task != null) {
            task.cancel(false);
        }
        
        BukkitTask initialTask = pendingInitialTasks.remove(uuid);
        if (initialTask != null) {
            initialTask.cancel();
        }
        
        colorIndices.remove(uuid);
    }
    
    /**
     * Verifica se a entidade está ativa neste efeito
     */
    public boolean isEntityActive(UUID uuid) {
        return activeTasks.containsKey(uuid) || pendingInitialTasks.containsKey(uuid);
    }
    
    /**
     * Obtém a cor atual para uma entidade
     * Para efeitos dinâmicos, retorna a primeira cor (iterator não expõe cor atual)
     */
    public ChatColor getCurrentColor(Player player) {
        if (player == null || colors.isEmpty()) {
            return ChatColor.RESET;
        }
        
        // Para efeitos estáticos, retornar a cor
        if (colors.size() == 1) {
            return colors.get(0);
        }
        
        // Para efeitos dinâmicos, retornar primeira cor (iterator não expõe estado)
        return colors.get(0);
    }
    
    /**
     * Callback chamado quando a cor deve ser atualizada
     */
    private java.util.function.BiConsumer<Player, ChatColor> onColorUpdate;
    
    /**
     * Define o callback para atualização de cor
     */
    public void setColorUpdateCallback(java.util.function.BiConsumer<Player, ChatColor> callback) {
        this.onColorUpdate = callback;
    }
    
    /**
     * Cancela e remove o efeito completamente
     */
    public void removeEffect() {
        // Cancelar todas as tasks
        for (ScheduledFuture<?> task : activeTasks.values()) {
            if (task != null) {
                task.cancel(false);
            }
        }
        
        for (BukkitTask task : pendingInitialTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        
        activeTasks.clear();
        pendingInitialTasks.clear();
        colorIndices.clear();
    }
    
    /**
     * Recarrega o efeito (mantém entidades ativas)
     */
    public void reloadEffect() {
        // Cancelar todas as tasks atuais
        List<UUID> activeUuids = new ArrayList<>(activeTasks.keySet());
        
        for (ScheduledFuture<?> task : activeTasks.values()) {
            if (task != null) {
                task.cancel(false);
            }
        }
        
        for (BukkitTask task : pendingInitialTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        
        activeTasks.clear();
        pendingInitialTasks.clear();
        colorIndices.clear();
        
        // Reativar para todos os players que estavam ativos
        for (UUID uuid : activeUuids) {
            Player player = plugin.getServer().getPlayer(uuid);
            if (player != null && player.isOnline()) {
                activateForEntity(player);
            }
        }
    }
    
    /**
     * Shutdown do scheduler (chamado ao desabilitar plugin)
     */
    public void shutdown() {
        removeEffect();
        for (BukkitTask task : pendingInitialTasks.values()) {
            if (task != null) {
                task.cancel();
            }
        }
        pendingInitialTasks.clear();
        colorIndices.clear();
        
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5L, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
    
    // Getters
    public String getEffectId() {
        return effectId;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public List<ChatColor> getColors() {
        return new ArrayList<>(colors);
    }
    
    public long getDelayMillis() {
        return delayMillis;
    }
    
    public int getActiveEntityCount() {
        return activeTasks.size();
    }
    
    public boolean isRunning() {
        return !activeTasks.isEmpty() || !pendingInitialTasks.isEmpty();
    }
    
    /**
     * Obtém a próxima cor do efeito para o player e já avança o índice
     */
    private ChatColor getNextColor(UUID uuid) {
        if (colors.isEmpty()) {
            return ChatColor.RESET;
        }
        
        int currentIndex = colorIndices.getOrDefault(uuid, 0);
        if (currentIndex < 0 || currentIndex >= colors.size()) {
            currentIndex = 0;
        }
        
        ChatColor color = colors.get(currentIndex);
        int nextIndex = (currentIndex + 1) % colors.size();
        colorIndices.put(uuid, nextIndex);
        
        return color;
    }
}

