package ktastic7.unsc.corecontinued.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.RepLevel;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.StarSystemAPI;

public class UNSCWorldGen implements SectorGeneratorPlugin {
    private static final org.apache.log4j.Logger log = Global.getLogger(UNSCWorldGen.class);
    public static final String EPSILON_ERIDANI_SYSTEM_ID = "unsc_cc_epsilon_eridani";
    public static final float EPSILON_ERIDANI_PREFERRED_X = 23117f;
    public static final float EPSILON_ERIDANI_PREFERRED_Y = 10777f;

    private static final float PLACEMENT_CLEARANCE_MARGIN = 300f;
    // Virtual search clearance only; physical Core system geometry is unchanged.
    private static final float PLACEMENT_CORE_RADIUS_BONUS = 500f;
    private static final float PLACEMENT_SEARCH_STEP = 250f;
    private static final float PLACEMENT_SEARCH_MAX_RADIUS = 12000f;
    private static final int PLACEMENT_ANGLE_SAMPLES = 36;

    private static final String EPSILON_ERIDANI_DEFAULT_NAME = "Epsilon Eridani";
    private static final String EPSILON_ERIDANI_REBORN_NAME = "Epsilon Eridani (Core)";
    private static final String EPSILON_ERIDANI_STAR_ID = "unsc_cc_epsilon_eridani_star";
    private static final String REBORN_MOD_ID = "UNSC_Reborn";
    private static final String NEX_CORVUS_MODE_MEMKEY = "$nex_corvusMode";


    /**
     * Uses the Core-owned optional unique system ID as the stable lookup identity.
     * For externally migrated 1.3.1 saves, bootstrap the optional unique ID from
     * the already-migrated Core star entity if the field was not serialized by
     * the external transformer. This does not recognize or alias any legacy ID.
     */
    public static void syncEpsilonEridaniDisplayName(SectorAPI sector) {
        if (sector == null) return;

        com.fs.starfarer.api.campaign.StarSystemAPI system =
                sector.getStarSystem(EPSILON_ERIDANI_SYSTEM_ID);

        if (system == null) {
            com.fs.starfarer.api.campaign.SectorEntityToken star =
                    sector.getEntityById(EPSILON_ERIDANI_STAR_ID);
            if (star != null && star.getStarSystem() != null) {
                system = star.getStarSystem();
                system.setOptionalUniqueId(EPSILON_ERIDANI_SYSTEM_ID);
            }
        }

        if (system == null) return;

        boolean rebornEnabled =
                Global.getSettings().getModManager().isModEnabled(REBORN_MOD_ID);
        String desiredName = rebornEnabled
                ? EPSILON_ERIDANI_REBORN_NAME
                : EPSILON_ERIDANI_DEFAULT_NAME;

        if (!desiredName.equals(system.getName())) {
            system.setName(desiredName);
        }
    }



    /**
     * Phase-4A delayed-placement experiment.
     *
     * The system is authored during onNewGame(), but its final hyperspace
     * position and hyperspace-facing links are finalized from
     * onNewGameAfterProcGen(). The preferred legacy coordinate is retained when
     * clear. If another system overlaps it, Core searches deterministically for
     * the nearest clear position around that preferred coordinate.
     *
     * This is intentionally a Core-local experiment rather than a claim to
     * solve arbitrary multi-mod placement ordering. A later shared/coordinated
     * framework can replace it without changing the system's technical ID.
     */
    public static void finalizeAfterProcGen(SectorAPI sector) {
        if (sector == null) return;

        StarSystemAPI core = sector.getStarSystem(EPSILON_ERIDANI_SYSTEM_ID);
        if (core == null) return;

        resolveEpsilonEridaniPlacement(sector, core);
        UNSCStar.finalizeAfterProcGen(sector, core);
        syncEpsilonEridaniDisplayName(sector);
        logCoexistencePlacementSnapshot(sector);
    }

    private static void resolveEpsilonEridaniPlacement(SectorAPI sector, StarSystemAPI core) {
        float preferredX = EPSILON_ERIDANI_PREFERRED_X;
        float preferredY = EPSILON_ERIDANI_PREFERRED_Y;

        StarSystemAPI blocker = findBlockingSystem(sector, core, preferredX, preferredY);
        if (blocker == null) {
            core.getLocation().set(preferredX, preferredY);
            log.info("[UNSC-CORE-PLACEMENT] delayed-placement preferred-clear x="
                    + preferredX + " y=" + preferredY);
            return;
        }

        log.info("[UNSC-CORE-PLACEMENT] delayed-placement preferred-blocked by='"
                + blocker.getName()
                + "' blockerUniqueId='" + blocker.getOptionalUniqueId()
                + "' blockerX=" + blocker.getLocation().x
                + " blockerY=" + blocker.getLocation().y
                + " blockerRadius=" + blocker.getMaxRadiusInHyperspace()
                + " coreRadius=" + core.getMaxRadiusInHyperspace()
                + " virtualRadiusBonus=" + PLACEMENT_CORE_RADIUS_BONUS
                + " margin=" + PLACEMENT_CLEARANCE_MARGIN);

        // Prefer displacement outward from the Sector center, then sample
        // alternating clockwise/counter-clockwise offsets at the same radius.
        // This keeps the move small while tending away from the dense core.
        double baseAngle = Math.atan2(preferredY, preferredX);

        for (float radius = PLACEMENT_SEARCH_STEP;
                radius <= PLACEMENT_SEARCH_MAX_RADIUS;
                radius += PLACEMENT_SEARCH_STEP) {

            for (int sample = 0; sample < PLACEMENT_ANGLE_SAMPLES; sample++) {
                int offsetIndex;
                if (sample == 0) {
                    offsetIndex = 0;
                } else {
                    int n = (sample + 1) / 2;
                    offsetIndex = (sample % 2 == 1) ? n : -n;
                }

                double angle = baseAngle
                        + Math.toRadians(offsetIndex * (360f / PLACEMENT_ANGLE_SAMPLES));
                float candidateX = preferredX + (float) Math.cos(angle) * radius;
                float candidateY = preferredY + (float) Math.sin(angle) * radius;

                if (findBlockingSystem(sector, core, candidateX, candidateY) != null) {
                    continue;
                }

                core.getLocation().set(candidateX, candidateY);
                log.info("[UNSC-CORE-PLACEMENT] delayed-placement selected x="
                        + candidateX + " y=" + candidateY
                        + " displacement=" + radius
                        + " preferredX=" + preferredX
                        + " preferredY=" + preferredY
                        + " coreRadius=" + core.getMaxRadiusInHyperspace()
                        + " virtualRadiusBonus=" + PLACEMENT_CORE_RADIUS_BONUS
                        + " margin=" + PLACEMENT_CLEARANCE_MARGIN);
                return;
            }
        }

        // Fail soft: preserve the historical coordinate and emit a loud warning
        // rather than silently moving to an unbounded/random location.
        core.getLocation().set(preferredX, preferredY);
        log.warn("[UNSC-CORE-PLACEMENT] delayed-placement FAILED to find a clear "
                + "candidate within " + PLACEMENT_SEARCH_MAX_RADIUS
                + " units; retaining preferred coordinate x=" + preferredX
                + " y=" + preferredY);
    }

