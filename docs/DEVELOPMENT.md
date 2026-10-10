# Development and Contribution Notes

This document covers public source/contributor information for **UNSC Core Continued**.

## Repository Layout

```text
data/                        Runtime data/config/content
graphics/                    Runtime graphics
sounds/                      Runtime music/sound
jars/UNSC-Core-Continued.jar Compiled runtime JAR
mod_info.json                Starsector mod metadata
unsc_core_continued.version  Core Version Checker declaration

src/
  ktastic7/unsc/corecontinued/  Java source
  tools/                       Development utilities and migration/rename maps
  version-checker/             Version Checker reference copies
```

## Target Environment

Current target: Starsector `0.98a-RC8`.

Build/reference dependencies include the real Starsector API/runtime libraries, LazyLib, Nexerelin, and Industrial Evolution. LazyLib is required at runtime; Nexerelin and Industrial Evolution are optional runtime integrations but source compilation uses their real APIs.

Do not substitute guessed API stubs for production builds.

## Runtime Identity

Compatibility-sensitive Core identities:

- loader ID: `unsc_core_continued`
- faction ID: `unsc_cc`
- owned content prefix: `unsc_cc_`
- blueprint tag: `unsc_cc_bp`
- Java package root: `ktastic7.unsc.corecontinued.*`
- system technical ID: `unsc_cc_epsilon_eridani`

Do not rename these casually.

## Java Source

Java source is under:

`src/ktastic7/unsc/corecontinued/`

The playable mod loads:

`jars/UNSC-Core-Continued.jar`

The repository includes the compiled runtime JAR so a checked-out release tree remains close to the playable mod.

## Development Utilities

Development utilities and machine-readable change/migration maps are under `src/tools/`.

## Version Checker

Core uses:

- `unsc_core_continued.version`
- `data/config/version/version_files.csv`

Reference copies are under `src/version-checker/`.

The old `unsc_continued.version` belongs only to the frozen legacy repository/update line and must not be reintroduced here.

## Contributions and Testing

Use the actual Starsector/partner APIs, keep optional integrations isolated, avoid unrelated cleanup, and preserve compatibility-sensitive IDs unless a deliberate migration is part of the change.

At minimum test the surface you changed. Depending on scope, include startup, new campaign/worldgen, market/integration checks, refit/combat, save loading, and `starsector.log` review.
