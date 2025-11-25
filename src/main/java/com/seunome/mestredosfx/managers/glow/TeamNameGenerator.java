package com.seunome.mestredosfx.managers.glow;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Gerador robusto de nomes de teams
 * Baseado no sistema do eGlow (Premium)
 * 
 * Características:
 * - Ofusca nome do jogador usando números
 * - Detecta e resolve conflitos automaticamente
 * - Suporta caracteres especiais (_, +, etc)
 * - Limita a 16 caracteres (limite do Minecraft)
 */
public class TeamNameGenerator {
    
    // Ordem de caracteres para ofuscação
    private static final String SORTING_ORDER = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    
    // Cache de nomes de teams gerados (para detectar conflitos)
    private final Set<String> usedTeamNames = new HashSet<>();
    
    // Limite máximo de tentativas para evitar loop infinito
    private static final int MAX_RESOLVE_ATTEMPTS = 100;
    
    /**
     * Gera um nome de team único para um jogador
     * 
     * @param player Jogador para gerar o nome
     * @param existingTeamNames Nomes de teams já existentes (para detectar conflitos)
     * @return Nome do team (máx 16 caracteres)
     */
    public String generateTeamName(Player player, Collection<String> existingTeamNames) {
        if (player == null) {
            return null;
        }
        
        // Usar nome do display name (pode ter cores, remover)
        String displayName = ChatColor.stripColor(player.getName());
        
        // CRÍTICO: Sanitizar nome - remover TODOS os caracteres especiais problemáticos
        displayName = sanitizePlayerName(displayName);
        
        // Gerar nome ofuscado
        String teamName = obfuscateTeamName(displayName);
        
        // Detectar e resolver conflitos (com limite de segurança)
        teamName = resolveConflicts(teamName, existingTeamNames);
        
        return teamName;
    }
    
    /**
     * Sanitiza o nome do jogador removendo caracteres especiais problemáticos
     * Substitui por caracteres seguros (apenas alfanuméricos)
     */
    private String sanitizePlayerName(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return "Player";
        }
        
        StringBuilder sanitized = new StringBuilder();
        
        for (char c : playerName.toCharArray()) {
            // Permitir apenas letras e números
            if (Character.isLetterOrDigit(c)) {
                sanitized.append(c);
            } else {
                // Substituir caracteres especiais por número baseado no código ASCII
                // Isso mantém determinismo: mesmo caractere sempre vira mesmo número
                int replacement = (c % 10); // 0-9
                sanitized.append(replacement);
            }
        }
        
        // Se ficou vazio após sanitização, usar fallback
        if (sanitized.length() == 0) {
            return "Player";
        }
        
