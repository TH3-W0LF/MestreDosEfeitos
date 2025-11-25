package com.seunome.mestredosfx.menus;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.hooks.ItemsAdderHook;
import com.seunome.mestredosfx.managers.GlowManager;
import com.seunome.mestredosfx.utils.ItemBuilder;
import com.seunome.mestredosfx.utils.PlayerUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public class GlowMenu implements Listener {

    private final MestreDosEfeitos plugin;
    private final GlowManager glowManager;
    private final ItemsAdderHook itemsAdder;
    private final PlayerEffectDAO dao;
    private final MiniMessage miniMessage;
    private final java.util.Map<org.bukkit.entity.Player, java.util.Map<Integer, String>> playerGlowSlots = new java.util.HashMap<>();

    public GlowMenu(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.glowManager = plugin.getGlowManager();
        this.itemsAdder = plugin.getItemsAdderHook();
        this.dao = new PlayerEffectDAO(plugin);
        this.miniMessage = MiniMessage.miniMessage();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        // Carregar título do YML
        String titleText = plugin.getConfigManager().getGlowsConfig().getString("menu-title", 
            plugin.getConfigManager().getGlowsConfig().getString("settings.default-menu-title", 
                "<gradient:yellow:gold>🌟 Glows</gradient>"));
        
        net.kyori.adventure.text.Component title = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
            .deserialize(titleText);
        Inventory inv = org.bukkit.Bukkit.createInventory(null, 54, title);

        List<String> allGlows = glowManager.getAllGlowIds();
        String activeGlow = glowManager.getActiveGlow(player);
        
        // Mapa de slot -> glow ID para este player
        java.util.Map<Integer, String> slotMap = new java.util.HashMap<>();
        playerGlowSlots.put(player, slotMap);

        // Obter configuração de glows
        FileConfiguration glowsConfig = plugin.getConfigManager().getGlowsConfig();
        String defaultIcon = glowsConfig.getString("settings.default-icon", "ENCHANTED_BOOK");
        
        // Separar glows com slots customizados e sem slots
        Map<Integer, String> customSlotGlows = new java.util.HashMap<>();
        List<String> autoSlotGlows = new ArrayList<>();
        Set<Integer> usedSlots = new HashSet<>();
        
        // Primeiro, identificar slots customizados
        for (String glowId : allGlows) {
            int customSlot = glowsConfig.getInt("glows." + glowId + ".slot", -1);
            if (customSlot >= 0 && customSlot < 54) {
                customSlotGlows.put(customSlot, glowId);
                usedSlots.add(customSlot);
            } else {
                autoSlotGlows.add(glowId);
            }
        }
        
        // Processar glows com slots customizados
        for (Map.Entry<Integer, String> entry : customSlotGlows.entrySet()) {
            int slot = entry.getKey();
            String glowId = entry.getValue();
            processGlowItem(player, glowId, slot, inv, slotMap, glowsConfig, defaultIcon, activeGlow);
        }
        
        // Processar glows sem slots customizados (slots automáticos)
        int autoSlot = 10;
        for (String glowId : autoSlotGlows) {
            // Pular slots já ocupados e bordas
            while (usedSlots.contains(autoSlot) || autoSlot >= 44 || 
                   (autoSlot + 1) % 9 == 0 || autoSlot % 9 == 0) {
                autoSlot++;
                if (autoSlot >= 44) break;
            }
            if (autoSlot >= 44) break;
            
            processGlowItem(player, glowId, autoSlot, inv, slotMap, glowsConfig, defaultIcon, activeGlow);
            usedSlots.add(autoSlot);
            
            autoSlot++;
            if ((autoSlot + 1) % 9 == 0) {
                autoSlot += 2; // Pular bordas
            }
        }

        // Botão voltar
        ItemStack backItem = new ItemBuilder(Material.ARROW)
            .name("<yellow>← Voltar ao Menu Principal</yellow>")
            .build();
        inv.setItem(45, backItem);

        // Botão fechar
        ItemStack closeItem = new ItemBuilder(Material.BARRIER)
            .name("<red>Fechar</red>")
            .build();
        inv.setItem(49, closeItem);

        player.openInventory(inv);
    }
    
    /**
     * Processa e adiciona um item de glow ao inventário
     */
    private void processGlowItem(Player player, String glowId, int slot, Inventory inv, 
                                   Map<Integer, String> slotMap, FileConfiguration glowsConfig, 
                                   String defaultIcon, String activeGlow) {
        boolean unlocked = dao.hasUnlocked(player.getUniqueId(), "glow", glowId);
        boolean isActive = glowId.equals(activeGlow);

        // Carregar display_name do YML
        String displayName = glowsConfig.getString("glows." + glowId + ".display_name");
        if (displayName == null || displayName.isEmpty()) {
            displayName = "<yellow>Glow: <white>" + glowId + "</white></yellow>";
        }

        // Carregar lore do YML
        List<String> loreFromConfig = glowsConfig.getStringList("glows." + glowId + ".lore");
        List<Component> loreComponents = new ArrayList<>();
        
        for (String loreLine : loreFromConfig) {
            loreComponents.add(miniMessage.deserialize(loreLine));
        }
        
        loreComponents.add(Component.empty());
        
        // Adicionar status do glow
        if (unlocked) {
            if (isActive) {
                loreComponents.add(miniMessage.deserialize("<green><bold>✅ ATIVO</bold></green>"));
                loreComponents.add(miniMessage.deserialize("<gray>Clique para desativar</gray>"));
            } else {
                loreComponents.add(miniMessage.deserialize("<green><bold>✅ DESBLOQUEADO</bold></green>"));
                loreComponents.add(miniMessage.deserialize("<gray>Clique para ativar</gray>"));
            }
        } else {
            loreComponents.add(miniMessage.deserialize("<red><bold>🔒 BLOQUEADO</bold></red>"));
            loreComponents.add(miniMessage.deserialize("<gray>Compre na loja</gray>"));
        }

        // Obter item configurado do YML
        String itemConfig = glowsConfig.getString("glows." + glowId + ".item");
        ItemStack item = null;
        
        // Tentar obter item configurado
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
            item = itemsAdder.getCustomItem("mestredosfx:glow_" + glowId);
        }
        
        if (item == null || item.getType() == Material.BARRIER) {
            String iconMaterial = glowsConfig.getString("glows." + glowId + ".icon", defaultIcon);
            Material material = Material.matchMaterial(iconMaterial);
            if (material == null) {
                material = Material.matchMaterial(defaultIcon);
                if (material == null) {
                    material = Material.ENCHANTED_BOOK;
                }
            }
            item = new ItemStack(material);
        }
        
        // Aplicar display name, lore e glow (encantamento)
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(miniMessage.deserialize(displayName));
            meta.lore(loreComponents);
            
            // Aplicar glow (encantamento) se configurado
            boolean shouldGlow = glowsConfig.getBoolean("glows." + glowId + ".glow", false);
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
        slotMap.put(slot, glowId);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        Inventory inv = event.getInventory();

        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Glows")) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getSlot();

        // Botão voltar ao menu principal
        if (slot == 45) {
            plugin.getMainMenu().open(player);
            return;
        }

        // Botão fechar
        if (slot == 49) {
            player.closeInventory();
            return;
        }

        // Verificar se é um slot válido de glow (pode ser qualquer slot agora devido a slots customizados)
        if (slot < 0 || slot >= 54 || slot == 45 || slot == 49) {
            return;
        }

        ItemStack clicked = inv.getItem(slot);
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        // Obter glow ID do slot mapeado
        String glowId = playerGlowSlots.getOrDefault(player, new java.util.HashMap<>()).get(slot);
        if (glowId == null) {
            return;
        }

        boolean unlocked = dao.hasUnlocked(player.getUniqueId(), "glow", glowId);

        if (!unlocked) {
            PlayerUtils.sendMessage(player, "<red><bold>🔒 Este glow está bloqueado!</bold></red>");
            PlayerUtils.sendMessage(player, "<gray>Compre na loja para desbloquear.</gray>");
            player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            return;
        }

        // Ativar/desativar glow
        String activeGlow = glowManager.getActiveGlow(player);
        if (glowId.equals(activeGlow)) {
            glowManager.removeGlow(player);
            PlayerUtils.sendMessage(player, "<gray>Glow desativado.</gray>");
        } else {
            glowManager.setGlow(player, glowId);
            PlayerUtils.sendMessage(player, "<green>Glow ativado: <white>" + glowId + "</white></green>");
        }

        // Atualizar menu sem reabrir - apenas atualizar o item clicado
        player.closeInventory();
    }

}

