# UNSC Core Continued

A continued version of AppleMarineXX's **United Nations Space Command** faction mod for [Starsector](https://fractalsoftworks.com/), maintained as an independently namespaced Core line for modern Halo-mod coexistence.

**Original mod:** AppleMarineXX  
**Continued and maintained by:** Kemptastic  
**Current release:** 2.0.0  
**Starsector target:** 0.98a-RC8  
**Loader mod ID:** `unsc_core_continued`  
**Faction ID:** `unsc_cc`

> For normal installation, download the packaged runtime ZIP from the **GitHub Releases** page. GitHub's automatically generated source archives are not the normal playable package.

## Requirements

- **Starsector 0.98a-RC8**
- **LazyLib**

Supported optional integration surfaces include Nexerelin, Industrial Evolution, Commissioned Crews, Starpocalypse, Treasure Hunt, and Version Checker-compatible metadata.

## Installation

1. Download `UNSC Core Continued (2.0.0).zip` from the Releases page.
2. Extract `UNSC Core Continued (2.0.0)` into the Starsector `mods` directory.
3. Enable LazyLib.
4. Enable **UNSC Core Continued**.
5. Enable optional supported integrations as desired.

## Important: migrating a legacy 1.3.1 save

Core Continued 2.0.0 is a deliberate technical-identity break from the legacy **UNSC Continued 1.3.1** line. Do **not** load an unmigrated 1.3.1 campaign directly under Core 2.0.0.

Use the separate release asset:

`ST-18-Phase3-B1e-Save-MigratorP1234.rar`

RAR password: `1234`

The validated migration workflow works on a copied save and preserves the original. See [docs/SAVE_MIGRATION_1.3.1_TO_2.0.0.md](docs/SAVE_MIGRATION_1.3.1_TO_2.0.0.md).

Fresh games are recommended when you want the full new world-generation/coexistence behavior.

## Coexistence

Validated pairwise configurations:

- UNSC Core Continued by itself
- UNSC Core Continued + UNSC Reborn 0.6.5
- UNSC Core Continued + Halo HomeSystems 0.7.6

An all-three support claim is **not** made because Reborn and HomeSystems retain independent conflicts outside Core Continued's ownership.

Core uses:

- loader ID `unsc_core_continued`
- faction ID `unsc_cc`
- owned content namespace `unsc_cc_*`
- blueprint tag `unsc_cc_bp`
- Java package root `ktastic7.unsc.corecontinued.*`
- Epsilon Eridani technical ID `unsc_cc_epsilon_eridani`

When supported UNSC Reborn is present, Core's system displays as **Epsilon Eridani (Core)** while retaining its own technical identity.

## 2.0.0 highlights

- Complete Core loader/faction/content/Java/worldgen namespace break from the legacy 1.x line.
- Namespaced Core graphics/audio resources.
- Dedicated **UNSC Core** simulator category with a Core-only military roster.
- Replacement faction flag, crest, and blueprint-package artwork.
- Green-primary / white-secondary faction presentation.
- Curated civilian logistics access for non-patrol fleets without making vanilla warships native Core designs.
- Corrected Sabre/Longsword Autofit classification so player Autofit and NPC fleet inflation can populate authored carrier bays.
- Core ships and fighters use **UNSC Core** as their design/manufacturer label in Codex and production UI.
- Nexerelin colony naming and optional Industrial Evolution authored content carried forward under the Core namespace.

## Repository layout

```text
data/                        Runtime data/config/content
graphics/                    Runtime graphics
sounds/                      Runtime music/sound
jars/UNSC-Core-Continued.jar Compiled runtime JAR
mod_info.json                Starsector mod metadata
unsc_core_continued.version  Core 2.x Version Checker declaration
src/                         Java source and development utilities
docs/                        Public contributor/download/migration documentation
```

See [docs/DEVELOPMENT.md](docs/DEVELOPMENT.md) for source-oriented notes and [docs/RELEASE_PROCESS.md](docs/RELEASE_PROCESS.md) for download/release-file guidance.

## Version Checker

Core Continued uses its own 2.x declaration:

`unsc_core_continued.version`

The legacy `unsc_continued.version` channel remains in the legacy repository and is not used to auto-upgrade 1.x users across the save-breaking boundary.

## Reporting bugs

When reporting a reproducible issue, include:

- Core Continued version
- Starsector version
- relevant mod list
- whether Nexerelin and/or Industrial Evolution are enabled
- what you were doing when the issue occurred
- `starsector.log` when applicable
- screenshots for visual/world-generation issues

## Credits and permissions

The original mod was created by **AppleMarineXX** and is continued by **Kemptastic** with the original author's permission.

See [CREDITS.md](CREDITS.md) and [PERMISSIONS.md](PERMISSIONS.md).

This is a fan-made Starsector/Halo mod and is not affiliated with or endorsed by Fractal Softworks, Microsoft, or the relevant Halo rights holders.
