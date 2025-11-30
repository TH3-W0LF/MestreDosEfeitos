package com.seunome.mestredosfx.listeners;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.managers.GlowManager;
import com.seunome.mestredosfx.managers.ParticleManager;
import com.seunome.mestredosfx.utils.PhysicalItemBuilder;
import com.seunome.mestredosfx.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.logging.Level;

/**
 * Listener para itens físicos (clique com botão direito)
 */
public class PhysicalItemListener implements Listener {

    private final GlowManager glowManager;
    private final ParticleManager particleManager;
    private final PlayerEffectDAO dao;

    public PhysicalItemListener(MestreDosEfeitos plugin) {
        this.glowManager = plugin.getGlowManager();
        this.particleManager = plugin.getParticleManager();
        this.dao = new PlayerEffectDAO(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = false)
    public void onPlayerInteract(PlayerInteractEvent event) {
        try {
            // Verificar se é clique direito PRIMEIRO (verificação rápida)
            if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            
            ItemStack item = event.getItem();
            
            // Verificação rápida: se não há item, sair imediatamente
            if (item == null) {
                return;
            }
            
            // VERIFICAR NBT TAGS PRIMEIRO (antes de verificar material)
            // Isso garante que cancelamos o evento antes que outros plugins processem
            if (!item.hasItemMeta()) {
                // Se não tem ItemMeta, não pode ser nosso item
                return;
            }
            
            // Verificar se é nosso item através dos NBT tags PRIMEIRO
            // Esta é a verificação mais importante - fazer antes de verificar material
            if (!PhysicalItemBuilder.isPhysicalItem(item)) {
                // Não é nosso item, deixar outros plugins processarem
                return;
            }
            
            // É nosso item! Cancelar IMEDIATAMENTE antes que outros plugins processem
            event.setCancelled(true);
            event.setUseItemInHand(org.bukkit.event.Event.Result.DENY);
            event.setUseInteractedBlock(org.bukkit.event.Event.Result.DENY);
            
            // Agora processar o item
            Player player = event.getPlayer();
            processPhysicalItem(player, item);
            
        } catch (Exception e) {
            // Log do erro mas não causa DC no jogador
            MestreDosEfeitos.getInstance().getLogger().log(Level.WARNING, 
                "Erro ao processar item físico: " + e.getMessage(), e);
        }
    }
    
    private void processPhysicalItem(Player player, ItemStack item) {
        String itemType = PhysicalItemBuilder.getItemType(item);
        
        if (itemType == null) {
            return;
        }

        // Determinar qual mão está segurando o item
        boolean isMainHand = item.equals(player.getInventory().getItemInMainHand());

        if (itemType.equals("particle")) {
            String particleId = PhysicalItemBuilder.getParticleId(item);
            if (particleId == null) {
                return;
            }

            // Verificar se está desbloqueado, se não, desbloquear
            if (!dao.hasUnlocked(player.getUniqueId(), "particle", particleId)) {
                dao.unlock(player.getUniqueId(), "particle", particleId);
                PlayerUtils.sendMessage(player, "<green>Partícula desbloqueada: <white>" + particleId + "</white></green>");
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

            // Remover 1 item do inventário
            removeItemFromInventory(player, item, isMainHand);

            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);

        } else if (itemType.equals("glow")) {
            String glowId = PhysicalItemBuilder.getGlowId(item);
            if (glowId == null) {
                return;
            }

            // Verificar se está desbloqueado, se não, desbloquear
            if (!dao.hasUnlocked(player.getUniqueId(), "glow", glowId)) {
                dao.unlock(player.getUniqueId(), "glow", glowId);
                PlayerUtils.sendMessage(player, "<green>Glow desbloqueado: <white>" + glowId + "</white></green>");
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

            // Remover 1 item do inventário
            removeItemFromInventory(player, item, isMainHand);

            player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 1.0f, 1.0f);
        }
    }
    
    private void removeItemFromInventory(Player player, ItemStack item, boolean isMainHand) {
        if (item.getAmount() > 1) {
            item.setAmount(item.getAmount() - 1);
            if (isMainHand) {
                player.getInventory().setItemInMainHand(item);
            } else {
                player.getInventory().setItemInOffHand(item);
            }
        } else {
            if (isMainHand) {
                player.getInventory().setItemInMainHand(null);
            } else {
                player.getInventory().setItemInOffHand(null);
            }
        }
    }
}