    private static StarSystemAPI findBlockingSystem(
            SectorAPI sector, StarSystemAPI core, float candidateX, float candidateY) {

        float coreRadius = core.getMaxRadiusInHyperspace() + PLACEMENT_CORE_RADIUS_BONUS;

        for (StarSystemAPI other : sector.getStarSystems()) {
            if (other == null || other == core) continue;

            float dx = other.getLocation().x - candidateX;
            float dy = other.getLocation().y - candidateY;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            float required = coreRadius
                    + other.getMaxRadiusInHyperspace()
                    + PLACEMENT_CLEARANCE_MARGIN;

            if (distance < required) {
                return other;
            }
        }
        return null;
    }

    /**
     * Phase-4A evidence hook. Logs the Core system and nearby authored systems
     * after game load so requested-vs-actual overlap behavior can be measured
     * without assuming whether Starsector performs collision avoidance.
     */
    public static void logCoexistencePlacementSnapshot(SectorAPI sector) {
        if (sector == null) return;
        com.fs.starfarer.api.campaign.StarSystemAPI core =
                sector.getStarSystem(EPSILON_ERIDANI_SYSTEM_ID);
        if (core == null) return;

        float cx = core.getLocation().x;
        float cy = core.getLocation().y;
        log.info("[UNSC-CORE-PLACEMENT] core name='" + core.getName()
                + "' uniqueId='" + core.getOptionalUniqueId()
                + "' x=" + cx
                + " y=" + cy
                + " maxRadius=" + core.getMaxRadiusInHyperspace());

        for (com.fs.starfarer.api.campaign.StarSystemAPI other : sector.getStarSystems()) {
            if (other == null || other == core) continue;
            float dx = other.getLocation().x - cx;
            float dy = other.getLocation().y - cy;
            float distance = (float) Math.sqrt(dx * dx + dy * dy);
            if (distance > 12000f) continue;

            log.info("[UNSC-CORE-PLACEMENT] nearby name='" + other.getName()
                    + "' uniqueId='" + other.getOptionalUniqueId()
                    + "' x=" + other.getLocation().x
                    + " y=" + other.getLocation().y
                    + " maxRadius=" + other.getMaxRadiusInHyperspace()
                    + " centerDistance=" + distance);
        }
    }

    @Override
    public void generate(SectorAPI sector) {
        // Nex Gate for scripted world generation:
        // - Without Nexerelin, always generate Epsilon Eridani.
        // - With Nexerelin, generate it only in Corvus/vanilla-sector mode.
        //
        // Nexerelin's ExerelinNewGameSetup calls SectorManager.setCorvusMode()
        // before mod onNewGame callbacks run. That setter writes the mode to the
        // sector memory key "$nex_corvusMode". Reading that key uses only the
        // Starsector API, avoiding both a hard Nex class dependency and Java
        // reflection (which Starsector's script sandbox blocks).
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        boolean generateScriptedSystem = !haveNexerelin
                || sector.getMemoryWithoutUpdate().getBoolean(NEX_CORVUS_MODE_MEMKEY);

        if (generateScriptedSystem) {
            new UNSCStar().generate(sector);
            initFactionRelationships(sector);
        }
    }

    public static void initFactionRelationships(SectorAPI sector) {
        FactionAPI UNSC = sector.getFaction("unsc_cc");

        UNSC.setRelationship("persean", RepLevel.FAVORABLE);
        UNSC.setRelationship("independent", RepLevel.FAVORABLE);
        UNSC.setRelationship("tritachyon", RepLevel.FAVORABLE);
        UNSC.setRelationship("hegemony", RepLevel.SUSPICIOUS);
        UNSC.setRelationship("luddic_church", RepLevel.SUSPICIOUS);
        UNSC.setRelationship("pirates", RepLevel.HOSTILE);
        UNSC.setRelationship("luddic_path", RepLevel.HOSTILE);
    }
}
