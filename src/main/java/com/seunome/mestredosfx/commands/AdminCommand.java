package com.seunome.mestredosfx.commands;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.database.PlayerEffectDAO;
import com.seunome.mestredosfx.managers.GlowManager;
import com.seunome.mestredosfx.managers.ParticleManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class AdminCommand implements CommandExecutor {

    private static final MiniMessage mm = MiniMessage.miniMessage();
    
    private final MestreDosEfeitos plugin;
    private final PlayerEffectDAO dao;
    private final GlowManager glowManager;
    private final ParticleManager particleManager;

    public AdminCommand(MestreDosEfeitos plugin) {
        this.plugin = plugin;
        this.dao = new PlayerEffectDAO(plugin);
        this.glowManager = plugin.getGlowManager();
        this.particleManager = plugin.getParticleManager();
    }
    
    private void sendMessage(CommandSender sender, String message) {
        Component component = mm.deserialize(message);
        sender.sendMessage(component);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mestredosfx.admin")) {
            sendMessage(sender, "<red>Você não tem permissão para usar este comando!</red>");
            return true;
        }

        if (args.length < 1) {
            sendUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "give":
                return handleGive(sender, args);
            case "giveitem":
                return handleGiveItem(sender, args);
            case "remove":
                return handleRemove(sender, args);
            case "unlockall":
                return handleUnlockAll(sender, args);
            case "reset":
                return handleReset(sender, args);
            case "reload":
                return handleReload(sender);
            default:
                sendUsage(sender);
                return true;
        }
    }

    private boolean handleGive(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sendMessage(sender, "<red>Uso: /meffeitos give <glow|particle> <jogador> <id></red>");
            return true;
        }

        String type = args[1].toLowerCase();
        String playerName = args[2];
        String effectId = args[3];

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, "<red>Jogador não encontrado: " + playerName + "</red>");
            return true;
        }

        UUID uuid = target.getUniqueId();

        if (type.equals("glow")) {
            if (!glowManager.isValidGlowId(effectId)) {
                sendMessage(sender, "<red>Glow ID inválido: " + effectId + "</red>");
                return true;
            }

            dao.unlock(uuid, "glow", effectId);
            glowManager.setGlow(target, effectId);
            
            sendMessage(sender, "<green>Glow '<white>" + effectId + "</white>' dado para <yellow>" + playerName + "</yellow></green>");
            com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<green>Você recebeu o glow: <white>" + effectId + "</white></green>");
            
            plugin.getLogger().info(sender.getName() + " deu glow " + effectId + " para " + playerName);
            
        } else if (type.equals("particle")) {
            if (!particleManager.isValidParticleId(effectId)) {
                sendMessage(sender, "<red>Partícula ID inválido: " + effectId + "</red>");
                return true;
            }

            dao.unlock(uuid, "particle", effectId);
            particleManager.setParticle(target, effectId);
            
            sendMessage(sender, "<green>Partícula '<white>" + effectId + "</white>' dada para <yellow>" + playerName + "</yellow></green>");
            com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<green>Você recebeu a partícula: <white>" + effectId + "</white></green>");
            
            plugin.getLogger().info(sender.getName() + " deu partícula " + effectId + " para " + playerName);
            
        } else {
            sendMessage(sender, "<red>Tipo inválido. Use 'glow' ou 'particle'</red>");
            return true;
        }

        return true;
    }

    private boolean handleRemove(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sendMessage(sender, "<red>Uso: /meffeitos remove <glow|particle> <jogador> <id></red>");
            return true;
        }

        String type = args[1].toLowerCase();
        String playerName = args[2];
        String effectId = args[3];

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, "<red>Jogador não encontrado: " + playerName + "</red>");
            return true;
        }

        UUID uuid = target.getUniqueId();

        if (type.equals("glow")) {
            String activeGlow = glowManager.getActiveGlow(target);
            if (effectId.equals(activeGlow)) {
                glowManager.removeGlow(target);
            }
            dao.removeUnlock(uuid, "glow", effectId);
            
            sendMessage(sender, "<green>Glow '<white>" + effectId + "</white>' removido de <yellow>" + playerName + "</yellow></green>");
            com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<red>O glow <white>" + effectId + "</white> foi removido.</red>");
            
            plugin.getLogger().info(sender.getName() + " removeu glow " + effectId + " de " + playerName);
            
        } else if (type.equals("particle")) {
            String activeParticle = particleManager.getActiveParticle(target);
            if (effectId.equals(activeParticle)) {
                particleManager.removeParticle(target);
            }
            dao.removeUnlock(uuid, "particle", effectId);
            
            sendMessage(sender, "<green>Partícula '<white>" + effectId + "</white>' removida de <yellow>" + playerName + "</yellow></green>");
            com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<red>A partícula <white>" + effectId + "</white> foi removida.</red>");
            
            plugin.getLogger().info(sender.getName() + " removeu partícula " + effectId + " de " + playerName);
            
        } else {
            sendMessage(sender, "<red>Tipo inválido. Use 'glow' ou 'particle'</red>");
            return true;
        }

        return true;
    }

    private boolean handleUnlockAll(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Uso: /meffeitos unlockall <jogador></red>");
            return true;
        }

        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, "<red>Jogador não encontrado: " + playerName + "</red>");
            return true;
        }

        UUID uuid = target.getUniqueId();
        dao.unlockAll(uuid);
        
        sendMessage(sender, "<green>Todos os efeitos desbloqueados para <yellow>" + playerName + "</yellow></green>");
        com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<green><bold>Todos os efeitos foram desbloqueados!</bold></green>");
        
        plugin.getLogger().info(sender.getName() + " desbloqueou todos os efeitos para " + playerName);

        return true;
    }

    private boolean handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sendMessage(sender, "<red>Uso: /meffeitos reset <jogador></red>");
            return true;
        }

        String playerName = args[1];
        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, "<red>Jogador não encontrado: " + playerName + "</red>");
            return true;
        }

        UUID uuid = target.getUniqueId();
        
        // Remover efeitos ativos
        glowManager.removeGlow(target);
        particleManager.removeParticle(target);
        
        // Resetar no banco
        dao.resetPlayer(uuid);
        
        sendMessage(sender, "<green>Jogador <yellow>" + playerName + "</yellow> foi resetado completamente.</green>");
        com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<red>Todos os seus efeitos foram resetados.</red>");
        
        plugin.getLogger().info(sender.getName() + " resetou " + playerName);

        return true;
    }

    private boolean handleGiveItem(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sendMessage(sender, "<red>Uso: /meffeitos giveitem <glow|particle> <jogador> <id> [quantidade]</red>");
            return true;
        }

        String type = args[1].toLowerCase();
        String playerName = args[2];
        String effectId = args[3];
        int amount = 1;

        if (args.length >= 5) {
            try {
                amount = Integer.parseInt(args[4]);
                amount = Math.max(1, Math.min(64, amount)); // Limitar entre 1 e 64
            } catch (NumberFormatException e) {
                sendMessage(sender, "<red>Quantidade inválida. Usando 1.</red>");
                amount = 1;
            }
        }

        Player target = Bukkit.getPlayer(playerName);
        if (target == null) {
            sendMessage(sender, "<red>Jogador não encontrado: " + playerName + "</red>");
            return true;
        }

        if (type.equals("glow")) {
            if (!glowManager.isValidGlowId(effectId)) {
                sendMessage(sender, "<red>Glow ID inválido: " + effectId + "</red>");
                return true;
            }

            // Criar item físico
            ItemStack item = com.seunome.mestredosfx.utils.PhysicalItemBuilder.createGlowItem(effectId);
            item.setAmount(amount);

            // Dar item para o player
            HashMap<Integer, ItemStack> excess = target.getInventory().addItem(item);
            
            if (excess.isEmpty()) {
                sendMessage(sender, "<green>Item físico de glow '<white>" + effectId + "</white>' dado para <yellow>" + playerName + "</yellow> (x" + amount + ")</green>");
                com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<green>Você recebeu o item físico de glow: <white>" + effectId + "</white> (x" + amount + ")</green>");
            } else {
                sendMessage(sender, "<yellow>Item dado parcialmente. Inventário cheio! (x" + amount + ")</yellow>");
                com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<yellow>Seu inventário está cheio! Alguns itens foram dropados.</yellow>");
                // Dropar itens que não couberam
                for (ItemStack excessItem : excess.values()) {
                    target.getWorld().dropItemNaturally(target.getLocation(), excessItem);
                }
            }

            plugin.getLogger().info(sender.getName() + " deu item físico de glow " + effectId + " (x" + amount + ") para " + playerName);
            
        } else if (type.equals("particle")) {
            if (!particleManager.isValidParticleId(effectId)) {
                sendMessage(sender, "<red>Partícula ID inválido: " + effectId + "</red>");
                return true;
            }

            // Criar item físico
            ItemStack item = com.seunome.mestredosfx.utils.PhysicalItemBuilder.createParticleItem(effectId);
            item.setAmount(amount);

            // Dar item para o player
            HashMap<Integer, ItemStack> excess = target.getInventory().addItem(item);
            
            if (excess.isEmpty()) {
                sendMessage(sender, "<green>Item físico de partícula '<white>" + effectId + "</white>' dado para <yellow>" + playerName + "</yellow> (x" + amount + ")</green>");
                com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<green>Você recebeu o item físico de partícula: <white>" + effectId + "</white> (x" + amount + ")</green>");
            } else {
                sendMessage(sender, "<yellow>Item dado parcialmente. Inventário cheio! (x" + amount + ")</yellow>");
                com.seunome.mestredosfx.utils.PlayerUtils.sendMessage(target, "<yellow>Seu inventário está cheio! Alguns itens foram dropados.</yellow>");
                // Dropar itens que não couberam
                for (ItemStack excessItem : excess.values()) {
                    target.getWorld().dropItemNaturally(target.getLocation(), excessItem);
                }
            }

            plugin.getLogger().info(sender.getName() + " deu item físico de partícula " + effectId + " (x" + amount + ") para " + playerName);
        } else {
            sendMessage(sender, "<red>Tipo inválido. Use 'glow' ou 'particle'</red>");
            return true;
        }

        return true;
    }

    private boolean handleReload(CommandSender sender) {
        // Recarregar configurações
        plugin.getConfigManager().reloadConfigs();
        
        // Recarregar managers que dependem das configurações
        if (glowManager != null) {
            glowManager.reload();
        }
        
        sendMessage(sender, "<green>Plugin recarregado com sucesso!</green>");
        sendMessage(sender, "<gray>Configurações dos glows e partículas foram recarregadas.</gray>");
        plugin.getLogger().info(sender.getName() + " recarregou o plugin");
        return true;
    }

    private void sendUsage(CommandSender sender) {
        sendMessage(sender, "<aqua>═══════════════════════════</aqua>");
        sendMessage(sender, "<gradient:aqua:blue>Mestre Dos Efeitos - Admin</gradient>");
        sendMessage(sender, "<aqua>═══════════════════════════</aqua>");
        sendMessage(sender, "<yellow>/meffeitos give <glow|particle> <jogador> <id></yellow>");
        sendMessage(sender, "<yellow>/meffeitos giveitem <glow|particle> <jogador> <id> [quantidade]</yellow>");
        sendMessage(sender, "<yellow>/meffeitos remove <glow|particle> <jogador> <id></yellow>");
        sendMessage(sender, "<yellow>/meffeitos unlockall <jogador></yellow>");
        sendMessage(sender, "<yellow>/meffeitos reset <jogador></yellow>");
        sendMessage(sender, "<yellow>/meffeitos reload</yellow>");
        sendMessage(sender, "<aqua>═══════════════════════════</aqua>");
    }
}

