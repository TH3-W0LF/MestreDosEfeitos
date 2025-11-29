package com.seunome.mestredosfx.utils;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.hooks.ItemsAdderHook;
import dev.lone.itemsadder.api.CustomStack;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

/**
 * Utilitário para criar itens físicos (PAPER com custom model do ItemsAdder)
 */
public class PhysicalItemBuilder {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    
    // CustomModelData único para identificar nossos itens físicos
    // Usado para evitar conflitos com outros plugins (como CustomEnderChest)
    private static final int PHYSICAL_ITEM_CUSTOM_MODEL_DATA = 999999;
    
    private static NamespacedKey getParticleIdKey() {
        return new NamespacedKey(MestreDosEfeitos.getInstance(), "particle_id");
    }
    
    private static NamespacedKey getGlowIdKey() {
        return new NamespacedKey(MestreDosEfeitos.getInstance(), "glow_id");
    }
    
    private static NamespacedKey getItemTypeKey() {
        return new NamespacedKey(MestreDosEfeitos.getInstance(), "item_type");
    }

    /**
     * Obtém um item configurado do YML ou fallback padrão
     * Suporta material do Minecraft (ex: "DIAMOND") ou custom item do ItemsAdder (ex: "itemsadder:meu_item")
     */
    private static ItemStack getConfiguredItem(String itemConfig, String defaultCustomItemId, Material defaultMaterial) {
        MestreDosEfeitos plugin = MestreDosEfeitos.getInstance();
        ItemsAdderHook itemsAdder = plugin.getItemsAdderHook();
        
        if (itemConfig == null || itemConfig.isEmpty()) {
            // Tentar usar custom item padrão
            ItemStack customItem = itemsAdder.getCustomItem(defaultCustomItemId);
            if (customItem != null && customItem.getType() != Material.BARRIER) {
                return customItem;
            }
            // Fallback para material padrão
            return new ItemStack(defaultMaterial);
        }
        
        // Verificar se é um custom item do ItemsAdder (contém ':')
        if (itemConfig.contains(":")) {
            ItemStack customItem = itemsAdder.getCustomItem(itemConfig);
            if (customItem != null && customItem.getType() != Material.BARRIER) {
                return customItem;
            }
            plugin.getLogger().warning("Custom item do ItemsAdder não encontrado: " + itemConfig + " - Usando material padrão");
            return new ItemStack(defaultMaterial);
        }
        
        // Tentar como material do Minecraft
        Material material = Material.matchMaterial(itemConfig);
        if (material != null) {
            return new ItemStack(material);
        }
        
        plugin.getLogger().warning("Material do Minecraft não encontrado: " + itemConfig + " - Usando material padrão");
        return new ItemStack(defaultMaterial);
    }

