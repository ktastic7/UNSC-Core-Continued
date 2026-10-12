# Changelog

Public release history for **UNSC Core Continued**.

## 2.0.1 — Lancer + Anlace Integration

### New ships

- Added **Lancer-class Heavy Escort Attack Corvette** (`unsc_cc_corvette_lancer`).
- Added **Anlace-class Advanced Escort Frigate** (`unsc_cc_frigate_anlace`).
- Added authored Lancer **Escort** and Anlace **Adv Escort** / **E-War** Autofit and fleet variants.

### New weapons

- Added **5D7C2 Light MAC** (`unsc_cc_5d7c2_light_mac`).
- Added **Streak PD System** (`unsc_cc_streak_pd_system`) plus namespaced projectile.
- Added **Helios High Energy Laser** (`unsc_cc_helios_high_energy_laser`).
- Reused validated Core MAC/Rampart/Spartan support assets where the Stargazer source matched existing Core resources instead of duplicating inherited content.

### Autofit, presentation, and art

- Exposed all three new authored variants as player Autofit goals.
- Shortened Anlace goal names to **Adv Escort** and **E-War** to avoid UI overlap.
- Replaced the imported Lancer sprite with the cleaned final sprite while preserving its 98x251 canvas/alignment bounds.
- Standardized all active Core weapon design/manufacturer rows to **UNSC Core**.

### Source-integration cleanup

- Corrected the Lancer source hull-name carryover from `Mako` to `Lancer`.
- Corrected Anlace EW technical spelling from `warefare` to `warfare` in the Core namespace.
- Corrected `Helios Hign Energy Laser` to `Helios High Energy Laser`.
- Lancer and Anlace use the existing `unsc_cc_bp` blueprint-package path; no new blueprint package item was introduced.
- No Stargazer Java/JAR code is imported by this subset.

### Version/update metadata

- Release version is **2.0.1**.
- Packaged and remote Version Checker metadata use forum topic **36142**.
- Core 2.x and legacy 1.x update channels remain separate.

### Compatibility

- Core 2.0.0 saves can continue under 2.0.1 without migration.
- Existing saves that already consumed the UNSC Blueprint Package may need to obtain/use another package copy to learn the new blueprints.
- The existing external 1.3.1 -> 2.0.0 migration tool remains the legacy-to-Core migration route because 2.0.1 does not alter the Core namespace.
- No other Stargazer expansion hulls are included in this release.

---

## 2.0.0 — First Core Continued Release

### Technical identity and coexistence

- New loader mod ID: `unsc_core_continued`.
- New faction ID: `unsc_cc`.
- Core-owned content uses the `unsc_cc_*` namespace and `unsc_cc_bp` blueprint tag.
- Project Java classes moved under `ktastic7.unsc.corecontinued.*`.
- Core uses its own Version Checker declaration, `unsc_core_continued.version`.
- Legacy UNSC Continued 1.x remains a separate frozen update channel.
- Core retains its own Epsilon Eridani interpretation under unique technical ID `unsc_cc_epsilon_eridani`.
- Pairwise coexistence validated with UNSC Reborn 0.6.5 and Halo HomeSystems 0.7.6.
- No all-three support claim is made because Reborn and HomeSystems retain independent conflicts outside Core's scope.

### Save migration

- Existing UNSC Continued 1.3.1 saves require the separately supplied ST-18 external migration tool.
- The validated migration path operates on a copy and preserves the original source save.
- Fresh games are recommended for the complete fresh-world coexistence/worldgen experience.

### Presentation and resources

- Core-owned graphics/audio resources use dedicated Core paths and lower-case `unsc_cc_*` basenames.
- Added replacement faction flag, crest, and blueprint-package artwork.
- Added green-primary / white-secondary segmented faction presentation.
- Added a dedicated **UNSC Core** simulator category with a Core-only military roster.

### Fleets, fighters, and design type

- Added curated non-priority civilian logistics access: Buffalo, Colossus, Atlas, Dram, Phaeton, Prometheus, Mercury, and Valkyrie.
- Broad vanilla blueprint tags remain removed so vanilla warships are not treated as native Core designs.
- Sabre wing receives `fighter5, fighter` Autofit classification.
- Longsword wing receives `bomber5, bomber` Autofit classification.
- Player Autofit and NPC fleet inflation can repopulate authored fighter/bomber bays correctly.
- Core ships and fighters use **UNSC Core** as their design/manufacturer label.

### Carried-forward content

- Preserves Reach, Tribute, Circumstance, Beta Gabriel, Casimir Station, Site 17, the authored wreck/recovery design, Nexerelin colony naming, and optional Industrial Evolution content/integration.

---

## Legacy 1.x history

Core Continued preserves Git history from the legacy **United Nations Space Command - Continued** repository. Version **1.3.1** was the final legacy release using loader ID `UNSC`, faction ID `unsc`, and the legacy `unsc_*` technical namespace.

For exact older behavior, consult the legacy repository/tags and preserved release packages.
