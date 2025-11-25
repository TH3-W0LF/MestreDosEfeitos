package com.drakkar.mestredosefeitos.glow;

import com.drakkar.mestredosefeitos.MestreDosEfeitos;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

class GlowMenu implements Listener {

    private static final int[] COLOR_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
    };

    private final MestreDosEfeitos plugin;
    private final GlowManager manager;
    private final NamespacedKey colorKey;
    private final String legacyTitle;
    private final ItemStack disableItem;

    GlowMenu(MestreDosEfeitos plugin, GlowManager manager, NamespacedKey colorKey) {
        this.plugin = plugin;
        this.manager = manager;
        this.colorKey = colorKey;
        FileConfiguration config = plugin.getGlowsConfig();
        String rawTitle = config.getString("settings.menu-title", "<gradient:#f7ff00:#ff00fb>Glows</gradient>");
        Component titleComponent = MiniMessage.miniMessage().deserialize(rawTitle);
        this.legacyTitle = LegacyComponentSerializer.legacySection().serialize(titleComponent);
        this.disableItem = createDisableItem();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    void unregister() {
        HandlerList.unregisterAll(this);
    }

    private ItemStack createDisableItem() {
        FileConfiguration config = plugin.getGlowsConfig();
        String materialName = config.getString("settings.disable-item.material", "RED_DYE");
        Material material = Material.matchMaterial(materialName.toUpperCase());
        if (material == null) {
            material = Material.RED_DYE;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Component name = MiniMessage.miniMessage().deserialize(config.getString("settings.disable-item.name", "<red>Remover Glow</red>"));
            meta.displayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, legacyTitle);
        List<GlowColor> colors = manager.getColors();
        for (int i = 0; i < colors.size() && i < COLOR_SLOTS.length; i++) {
            GlowColor color = colors.get(i);
            ItemStack item = new ItemStack(color.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                Component display = color.displayNameComponent();
                meta.displayName(display);
                List<Component> lore = buildLore(player, color);
                meta.lore(lore);
                meta.getPersistentDataContainer().set(colorKey, PersistentDataType.STRING, color.getId());
                item.setItemMeta(meta);
            }
            inventory.setItem(COLOR_SLOTS[i], item);
        }
        inventory.setItem(49, disableItem);
        player.openInventory(inventory);
        FileConfiguration config = plugin.getGlowsConfig();
        String raw = config.getString("messages.menu-opened", "<green>Menu de glows aberto.</green>");
        player.sendMessage(MiniMessage.miniMessage().deserialize(raw));
    }

    private List<Component> buildLore(Player player, GlowColor color) {
        List<Component> lore = new ArrayList<>();
        for (String entry : color.getDescription()) {
            lore.add(MiniMessage.miniMessage().deserialize(entry));
        }
        if (manager.isUnlocked(player, color)) {
            lore.add(MiniMessage.miniMessage().deserialize("<green>Disponível</green>"));
            if (color.getId().equalsIgnoreCase(manager.getStoredColor(player))) {
                lore.add(MiniMessage.miniMessage().deserialize("<yellow>Ativo atualmente.</yellow>"));
            }
        } else {
            lore.add(MiniMessage.miniMessage().deserialize("<red>Bloqueado</red>"));
            lore.add(MiniMessage.miniMessage().deserialize("<gray>Requer nível " + color.getRequiredLevel() + "</gray>"));
        }
        return lore;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }
        if (!event.getView().getTitle().equals(legacyTitle)) {
            return;
        }
        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        if (clicked.isSimilar(disableItem)) {
            manager.disableGlow(player, true);
            player.closeInventory();
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null) {
            return;
        }
        String colorId = meta.getPersistentDataContainer().get(colorKey, PersistentDataType.STRING);
        if (colorId == null) {
            return;
        }
        GlowColor color = manager.getColorById(colorId);
        manager.applyGlow(player, color, true);
        player.closeInventory();
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getView().getTitle().equals(legacyTitle)) {
            event.setCancelled(true);
        }
    }
}

