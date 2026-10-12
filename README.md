# UNSC Core Continued

A continued version of AppleMarineXX's **United Nations Space Command** faction mod for [Starsector](https://fractalsoftworks.com/), maintained as an independently namespaced Core line for modern Halo-mod coexistence.

**Original mod:** AppleMarineXX  
**Continued and maintained by:** Kemptastic  
**Current release:** 2.0.1  
**Starsector target:** 0.98a-RC8  
**Loader mod ID:** `unsc_core_continued`  
**Faction ID:** `unsc_cc`

> For normal installation, download the packaged runtime ZIP from the **GitHub Releases** page. GitHub's automatically generated source archives are not the normal playable package.

## Requirements

- **Starsector 0.98a-RC8**
- **LazyLib**

Supported optional integration surfaces include Nexerelin, Industrial Evolution, Commissioned Crews, Starpocalypse, Treasure Hunt, and Version Checker-compatible metadata.

## Installation

1. Download `UNSC Core Continued (2.0.1).zip` from the Releases page.
2. Extract `UNSC Core Continued (2.0.1)` into the Starsector `mods` directory.
3. Enable LazyLib.
4. Enable **UNSC Core Continued**.
5. Enable optional supported integrations as desired.

## Updating from Core 2.0.0

Core 2.0.1 keeps the established Core 2.x technical namespace, so **no 2.0.0 -> 2.0.1 save migration is required**.

If an existing campaign already consumed the UNSC Blueprint Package before the new Lancer/Anlace blueprints existed, obtain/use another copy of the package to learn the newly added content.

## Important: migrating a legacy 1.3.1 save

Core Continued 2.x is a deliberate technical-identity break from the legacy **UNSC Continued 1.3.1** line. Do **not** load an unmigrated 1.3.1 campaign directly under Core Continued.

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

## 2.0.1 highlights

- Adds the **Lancer-class Heavy Escort Attack Corvette**.
- Adds the **Anlace-class Advanced Escort Frigate**.
- Adds the **5D7C2 Light MAC**, **Streak PD System**, and **Helios High Energy Laser**.
- Adds authored Autofit goals for both ships, including compact Anlace **Adv Escort** and **E-War** labels.
- Uses the cleaned final Lancer sprite while preserving its canvas/alignment geometry.
- Standardizes active Core weapon design/manufacturer presentation to **UNSC Core**.
- Carries the existing `unsc_cc_bp` blueprint-package path forward for the new hulls.
- Carries forum topic **36142** into the packaged and remote Version Checker declaration.

## Core 2.x architecture

The 2.0.0 release established the independent Core technical identity, namespaced graphics/audio resources, Core-only simulator presentation, curated civilian logistics access, corrected Sabre/Longsword carrier Autofit classifications, and pairwise Halo-mod coexistence architecture retained by 2.0.1.

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

The original mod was created by **AppleMarineXX** and is continued by **Kemptastic** with the original author's permission. **StargazerRJL (Ryan)** granted permission to integrate/use material from his UNSC Expansion; 2.0.1's Lancer/Anlace integration derives from that authorized source material.

See [CREDITS.md](CREDITS.md) and [PERMISSIONS.md](PERMISSIONS.md).

This is a fan-made Starsector/Halo mod and is not affiliated with or endorsed by Fractal Softworks, Microsoft, or the relevant Halo rights holders.
