# Changelog

Public release history for **UNSC Core Continued**.

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
