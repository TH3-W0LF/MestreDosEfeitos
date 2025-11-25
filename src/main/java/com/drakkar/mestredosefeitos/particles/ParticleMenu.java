package com.drakkar.mestredosefeitos.particles;

import com.drakkar.mestredosefeitos.MestreDosEfeitos;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

class ParticleMenu implements Listener {

    private static final int INVENTORY_SIZE = 54;
    private static final int[] EFFECT_SLOTS = {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25
    };

    private final MestreDosEfeitos plugin;
    private final ParticlesManager manager;
    private final Map<Inventory, Integer> inventoryPages = new HashMap<>();

    private final ItemStack borderGlass;
    private final ItemStack nextPageItem;
    private final ItemStack prevPageItem;
    private final ItemStack removeEffectItem;

    ParticleMenu(MestreDosEfeitos plugin, ParticlesManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        borderGlass = createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE, " ");
        nextPageItem = createItem(Material.ARROW, "§aPróxima página");
        prevPageItem = createItem(Material.ARROW, "§aPágina anterior");
        removeEffectItem = createItem(Material.RED_DYE, "§cRemover efeito");
    }

    void unregister() {
        HandlerList.unregisterAll(this);
    }

    private ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    public void open(Player player) {
        open(player, 0);
    }

    public void open(Player player, int page) {
        String title = plugin.getParticlesConfig().getString("menu-title", "§6§lPartículas ✨");
        Inventory inventory = Bukkit.createInventory(null, INVENTORY_SIZE, title);

        inventoryPages.put(inventory, page);

        for (int slot = 0; slot < INVENTORY_SIZE; slot++) {
            inventory.setItem(slot, borderGlass);
        }

        inventory.setItem(49, removeEffectItem);
        if (page > 0) {
            inventory.setItem(45, prevPageItem);
        }

        ParticleEffectType[] values = ParticleEffectType.values();
        int maxPages = (int) Math.ceil((double) values.length / EFFECT_SLOTS.length);
        page = Math.min(Math.max(page, 0), Math.max(maxPages - 1, 0));
        int start = page * EFFECT_SLOTS.length;
        int end = Math.min(start + EFFECT_SLOTS.length, values.length);

        for (int index = start; index < end; index++) {
            ParticleEffectType type = values[index];
            ItemStack item = new ItemStack(type.getIcon());
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§b" + type.getDisplayName());
                meta.setLore(Arrays.asList(
                        "§7Clique para ativar",
                        "§8✨ Personalize seu visual ✨"
                ));
                item.setItemMeta(meta);
            }
            inventory.setItem(EFFECT_SLOTS[index - start], item);
        }

        if (page < maxPages - 1) {
            inventory.setItem(53, nextPageItem);
        }

        player.openInventory(inventory);
    }

    private int getPage(Inventory inventory) {
        return inventoryPages.getOrDefault(inventory, 0);
    }

    private String msg(String path, String def) {
        FileConfiguration config = plugin.getParticlesConfig();
        return config.getString("messages." + path, def);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) {
            return;
        }

        String title = plugin.getParticlesConfig().getString("menu-title", "§6§lPartículas ✨");
        if (!title.equals(event.getView().getTitle())) {
            return;
        }

        event.setCancelled(true);
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) {
            return;
        }

        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) {
            return;
        }

        String name = meta.getDisplayName();
        if (name.equals(nextPageItem.getItemMeta().getDisplayName())) {
            open(player, getPage(event.getInventory()) + 1);
            return;
        }
        if (name.equals(prevPageItem.getItemMeta().getDisplayName())) {
            open(player, getPage(event.getInventory()) - 1);
            return;
        }
        if (name.equals(removeEffectItem.getItemMeta().getDisplayName())) {
            if (manager.removeEffect(player)) {
                player.sendMessage(msg("effect-removed", "§cEfeito removido."));
            } else {
                player.sendMessage(msg("effect-none", "§eVocê não possui efeito ativo."));
            }
            player.closeInventory();
            return;
        }

        String cleanName = name.replace("§b", "");
        for (ParticleEffectType type : ParticleEffectType.values()) {
            if (type.getDisplayName().equalsIgnoreCase(cleanName)) {
                boolean applied = manager.applyEffect(player, type);
                if (applied) {
                    player.sendMessage(msg("effect-applied", "§aEfeito ativado: ") + cleanName);
                } else {
                    player.sendMessage(msg("effect-already", "§eVocê já está usando este efeito."));
                }
                player.closeInventory();
                break;
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        String title = plugin.getParticlesConfig().getString("menu-title", "§6§lPartículas ✨");
        if (title.equals(event.getView().getTitle())) {
            event.setCancelled(true);
        }
    }
}

