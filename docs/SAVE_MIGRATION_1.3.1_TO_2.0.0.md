# Migrating a UNSC Continued 1.3.1 Save to Core Continued 2.x

Core Continued 2.x uses new technical identities and does not include a permanent legacy-ID alias layer.

Use the GitHub Release asset:

`ST-18-Phase3-B1e-Save-MigratorP1234.rar`

Password: `1234`

SHA-256:

`2f30e7b8a9a394425604bf67bea4e626af4f298f69152da68c2cb7b74ec860ed`

## Recommended workflow

1. Make sure the old campaign is running with UNSC Continued **1.3.1**.
2. Load the campaign under 1.3.1 and use **Save As**.
3. Exit Starsector.
4. Keep the original save untouched as rollback.
5. Extract the migration package using password `1234`.
6. Run `Migrate-UNSC-Continued-Save.cmd`.
7. Use DryRun first if desired, then Apply against the copied 1.3.1 save.
8. Disable legacy UNSC Continued.
9. Enable the current UNSC Core Continued 2.x release (2.0.1 at the time of this document) and LazyLib.
10. Load the migrated save and save normally.

The validated migration process operates on a copied save rather than overwriting the original. A long-lived James Holden campaign was migrated and subsequently used successfully under Core Continued.

The migration establishes the Core 2.x namespace. No separate 2.0.0 -> 2.0.1 save migration is required.

Fresh games remain recommended for the complete fresh-world coexistence/system-placement behavior.
