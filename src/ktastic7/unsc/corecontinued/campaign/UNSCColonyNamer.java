package ktastic7.unsc.corecontinued.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.procgen.ProcgenUsedNames;
import exerelin.campaign.intel.colony.ColonyExpeditionIntel;
import exerelin.campaign.intel.colony.ColonyExpeditionIntel.ColonyOutcome;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Gives successful Nexerelin UNSC colony expeditions faction-specific names.
 *
 * 1.01h replaces the old recurring whole-sector scan with a small Nex expedition
 * watcher. Every 15 seconds it asks Nex's normal public intel API for colony
 * expeditions and only tracks UNSC targets. A full sector name scan happens only
 * when an eligible UNSC expedition actually reports ColonyOutcome.SUCCESS.
 *
 * There is deliberately no Java reflection in this implementation.
 *
 * Before colonization, the watcher remembers whether a target was uncolonized and
 * still had Nex's generic system-style name. Properly named worlds are never
 * renamed. On success, occupied names are collected from both PlanetAPI.getName()
 * and each planet market's MarketAPI.getName(), normalized into one HashSet. The
 * first unused configured UNSC name is assigned deterministically and registered
 * with ProcgenUsedNames as used.
 */
public class UNSCColonyNamer implements EveryFrameScript {

    public static final String FACTION_ID = "unsc_cc";
    public static final String NEX_COLONY_KEY = "$nex_npcColony";
    public static final String PROCESSED_KEY = "$unsc_cc_colony_name_processed";
    public static final String CONFIG_PATH = "data/config/unsc_cc_colony_names.json";
    public static final String MOD_ID = "unsc_core_continued";

    private static final String TRACKING_DATA_KEY = "unsc_cc_colony_namer_tracked_expeditions_v1";
    private static final String NEXT_SEQUENCE_KEY = "unsc_cc_colony_namer_next_sequence_v1";

    private static final Logger log = Global.getLogger(UNSCColonyNamer.class);
    private static final float CHECK_INTERVAL = 15f;
    private static final int PROFILE_REPORT_EVERY_POLLS = 20; // ~5 minutes unpaused
    private static final String PROFILE_PREFIX = "[UNSC-CORE-PROFILE][ColonyNamer]";
    private static final String PROFILE_MOD_VERSION = "2.0.0";
    private static final String NAMER_ARCHITECTURE_VERSION = "1.01h";

    private float elapsed = 0f;
    private long totalScheduledPolls = 0L;
    private int reportNumber = 0;

    // Profiling counters for the current reporting window.
    private int profilePolls = 0;
    private double profileAdvanceSeconds = 0d;
    private long profileWatcherTotalNs = 0L;
    private long profileWatcherMaxNs = 0L;
    private long profileIntelEntriesSeen = 0L;
    private long profileUnscIntelEntriesSeen = 0L;
    private long profileTrackedTargetChecks = 0L;
    private long profileNewTracks = 0L;
    private long profileTargetDisqualifications = 0L;
    private long profileTerminalDiscards = 0L;
    private long profileSuccessOutcomes = 0L;
    private long profileEligibleNamingEvents = 0L;
    private long profileFullSectorNameScans = 0L;
    private long profileSystemsVisited = 0L;
    private long profilePlanetsVisited = 0L;
    private long profilePlanetNamesAdded = 0L;
    private long profileMarketNamesAdded = 0L;
    private long profileOccupiedNamesMax = 0L;
    private long profileCustomNamesTested = 0L;
    private long profileHashLookups = 0L;
    private long profileGlobalNameScanNs = 0L;
    private long profileNameSelectionNs = 0L;
    private long profileAssignments = 0L;
    private long profileFallbacks = 0L;
    private long profileProcgenNotifies = 0L;
    private long profileConfigErrors = 0L;
    private long profileUntrackedSuccessesSkipped = 0L;

