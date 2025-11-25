package com.seunome.mestredosfx.commands;

import com.seunome.mestredosfx.MestreDosEfeitos;
import com.seunome.mestredosfx.utils.PlayerUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EfeitosCommand implements CommandExecutor, TabCompleter {

    private final MestreDosEfeitos plugin;

    public EfeitosCommand(MestreDosEfeitos plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            net.kyori.adventure.text.Component msg = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage()
                .deserialize("<red>Este comando só pode ser executado por jogadores!</red>");
            sender.sendMessage(msg);
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            // Abrir menu principal
            plugin.getMainMenu().open(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "off":
            case "desativar":
                // Desativar todos os efeitos
                plugin.getGlowManager().removeGlow(player);
                plugin.getParticleManager().removeParticle(player);
                PlayerUtils.sendMessage(player, "<green>Todos os efeitos foram desativados!</green>");
                return true;

            case "info":
            case "status":
                // Mostrar efeitos ativos
                String activeGlow = plugin.getGlowManager().getActiveGlow(player);
                String activeParticle = plugin.getParticleManager().getActiveParticle(player);
                
                PlayerUtils.sendMessage(player, "<aqua>═══════════════════════</aqua>");
                PlayerUtils.sendMessage(player, "<gradient:aqua:blue>✦ Seus Efeitos Ativos ✦</gradient>");
                PlayerUtils.sendMessage(player, "");
                
                if (activeGlow != null && !activeGlow.isEmpty()) {
                    PlayerUtils.sendMessage(player, "<yellow>🌟 Glow: <white>" + activeGlow + "</white></yellow>");
                } else {
                    PlayerUtils.sendMessage(player, "<gray>🌟 Glow: <red>Nenhum</red></gray>");
                }
                
                if (activeParticle != null && !activeParticle.isEmpty()) {
                    PlayerUtils.sendMessage(player, "<light_purple>✨ Partícula: <white>" + activeParticle + "</white></light_purple>");
                } else {
                    PlayerUtils.sendMessage(player, "<gray>✨ Partícula: <red>Nenhuma</red></gray>");
                }
                
                PlayerUtils.sendMessage(player, "<aqua>═══════════════════════</aqua>");
                return true;

            default:
                PlayerUtils.sendMessage(player, "<red>Uso: /efeitos [off|info]</red>");
                return true;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("off", "info");
        }
        return new ArrayList<>();
    }
}

