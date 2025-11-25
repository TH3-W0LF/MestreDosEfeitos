package com.drakkar.mestredosefeitos.particles;

import com.drakkar.mestredosefeitos.MestreDosEfeitos;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

class ParticleJoinListener implements Listener {

    private final MestreDosEfeitos plugin;
    private final ParticlesManager manager;
    private final ItemStack particleItem;

    ParticleJoinListener(MestreDosEfeitos plugin, ParticlesManager manager) {
        this.plugin = plugin;
        this.manager = manager;
        this.particleItem = createConfiguredItem();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    void unregister() {
        HandlerList.unregisterAll(this);
    }

    private ItemStack createConfiguredItem() {
        FileConfiguration config = plugin.getParticlesConfig();
        String materialName = config.getString("item.material", "ENDER_CHEST");
        Material material = Material.matchMaterial(materialName.toUpperCase());
        if (material == null) {
            material = Material.ENDER_CHEST;
        }
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            String rawName = config.getString("item.name", "<gold><bold>Partículas ✨</bold></gold>");
            meta.displayName(MiniMessage.miniMessage().deserialize(rawName));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        FileConfiguration config = plugin.getParticlesConfig();

        sendMessage(player, "messages.welcome", "<gold><bold>Bem-vindo, <player>!</bold></gold>", "<player>", player.getName());
        sendMessage(player, "messages.join-instructions", "<gray>Clique no item especial para abrir o menu.</gray>");
        boolean giveItem = config.getBoolean("give-particle-item-on-join", false);
        if (giveItem) {
            giveParticleItem(player);
        } else {
            sendMessage(player, "messages.use-command-msg", "<gray>Use /efeitos particulas para abrir o menu.</gray>");
        }
    }

    private void giveParticleItem(Player player) {
        FileConfiguration config = plugin.getParticlesConfig();
        ItemStack clone = particleItem.clone();
        if (player.getInventory().containsAtLeast(clone, 1)) {
            sendMessage(player, "messages.item-already-has", "<yellow>Você já possui o item de partículas.</yellow>");
            return;
        }

        int slot = Math.max(0, Math.min(35, config.getInt("particle-item-slot", 1)));
        if (player.getInventory().getItem(slot) == null || player.getInventory().getItem(slot).getType() == Material.AIR) {
            player.getInventory().setItem(slot, clone);
        } else {
            player.getInventory().addItem(clone);
        }
        sendMessage(player, "messages.received-item", "<green>Item de partículas adicionado.</green>");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !item.isSimilar(particleItem)) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            player.playSound(player.getLocation(), Sound.BLOCK_ENDER_CHEST_OPEN, 1f, 1f);
            manager.openMenu(player);
            sendMessage(player, "messages.menu-opened", "<green>Menu aberto.</green>");
            event.setCancelled(true);
        }
    }

    private void sendMessage(Player player, String path, String fallback, String... replacements) {
        FileConfiguration config = plugin.getParticlesConfig();
        String raw = config.getString(path, fallback);
        for (int i = 0; i + 1 < replacements.length; i += 2) {
            raw = raw.replace(replacements[i], replacements[i + 1]);
        }
        player.sendMessage(MiniMessage.miniMessage().deserialize(raw));
    }
}

