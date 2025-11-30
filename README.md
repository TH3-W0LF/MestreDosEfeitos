# MestreDosEfeitos

> Plugin de Efeitos Especiais para Minecraft - Desenvolvido com exclusividade para **DrakkarMc** e toda sua rede

![Java](https://img.shields.io/badge/Java-21-orange)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-green)
![License](https://img.shields.io/badge/License-Private-red)

---

## 📋 Sobre o Projeto

Plugin premium de efeitos visuais para servidores Minecraft Paper/Spigot, desenvolvido com tecnologia avançada de ProtocolLib para oferecer a melhor experiência visual possível.

### ✨ Funcionalidades Principais

- 🌈 **Sistema de Glows Coloridos**: 16+ cores disponíveis com efeitos especiais
- ✨ **Sistema de Partículas**: Mais de 70 tipos de partículas com efeito Helix
- 🎨 **Efeito Rainbow**: Glow que alterna entre múltiplas cores automaticamente
- 💾 **Persistência Completa**: Efeitos salvos entre relogins
- 🎯 **Menus Interativos**: Interface gráfica intuitiva para seleção de efeitos
- 🔄 **Sistema de Unlock**: Desbloqueio progressivo de efeitos
- 🎁 **Itens Físicos**: Sistema de itens consumíveis para desbloquear efeitos

---

## 🎮 Comandos

| Comando | Descrição | Permissão |
|---------|-----------|-----------|
| `/efeitos` | Abre o menu principal de efeitos | `mestredosfx.usar` |
| `/efeitos glow <id>` | Aplica um glow específico | `mestredosfx.glow` |
| `/efeitos glow off` | Desativa o glow atual | `mestredosfx.glow` |
| `/efeitos particulas` | Abre o menu de partículas | `mestredosfx.particulas` |
| `/meffeitos reload` | Recarrega as configurações | `mestredosfx.admin.reload` |
| `/meffeitos giveitem <tipo> <jogador> <id> [quantidade]` | Dá item físico para jogador | `mestredosfx.admin.giveitem` |

---

## 🔧 Tecnologias Utilizadas

- **Java 21**: Linguagem de programação
- **Paper/Spigot API 1.21.4**: API do Minecraft Server
- **ProtocolLib**: Manipulação avançada de pacotes de rede
- **PlaceholderAPI**: Integração com plugins de chat (LeafTags/TAB)
- **ItemsAdder**: Suporte a itens customizados
- **SQLite**: Banco de dados para persistência
- **MiniMessage**: Formatação avançada de texto

---

## ⚙️ Instalação

1. Baixe a versão mais recente do plugin
2. Coloque o arquivo `.jar` na pasta `plugins/` do seu servidor
3. Certifique-se de ter os seguintes plugins instalados:
   - **ProtocolLib** (obrigatório)
   - **PlaceholderAPI** (recomendado)
   - **ItemsAdder** (opcional, para itens customizados)
4. Reinicie o servidor
5. Configure os arquivos em `plugins/MestreDosEfeitos/`

---

## 📁 Estrutura do Projeto

```
src/main/java/com/seunome/mestredosfx/
├── commands/          # Sistema de comandos
├── database/          # Gerenciamento de banco de dados SQLite
├── hooks/             # Integrações com outros plugins
├── listeners/         # Event listeners do Bukkit
├── managers/          # Gerenciadores principais
│   ├── glow/          # Sistema de glows
│   │   ├── GlowEffect.java
│   │   ├── GlowPacketManager.java  # ProtocolLib integration
│   │   └── TeamNameGenerator.java
│   ├── GlowManager.java
│   └── ParticleManager.java
├── menus/             # Interfaces gráficas (menus)
└── utils/             # Utilitários e helpers
```

---

## 🔐 Permissões

### Permissões Principais

- `mestredosfx.*` - Todas as permissões
- `mestredosfx.usar` - Usar o comando principal (padrão: `true`)
- `mestredosfx.glow` - Usar sistema de glows (padrão: `true`)
- `mestredosfx.particulas` - Usar sistema de partículas (padrão: `true`)

### Permissões Administrativas

- `mestredosfx.admin.*` - Todas as permissões administrativas
- `mestredosfx.admin.reload` - Recarregar configurações
- `mestredosfx.admin.giveitem` - Dar itens físicos para jogadores

---

## 📖 Configuração

### glows.yml

```yaml
settings:
  rainbow:
    change-interval: 3.0  # Intervalo em segundos para mudança de cor
  menu-title: "<gradient:aqua:blue>🌟 Glows Disponíveis</gradient>"

glows:
  rainbow:
    display-name: "<gradient:red:orange:yellow:green:blue:purple>Rainbow</gradient>"
    required-level: 0
  
  red:
    display-name: "<red>Vermelho</red>"
    material: RED_WOOL
    team-color: RED
    required-level: 100
```

### particles.yml

```yaml
settings:
  menu-title: "<gradient:aqua:blue>✨ Partículas Disponíveis</gradient>"
  
particles:
  - id: "hearts"
    display-name: "<red>Coracões</red>"
    material: RED_DYE
```

---

## 🌟 Recursos Avançados

### Sistema de Glow com ProtocolLib

O plugin utiliza ProtocolLib para manipulação direta de pacotes de rede, garantindo:
- ✅ **Performance Otimizada**: Sem lag em servidores com muitos jogadores
- ✅ **Persistência Visual**: Glow não desaparece ao andar/correr/se agachar
- ✅ **Compatibilidade**: Funciona com plugins de chat (LeafTags/TAB)
- ✅ **Efeito Rainbow**: Alternância suave entre cores sem flicker

### Integração com PlaceholderAPI

- Suporte completo para placeholders do LeafTags
- Herança automática de prefixos e sufixos do servidor
- Preservação da formatação original do rank

---

## 🐛 Troubleshooting

### O glow desaparece quando o jogador anda

✅ **Solução**: Certifique-se de que o ProtocolLib está instalado e atualizado.

### Erro ao compilar

✅ **Solução**: Certifique-se de ter:
- Java 21 ou superior
- Maven instalado
- Todas as dependências no `pom.xml`

### Prefixo/sufixo não aparecem

✅ **Solução**: Instale o PlaceholderAPI e certifique-se de que o LeafTags está configurado corretamente.

---

## 📞 Contato e Suporte

### 🔗 Links

- **Site Oficial**: [drakkarmc.com.br](https://drakkarmc.com.br)
- **Discord**: [mestree.dev](https://discord.com/users/mestree.dev)

### 👨‍💻 Desenvolvedor

**Desenvolvedor JAVA MestreBR**

---

## 📝 Licença

> ⚠️ **Este projeto é propriedade exclusiva de DrakkarMc e sua rede.**
> 
> Todo o código fonte está protegido e destinado apenas para uso interno do servidor.
> 
> **É proibida a reprodução, distribuição ou uso não autorizado deste código.**

---

## 🎯 Créditos

- **Desenvolvido por**: MestreBR (Desenvolvedor JAVA)
- **Exclusivo para**: DrakkarMc e toda sua rede
- **Versão**: 1.0.0
- **Data**: 2024

---

<div align="center">

**Desenvolvido com ❤️ para DrakkarMc**

[![DrakkarMc](https://img.shields.io/badge/DrakkarMc-Official-blue)](https://drakkarmc.com.br)

</div>