        return sanitized.toString();
    }
    
    /**
     * Ofusca o nome do jogador em um número baseado na ordem de caracteres
     */
    private String obfuscateTeamName(String playerName) {
        if (playerName == null || playerName.isEmpty()) {
            return "0000"; // Fallback
        }
        
        // Limitar a 8 caracteres para manter nome dentro do limite
        if (playerName.length() > 8) {
            playerName = playerName.substring(0, 8);
        }
        
        StringBuilder obfuscated = new StringBuilder();
        
        // Converter cada caractere para número
        for (int i = 0; i < playerName.length(); i++) {
            char c = playerName.charAt(i);
            int index = SORTING_ORDER.indexOf(c);
            
            // Se caractere não está na lista, usar 0 como fallback
            if (index == -1) {
                index = 0;
            } else {
                index += 1; // +1 para evitar 0
            }
            
            // Adicionar com zero à esquerda se necessário
            if (index < 10) {
                obfuscated.append("0");
            }
            obfuscated.append(index);
        }
        
        // Garantir que não ultrapassa 16 caracteres (limite do Minecraft)
        String result = obfuscated.toString();
        if (result.length() > 15) {
            result = result.substring(0, 15);
        }
        
        return result;
    }
    
    /**
     * Detecta e resolve conflitos de nomes de teams
     * Algoritmo determinístico: tenta incrementar sufixo numérico sequencialmente
     * CRÍTICO: Sempre sai do loop após MAX_RESOLVE_ATTEMPTS tentativas
     */
    private String resolveConflicts(String teamName, Collection<String> existingTeamNames) {
        if (existingTeamNames == null || existingTeamNames.isEmpty()) {
            return teamName;
        }
        
        // Se o nome não existe, retornar imediatamente
        if (!existingTeamNames.contains(teamName)) {
            return teamName;
        }
        
        String baseName = teamName;
        int suffix = 1;
        int attempts = 0;
        
        // CRÍTICO: Limite máximo de tentativas para evitar loop infinito
        while (attempts < MAX_RESOLVE_ATTEMPTS) {
            attempts++;
            
            // Tentar adicionar sufixo numérico: baseName_1, baseName_2, etc
            String candidateName;
            
            // Calcular tamanho disponível para o sufixo
            int suffixLength = String.valueOf(suffix).length();
            int maxBaseLength = 15 - suffixLength - 1; // -1 para o underscore
            
            if (maxBaseLength < 1) {
                // Se baseName é muito longo, truncar e usar sufixo curto
                baseName = baseName.substring(0, Math.min(10, baseName.length()));
                maxBaseLength = 10;
            }
            
            // Construir nome candidato: baseName_suffix
            candidateName = baseName.substring(0, Math.min(maxBaseLength, baseName.length())) + "_" + suffix;
            
            // Garantir que não ultrapassa 15 caracteres (limite do Minecraft é 16, mas deixamos 1 de margem)
            if (candidateName.length() > 15) {
                candidateName = candidateName.substring(0, 15);
            }
            
            // Verificar se este nome está disponível
            if (!existingTeamNames.contains(candidateName)) {
                return candidateName;
            }
            
            // Incrementar sufixo para próxima tentativa
            suffix++;
            
            // Se sufixo ficou muito grande, resetar baseName e usar UUID parcial
            if (suffix > 999) {
                // Fallback determinístico: usar hash do nome original
                return generateUniqueFallback(teamName, existingTeamNames);
            }
        }
        
        // Se chegou aqui, excedeu tentativas - usar fallback único garantido
        return generateUniqueFallback(teamName, existingTeamNames);
    }
    
    /**
     * Gera um nome único garantido usando hash determinístico
     * Usado como fallback quando o algoritmo normal falha
     */
    private String generateUniqueFallback(String originalName, Collection<String> existingTeamNames) {
        // Usar hash do nome original para garantir determinismo
        int hash = originalName.hashCode();
        
        // Converter para string positiva (remover sinal negativo)
        String hashStr = String.valueOf(Math.abs(hash));
        
        // Limitar a 8 caracteres
        if (hashStr.length() > 8) {
            hashStr = hashStr.substring(0, 8);
        }
        
        // Construir nome: G_ + hash
        String fallbackName = "G_" + hashStr;
        
        // Garantir que não ultrapassa 15 caracteres
        if (fallbackName.length() > 15) {
            fallbackName = fallbackName.substring(0, 15);
        }
        
        // Se ainda houver conflito (improvável), adicionar sufixo incremental
        int suffix = 0;
        String finalName = fallbackName;
        while (existingTeamNames.contains(finalName) && suffix < 100) {
            suffix++;
            String suffixStr = String.valueOf(suffix);
            int maxLength = 15 - suffixStr.length();
            finalName = fallbackName.substring(0, Math.min(maxLength, fallbackName.length())) + suffixStr;
        }
        
        return finalName;
    }
    
    /**
     * Registra um nome de team como usado (para detecção de conflitos)
     */
    public void registerTeamName(String teamName) {
        if (teamName != null && !teamName.isEmpty()) {
            usedTeamNames.add(teamName);
        }
    }
    
    /**
     * Remove um nome de team do registro
     */
    public void unregisterTeamName(String teamName) {
        if (teamName != null) {
            usedTeamNames.remove(teamName);
        }
    }
    
    /**
     * Limpa todos os nomes registrados
     */
    public void clear() {
        usedTeamNames.clear();
    }
    
    /**
     * Obtém todos os nomes registrados
     */
    public Set<String> getUsedTeamNames() {
        return new HashSet<>(usedTeamNames);
    }
}
