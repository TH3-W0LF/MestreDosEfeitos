package com.seunome.mestredosfx.menus;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.hooks.ItemsAdderHook;
import com.seunome.mestredosfx.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MainMenu implements Listener {

    private final MestreDosEfeitos plugin;
    private final ItemsAdderHook itemsAdder;

    public MainMenu(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.itemsAdder = plugin.getItemsAdderHook();
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player) {
        net.kyori.adventure.text.Component title = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
            .deserialize("<gradient:aqua:blue>✦ Efeitos Especiais ✦</gradient>");
        org.bukkit.inventory.Inventory inv = org.bukkit.Bukkit.createInventory(null, 27, title);

        // Slot 11 - Glows
        ItemStack glowItem = itemsAdder.getCustomItem("mestredosfx:menu_glows");
        if (glowItem == null || glowItem.getType() == Material.BARRIER) {
            glowItem = new ItemBuilder(Material.ENCHANTED_BOOK)
                .name("<yellow>🌟 Glows</yellow>")
                .lore(
                    "<gray>Clique para visualizar e gerenciar</gray>",
                    "<gray>seus glows</gray>"
                )
                .build();
        }
        inv.setItem(11, glowItem);

        // Slot 15 - Partículas
        ItemStack particleItem = itemsAdder.getCustomItem("mestredosfx:menu_particles");
        if (particleItem == null || particleItem.getType() == Material.BARRIER) {
            particleItem = new ItemBuilder(Material.NETHER_STAR)
                .name("<light_purple>✨ Partículas</light_purple>")
                .lore(
                    "<gray>Clique para visualizar e gerenciar</gray>",
                    "<gray>suas partículas</gray>"
                )
                .build();
        }
        inv.setItem(15, particleItem);

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        
        // Verificar se é o menu principal
        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(event.getView().title());
        if (!title.contains("Efeitos Especiais")) {
            return;
        }

        event.setCancelled(true);

        int slot = event.getSlot();

        // Slot 11 - Glows
        if (slot == 11) {
            plugin.getGlowMenu().open(player);
        }
        // Slot 15 - Partículas
        else if (slot == 15) {
            plugin.getParticleMenu().open(player, 0);
        }
    }
}