    public UNSCColonyNamer() {
        log.info(PROFILE_PREFIX + " START modVersion=" + PROFILE_MOD_VERSION
                + " namerArchitecture=" + NAMER_ARCHITECTURE_VERSION
                + " intervalSec=15.0 reportEveryPolls=20 "
                + "primePollExcluded=true");

        // Prime immediately so expeditions already in flight are tracked before
        // they can arrive. This poll is deliberately excluded from profile cadence.
        poll(false);
    }

    @Override
    public boolean isDone() {
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        elapsed += amount;
        if (elapsed < CHECK_INTERVAL) return;
        elapsed = 0f;
        poll(true);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, Object>> getTrackingData() {
        Map<String, Object> persistent = Global.getSector().getPersistentData();
        Object existing = persistent.get(TRACKING_DATA_KEY);
        if (existing instanceof Map) {
            return (Map<String, Map<String, Object>>) existing;
        }
        Map<String, Map<String, Object>> created = new HashMap<String, Map<String, Object>>();
        persistent.put(TRACKING_DATA_KEY, created);
        return created;
    }

    private long nextSequence() {
        Map<String, Object> persistent = Global.getSector().getPersistentData();
        Object existing = persistent.get(NEXT_SEQUENCE_KEY);
        long next = 1L;
        if (existing instanceof Number) {
            next = ((Number) existing).longValue();
            if (next < 1L) next = 1L;
        }
        persistent.put(NEXT_SEQUENCE_KEY, Long.valueOf(next + 1L));
        return next;
    }

    @SuppressWarnings("unchecked")
    private List<ColonyExpeditionIntel> getColonyExpeditions() {
        SectorAPI sector = Global.getSector();
        if (sector == null || sector.getIntelManager() == null) {
            return Collections.emptyList();
        }
        List<?> raw = sector.getIntelManager().getIntel(ColonyExpeditionIntel.class);
        if (raw == null || raw.isEmpty()) return Collections.emptyList();

        List<ColonyExpeditionIntel> result = new ArrayList<ColonyExpeditionIntel>(raw.size());
        for (Object obj : raw) {
            if (obj instanceof ColonyExpeditionIntel) {
                result.add((ColonyExpeditionIntel) obj);
            }
        }
        return result;
    }

    private void poll(boolean scheduled) {
        long pollStart = System.nanoTime();
        try {
            SectorAPI sector = Global.getSector();
            if (sector == null) return;

            Map<String, Map<String, Object>> tracking = getTrackingData();
            List<ColonyExpeditionIntel> intels = getColonyExpeditions();
            List<SuccessCandidate> successes = new ArrayList<SuccessCandidate>();
            List<NewTrackCandidate> newTracks = new ArrayList<NewTrackCandidate>();

            for (ColonyExpeditionIntel intel : intels) {
                if (intel == null) continue;
                if (scheduled) profileIntelEntriesSeen++;

                FactionAPI faction = intel.getFaction();
                if (faction == null || !FACTION_ID.equals(faction.getId())) continue;
                if (scheduled) profileUnscIntelEntriesSeen++;

                PlanetAPI planet = intel.getTargetPlanet();
                MarketAPI market = intel.getTarget();
                if (planet == null || market == null || market.getId() == null) continue;

                String marketId = market.getId();
                Map<String, Object> track = tracking.get(marketId);
                ColonyOutcome outcome = intel.getColonyOutcome();

                // Completed successful intel can remain visible for a while. Once
                // this market has been handled, ignore the lingering intel so it
                // does not inflate profiler counters or repeat any work.
                if (outcome == ColonyOutcome.SUCCESS
                        && market.getMemoryWithoutUpdate() != null
                        && market.getMemoryWithoutUpdate().getBoolean(PROCESSED_KEY)) {
                    if (track != null) tracking.remove(marketId);
                    continue;
                }

                if (track == null) {
                    if (outcome == ColonyOutcome.SUCCESS) {
                        // We never observed this expedition before it succeeded, so
                        // there is no safe way to prove that its original world name
                        // was generic. Conservatively leave it alone.
                        markProcessedIfSafe(market);
                        if (scheduled) {
                            profileSuccessOutcomes++;
                            profileUntrackedSuccessesSkipped++;
                        }
                        continue;
                    }

                    if (outcome != null || intel.isEnding() || intel.isEnded()) {
                        continue;
                    }

                    if (!market.isInEconomy()) {
                        boolean eligible = isGenericPreColonization(planet, market);
                        newTracks.add(new NewTrackCandidate(marketId, eligible));
                    }
                    continue;
                }

                if (scheduled) profileTrackedTargetChecks++;

                if (outcome == ColonyOutcome.SUCCESS) {
                    successes.add(new SuccessCandidate(planet, market, track));
                    if (scheduled) profileSuccessOutcomes++;
                    continue;
                }

                if (outcome != null) {
                    tracking.remove(marketId);
                    if (scheduled) profileTerminalDiscards++;
                    continue;
                }

                if (intel.isEnding() || intel.isEnded()) {
                    tracking.remove(marketId);
                    if (scheduled) profileTerminalDiscards++;
                    continue;
                }

                // While the target remains uncolonized, revoke eligibility if
                // another mod/player gives either the planet or its market a proper
                // name before the expedition arrives. Never re-enable once revoked.
                if (!market.isInEconomy() && getBoolean(track, "eligible")) {
                    if (!isGenericPreColonization(planet, market)) {
                        track.put("eligible", Boolean.FALSE);
                        if (scheduled) profileTargetDisqualifications++;
                    }
                }
            }

            // Assign deterministic sequence numbers to newly seen expeditions.
            // Sorting by market id removes dependence on IntelManager iteration
            // order when several expeditions are discovered on the same poll.
            Collections.sort(newTracks, new Comparator<NewTrackCandidate>() {
                @Override
                public int compare(NewTrackCandidate a, NewTrackCandidate b) {
                    return a.marketId.compareTo(b.marketId);
                }
            });
            for (NewTrackCandidate candidate : newTracks) {
                if (tracking.containsKey(candidate.marketId)) continue;
                Map<String, Object> track = createTrack(candidate.eligible, true);
                tracking.put(candidate.marketId, track);
                if (scheduled) profileNewTracks++;
            }

            if (!successes.isEmpty()) {
                processSuccesses(successes, tracking, scheduled);
            }
        } catch (LinkageError ex) {
            // If a future Nex version changes one of the public methods this build
            // was compiled against, fail closed instead of touching colony names.
            log.error("UNSC colony namer: Nexerelin API linkage failed; leaving colony names unchanged for this poll.", ex);
        } catch (Exception ex) {
            log.error("UNSC colony namer: expedition watcher poll failed; leaving colony names unchanged for this poll.", ex);
        } finally {
            long elapsedNs = System.nanoTime() - pollStart;
            if (scheduled) {
                profilePolls++;
                totalScheduledPolls++;
                profileAdvanceSeconds += CHECK_INTERVAL;
                profileWatcherTotalNs += elapsedNs;
                if (elapsedNs > profileWatcherMaxNs) profileWatcherMaxNs = elapsedNs;

                if (profilePolls >= PROFILE_REPORT_EVERY_POLLS) {
                    emitProfileReport();
                    resetProfileWindow();
                }
            }
        }
    }

    private Map<String, Object> createTrack(boolean eligible, boolean originallyUncolonized) {
        Map<String, Object> track = new HashMap<String, Object>();
        track.put("sequence", Long.valueOf(nextSequence()));
        track.put("eligible", Boolean.valueOf(eligible));
        track.put("originallyUncolonized", Boolean.valueOf(originallyUncolonized));
        return track;
    }

    private boolean isGenericPreColonization(PlanetAPI planet, MarketAPI market) {
        if (planet == null || market == null || market.isInEconomy()) return false;
        StarSystemAPI system = planet.getStarSystem();
        if (system == null || system.getBaseName() == null) return false;
        String prefix = system.getBaseName() + " ";

        String marketName = market.getName();
        if (marketName == null || !marketName.startsWith(prefix)) return false;

        // The market name is Nex's actual generic-name criterion. Also reject a
        // mismatched proper PlanetAPI name so another mod cannot have its authored
        // planet name overwritten merely because the market copy stayed generic.
        String planetName = planet.getName();
        return planetName == null || planetName.startsWith(prefix);
    }

    private void processSuccesses(List<SuccessCandidate> successes,
                                  Map<String, Map<String, Object>> tracking,
                                  boolean scheduled) {
        Collections.sort(successes, new Comparator<SuccessCandidate>() {
            @Override
            public int compare(SuccessCandidate a, SuccessCandidate b) {
                long sa = getLong(a.track, "sequence");
                long sb = getLong(b.track, "sequence");
                if (sa < sb) return -1;
                if (sa > sb) return 1;
                return a.market.getId().compareTo(b.market.getId());
            }
        });

        Set<String> occupiedNames = null;
        List<String> customNames = null;
        boolean configLoaded = false;

        for (SuccessCandidate success : successes) {
            String marketId = success.market.getId();
            Map<String, Object> track = success.track;

            boolean eligible = getBoolean(track, "eligible")
                    && getBoolean(track, "originallyUncolonized");

            if (!eligible || !passesSuccessSafetyChecks(success.market)) {
                markProcessedIfSafe(success.market);
                tracking.remove(marketId);
                continue;
            }

            if (scheduled) profileEligibleNamingEvents++;

            if (occupiedNames == null) {
                occupiedNames = buildOccupiedNameSet(scheduled);
            }
            if (!configLoaded) {
                customNames = loadCustomNames(scheduled);
                configLoaded = true;
            }

            String customName = pickUnusedCustomName(customNames, occupiedNames, scheduled);
            MemoryAPI memory = success.market.getMemoryWithoutUpdate();
            if (customName != null) {
                String oldName = success.market.getName();
                success.market.setName(customName);
                success.planet.setName(customName);
                occupiedNames.add(normalize(customName));
                try {
                    ProcgenUsedNames.notifyUsed(customName);
                    if (scheduled) profileProcgenNotifies++;
                } catch (LinkageError ex) {
                    log.warn("UNSC colony namer: assigned '" + customName
                            + "' but could not notify ProcgenUsedNames; continuing.", ex);
                } catch (Exception ex) {
                    log.warn("UNSC colony namer: assigned '" + customName
                            + "' but could not notify ProcgenUsedNames; continuing.", ex);
                }
                log.info("UNSC colony namer: renamed successful Nex colony '" + oldName
                        + "' to '" + customName + "'.");
                if (scheduled) profileAssignments++;
            } else {
                log.info("UNSC colony namer: custom name pool exhausted or unavailable; keeping Nexerelin fallback name '"
                        + success.market.getName() + "'.");
                if (scheduled) profileFallbacks++;
            }

            memory.set(PROCESSED_KEY, true);
            tracking.remove(marketId);
        }
    }

    private boolean passesSuccessSafetyChecks(MarketAPI market) {
        if (market == null || !market.isInEconomy()) return false;
        if (!FACTION_ID.equals(market.getFactionId())) return false;
        MemoryAPI memory = market.getMemoryWithoutUpdate();
        return memory != null
                && memory.getBoolean(NEX_COLONY_KEY)
                && !memory.getBoolean(PROCESSED_KEY);
    }

    private void markProcessedIfSafe(MarketAPI market) {
        if (market == null || !market.isInEconomy()) return;
        if (!FACTION_ID.equals(market.getFactionId())) return;
        MemoryAPI memory = market.getMemoryWithoutUpdate();
        if (memory != null && memory.getBoolean(NEX_COLONY_KEY)) {
            memory.set(PROCESSED_KEY, true);
        }
    }

    private Set<String> buildOccupiedNameSet(boolean scheduled) {
        long start = System.nanoTime();
        Set<String> occupied = new HashSet<String>();
        SectorAPI sector = Global.getSector();
        if (sector == null) return occupied;

        List<StarSystemAPI> systems = sector.getStarSystems();
        if (systems != null) {
            for (StarSystemAPI system : systems) {
                if (system == null) continue;
                if (scheduled) profileSystemsVisited++;
                List<PlanetAPI> planets = system.getPlanets();
                if (planets == null) continue;

                for (PlanetAPI planet : planets) {
                    if (planet == null) continue;
                    if (scheduled) profilePlanetsVisited++;

                    String planetName = normalize(planet.getName());
                    if (planetName != null && occupied.add(planetName) && scheduled) {
                        profilePlanetNamesAdded++;
                    }

                    MarketAPI market = planet.getMarket();
                    if (market != null) {
                        String marketName = normalize(market.getName());
                        if (marketName != null && occupied.add(marketName) && scheduled) {
                            profileMarketNamesAdded++;
                        }
                    }
                }
            }
        }

        if (scheduled) {
            profileFullSectorNameScans++;
            profileGlobalNameScanNs += System.nanoTime() - start;
            if (occupied.size() > profileOccupiedNamesMax) profileOccupiedNamesMax = occupied.size();
        }
        return occupied;
    }

    private List<String> loadCustomNames(boolean scheduled) {
        List<String> result = new ArrayList<String>();
        try {
            JSONObject config = Global.getSettings().loadJSON(CONFIG_PATH, MOD_ID);
            JSONArray names = config.getJSONArray("names");
            for (int i = 0; i < names.length(); i++) {
                String candidate = names.getString(i);
                if (candidate == null) continue;
                candidate = candidate.trim();
                if (candidate.length() > 0) result.add(candidate);
            }
        } catch (Exception ex) {
            if (scheduled) profileConfigErrors++;
            log.error("UNSC colony namer: failed to load " + CONFIG_PATH
                    + "; using Nexerelin fallback names.", ex);
        }
        return result;
    }

    private String pickUnusedCustomName(List<String> names, Set<String> occupied, boolean scheduled) {
        long start = System.nanoTime();
        try {
            if (names == null || occupied == null) return null;
            for (String candidate : names) {
                if (scheduled) {
                    profileCustomNamesTested++;
                    profileHashLookups++;
                }
                String normalized = normalize(candidate);
                if (normalized != null && !occupied.contains(normalized)) {
                    return candidate;
                }
            }
            return null;
        } finally {
            if (scheduled) profileNameSelectionNs += System.nanoTime() - start;
        }
    }

    private String normalize(String name) {
        if (name == null) return null;
        String normalized = name.trim();
        if (normalized.length() == 0) return null;
        return normalized.toLowerCase(Locale.ROOT);
    }

    private boolean getBoolean(Map<String, Object> map, String key) {
        if (map == null) return false;
        Object value = map.get(key);
        return value instanceof Boolean && ((Boolean) value).booleanValue();
    }

    private long getLong(Map<String, Object> map, String key) {
        if (map == null) return Long.MAX_VALUE;
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.MAX_VALUE;
    }

    private void emitProfileReport() {
        reportNumber++;
        double watcherTotalMs = profileWatcherTotalNs / 1000000d;
        double watcherAvgMs = profilePolls > 0 ? watcherTotalMs / profilePolls : 0d;
        double watcherMaxMs = profileWatcherMaxNs / 1000000d;
        double globalNameScanMs = profileGlobalNameScanNs / 1000000d;
        double nameSelectionMs = profileNameSelectionNs / 1000000d;
        double dutyPct = profileAdvanceSeconds > 0d
                ? (profileWatcherTotalNs / 1000000000d) / profileAdvanceSeconds * 100d
                : 0d;

        Map<String, Map<String, Object>> tracking = getTrackingData();

        log.info(PROFILE_PREFIX
                + " REPORT report=" + reportNumber
                + " modVersion=" + PROFILE_MOD_VERSION
                + " namerArchitecture=" + NAMER_ARCHITECTURE_VERSION
                + " watcherPolls=" + profilePolls
                + " totalWatcherPolls=" + totalScheduledPolls
                + " intervalAdvanceSec=" + format3(profileAdvanceSeconds)
                + " watcherTotalMs=" + format3(watcherTotalMs)
                + " watcherAvgMs=" + format3(watcherAvgMs)
                + " watcherMaxMs=" + format3(watcherMaxMs)
                + " estimatedDutyPct=" + format6(dutyPct)
                + " intelEntriesSeen=" + profileIntelEntriesSeen
                + " unscIntelEntriesSeen=" + profileUnscIntelEntriesSeen
                + " trackedTargetChecks=" + profileTrackedTargetChecks
                + " newTracks=" + profileNewTracks
                + " disqualifiedTargets=" + profileTargetDisqualifications
                + " terminalDiscards=" + profileTerminalDiscards
                + " successOutcomes=" + profileSuccessOutcomes
                + " eligibleNamingEvents=" + profileEligibleNamingEvents
                + " fullSectorNameScans=" + profileFullSectorNameScans
                + " systemsVisited=" + profileSystemsVisited
                + " planetsVisited=" + profilePlanetsVisited
                + " planetNamesAdded=" + profilePlanetNamesAdded
                + " marketNamesAdded=" + profileMarketNamesAdded
                + " occupiedNamesMax=" + profileOccupiedNamesMax
                + " customNamesTested=" + profileCustomNamesTested
                + " hashLookups=" + profileHashLookups
                + " globalNameScanMs=" + format3(globalNameScanMs)
                + " nameSelectionMs=" + format3(nameSelectionMs)
                + " assignments=" + profileAssignments
                + " fallbacks=" + profileFallbacks
                + " procgenNotifies=" + profileProcgenNotifies
                + " configErrors=" + profileConfigErrors
                + " untrackedSuccessesSkipped=" + profileUntrackedSuccessesSkipped
                + " trackedNow=" + tracking.size());
    }

    private String format3(double value) {
        return String.format(Locale.ROOT, "%.3f", value);
    }

    private String format6(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    private void resetProfileWindow() {
        profilePolls = 0;
        profileAdvanceSeconds = 0d;
        profileWatcherTotalNs = 0L;
        profileWatcherMaxNs = 0L;
        profileIntelEntriesSeen = 0L;
        profileUnscIntelEntriesSeen = 0L;
        profileTrackedTargetChecks = 0L;
        profileNewTracks = 0L;
        profileTargetDisqualifications = 0L;
        profileTerminalDiscards = 0L;
        profileSuccessOutcomes = 0L;
        profileEligibleNamingEvents = 0L;
        profileFullSectorNameScans = 0L;
        profileSystemsVisited = 0L;
        profilePlanetsVisited = 0L;
        profilePlanetNamesAdded = 0L;
        profileMarketNamesAdded = 0L;
        profileOccupiedNamesMax = 0L;
        profileCustomNamesTested = 0L;
        profileHashLookups = 0L;
        profileGlobalNameScanNs = 0L;
        profileNameSelectionNs = 0L;
        profileAssignments = 0L;
        profileFallbacks = 0L;
        profileProcgenNotifies = 0L;
        profileConfigErrors = 0L;
        profileUntrackedSuccessesSkipped = 0L;
    }

    private static final class NewTrackCandidate {
        private final String marketId;
        private final boolean eligible;

        private NewTrackCandidate(String marketId, boolean eligible) {
            this.marketId = marketId;
            this.eligible = eligible;
        }
    }

    private static final class SuccessCandidate {
        private final PlanetAPI planet;
        private final MarketAPI market;
        private final Map<String, Object> track;

        private SuccessCandidate(PlanetAPI planet, MarketAPI market, Map<String, Object> track) {
            this.planet = planet;
            this.market = market;
            this.track = track;
        }
    }
}
