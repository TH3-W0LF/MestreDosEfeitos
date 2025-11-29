package com.seunome.mestredosfx.menus;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.hooks.ItemsAdderHook;
import com.seunome.mestredosfx.managers.GlowManager;
import com.seunome.mestredosfx.utils.PlayerUtils;
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
    private final PlayerEffectDAO dao;
    private final GlowManager glowManager;

    public MainMenu(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.itemsAdder = plugin.getItemsAdderHook();
        this.dao = new PlayerEffectDAO(plugin);
        this.glowManager = plugin.getGlowManager();
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

        // Slot 13 - Toggle Cor do Nick
        boolean nickUnlocked = dao.hasUnlockedNickColor(player.getUniqueId());
        boolean nickEnabled = dao.isNickColorEnabled(player.getUniqueId());
        ItemStack nickItem;
        if (!nickUnlocked) {
            nickItem = new ItemBuilder(Material.NAME_TAG)
                .name("<red>❌ Cor do Nick Bloqueada</red>")
                .lore(
                    "<gray>Use o item físico</gray>",
                    "<gray>\"Desbloqueador de Cor do Nick\"</gray>",
                    "<gray>para liberar esta função.</gray>"
                )
                .build();
        } else if (nickEnabled) {
            nickItem = new ItemBuilder(Material.NAME_TAG)
                .name("<green>✅ Cor do Nick: ATIVADA</green>")
                .lore(
                    "<gray>Seu nick está seguindo</gray>",
                    "<gray>a cor do glow.</gray>",
                    "",
                    "<yellow>Clique para desativar.</yellow>"
                )
                .build();
        } else {
            nickItem = new ItemBuilder(Material.NAME_TAG)
                .name("<yellow>⚠ Cor do Nick: DESATIVADA</yellow>")
                .lore(
                    "<gray>Ative para que o nick</gray>",
                    "<gray>use a cor do glow.</gray>",
                    "",
                    "<yellow>Clique para ativar.</yellow>"
                )
                .build();
        }
        inv.setItem(13, nickItem);

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
        // Slot 13 - Toggle cor do nick
        else if (slot == 13) {
            boolean unlocked = dao.hasUnlockedNickColor(player.getUniqueId());
            if (!unlocked) {
                PlayerUtils.sendMessage(player, "<red>Você precisa do item especial para liberar a cor do nick.</red>");
                player.playSound(player.getLocation(), org.bukkit.Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
                return;
            }

            boolean enabled = dao.isNickColorEnabled(player.getUniqueId());
            boolean newState = !enabled;
            dao.setNickColorEnabled(player.getUniqueId(), newState);
            glowManager.updateNickColorPreference(player, newState);

            if (newState) {
                PlayerUtils.sendMessage(player, "<green>Cor do nick ativada! Agora segue a cor do glow.</green>");
            } else {
                PlayerUtils.sendMessage(player, "<yellow>Cor do nick desativada. Seu nick ficará branco.</yellow>");
            }
            open(player);
        }
    }
}

