package com.seunome.mestredosfx.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class AdminTabComplete implements TabCompleter {

    private static final List<String> SUBCOMMANDS = Arrays.asList("give", "giveitem", "remove", "unlockall", "reset", "reload");
    private static final List<String> TYPES = Arrays.asList("glow", "particle");
    
    // IDs de glows disponíveis (incluindo todas as cores e rainbow)
    private static final List<String> GLOW_IDS = Arrays.asList(
        "black", "dark_blue", "dark_green", "dark_aqua", "dark_red", "dark_purple", 
        "gold", "gray", "dark_gray", "blue", "green", "aqua", "red", "light_purple", 
        "yellow", "white", "cyan", "purple", "pink", "orange", "rainbow"
    );
    
    // IDs de partículas disponíveis (todas as da source original)
    private static final List<String> PARTICLE_IDS = Arrays.asList(
        "helix", "spell", "flame", "fire", "heart", "cloud", "smoke", "damage_indicator", "water", "lava",
        "crit", "soul", "note", "slime", "snow", "drip_lava", "spark", "dragon_breath", "end_rod", "totem",
        "portal", "sonic_boom", "falling_lava", "falling_water", "snow_shovel", "composter", "angry_villager",
        "happy_villager", "explosion", "bubble", "splash", "fishing", "large_smoke", "instant_effect", "mycelium",
        "block", "rain", "dust_plume", "sweep_attack", "sculk_soul", "spit", "squid_ink", "bubble_pop",
        "current_down", "bubble_column_up", "nautilus", "dolphin", "sneeze", "campfire_cosy_smoke",
        "campfire_signal_smoke", "flash", "landing_lava", "dripping_honey", "falling_honey", "landing_honey",
        "falling_nectar", "soul_fire_flame", "ash", "crimson_spore", "warped_spore", "dripping_obsidian_tear",
        "falling_obsidian_tear", "landing_obsidian_tear", "reverse_portal", "white_ash", "egg_crack",
        "glowstone_dust", "falling_spore_blossom", "spore_blossom_air", "magic", "rainbow"
    );

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mestredosfx.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return SUBCOMMANDS.stream()
                .filter(sub -> sub.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }

        if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give") || subCommand.equals("giveitem") || subCommand.equals("remove")) {
                return TYPES.stream()
                    .filter(type -> type.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            } else if (subCommand.equals("unlockall") || subCommand.equals("reset")) {
                return getOnlinePlayerNames(args[1]);
            }
        }

        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give") || subCommand.equals("giveitem") || subCommand.equals("remove")) {
                return getOnlinePlayerNames(args[2]);
            }
        }

        if (args.length == 4) {
            String subCommand = args[0].toLowerCase();
            if (subCommand.equals("give") || subCommand.equals("giveitem") || subCommand.equals("remove")) {
                String type = args[1].toLowerCase();
                if (type.equals("glow")) {
                    return GLOW_IDS.stream()
                        .filter(id -> id.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
                } else if (type.equals("particle")) {
                    return PARTICLE_IDS.stream()
                        .filter(id -> id.toLowerCase().startsWith(args[3].toLowerCase()))
                        .collect(Collectors.toList());
                }
            }
        }

        return new ArrayList<>();
    }

    private List<String> getOnlinePlayerNames(String input) {
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .filter(name -> name.toLowerCase().startsWith(input.toLowerCase()))
            .collect(Collectors.toList());
    }
}

