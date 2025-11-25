# Mestre Dos Efeitos

Plugin standalone de partículas e glows para Minecraft (Paper/Spigot).

## Funcionalidades

- **Sistema de Partículas**: Mais de 70 tipos de partículas disponíveis com efeito Helix
- **Sistema de Glows**: 16 cores de glow desbloqueáveis por níveis
- **Menus Interativos**: Interface gráfica para seleção de efeitos
- **Persistência**: Efeitos salvos entre relogins

## Comandos

- `/efeitos` - Abre o menu principal
- `/efeitos particulas` - Abre o menu de partículas
- `/efeitos glow` - Abre o menu de glows
- `/efeitos glow disable` - Desativa o glow atual
- `/efeitos reload` - Recarrega as configurações (requer permissão)

## Permissões

- `mestredosefeitos.usar` - Permissão para usar o comando principal (padrão: true)
- `mestredosefeitos.particulas` - Permissão para usar partículas (padrão: true)
- `mestredosefeitos.glow` - Permissão para usar glow (padrão: true)
- `mestredosefeitos.reload` - Permissão para recarregar (padrão: op)
- `mestredosefeitos.*` - Todas as permissões

## Configuração

### particles.yml
- `give-particle-item-on-join`: Define se o item de partículas é dado automaticamente no join
- `particle-item-slot`: Slot do inventário onde o item será colocado
- `menu-title`: Título do menu de partículas
- `messages.*`: Mensagens do sistema de partículas

### glows.yml
- `settings.unlock-step`: Intervalo de níveis para desbloquear novas cores (padrão: 100)
- `settings.menu-title`: Título do menu de glows
- `colors.*`: Configuração de cada cor de glow
  - `display-name`: Nome exibido (suporta MiniMessage)
  - `material`: Material do ícone
  - `team-color`: Cor do ChatColor para o glow
  - `required-reinc-level`: Nível necessário para desbloquear
  - `description`: Lista de descrições (suporta MiniMessage)

## API

### Partículas

```java
MestreDosEfeitos plugin = MestreDosEfeitos.getInstance();
ParticlesManager particlesManager = plugin.getParticlesManager();

// Abrir menu
particlesManager.openMenu(player);

// Aplicar efeito
particlesManager.applyEffect(player, ParticleEffectType.HELIX);

// Remover efeito
particlesManager.removeEffect(player);
```

### Glows

```java
MestreDosEfeitos plugin = MestreDosEfeitos.getInstance();
GlowManager glowManager = plugin.getGlowManager();

// Abrir menu
glowManager.openMenu(player);

// Aplicar glow
GlowColor color = glowManager.getColorById("dourado");
glowManager.applyGlow(player, color, true);

// Desativar glow
glowManager.disableGlow(player, true);

// Verificar se está desbloqueado
boolean unlocked = glowManager.isUnlocked(player, color);

// Definir nível máximo (para desbloquear cores)
glowManager.setMaxLevel(player, 500);

// Desbloquear todas as cores
glowManager.unlockAllColors(player, true);
```

## Compilação

```bash
mvn clean package
```

O arquivo JAR será gerado em `target/MestreDosEfeitos-1.0.0.jar`

## Notas

- Este plugin foi extraído do ItemStatsTracker para uso standalone
- Requer Paper/Spigot 1.21.1 ou superior
- Suporta MiniMessage para formatação de texto avançada
- Os glows são baseados no sistema de Scoreboard Teams do Bukkit

