UNSC Core Continued 2.0.0
=========================

A continued version of AppleMarineXX's United Nations Space Command faction mod
for Starsector, maintained as an independently namespaced Core line for modern
Halo-mod coexistence.

Original mod: AppleMarineXX
Continued by: Kemptastic
Starsector: 0.98a-RC8
Required dependency: LazyLib
Loader mod ID: unsc_core_continued
Faction ID: unsc_cc

INSTALLATION
------------
1. Extract the folder "UNSC Core Continued (2.0.0)" into Starsector/mods.
2. Enable LazyLib.
3. Enable UNSC Core Continued in the Starsector launcher.
4. Enable optional supported integrations as desired.

Do not use the legacy UNSC Continued 1.x mod as a substitute for Core 2.0 in a
migrated save. The legacy 1.x and Core 2.x update channels are deliberately
separate.

SAVE COMPATIBILITY / 1.3.1 MIGRATION
------------------------------------
Core Continued 2.0.0 is a breaking technical-identity release. Existing
UNSC Continued 1.3.1 saves must be converted with the separately supplied
ST-18 save migration tool before loading them with Core Continued.

Recommended migration workflow:
1. Update the old Continued install to 1.3.1.
2. Load the campaign under 1.3.1 and Save As.
3. Exit Starsector and back up the save.
4. Run the supplied 1.3.1 -> 2.0.0 migration tool against the copied save.
5. Disable legacy Continued and enable Core Continued 2.0.0.
6. Load the migrated save and save normally.

Never overwrite your only legacy save. The migration tool is designed to work
on a copied save and preserve the original. Fresh games are recommended when
you want the full new multi-Halo-mod world-generation/coexistence behavior.

COEXISTENCE
-----------
Validated supported configurations include:
- UNSC Core Continued by itself
- UNSC Core Continued + UNSC Reborn 0.6.5
- UNSC Core Continued + Halo HomeSystems 0.7.6

No all-three support claim is made because Reborn and HomeSystems retain
independent conflicts with each other that are outside Core Continued's
ownership.

Core uses its own technical namespace (unsc_cc_*) and Java package root
(ktastic7.unsc.corecontinued.*). Its Epsilon Eridani has the unique technical
identity unsc_cc_epsilon_eridani. On fresh games, Core uses deterministic
post-generation placement to avoid occupying the same hyperspace position as
supported partner systems. When UNSC Reborn is present, Core's display name is
"Epsilon Eridani (Core)" while the technical identity remains unchanged.

2.0.0 HIGHLIGHTS
----------------
- Complete Core loader/faction/content/Java/worldgen namespace break from the
  legacy 1.x line.
- 56 Core graphics/audio resources use dedicated Core paths and unsc_cc_
  basenames.
- Dedicated UNSC Core simulator category with a Core-only military roster.
- New faction flag, crest, and blueprint-package artwork.
- Green-primary / white-secondary faction presentation.
- Curated civilian logistics access for trade, expedition and Nex-style
  non-patrol fleets without making vanilla warships native UNSC Core designs.
- Sabre and Longsword fighter-wing Autofit classifications corrected so player
  Autofit and NPC fleet inflation can populate authored carrier bays.
- Core ships and fighters use the design/manufacturer label "UNSC Core" in
  production and Codex filtering.
- Existing Nexerelin colony naming, Industrial Evolution authored content,
  Commissioned Crews, Starpocalypse and other supported integrations carried
  forward under the new namespace.

OPTIONAL INTEGRATIONS
---------------------
Supported integration surfaces include Nexerelin, Industrial Evolution,
Commissioned Crews, Starpocalypse, Treasure Hunt, and Version Checker-compatible
metadata. Optional integrations are gated so their absence does not create a
hard dependency unless otherwise noted.

VERSION CHECKER
---------------
Core Continued uses its own 2.x declaration:
  unsc_core_continued.version

The frozen legacy unsc_continued.version channel remains on the 1.x line and is
not used to auto-upgrade legacy players across this save-breaking boundary.

BUG REPORTS
-----------
When reporting a reproducible problem, include the Core Continued version,
Starsector version, relevant mod list, whether Nexerelin/Industrial Evolution
are enabled, what you were doing, and starsector.log when applicable. Screenshots
are useful for visual or world-generation issues.

This is a fan-made Starsector/Halo mod and is not affiliated with or endorsed
by Fractal Softworks, Microsoft, or the relevant Halo rights holders.
