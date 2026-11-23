# SafeTool

A lightweight, **100% client-side** Fabric mod for Minecraft Java Edition (`26.3`) that prevents accidental breakage of tools, weapons, and Elytra, paired with a clean numeric durability HUD.

## Features

- **Accidental Breakage Prevention:** Blocks block-breaking, entity attacks, and item/block usage when your active hand item's remaining uses reach or fall below the configured critical threshold (default: `5`).
- **Elytra Boost Protection:** Blocks using a Firework Rocket if your equipped Elytra is at or below the critical durability threshold.
- **Audio & Action Bar Alerts:** Displays a clear Action Bar warning and plays a rate-limited client-side warning sound (500 ms cooldown) whenever an action is blocked.
- **Numeric Durability HUD:** Displays remaining uses next to item icons for your main hand, offhand, and equipped damageable armor pieces with dynamic color coding:
  - **Green:** `> 30%` durability
  - **Yellow:** `15% – 30%` durability
  - **Red:** `<= 15%` or at/below the critical threshold
- **Zero Server Requirements:** Works on any vanilla or modded multiplayer server (`"environment": "client"`).
- **Native Configuration Screen:** Accessible via [Mod Menu](https://modrinth.com/mod/modmenu) (optional) with zero external UI library dependencies, persisted in `config/safetool.json`.

## Controls

| Key | Action |
| --- | --- |
| `K` | Toggle SafeTool protection on/off (customizable in Controls under **SafeTool**) |
| `Shift` (hold) | Temporarily override protection to intentionally use a critical tool or boost with a critical Elytra |

## Requirements

- Minecraft `26.3`
- Fabric Loader `>= 0.20.1`
- [Fabric API](https://modrinth.com/mod/fabric-api)
- [Mod Menu](https://modrinth.com/mod/modmenu) *(optional, for the in-game config screen)*

## Building from Source

```bash
./gradlew build
```

The compiled JAR is generated in `build/libs/`.

## License

This project is licensed under the [MIT License](LICENSE).
