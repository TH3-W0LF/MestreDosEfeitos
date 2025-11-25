package com.drakkar.mestredosefeitos;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EfeitosCommand implements CommandExecutor, TabCompleter {

    private final MestreDosEfeitos plugin;

    public EfeitosCommand(MestreDosEfeitos plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getParticlesConfig().getString("messages.only-players", "<red>Somente jogadores.</red>")));
            return true;
        }

        if (!player.hasPermission("mestredosefeitos.usar")) {
            player.sendMessage(MiniMessage.miniMessage().deserialize(
                plugin.getParticlesConfig().getString("messages.no-permission", "<red>Sem permissão.</red>")));
            return true;
        }

        if (args.length == 0) {
            openMainMenu(player);
            return true;
        }

        String subCommand = args[0].toLowerCase(Locale.ROOT);
        switch (subCommand) {
            case "particulas", "particula", "particles" -> {
                if (!player.hasPermission("mestredosefeitos.particulas")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                        plugin.getParticlesConfig().getString("messages.no-permission", "<red>Sem permissão.</red>")));
                    return true;
                }
                if (args.length > 1 && args[1].equalsIgnoreCase("reload")) {
                    if (!player.hasPermission("mestredosefeitos.reload")) {
                        player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Sem permissão para recarregar.</red>"));
                        return true;
                    }
                    plugin.loadConfigs();
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                        plugin.getParticlesConfig().getString("messages.config-reloaded", "<green>Config recarregada.</green>")));
                    return true;
                }
                if (plugin.getParticlesManager() == null) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                        plugin.getParticlesConfig().getString("messages.menu-not-loaded", "<red>Menu indisponível.</red>")));
                    return true;
                }
                plugin.getParticlesManager().openMenu(player);
                return true;
            }
            case "glow", "glows" -> {
                if (!player.hasPermission("mestredosefeitos.glow")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                        plugin.getGlowsConfig().getString("messages.not-available", "<red>Sem permissão.</red>")));
                    return true;
                }
                if (plugin.getGlowManager() == null) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Sistema de glow indisponível.</red>"));
                    return true;
                }
                if (args.length > 1 && args[1].equalsIgnoreCase("disable")) {
                    plugin.getGlowManager().disableGlow(player, true);
                } else {
                    plugin.getGlowManager().openMenu(player);
                }
                return true;
            }
            case "reload" -> {
                if (!player.hasPermission("mestredosefeitos.reload")) {
                    player.sendMessage(MiniMessage.miniMessage().deserialize("<red>Sem permissão.</red>"));
                    return true;
                }
                plugin.loadConfigs();
                player.sendMessage(MiniMessage.miniMessage().deserialize("<green>Configurações recarregadas.</green>"));
                return true;
            }
            default -> {
                openMainMenu(player);
                return true;
            }
        }
    }

    private void openMainMenu(Player player) {
        player.sendMessage(MiniMessage.miniMessage().deserialize(
            "<gold><bold>Mestre Dos Efeitos</bold></gold>\n" +
            "<yellow>/efeitos particulas</yellow> - Abre menu de partículas\n" +
            "<yellow>/efeitos glow</yellow> - Abre menu de glows"));
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Arrays.asList("particulas", "glow", "reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("particulas")) {
            return Arrays.asList("reload");
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("glow")) {
            return Arrays.asList("disable");
        }
        return new ArrayList<>();
    }
}

