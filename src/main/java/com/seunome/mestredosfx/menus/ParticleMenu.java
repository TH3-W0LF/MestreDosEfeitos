package com.seunome.mestredosfx.menus;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.hooks.ItemsAdderHook;
import com.seunome.mestredosfx.managers.ParticleManager;
import com.seunome.mestredosfx.utils.ItemBuilder;
import com.seunome.mestredosfx.utils.PlayerUtils;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class ParticleMenu implements Listener {

    private final MestreDosEfeitos plugin;
    private final ParticleManager particleManager;
    private final ItemsAdderHook itemsAdder;
    private final PlayerEffectDAO dao;
    private final Map<UUID, Integer> playerPages = new HashMap<>();
    private final Map<Player, Map<Integer, String>> playerParticleSlots = new HashMap<>();
    private static final int ITEMS_PER_PAGE = 28; // Área útil do inventário (54 - bordas - botões)

    public ParticleMenu(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.particleManager = plugin.getParticleManager();
        this.itemsAdder = plugin.getItemsAdderHook();
        this.dao = new PlayerEffectDAO(plugin);
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, int page) {
        List<String> allParticles = particleManager.getAllParticleIds();
        int totalPages = (int) Math.ceil((double) allParticles.size() / ITEMS_PER_PAGE);
        
        if (page < 0) page = 0;
        if (page >= totalPages) page = Math.max(0, totalPages - 1);
        
        playerPages.put(player.getUniqueId(), page);

        // Carregar título do YML (seguindo o mesmo padrão dos outros menus)
        String titleText = plugin.getConfigManager().getParticlesConfig().getString("menu-title", 
            plugin.getConfigManager().getParticlesConfig().getString("settings.default-menu-title", 
                "<gradient:light_purple:pink>✨ Partículas</gradient>"));
        
        // Usar MiniMessage para parsear o título (igual aos outros menus)
        MiniMessage mm = MiniMessage.miniMessage();
        net.kyori.adventure.text.Component title = mm.deserialize(titleText);
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, title);

        String activeParticle = particleManager.getActiveParticle(player);
        
        // Mapa de slot -> particle ID para este player
        Map<Integer, String> slotMap = new HashMap<>();
        playerParticleSlots.put(player, slotMap);

        // Obter configuração de partículas
        org.bukkit.configuration.file.FileConfiguration particlesConfig = plugin.getConfigManager().getParticlesConfig();
        
        // Separar partículas com slots customizados e sem slots
        Map<Integer, String> customSlotParticles = new HashMap<>();
        Set<Integer> usedSlots = new HashSet<>();
        
        // Identificar slots customizados (todas as partículas, independente da página)
        for (String particleId : allParticles) {
            int customSlot = particlesConfig.getInt("particles." + particleId + ".slot", -1);
            if (customSlot >= 0 && customSlot < 54) {
                customSlotParticles.put(customSlot, particleId);
                usedSlots.add(customSlot);
            }
        }
        
        // Processar partículas com slots customizados (sempre aparecem)
        for (Map.Entry<Integer, String> entry : customSlotParticles.entrySet()) {
            int slot = entry.getKey();
            String particleId = entry.getValue();
            processParticleItem(player, particleId, slot, inv, slotMap, particlesConfig, activeParticle);
        }
        
        // Processar partículas sem slots customizados (paginação normal)
        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, allParticles.size());
        
        for (int i = startIndex; i < endIndex; i++) {
            String particleId = allParticles.get(i);
            
            // Pular se já está em slot customizado
            if (customSlotParticles.containsValue(particleId)) {
                continue;
            }
            
            // Encontrar próximo slot disponível
            int autoSlot = 10;
            while (usedSlots.contains(autoSlot) || autoSlot >= 44 || 
                   (autoSlot + 1) % 9 == 0 || autoSlot % 9 == 0) {
                autoSlot++;
                if (autoSlot >= 44) break;
            }
            if (autoSlot >= 44) break;
            
            processParticleItem(player, particleId, autoSlot, inv, slotMap, particlesConfig, activeParticle);
            usedSlots.add(autoSlot);
        }

        // Botão voltar ao menu principal
        ItemStack backItem = new ItemBuilder(Material.ARROW)
            .name("<yellow>← Voltar ao Menu Principal</yellow>")
            .build();
        inv.setItem(45, backItem);

        // Botões de navegação de página
        if (page > 0) {
            ItemStack prevItem = new ItemBuilder(Material.ARROW)
                .name("<yellow>← Página anterior</yellow>")
                .build();
            inv.setItem(46, prevItem);
        }

        ItemStack closeItem = new ItemBuilder(Material.BARRIER)
            .name("<red>Fechar</red>")
            .build();
        inv.setItem(49, closeItem);

        if (page < totalPages - 1) {
            ItemStack nextItem = new ItemBuilder(Material.ARROW)
                .name("<yellow>Próxima página →</yellow>")
                .build();
            inv.setItem(53, nextItem);
        }

        player.openInventory(inv);
    }
    
    /**
     * Processa e adiciona um item de partícula ao inventário
     */
    private void processParticleItem(Player player, String particleId, int slot, Inventory inv,
                                      Map<Integer, String> slotMap, 
                                      org.bukkit.configuration.file.FileConfiguration particlesConfig,
                                      String activeParticle) {
        boolean unlocked = dao.hasUnlocked(player.getUniqueId(), "particle", particleId);
        boolean isActive = particleId.equals(activeParticle);

        // Obter configuração do YML
        String itemConfig = particlesConfig.getString("particles." + particleId + ".item");
        String displayNameConfig = particlesConfig.getString("particles." + particleId + ".display-name");
        List<String> loreConfig = particlesConfig.getStringList("particles." + particleId + ".lore");
        
        // Construir lore
        List<String> lore = new ArrayList<>();
        if (!loreConfig.isEmpty()) {
            lore.addAll(loreConfig);
        }
        lore.add("");
        if (unlocked) {
            if (isActive) {
                lore.add("<green><bold>✅ ATIVA</bold></green>");
                lore.add("<gray>Clique para desativar</gray>");
            } else {
                lore.add("<green><bold>✅ DESBLOQUEADA</bold></green>");
                lore.add("<gray>Clique para ativar</gray>");
            }
        } else {
            lore.add("<red><bold>🔒 BLOQUEADA</bold></red>");
            lore.add("<gray>Compre na loja</gray>");
        }

        // Obter nome de exibição
        String displayName = displayNameConfig != null && !displayNameConfig.isEmpty() 
            ? displayNameConfig 
            : "<light_purple>Partícula: <white>" + particleId + "</white></light_purple>";

        // Obter item configurado
        ItemStack item = null;
        
        // Tentar obter item configurado do YML
        if (itemConfig != null && !itemConfig.isEmpty()) {
            if (itemConfig.contains(":")) {
                item = itemsAdder.getCustomItem(itemConfig);
            } else {
                Material material = Material.matchMaterial(itemConfig);
                if (material != null) {
                    item = new ItemStack(material);
                }
            }
        }
        
        // Fallbacks
        if (item == null || item.getType() == Material.BARRIER) {
            item = itemsAdder.getCustomItem("mestredosfx:particle_" + particleId);
        }
        
        if (item == null || item.getType() == Material.BARRIER) {
            String iconMaterial = particlesConfig.getString("particles." + particleId + ".icon", "NETHER_STAR");
            Material material = Material.matchMaterial(iconMaterial);
            if (material == null) {
                material = Material.NETHER_STAR;
            }
            item = new ItemStack(material);
        }

        // Aplicar nome, lore e glow (encantamento)
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(MiniMessage.miniMessage().deserialize(displayName));
            List<net.kyori.adventure.text.Component> loreComponents = new ArrayList<>();
            for (String line : lore) {
                loreComponents.add(MiniMessage.miniMessage().deserialize(line));
            }
            meta.lore(loreComponents);
            
            // Aplicar glow (encantamento) se configurado
            boolean shouldGlow = particlesConfig.getBoolean("particles." + particleId + ".glow", false);
            if (shouldGlow) {
                // Adicionar encantamento invisível para criar o efeito de brilho
                try {
                    org.bukkit.enchantments.Enchantment glowEnchant = org.bukkit.enchantments.Enchantment.getByKey(
                        org.bukkit.NamespacedKey.minecraft("protection"));
                    if (glowEnchant != null) {
                        meta.addEnchant(glowEnchant, 1, true);
                        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                    }
                } catch (Exception e) {
                    // Fallback: usar addUnsafeEnchantment se o método normal falhar
                    try {
                        org.bukkit.enchantments.Enchantment glowEnchant = org.bukkit.enchantments.Enchantment.getByKey(
                            org.bukkit.NamespacedKey.minecraft("protection"));
                        if (glowEnchant != null) {
                            item.addUnsafeEnchantment(glowEnchant, 1);
                            meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
                        }
                    } catch (Exception e2) {
                        // Se falhar, simplesmente não adiciona o glow
                    }
                }
            }
            
            item.setItemMeta(meta);
        }

        inv.setItem(slot, item);
        slotMap.put(slot, particleId);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Partículas")) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getSlot();
        int currentPage = playerPages.getOrDefault(player.getUniqueId(), 0);

        // Botão voltar ao menu principal
        if (slot == 45) {
            plugin.getMainMenu().open(player);
            return;
        }

        // Botão página anterior
        if (slot == 46) {
            open(player, currentPage - 1);
            return;
        }

        // Botão fechar
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        // Botão próxima página
        if (slot == 53) {
            open(player, currentPage + 1);
            return;
        }

        // Verificar se é um slot válido de partícula (pode ser qualquer slot agora devido a slots customizados)
        if (slot < 0 || slot >= 54 || slot == 45 || slot == 46 || slot == 49 || slot == 53) {
            return;
        }

        ItemStack clicked = inv.getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        // Obter partícula ID do slot mapeado
        String particleId = playerParticleSlots.getOrDefault(player, new HashMap<>()).get(slot);
        if (particleId == null) {
            return;
        }

        boolean unlocked = dao.hasUnlocked(player.getUniqueId(), "particle", particleId);

        if (!unlocked) {
            PlayerUtils.sendMessage(player, "<red><bold>🔒 Esta partícula está bloqueada!</bold></red>");
            PlayerUtils.sendMessage(player, "<gray>Compre na loja para desbloquear.</gray>");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Ativar/desativar partícula
        String activeParticle = particleManager.getActiveParticle(player);
        if (particleId.equals(activeParticle)) {
            particleManager.removeParticle(player);
            PlayerUtils.sendMessage(player, "<gray>Partícula desativada.</gray>");
        } else {
            particleManager.setParticle(player, particleId);
            PlayerUtils.sendMessage(player, "<green>Partícula ativada: <white>" + particleId + "</white></green>");
        }

        // Fechar menu (sem reabrir para evitar mensagens duplicadas)
        player.closeInventory();
    }

}

