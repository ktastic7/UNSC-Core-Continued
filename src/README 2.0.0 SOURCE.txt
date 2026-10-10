UNSC Core Continued 2.0.0 - Development Source
================================================

This source snapshot corresponds to the final-labeled Core Continued 2.0.0
promotion candidate. It is derived from the user-validated RR4 release-shaping
baseline with no gameplay/data change beyond final release identity/profiler
labeling and public documentation cleanup.

Java package root:
    ktastic7.unsc.corecontinued

Expected runtime JAR:
    jars/UNSC-Core-Continued.jar

Build target:
    Java 8 (--release 8)

Exact registered build references:
- Starsector 0.98a-RC8 starfarer.api.jar
- LazyLib 3.0.0
- Nexerelin 0.12.2c ExerelinCore.jar
- Industrial Evolution 4.1.b IndEvo.jar
- exact Starsector-bundled json/log4j/lwjgl support JARs used by the project

Do not substitute guessed API stubs for production builds.

Core technical identity:
- loader: unsc_core_continued
- faction: unsc_cc
- owned prefix: unsc_cc_
- blueprint/content tag: unsc_cc_bp
- Java root: ktastic7.unsc.corecontinued.*
- system technical ID: unsc_cc_epsilon_eridani

The source package retains development/provenance utilities under tools/,
including the migration-map copy and the RR1-RR4 bounded-change manifests.
These are development evidence and do not add runtime behavior by themselves.

The external 1.3.1 -> 2.0.0 save migrator is distributed separately. Do not
load an unmigrated Continued 1.3.1 save directly under Core 2.0.0.

Version Checker support files are preserved under version-checker/. The Core
2.x update channel is deliberately separate from the frozen legacy 1.x channel.
