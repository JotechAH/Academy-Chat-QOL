# Academy Chat QOL

<div align="center">

![Minecraft](https://img.shields.io/badge/Minecraft-1.21.1-green.svg)
![Fabric](https://img.shields.io/badge/Fabric-0.18.1-orange.svg)
![License](https://img.shields.io/badge/license-MIT-blue.svg)
![Downloads](https://img.shields.io/github/downloads/JotechAH/academy-chat-qol/total)

**A client-side Fabric mod that enhances your Minecraft chat experience with customizable formatting, player mentions, and ignore functionality.**

[Download Latest Release](https://github.com/JotechAH/academy-chat-qol/releases/latest) • [Report Bug](https://github.com/JotechAH/academy-chat-qol/issues) • [Request Feature](https://github.com/JotechAH/academy-chat-qol/issues)

</div>

---

## Disclaimer

This mod is not affiliated with or endorsed by the Cobblemon Academy 2.0 modpack team. It was created independently for personal use and shared with the community. For any issues or questions regarding this mod, please use this GitHub repository's issue tracker.

---


## Table of Contents

- [Features](#-features)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Commands](#-commands)
- [Examples](#-examples)
- [FAQ](#-faq)
- [Contributing](#-contributing)
- [License](#-license)


## ✨ Features

### Custom Chat Formatting
- **Fully customizable chat display** with hex color support
- **Rank symbols** preserved from server messages
- **Hex color syntax**: `<#HEXCOLOR>text</#HEXCOLOR>`

### Player Mentions
- **Automatic mention detection** when someone types your username (case-insensitive)
- **Visual highlight**: Mentions appear as `@YourName` with bold, underline, and custom color
- **Sound notifications**: Configurable sound, volume, and pitch
- **Smart detection**: Works with partial matches of your username

### Player Ignore System
- **Ignore specific players** to hide their messages
- **Case-sensitive matching** for precise control
- **Persistent storage**: Ignored players saved between sessions
- **Easy management**: Add/remove players via commands or config file

### Live Reload
- **Instant config updates** without restarting Minecraft
- **Preview formatting** when reloading configuration

---

## Installation

### Requirements
- **[Cobblemon Academy 2.0 modpack](https://www.curseforge.com/minecraft/modpacks/cobblemon-academy-2-0)**: As this mod was designed for this server specifically

### Steps

1. **Download the mod**
   - Get the latest version from the [Releases page](https://github.com/JotechAH/academy-chat-qol/releases/latest)

2. **Download and install the modpack via curseforge**
   - Download and install via this link [Cobblemon Academy 2.0 modpack](https://www.curseforge.com/minecraft/modpacks/cobblemon-academy-2-0)
   - Or search "Cobblemon Academy 2.0" on curseforge

3. **Add the mod**
   - Right click on the modpack in curseforge
   - Select "Open Folder"
   - Place the downloaded `.jar` file in your `mods` folder

4. **Launch Minecraft**
   - Start the modpack
   - The mod will generate configuration files on first launch

---

## Configuration

Configuration files are located in `Cobblemon Academy 2.0/config/academy-chat-qol/`

### `config.json5`

```json5
{
  /* Chat format configuration */
  "chatDisplay": "{rank} <#AAAAAA>{nickname} ></#AAAAAA> <#FFFFFF>{message}</#FFFFFF>",
  
  /* Mention settings */
  "mentionColor": "#5DADE2",
  "mentionSound": "cobblemon:evolution.notification",
  "mentionVolume": 1.0,
}
```

#### Color Format
Use hex colors with the syntax: `<#HEXCOLOR>text</#HEXCOLOR>`

**Examples:**
- `<#FF5555>Red text</#FF5555>`
- `<#55FF55>Green text</#55FF55>`
- `<#5555FF>Blue text</#5555FF>`

#### Mention Sounds
A complete list of available sounds is generated in `available_sounds.txt`.

### `ignored.json5`

```json5
{
  /* List of ignored players (case sensitive) */
  "ignoredPlayers": [
    "AnnoyingPlayer1",
    "Spammer123"
  ]
}
```

**Note**: Player names are case-sensitive. `Player123` ≠ `player123`

### `available_sounds.txt`

Automatically generated file containing all available sounds from:
- Vanilla Minecraft
- Installed mods
- Resource packs
- Data packs

Simply copy-paste any sound ID from this file into your config!

---

## Commands

All commands are client-side and don't require server permissions.

### `/acq ignore <player>`
Ignore a player's messages.

**Example:**
```
/acq ignore AnnoyingPlayer
```

### `/acq unignore <player>`
Stop ignoring a player.

**Example:**
```
/acq unignore AnnoyingPlayer
```
- **Note**: it will suggests currently ignored players (even if they are not connected)

### `/acq reload`
Reload configuration and ignored players list.

**Example:**
```
/acq reload
```
- Reloads all settings instantly

### `/acq status`
Display current configuration status.

**Example:**
```
/acq status
```

**Shows:**
- Current chat format (with color preview)
- Number of ignored players
- Clickable link to open config folder


## FAQ

### Q: Does this mod work on servers?
**A:** Yes! This is a **client-side** mod that works on any server. It doesn't require the server to have the mod installed.

### Q: Will I get banned for using this?
**A:** No. This mod is purely cosmetic and client-side. It only changes how you see chat messages, not how they're sent or received.

### Q: Can other players see my custom formatting?
**A:** No. The formatting is only visible to you. Other players see normal chat.

### Q: Does the ignore feature work on all servers?
**A:** Yes. Since it's client-side, it works everywhere. Messages from ignored players simply won't be displayed in your chat.

### Q: Can I use this with other chat mods?
**A:** It depends. Some chat mods might conflict. Test compatibility and report any issues.

### Q: The mod doesn't format messages correctly!
**A:** Make sure the server's chat format is supported. The mod detects:
- Format: `[Unicode] Username: message` (most cross-server networks)
- Format: `<Username> message` (vanilla-like)

### Q: How do I find more sounds?
**A:** Check the `available_sounds.txt` file in your config folder. It contains ALL sounds available in your game (vanilla + mods).

### Q: Mentions aren't working!
**A:** Make sure:
- The player typed your exact username (case doesn't matter)
- Sound is enabled in Minecraft settings

---

## Contributing

Contributions are welcome! Here's how you can help:

### Reporting Bugs
1. Check if the issue already exists in [Issues](https://github.com/JotechAH/academy-chat-qol/issues)
2. Create a new issue with:
   - Minecraft version
   - Fabric Loader version
   - Mod version
   - Steps to reproduce
   - Screenshots (if applicable)

### Suggesting Features
Open an issue with the `enhancement` label and describe:
- What feature you'd like
- Why it would be useful
- How it should work

---

## License

This project is licensed under the MIT License - see the [LICENSE](https://github.com/JotechAH/academy-chat-qol/LICENSE) file for details.

---

## Acknowledgments

- Thanks to the Fabric team for their template mod generator
- Thanks to the Cobblemon Academy team for their amazing work through the server and the modpack
- Inspired by various chat enhancement mods
- Built for the community, by the community

---

## Support

- **Issues**: [GitHub Issues](https://github.com/JotechAH/academy-chat-qol/issues)
- **Discussions**: [GitHub Discussions](https://github.com/JotechAH/academy-chat-qol/discussions)

---
