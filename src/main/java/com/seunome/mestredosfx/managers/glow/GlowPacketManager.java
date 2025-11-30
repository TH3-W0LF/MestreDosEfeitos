package com.seunome.mestredosfx.managers.glow;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlowPacketManager {

    private final ProtocolManager protocolManager;
    private final Set<Integer> glowingEntities = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private boolean listenerRegistered = false;
    private final Plugin plugin;

    public GlowPacketManager(Plugin plugin) {
        this.plugin = plugin;
        this.protocolManager = ProtocolLibrary.getProtocolManager();
        registerListener(plugin);
    }

    public void shutdown() {
        glowingEntities.clear();
        protocolManager.removePacketListeners(plugin);
    }

    public void clearPlayer(Player player) {
        if (player == null) return;
        glowingEntities.remove(player.getEntityId());
        try {
            updateEntityMetadata(player, player, false);
            sendTeamPacket(player, player, null, 1, true); // Remover
        } catch (Exception e) {
            // Ignora
        }
    }

    private void registerListener(Plugin plugin) {
        if (listenerRegistered) return;

        protocolManager.addPacketListener(new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Server.ENTITY_METADATA) {
            @Override
            public void onPacketSending(PacketEvent event) {
                if (event.isCancelled()) return;
                int entityId = event.getPacket().getIntegers().read(0);

                if (!glowingEntities.contains(entityId)) return;

                List<WrappedDataValue> dataValues = event.getPacket().getDataValueCollectionModifier().read(0);
                WrappedDataValue index0Value = null;

                for (WrappedDataValue value : dataValues) {
                    if (value.getIndex() == 0) {
                        index0Value = value;
                        break;
                    }
                }

                if (index0Value != null) {
                    byte currentByte = (byte) index0Value.getValue();
                    byte modifiedByte = (byte) (currentByte | 0x40); 
                    index0Value.setValue(modifiedByte);
                }
            }
        });
        listenerRegistered = true;
    }

    // --- MÉTODOS PÚBLICOS ATUALIZADOS COM O BOOLEAN ---

    /**
     * @param keepOriginalColor Se true, o nome do jogador mantém a cor original (ex: branco).
     * Se false, o nome do jogador fica da cor do Glow.
     */
    public void setGlow(Player target, Player observer, ChatColor color, boolean keepOriginalColor) {
        glowingEntities.add(target.getEntityId());
        try {
            updateEntityMetadata(target, observer, true);
            sendTeamPacket(target, observer, color, 0, keepOriginalColor); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRainbowColor(Player target, Player observer, ChatColor newColor, boolean keepOriginalColor) {
        glowingEntities.add(target.getEntityId());
        try {
            sendTeamPacket(target, observer, newColor, 2, keepOriginalColor); 
        } catch (Exception e) {
            // Silencioso
        }
    }

    public void removeGlow(Player target, Player observer) {
        glowingEntities.remove(target.getEntityId());
        try {
            updateEntityMetadata(target, observer, false);
            sendTeamPacket(target, observer, null, 1, true); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifica se o jogador tem glow ativo
     */
    public boolean hasGlow(Player player) {
        if (player == null) return false;
        return glowingEntities.contains(player.getEntityId());
    }

    /**
     * Atualiza o glow existente sem recriar o time (evita flicker)
     */
    public void refreshGlow(Player target, Player observer, ChatColor color, boolean keepOriginalColor) {
        if (!hasGlow(target)) {
            // Se não tem glow, não faz nada (deve usar setGlow primeiro)
            return;
        }
        
        try {
            // Usa Mode 2 (Update) para atualizar sem destruir o time
            sendTeamPacket(target, observer, color, 2, keepOriginalColor);
        } catch (Exception e) {
            // Silencioso para atualizações rápidas
        }
    }

    // -----------------------------------------------------

    private void updateEntityMetadata(Player target, Player observer, boolean glowing) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, target.getEntityId());

        List<WrappedDataValue> dataValues = new ArrayList<>();
        byte flags = glowing ? (byte) 0x40 : (byte) 0;
        
        dataValues.add(new WrappedDataValue(
                0,
                WrappedDataWatcher.Registry.get(Byte.class),
                flags
        ));

        packet.getDataValueCollectionModifier().write(0, dataValues);

        try {
            protocolManager.sendServerPacket(observer, packet);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao enviar metadados de glow", e);
        }
    }

    private void sendTeamPacket(Player target, Player observer, ChatColor color, int mode, boolean keepOriginalColor) {
        try {
            PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SCOREBOARD_TEAM);
            Object handle = packet.getHandle();
            String teamName = "Glow-" + target.getEntityId();

            setField(handle, String.class, teamName);
            setField(handle, int.class, mode);

            if (mode == 0 || mode == 2) {
                Object parameters = createParameters(target, color == null ? ChatColor.WHITE : color, keepOriginalColor);
                setField(handle, Optional.class, Optional.of(parameters));
            }

            if (mode == 0 || mode == 3) {
                Collection<String> players = Collections.singletonList(target.getName());
                setField(handle, Collection.class, players);
            }

            protocolManager.sendServerPacket(observer, packet);

        } catch (Exception e) {
            if (mode != 2) e.printStackTrace();
        }
    }

    private void setField(Object target, Class<?> fieldType, Object value) throws Exception {
        for (Field f : target.getClass().getDeclaredFields()) {
            if (Modifier.isStatic(f.getModifiers())) continue;

            if (f.getType().equals(fieldType)) {
                f.setAccessible(true);
                f.set(target, value);
                return; 
            }
        }
        if (fieldType == Collection.class) {
             for (Field f : target.getClass().getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;

                if (Collection.class.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);
                    f.set(target, value);
                    return;
                }
            }
        }
        throw new RuntimeException("Campo não encontrado para o tipo: " + fieldType.getSimpleName());
    }

    private Object createParameters(Player target, ChatColor color, boolean keepOriginalColor) throws Exception {
        Class<?> scoreboardClass = Class.forName("net.minecraft.world.scores.Scoreboard");
        Class<?> teamClass = Class.forName("net.minecraft.world.scores.PlayerTeam");
        Class<?> chatFormattingClass = Class.forName("net.minecraft.ChatFormatting");
        Class<?> componentClass = Class.forName("net.minecraft.network.chat.Component");

        Object scoreboard = scoreboardClass.getConstructor().newInstance();
        Object team = teamClass.getConstructor(scoreboardClass, String.class).newInstance(scoreboard, "dummy");

        // Cor
        Object nmsColor = getNMSColor(color);
        Method setColorMethod = findMethodByParameter(teamClass, chatFormattingClass);
        if (setColorMethod != null) setColorMethod.invoke(team, nmsColor);

        // Prefixos e Sufixos via PlaceholderAPI
        if (target != null) {
            String prefix = "";
            String suffix = "";

            try {
                if (org.bukkit.Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
                    prefix = PlaceholderAPI.setPlaceholders(target, "%leaftags_tag_prefix%");
                    
                    String tagSuffix = PlaceholderAPI.setPlaceholders(target, "%leaftags_tag_suffix%");
                    String customSuffix = PlaceholderAPI.setPlaceholders(target, "%leaftags_suffix_suffix%");
                    
                    StringBuilder fullSuffix = new StringBuilder();
                    if (tagSuffix != null) fullSuffix.append(tagSuffix);
                    if (customSuffix != null) fullSuffix.append(customSuffix);
                    suffix = fullSuffix.toString();
                }
            } catch (Exception ex) {
                // Falha silenciosa
            }

            // --- LÓGICA DO TOGGLE DE COR DO NOME ---
            if (keepOriginalColor) {
                // Se o player quer manter a cor original, forçamos o Reset (§f)
                if (!prefix.isEmpty() && !prefix.endsWith("§f") && !prefix.endsWith("§r")) {
                    prefix += "§f";
                }
                // Se o prefixo for vazio, adicionamos §f para garantir que a cor do time não vaze
                if (prefix.isEmpty()) {
                    prefix = "§f";
                }
            }
            // Se keepOriginalColor for false, não fazemos nada, e o nome herda a cor do Glow (Team Color)

            Object nmsPrefix = WrappedChatComponent.fromLegacyText(prefix).getHandle();
            Object nmsSuffix = WrappedChatComponent.fromLegacyText(suffix).getHandle();

            int componentFieldCount = 0;
            for (Field f : teamClass.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers())) continue;

                if (componentClass.isAssignableFrom(f.getType())) {
                    f.setAccessible(true);

                    if (componentFieldCount == 1) {
                        f.set(team, nmsPrefix);
                    } else if (componentFieldCount == 2) {
                        f.set(team, nmsSuffix);
                    }

                    componentFieldCount++;
                }
            }
        }

        Class<?> packetClass = PacketType.Play.Server.SCOREBOARD_TEAM.getPacketClass();
        Class<?> parametersClass = null;
        for (Class<?> declared : packetClass.getDeclaredClasses()) {
            if (declared.getSimpleName().equals("Parameters")) {
                parametersClass = declared;
                break;
            }
        }
        if (parametersClass == null) parametersClass = packetClass.getDeclaredClasses()[0];

        Constructor<?> constructor = parametersClass.getConstructor(teamClass);
        return constructor.newInstance(team);
    }

    private Method findMethodByParameter(Class<?> clazz, Class<?> paramType) {
        for (Method method : clazz.getMethods()) {
            if (method.getParameterCount() == 1 && method.getParameterTypes()[0].equals(paramType)) {
                return method;
            }
        }
        return null;
    }

    private Object getNMSColor(ChatColor bukkitColor) throws Exception {
        Class<?> chatFormattingClass = Class.forName("net.minecraft.ChatFormatting");
        String name = bukkitColor.name();
        if (name.equals("MAGIC")) name = "OBFUSCATED";
        try {
            Method valueOf = chatFormattingClass.getMethod("valueOf", String.class);
            return valueOf.invoke(null, name);
        } catch (Exception e) {
            Method valueOf = chatFormattingClass.getMethod("valueOf", String.class);
            return valueOf.invoke(null, "WHITE");
        }
    }
}
