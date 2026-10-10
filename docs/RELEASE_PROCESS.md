# Releases and Downloads

This page explains the public release files for **UNSC Core Continued**.

## Normal Download

Use the repository's **GitHub Releases** page. The playable package for 2.0.0 is:

`UNSC Core Continued (2.0.0).zip`

The corresponding Java/development snapshot is:

`UNSC Core Continued (2.0.0) - Development Source.zip`

GitHub's automatically generated source archives are repository snapshots and are **not** the normal playable package.

## Legacy-save migration

Players migrating an existing UNSC Continued 1.3.1 campaign should also download:

`ST-18-Phase3-B1e-Save-MigratorP1234.rar`

Password: `1234`

See [SAVE_MIGRATION_1.3.1_TO_2.0.0.md](SAVE_MIGRATION_1.3.1_TO_2.0.0.md).

## Installation

1. Download the runtime ZIP.
2. Extract `UNSC Core Continued (2.0.0)` into Starsector's `mods` directory.
3. Enable LazyLib.
4. Enable UNSC Core Continued.
5. Enable optional supported integrations as desired.

## Release Versions

Git tags use a `v` prefix, such as `v2.0.0`. The in-game version omits it.

## Version Checker Boundary

Core uses `unsc_core_continued.version`, hosted in this repository.

The legacy `unsc_continued.version` channel remains permanently in the legacy `UNSC-Continued` repository and is not repointed across the save-breaking 1.x -> 2.x boundary.

## Checksums

Release notes publish SHA-256 values for runtime, Development Source, and save-migration assets. GitHub Release assets should match the already validated/archived release bytes; do not rebuild or repack solely for GitHub.