    /**
     * Cria um item físico de partícula
     * Usa configuração do YML para determinar o item, ou fallback padrão
     */
    public static ItemStack createParticleItem(String particleId) {
        MestreDosEfeitos plugin = MestreDosEfeitos.getInstance();
        FileConfiguration particlesConfig = plugin.getConfigManager().getParticlesConfig();

        // Obter item configurado do YML
        String itemConfig = particlesConfig.getString("particles." + particleId + ".item");
        String defaultCustomItemId = "mestredosfx:particle_" + particleId;
        ItemStack item = getConfiguredItem(itemConfig, defaultCustomItemId, Material.PAPER);

        // Se for BARRIER (custom item não encontrado), usar PAPER como fallback
        if (item.getType() == Material.BARRIER) {
            item = new ItemStack(Material.PAPER);
        }

        // Adicionar metadados ao item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Nome do item
            String displayName = capitalizeFirst(particleId.replace("_", " "));
            Component name = mm.deserialize("<light_purple>✨ Partícula: <white>" + displayName + "</white></light_purple>");
            meta.displayName(name);

            // Lore
            java.util.List<Component> lore = new java.util.ArrayList<>();
            lore.add(mm.deserialize("<gray>Item físico de partícula</gray>"));
            lore.add(mm.deserialize("<gray>ID: <white>" + particleId + "</white></gray>"));
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<yellow>Clique com botão direito</yellow>"));
            lore.add(mm.deserialize("<yellow>para usar esta partícula!</yellow>"));
            meta.lore(lore);

            // Adicionar dados persistentes
            meta.getPersistentDataContainer().set(getParticleIdKey(), PersistentDataType.STRING, particleId);
            meta.getPersistentDataContainer().set(getItemTypeKey(), PersistentDataType.STRING, "particle");

            // Adicionar CustomModelData para evitar conflitos com CustomEnderChest
            // CustomEnderChest ignora itens com CustomModelData
            if (!meta.hasCustomModelData()) {
                meta.setCustomModelData(PHYSICAL_ITEM_CUSTOM_MODEL_DATA);
            }

            // Se for um custom item do ItemsAdder, tentar copiar custom model data
            if (itemConfig != null && itemConfig.contains(":")) {
                try {
                    CustomStack customStack = CustomStack.getInstance(itemConfig);
                    if (customStack != null) {
                        ItemStack customItemStack = customStack.getItemStack();
                        if (customItemStack != null && customItemStack.hasItemMeta()) {
                            ItemMeta customMeta = customItemStack.getItemMeta();
                            if (customMeta != null && customMeta.hasCustomModelData()) {
                                meta.setCustomModelData(customMeta.getCustomModelData());
                            }
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().fine("Custom model não encontrado para: " + itemConfig);
                }
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Cria um item físico de glow
     * Usa configuração do YML para determinar o item, ou fallback padrão
     */
    public static ItemStack createGlowItem(String glowId) {
        MestreDosEfeitos plugin = MestreDosEfeitos.getInstance();
        FileConfiguration glowsConfig = plugin.getConfigManager().getGlowsConfig();

        // Obter item configurado do YML
        String itemConfig = glowsConfig.getString("glows." + glowId + ".item");
        String defaultCustomItemId = "mestredosfx:glow_" + glowId;
        ItemStack item = getConfiguredItem(itemConfig, defaultCustomItemId, Material.PAPER);

        // Se for BARRIER (custom item não encontrado), usar PAPER como fallback
        if (item.getType() == Material.BARRIER) {
            item = new ItemStack(Material.PAPER);
        }

        // Adicionar metadados ao item
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            // Nome do item
            String displayName = capitalizeFirst(glowId.replace("_", " "));
            Component name = mm.deserialize("<yellow>🌟 Glow: <white>" + displayName + "</white></yellow>");
            meta.displayName(name);

            // Lore
            java.util.List<Component> lore = new java.util.ArrayList<>();
            lore.add(mm.deserialize("<gray>Item físico de glow</gray>"));
            lore.add(mm.deserialize("<gray>ID: <white>" + glowId + "</white></gray>"));
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<yellow>Clique com botão direito</yellow>"));
            lore.add(mm.deserialize("<yellow>para usar este glow!</yellow>"));
            meta.lore(lore);

            // Adicionar dados persistentes
            meta.getPersistentDataContainer().set(getGlowIdKey(), PersistentDataType.STRING, glowId);
            meta.getPersistentDataContainer().set(getItemTypeKey(), PersistentDataType.STRING, "glow");

            // Adicionar CustomModelData para evitar conflitos com CustomEnderChest
            // CustomEnderChest ignora itens com CustomModelData
            if (!meta.hasCustomModelData()) {
                meta.setCustomModelData(PHYSICAL_ITEM_CUSTOM_MODEL_DATA);
            }

            // Se for um custom item do ItemsAdder, tentar copiar custom model data
            if (itemConfig != null && itemConfig.contains(":")) {
                try {
                    CustomStack customStack = CustomStack.getInstance(itemConfig);
                    if (customStack != null) {
                        ItemStack customItemStack = customStack.getItemStack();
                        if (customItemStack != null && customItemStack.hasItemMeta()) {
                            ItemMeta customMeta = customItemStack.getItemMeta();
                            if (customMeta != null && customMeta.hasCustomModelData()) {
                                meta.setCustomModelData(customMeta.getCustomModelData());
                            }
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().fine("Custom model não encontrado para: " + itemConfig);
                }
            }

            item.setItemMeta(meta);
        }

        return item;
    }

    /**
     * Cria o item físico para desbloquear a cor do nick
     */
    public static ItemStack createNickColorItem() {
        ItemStack item = new ItemStack(Material.NAME_TAG);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.displayName(mm.deserialize("<gold>🎨 Desbloqueador de Cor do Nick</gold>"));
            java.util.List<Component> lore = new java.util.ArrayList<>();
            lore.add(mm.deserialize("<gray>Use este item para liberar</gray>"));
            lore.add(mm.deserialize("<gray>a cor do seu nick nos glows.</gray>"));
            lore.add(mm.deserialize(""));
            lore.add(mm.deserialize("<yellow>Clique com botão direito</yellow>"));
            lore.add(mm.deserialize("<yellow>para ativar permanentemente!</yellow>"));
            meta.lore(lore);
            meta.getPersistentDataContainer().set(getItemTypeKey(), PersistentDataType.STRING, "nick_color_unlock");
            if (!meta.hasCustomModelData()) {
                meta.setCustomModelData(PHYSICAL_ITEM_CUSTOM_MODEL_DATA);
            }
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * Verifica se um item é um item físico do plugin
     * Verificação rápida e otimizada - verifica NBT tags primeiro
     */
    public static boolean isPhysicalItem(ItemStack item) {
        // Verificações rápidas primeiro
        if (item == null) {
            return false;
        }
        
        if (!item.hasItemMeta()) {
            return false;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return false;
        }
        
        // Verificar se tem nossa tag de tipo (verificação mais rápida)
        return meta.getPersistentDataContainer().has(getItemTypeKey(), PersistentDataType.STRING);
    }

    /**
     * Obtém o tipo do item físico (particle ou glow)
     */
    public static String getItemType(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        return meta.getPersistentDataContainer().get(getItemTypeKey(), PersistentDataType.STRING);
    }

    /**
     * Obtém o ID da partícula de um item físico
     */
    public static String getParticleId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        return meta.getPersistentDataContainer().get(getParticleIdKey(), PersistentDataType.STRING);
    }

    /**
     * Obtém o ID do glow de um item físico
     */
    public static String getGlowId(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return null;
        }
        
        return meta.getPersistentDataContainer().get(getGlowIdKey(), PersistentDataType.STRING);
    }

    private static String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        
        String[] words = str.split(" ");
        StringBuilder result = new StringBuilder();
        
        for (String word : words) {
            if (result.length() > 0) {
                result.append(" ");
            }
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) {
                    result.append(word.substring(1));
                }
            }
        }
        
        return result.toString();
    }
}

