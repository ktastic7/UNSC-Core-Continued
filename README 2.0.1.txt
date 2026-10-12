UNSC Core Continued 2.0.1
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
Forum thread: https://fractalsoftworks.com/forum/index.php?topic=36142

INSTALLATION
------------
1. Extract the folder "UNSC Core Continued (2.0.1)" into Starsector/mods.
2. Enable LazyLib.
3. Enable UNSC Core Continued in the Starsector launcher.
4. Enable optional supported integrations as desired.

UPDATING FROM CORE 2.0.0
------------------------
2.0.1 is an additive/content-and-presentation update to the existing Core 2.x
namespace. Core 2.0.0 saves can continue under 2.0.1; no 2.0.0 -> 2.0.1 save
migration is required.

If a 2.0.0 campaign already consumed the UNSC Blueprint Package before these
new blueprints existed, the player will need to obtain/use another UNSC Blueprint
Package to learn the newly added Lancer/Anlace content. This does not affect save
loading or existing learned blueprints.

SAVE COMPATIBILITY / LEGACY 1.3.1 MIGRATION
-------------------------------------------
Core Continued 2.x uses a deliberate technical-identity break from legacy
UNSC Continued 1.3.1. Existing legacy 1.3.1 saves must first be converted with
the separately supplied ST-18 1.3.1 -> 2.0.0 migration tool. The Core 2.x
technical namespace is unchanged in 2.0.1, so the same migration output can be
loaded under 2.0.1.

Recommended legacy migration workflow:
1. Update the old Continued install to 1.3.1.
2. Load the campaign under 1.3.1 and Save As.
3. Exit Starsector and back up the save.
4. Run the supplied 1.3.1 -> 2.0.0 migration tool against the copied save.
5. Disable legacy Continued and enable Core Continued 2.0.1.
6. Load the migrated save and save normally.

Never overwrite your only legacy save. Fresh games remain recommended when you
want the full multi-Halo-mod world-generation/coexistence behavior.

2.0.1 HIGHLIGHTS
----------------
- Adds the Lancer-class Heavy Escort Attack Corvette.
- Adds the Anlace-class Advanced Escort Frigate.
- Adds the 5D7C2 Light MAC, Streak PD System, and Helios High Energy Laser.
- Adds authored Autofit goals for Lancer and Anlace; Anlace goals use compact
  "Adv Escort" and "E-War" labels for clean UI presentation.
- Uses the user-cleaned Lancer sprite while preserving its existing canvas and
  alignment geometry.
- Standardizes active Core weapon design/manufacturer presentation to
  "UNSC Core".
- Carries the existing Core blueprint-package path forward for the new hulls.
- Carries the corrected Core forum thread ID (36142) into the packaged Version
  Checker declaration as well as the remote update channel.

COEXISTENCE
-----------
Validated supported configurations from the Core 2.x architecture include:
- UNSC Core Continued by itself
- UNSC Core Continued + UNSC Reborn 0.6.5
- UNSC Core Continued + Halo HomeSystems 0.7.6

No all-three support claim is made because Reborn and HomeSystems retain
independent conflicts with each other that are outside Core Continued's
ownership.

Core uses its own technical namespace (unsc_cc_*) and Java package root
(ktastic7.unsc.corecontinued.*). Its Epsilon Eridani has technical identity
unsc_cc_epsilon_eridani. When UNSC Reborn is present, Core's display name is
"Epsilon Eridani (Core)" while the technical identity remains unchanged.

OPTIONAL INTEGRATIONS
---------------------
Supported integration surfaces include Nexerelin, Industrial Evolution,
Commissioned Crews, Starpocalypse, Treasure Hunt, and Version Checker-compatible
metadata. Optional integrations remain gated so their absence does not create a
hard dependency unless otherwise noted.

VERSION CHECKER
---------------
Core Continued uses its own 2.x declaration:
  unsc_core_continued.version

The frozen legacy unsc_continued.version channel remains on the 1.x line and is
not used to auto-upgrade legacy players across the save-breaking 1.x -> 2.x
boundary.

BUG REPORTS
-----------
When reporting a reproducible problem, include the Core Continued version,
Starsector version, relevant mod list, whether Nexerelin/Industrial Evolution
are enabled, what you were doing, and starsector.log when applicable. Screenshots
are useful for visual or world-generation issues.

This is a fan-made Starsector/Halo mod and is not affiliated with or endorsed
by Fractal Softworks, Microsoft, or the relevant Halo rights holders.
