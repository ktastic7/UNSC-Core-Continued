package ktastic7.unsc.corecontinued.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictShipData;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Entities;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SalvageSpecialAssigner.ShipRecoverySpecialCreator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.PerShipData;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial.ShipCondition;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldParams;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin.DebrisFieldSource;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import org.lazywizard.lazylib.MathUtils;

public class UNSCStar {

    private static final String CORE_WORLD_STORY_CRITICAL_REASON = "unsc_cc_core_world";
    private static final String AFTER_PROCGEN_FINALIZED_KEY = "$unsc_cc_after_procgen_finalized_v1";
    private static final float INNER_BELT_DISTANCE = 6950f;

    private static void addUncolonizedConditions(PlanetAPI planet, String... conditions) {
        // Match vanilla scripted-system worldgen: uncolonized planets use a
        // size-0 condition market so survey/resources work without making the
        // body an economy market or colony.
        Misc.initConditionMarket(planet);
        MarketAPI market = planet.getMarket();
        for (String condition : conditions) {
            market.addCondition(condition);
        }
    }

    private static SectorEntityToken addBattleDebris(StarSystemAPI system, SectorEntityToken star, String id,
            float fieldRadius, float orbitAngle, float orbitRadius, float orbitDays) {
        DebrisFieldParams params = new DebrisFieldParams(
                fieldRadius,
                -1f,
                10000000f,
                0f);
        params.source = DebrisFieldSource.MIXED;
        // Leave DebrisFieldParams.baseSalvageXP at Starsector's default. Earlier
        // development used an incorrect hand-written API stub for this field's type;
        // omitting the override is intentional and lets the game handle salvage XP.
        SectorEntityToken debris = Misc.addDebrisField(system, params, StarSystemGenerator.random);
        SalvageSpecialAssigner.assignSpecialForDebrisField(debris);
        debris.setId(id);
        debris.setSensorProfile(null);
        debris.setDiscoverable(null);
        debris.setCircularOrbit(star, MathUtils.clampAngle(orbitAngle), orbitRadius, orbitDays);
        return debris;
    }

    private static SectorEntityToken addSalvageEntity(StarSystemAPI system, String type, String id, String name,
            SectorEntityToken focus, float angle, float radius, float orbitDays) {
        SectorEntityToken entity = BaseThemeGenerator.addSalvageEntity(system, type, Factions.NEUTRAL, null);
        if (id != null) entity.setId(id);
        if (name != null) entity.setName(name);
        entity.setCircularOrbit(focus, MathUtils.clampAngle(angle), radius, orbitDays);
        return entity;
    }

    private static SectorEntityToken addUNSCDerelict(StarSystemAPI system, String id, String variantId,
            SectorEntityToken focus, float angle, float radius, float orbitDays, boolean freelyRecoverable) {
        DerelictShipData params = new DerelictShipData(
                new PerShipData(variantId, ShipCondition.WRECKED, 0f), false);
        SectorEntityToken ship = BaseThemeGenerator.addSalvageEntity(system, Entities.WRECK, Factions.NEUTRAL, params);
        ship.setId(id);
        ship.setDiscoverable(true);
        ship.setCircularOrbit(focus, MathUtils.clampAngle(angle), radius, orbitDays);

        // Match vanilla derelict behavior: only the selected six receive the normal
        // recovery special. The other wrecks remain salvageable and can use the
        // game's Story Point "make recoverable" route.
        if (freelyRecoverable) {
            ShipRecoverySpecialCreator creator = new ShipRecoverySpecialCreator(null, 0, 0, false, null, null);
            Misc.setSalvageSpecial(ship, creator.createSpecial(ship, null));
        }
        return ship;
    }

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Epsilon Eridani");
        system.setOptionalUniqueId(UNSCWorldGen.EPSILON_ERIDANI_SYSTEM_ID);
        UNSCWorldGen.syncEpsilonEridaniDisplayName(sector);
        // Provisional authored position. The final hyperspace placement is
        // resolved deterministically in onNewGameAfterProcGen(), after the
        // normal new-game system generators have populated the sector.
        system.getLocation().set(
                UNSCWorldGen.EPSILON_ERIDANI_PREFERRED_X,
                UNSCWorldGen.EPSILON_ERIDANI_PREFERRED_Y);
        PlanetAPI epsilon_eridani_star = system.initStar("unsc_cc_epsilon_eridani_star",
                "star_orange",
                600,
                500);
        epsilon_eridani_star.setName("Epsilon Eridani");

        // Rebuilt Halo-lore-focused planetary order. Distances are intentionally
        // compressed for Starsector gameplay while giving the major bodies extra orbital
        // room. Reach/Tribute are phase-locked for map readability, and Site 17
        // is deliberately remote in the outer system.
        final float epsilon_eridani_i_distance = 1800f;
        final float reach_distance = 3500f;
        final float tribute_distance = 4850f;
        final float circumstance_distance = 6150f;
        final float inner_belt_distance = INNER_BELT_DISTANCE;
        final float beta_gabriel_distance = 7700f;
        final float tantalus_distance = 9350f;
        final float outer_belt_distance = 10450f;
        final float site_17_distance = 14500f;

        // Epsilon Eridani I - innermost barren world, uncolonized.
        PlanetAPI epsilon_eridani_i = system.addPlanet("unsc_cc_epsilon_eridani_i",
                epsilon_eridani_star,
                "Epsilon Eridani I",
                "barren",
                360f * (float) Math.random(),
                85f,
                epsilon_eridani_i_distance,
                190f);
        epsilon_eridani_i.setCustomDescriptionId("unsc_cc_epsilon_eridani_i");
        addUncolonizedConditions(epsilon_eridani_i,
                "no_atmosphere",
                "very_hot",
                "ore_moderate");

        // Reach - Epsilon Eridani II.
        float reach_angle = 360f * (float) Math.random();
        PlanetAPI reach = system.addPlanet("unsc_cc_reach",
                epsilon_eridani_star,
                "Reach",
                "barren-bombarded",
                reach_angle,
                120f,
                reach_distance,
                390f);
        reach.setCustomDescriptionId("unsc_cc_reach");
        MarketAPI reach_market = UNSC_AddMarketplace.addMarketplace("unsc_cc", "unsc_cc_reach_market", reach, null,
                "Reach",
                5,
                new ArrayList<String>(Arrays.asList(
                        Conditions.POPULATION_5,
                        Conditions.DECIVILIZED_SUBPOP,
                        Conditions.RUINS_VAST,
                        Conditions.THIN_ATMOSPHERE,
                        Conditions.ORE_MODERATE,
                        Conditions.RARE_ORE_ABUNDANT
                )),
                new ArrayList<String>(Arrays.asList(
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_STORAGE
                )),
                new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.MINING,
                        Industries.BATTLESTATION_MID,
                        Industries.HEAVYBATTERIES,
                        Industries.MILITARYBASE
                )),
                true,
                false);
        reach_market.addIndustry(Industries.ORBITALWORKS, new ArrayList<String>(Arrays.asList(Items.CORRUPTED_NANOFORGE)));
        Industry reachfort = reach.getMarket().getIndustry(Industries.BATTLESTATION_MID);
        reachfort.setAICoreId("alpha_core");
        Industry reachbatteries = reach_market.getIndustry(Industries.HEAVYBATTERIES);
        reachbatteries.setAICoreId("alpha_core");
        Industry reachmilitary = reach_market.getIndustry(Industries.MILITARYBASE);
        reachmilitary.setAICoreId("alpha_core");
        Misc.makeStoryCritical(reach_market, CORE_WORLD_STORY_CRITICAL_REASON);

        // Industrial Evolution gate: only touch IndEvo-specific integration when
        // Industrial Evolution is actually enabled. The direct IndEvo reference
        // lives in UNSCIndEvoIntegration so non-IndEvo games never resolve it.
        if (Global.getSettings().getModManager().isModEnabled("IndEvo")) {
            UNSCIndEvoIntegration.addReachRailgun(reach, reach_market);
        }

        reach.setInteractionImage("illustrations", "unsc_cc_reach");

        // Casimir Station - remains 550 units from Reach.
        SectorEntityToken casimir_station = system.addCustomEntity("unsc_cc_casimir_station", "Casimir Station", "station_midline2", "unsc_cc");
        casimir_station.setCircularOrbitPointingDown(reach, 0f, 550f, 117f);
        casimir_station.setCustomDescriptionId("unsc_cc_casimir_station");
        MarketAPI casimir_station_market = UNSC_AddMarketplace.addMarketplace("unsc_cc", "unsc_cc_casimir_station_market", casimir_station, null,
                "Casimir Station",
                5,
                new ArrayList<String>(Arrays.asList(
                        Conditions.POPULATION_5
                )),
                new ArrayList<String>(Arrays.asList(
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_STORAGE
                )),
                new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        Industries.WAYSTATION,
                        Industries.FUELPROD,
                        Industries.BATTLESTATION_MID,
                        Industries.GROUNDDEFENSES,
                        Industries.PATROLHQ
                )),
                true,
                false);
        casimir_station.setInteractionImage("illustrations", "unsc_cc_casimir_station");

        // Reach moon: Turul - smaller inner captured moon. Uncolonized.
        final float turul_angle = 360f * (float) Math.random();
        PlanetAPI turul = system.addPlanet("unsc_cc_turul",
                reach,
                "Turul",
                "rocky_unstable",
                turul_angle,
                35f,
                850f,
                200f);
        addUncolonizedConditions(turul,
                "no_atmosphere",
                "tectonic_activity",
                "low_gravity",
                "ore_sparse");

        // Reach moon: Csodaszarvas - outer atmospheric moon with rings, phase-locked to Turul.
        PlanetAPI csodaszarvas = system.addPlanet("unsc_cc_csodaszarvas",
                reach,
                "Csodaszarvas",
                "barren-desert",
                MathUtils.clampAngle(turul_angle + 160f),
                55f,
                1100f,
                200f);
        addUncolonizedConditions(csodaszarvas,
                "low_gravity",
                "dense_atmosphere",
                "hot");
        system.addRingBand(csodaszarvas, "misc", "rings_dust0", 256f, 1, Color.gray, 128f, 175f, 18f);
        system.addRingBand(csodaszarvas, "misc", "rings_asteroids0", 256f, 0, Color.gray, 128f, 215f, 22f);
        system.addRingBand(csodaszarvas, "misc", "rings_dust0", 256f, 3, Color.gray, 128f, 255f, 27f);

        // Tribute - Epsilon Eridani III, phase-locked 160 degrees from Reach.
        final float tribute_angle = MathUtils.clampAngle(reach_angle + 160f);
        PlanetAPI tribute = system.addPlanet("unsc_cc_tribute",
                epsilon_eridani_star,
                "Tribute",
                "tundra",
                tribute_angle,
                160f,
                tribute_distance,
                390f);
        tribute.setCustomDescriptionId("unsc_cc_tribute");
        MarketAPI tribute_market = UNSC_AddMarketplace.addMarketplace("unsc_cc", "unsc_cc_tribute_market", tribute, null,
                "Tribute",
                6,
                new ArrayList<String>(Arrays.asList(
                        Conditions.POPULATION_6,
                        Conditions.ORE_SPARSE,
                        Conditions.RARE_ORE_SPARSE,
                        Conditions.ORGANICS_PLENTIFUL,
                        Conditions.COLD,
                        Conditions.FARMLAND_POOR,
                        Conditions.HABITABLE,
                        Conditions.EXTREME_WEATHER,
                        Conditions.RUINS_WIDESPREAD
                )),
                new ArrayList<String>(Arrays.asList(
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_STORAGE
                )),
                new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.MINING,
                        Industries.FARMING,
                        Industries.ORBITALWORKS,
                        Industries.STARFORTRESS_MID,
                        Industries.HEAVYBATTERIES,
                        Industries.HIGHCOMMAND,
                        Industries.WAYSTATION
                )),
                true,
                false);
        Industry tributefort = tribute.getMarket().getIndustry(Industries.STARFORTRESS_MID);
        tributefort.setAICoreId("alpha_core");
        Industry tributecommand = tribute.getMarket().getIndustry(Industries.HIGHCOMMAND);
        tributecommand.setAICoreId("alpha_core");
        Industry tributebatteries = tribute_market.getIndustry(Industries.HEAVYBATTERIES);
        tributebatteries.setAICoreId("alpha_core");
        Misc.makeStoryCritical(tribute_market, CORE_WORLD_STORY_CRITICAL_REASON);
        tribute.setInteractionImage("illustrations", "unsc_cc_tribute");

        // Tribute moon: Emese - uncolonized rocky-metallic moon.
        PlanetAPI emese = system.addPlanet("unsc_cc_emese",
                tribute,
                "Emese",
                "rocky_metallic",
                360f * (float) Math.random(),
                45f,
                800f,
                165f);
        addUncolonizedConditions(emese,
                "low_gravity",
                "no_atmosphere",
                "ore_moderate",
                "rare_ore_moderate");

        JumpPointAPI tribute_jump = Global.getFactory().createJumpPoint("unsc_cc_tribute_jump", "Tribute Jump Point");
        tribute_jump.setCircularOrbit(epsilon_eridani_star, tribute_angle + 15f, tribute_distance, 390f);
        tribute_jump.setRelatedPlanet(tribute);
        system.addEntity(tribute_jump);

        // Circumstance - Epsilon Eridani IV in this mod's interpretation.
        PlanetAPI circumstance = system.addPlanet("unsc_cc_circumstance",
                epsilon_eridani_star,
                "Circumstance",
                "terran-eccentric",
                360f * (float) Math.random(),
                165f,
                circumstance_distance,
                900f);
        circumstance.setCustomDescriptionId("unsc_cc_circumstance");
        MarketAPI circumstance_market = UNSC_AddMarketplace.addMarketplace("unsc_cc", "unsc_cc_circumstance_market", circumstance, null,
                "Circumstance",
                7,
                new ArrayList<String>(Arrays.asList(
                        Conditions.POPULATION_7,
                        Conditions.HABITABLE,
                        "organics_abundant",
                        "farmland_rich",
                        "ruins_scattered"
                )),
                new ArrayList<String>(Arrays.asList(
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.GENERIC_MILITARY,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_STORAGE
                )),
                new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.FARMING,
                        Industries.LIGHTINDUSTRY,
                        Industries.REFINING,
                        Industries.HEAVYBATTERIES,
                        Industries.HIGHCOMMAND,
                        Industries.STARFORTRESS_MID,
                        Industries.WAYSTATION
                )),
                true,
                false);
        Industry circumstancebatteries = circumstance_market.getIndustry(Industries.HEAVYBATTERIES);
        circumstancebatteries.setAICoreId("alpha_core");
        Industry circumstancecommand = circumstance_market.getIndustry(Industries.HIGHCOMMAND);
        circumstancecommand.setAICoreId("alpha_core");
        Industry circumstancefort = circumstance_market.getIndustry(Industries.STARFORTRESS_MID);
        circumstancefort.setAICoreId("alpha_core");
        Misc.makeStoryCritical(circumstance_market, CORE_WORLD_STORY_CRITICAL_REASON);

        // Inner canonical asteroid belt between Circumstance and Beta Gabriel.
        system.addAsteroidBelt(epsilon_eridani_star, 300, inner_belt_distance, 600f, 1000f, 1150f, Terrain.ASTEROID_BELT, "Epsilon Eridani Inner Belt");
        system.addRingBand(epsilon_eridani_star, "misc", "rings_asteroids0", 256f, 2, Color.gray, 256f, inner_belt_distance - 200f, 1050f);
        system.addRingBand(epsilon_eridani_star, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, inner_belt_distance, 1075f);
        system.addRingBand(epsilon_eridani_star, "misc", "rings_asteroids0", 256f, 1, Color.gray, 256f, inner_belt_distance + 200f, 1100f);

        // Beta Gabriel - water world, resettled UNSC colony.
        PlanetAPI beta_gabriel = system.addPlanet("unsc_cc_beta_gabriel",
                epsilon_eridani_star,
                "Beta Gabriel",
                "water",
                360f * (float) Math.random(),
                135f,
                beta_gabriel_distance,
                1260f);
        beta_gabriel.setCustomDescriptionId("unsc_cc_beta_gabriel");
        MarketAPI beta_gabriel_market = UNSC_AddMarketplace.addMarketplace("unsc_cc", "unsc_cc_beta_gabriel_market", beta_gabriel, null,
                "Beta Gabriel",
                4,
                new ArrayList<String>(Arrays.asList(
                        "population_4",
                        Conditions.HABITABLE,
                        Conditions.SOLAR_ARRAY,
                        "organics_trace",
                        "ruins_scattered",
                        "water_surface"
                )),
                new ArrayList<String>(Arrays.asList(
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_STORAGE
                )),
                new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.SPACEPORT,
                        "aquaculture",
                        Industries.GROUNDDEFENSES,
                        Industries.PATROLHQ
                )),
                true,
                false);

        // Visible Orbital Solar Array hardware, following vanilla mirror-array patterns.
        SectorEntityToken beta_mirror_alpha = system.addCustomEntity("unsc_cc_beta_gabriel_mirror_alpha", "Beta Gabriel Stellar Mirror Alpha", "stellar_mirror", "unsc_cc");
        beta_mirror_alpha.setCircularOrbitPointingDown(beta_gabriel, 0f, 220f, 40f);
        beta_mirror_alpha.setCustomDescriptionId("stellar_mirror");
        SectorEntityToken beta_mirror_beta = system.addCustomEntity("unsc_cc_beta_gabriel_mirror_beta", "Beta Gabriel Stellar Mirror Beta", "stellar_mirror", "unsc_cc");
        beta_mirror_beta.setCircularOrbitPointingDown(beta_gabriel, 120f, 220f, 40f);
        beta_mirror_beta.setCustomDescriptionId("stellar_mirror");
        SectorEntityToken beta_mirror_gamma = system.addCustomEntity("unsc_cc_beta_gabriel_mirror_gamma", "Beta Gabriel Stellar Mirror Gamma", "stellar_mirror", "unsc_cc");
        beta_mirror_gamma.setCircularOrbitPointingDown(beta_gabriel, 240f, 220f, 40f);
        beta_mirror_gamma.setCustomDescriptionId("stellar_mirror");

        // Tantalus - glassed/irradiated outer world, uncolonized in this era.
        final float tantalus_angle = 360f * (float) Math.random();
        PlanetAPI tantalus = system.addPlanet("unsc_cc_tantalus",
                epsilon_eridani_star,
                "Tantalus",
                "irradiated",
                tantalus_angle,
                145f,
                tantalus_distance,
                1700f);
        tantalus.setCustomDescriptionId("unsc_cc_tantalus");
        addUncolonizedConditions(tantalus,
                "irradiated",
                "rare_ore_rich",
                "ore_rich",
                "ruins_vast",
                "thin_atmosphere");

        // Outer canonical asteroid belt before the Oort-cloud Site 17 region.
        system.addAsteroidBelt(epsilon_eridani_star, 400, outer_belt_distance, 700f, 1850f, 2150f, Terrain.ASTEROID_BELT, "Epsilon Eridani Outer Belt");
        system.addRingBand(epsilon_eridani_star, "misc", "rings_asteroids0", 256f, 3, Color.gray, 256f, outer_belt_distance - 250f, 1950f);
        system.addRingBand(epsilon_eridani_star, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, outer_belt_distance, 2000f);
        system.addRingBand(epsilon_eridani_star, "misc", "rings_asteroids0", 256f, 2, Color.gray, 256f, outer_belt_distance + 250f, 2050f);

        // Site 17 - simplified as the planetoid's in-game name for readability.
        PlanetAPI site_17 = system.addPlanet("unsc_cc_site_17",
                epsilon_eridani_star,
                "Site 17",
                "rocky_ice",
                360f * (float) Math.random(),
                65f,
                site_17_distance,
                2650f);
        site_17.setCustomDescriptionId("unsc_cc_site_17");
        addUncolonizedConditions(site_17,
                "ore_sparse",
                "no_atmosphere",
                "low_gravity",
                "poor_light",
                "volatiles_trace");

        // Teller Station - abandoned orbital facility over Site 17.
        SectorEntityToken teller_station = system.addCustomEntity("unsc_cc_teller_station", "Teller Station", "station_side00", "neutral");
        teller_station.setCircularOrbitPointingDown(site_17, 45f, 250f, 30f);
        teller_station.setInteractionImage("illustrations", "abandoned_station3");
        Misc.setAbandonedStationMarket("unsc_cc_teller_station_market", teller_station);

        // Battle debris fields: kept in the orbital neighborhoods of Reach, Tribute,
        // and Tantalus, but with different periods so they drift instead of remaining
        // phase-locked to the planets. Initial angles deliberately avoid the planets.
        addBattleDebris(system, epsilon_eridani_star, "unsc_cc_debris_reach_alpha",
                220f, reach_angle + 75f, 3350f, 430f);
        addBattleDebris(system, epsilon_eridani_star, "unsc_cc_debris_reach_beta",
                260f, reach_angle + 225f, 3700f, 350f);
        addBattleDebris(system, epsilon_eridani_star, "unsc_cc_debris_tribute",
                240f, tribute_angle + 120f, 5050f, 450f);
        addBattleDebris(system, epsilon_eridani_star, "unsc_cc_debris_tantalus_alpha",
                300f, tantalus_angle + 80f, 9150f, 1600f);
        addBattleDebris(system, epsilon_eridani_star, "unsc_cc_debris_tantalus_beta",
                280f, tantalus_angle + 230f, 9600f, 1825f);

        // Vanilla salvage caches placed in the larger gaps and outer system.
        SectorEntityToken equipment_cache = addSalvageEntity(system, Entities.EQUIPMENT_CACHE,
                "unsc_cc_equipment_cache", null, epsilon_eridani_star, 80f, 8500f, 1500f);
        SectorEntityToken weapons_cache_outer = addSalvageEntity(system, Entities.WEAPONS_CACHE,
                "unsc_cc_weapons_cache_outer", null, epsilon_eridani_star, 190f, 11500f, 2150f);
        SectorEntityToken weapons_cache_deep = addSalvageEntity(system, Entities.WEAPONS_CACHE,
                "unsc_cc_weapons_cache_deep", null, epsilon_eridani_star, 40f, 13500f, 2500f);

        // Vanilla abandoned exploration stations.
        SectorEntityToken tantalus_habitat = addSalvageEntity(system, Entities.ORBITAL_HABITAT,
                "unsc_cc_tantalus_habitat", "Abandoned Orbital Habitat", tantalus, 75f, 320f, 65f);
        SectorEntityToken outer_research = addSalvageEntity(system, Entities.STATION_RESEARCH,
                "unsc_cc_outer_research_station", "Abandoned Research Station", epsilon_eridani_star,
                150f, outer_belt_distance, 2000f);

        // Inactive Domain-era travel gate between the outer asteroid belt and Site 17.
        SectorEntityToken epsilon_eridani_gate = system.addCustomEntity(
                "unsc_cc_epsilon_eridani_gate", "Epsilon Eridani Gate", Entities.INACTIVE_GATE, null);
        epsilon_eridani_gate.setCircularOrbit(epsilon_eridani_star, 300f, 12500f, 2300f);

        // UNSC derelict clusters. Exactly six receive normal recovery specials; the
        // remaining fourteen are intended to require the vanilla Story Point recovery route.
        // Epsilon Eridani I cluster: Mako (free), Charon (SP), Paris (free).
        addUNSCDerelict(system, "unsc_cc_wreck_e1_mako", "unsc_cc_corvette_mako_assault",
                epsilon_eridani_i, 40f, 250f, 55f, true);
        addUNSCDerelict(system, "unsc_cc_wreck_e1_charon", "unsc_cc_frigate_charon_logistics",
                epsilon_eridani_i, 62f, 350f, 70f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_e1_paris", "unsc_cc_frigate_paris_multirole",
                epsilon_eridani_i, 25f, 450f, 90f, true);

        // Inner belt cluster: Mako (SP), Charon (free), Paris (SP), Halberd (SP).
        addUNSCDerelict(system, "unsc_cc_wreck_inner_mako", "unsc_cc_corvette_mako_strike",
                epsilon_eridani_star, 238f, 6800f, 1025f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_inner_charon", "unsc_cc_frigate_charon_logistics",
                epsilon_eridani_star, 247f, 6900f, 1050f, true);
        addUNSCDerelict(system, "unsc_cc_wreck_inner_paris", "unsc_cc_frigate_paris_escort",
                epsilon_eridani_star, 257f, 7010f, 1090f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_inner_halberd", "unsc_cc_destroyer_halberd_assault",
                epsilon_eridani_star, 266f, 7110f, 1125f, false);

        // Equipment-cache cluster between Beta Gabriel and Tantalus:
        // Charon (free), Paris (free), Strident (SP).
        addUNSCDerelict(system, "unsc_cc_wreck_gap_charon", "unsc_cc_frigate_charon_logistics",
                epsilon_eridani_star, 70f, 8400f, 1475f, true);
        addUNSCDerelict(system, "unsc_cc_wreck_gap_paris", "unsc_cc_frigate_paris_strike",
                epsilon_eridani_star, 80f, 8500f, 1525f, true);
        addUNSCDerelict(system, "unsc_cc_wreck_gap_strident", "unsc_cc_frigate_strident_backline",
                epsilon_eridani_star, 91f, 8600f, 1580f, false);

        // Tantalus cluster around the abandoned habitat: all Story Point recoveries.
        addUNSCDerelict(system, "unsc_cc_wreck_tantalus_mako", "unsc_cc_corvette_mako_support",
                tantalus, 50f, 420f, 80f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_tantalus_paris", "unsc_cc_frigate_paris_aa",
                tantalus, 70f, 470f, 90f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_tantalus_halberd", "unsc_cc_destroyer_halberd_strike",
                tantalus, 92f, 530f, 100f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_tantalus_punic", "unsc_cc_carrier_punic_support",
                tantalus, 112f, 600f, 110f, false);

        // Outer belt cluster around the research station: Charon (SP),
        // Strident (free), Halberd (SP).
        addUNSCDerelict(system, "unsc_cc_wreck_outer_charon", "unsc_cc_frigate_charon_logistics",
                epsilon_eridani_star, 140f, 10350f, 1975f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_outer_strident", "unsc_cc_frigate_strident_support",
                epsilon_eridani_star, 151f, 10450f, 2025f, true);
        addUNSCDerelict(system, "unsc_cc_wreck_outer_halberd", "unsc_cc_destroyer_halberd_strike",
                epsilon_eridani_star, 161f, 10550f, 2075f, false);

        // Gate cluster: both capital-grade finds require Story Points.
        addUNSCDerelict(system, "unsc_cc_wreck_gate_punic", "unsc_cc_carrier_punic_assault",
                epsilon_eridani_gate, 25f, 650f, 120f, false);
        addUNSCDerelict(system, "unsc_cc_wreck_gate_halcyon", "unsc_cc_cruiser_halcyon_assault",
                epsilon_eridani_gate, 205f, 850f, 155f, false);

        // Deep Site 17 find: Marathon requires a Story Point and remains near Teller Station.
        addUNSCDerelict(system, "unsc_cc_wreck_site17_marathon", "unsc_cc_cruiser_marathon_fleet",
                site_17, 210f, 450f, 95f, false);

        // Hyperspace-facing jump links, optional IndEvo outer-jump support,
        // and hyperspace clearing are deliberately deferred until
        // onNewGameAfterProcGen(), after the final collision-aware placement is
        // selected.

        SectorEntityToken relay = system.addCustomEntity("unsc_cc_epsilon_eridani_relay",
                "Epsilon Eridani Relay",
                "comm_relay_makeshift",
                "unsc_cc");
        relay.setCircularOrbit(epsilon_eridani_star, MathUtils.clampAngle(tribute_angle - 65f), tribute_distance + 250f, 500f);
    }

    /**
     * Finalize only the hyperspace-facing parts of Epsilon Eridani after Core's
     * delayed placement resolver has selected the final system coordinate.
     */
    public static void finalizeAfterProcGen(SectorAPI sector, StarSystemAPI system) {
        if (sector == null || system == null) return;
        if (system.getMemoryWithoutUpdate().getBoolean(AFTER_PROCGEN_FINALIZED_KEY)) return;

        system.autogenerateHyperspaceJumpPoints(true, true);

        // Industrial Evolution's outer-jump watchtower requires the
        // autogenerated fringe/outer jump point to exist, so this integration
        // belongs after final placement and jump generation.
        if (Global.getSettings().getModManager().isModEnabled("IndEvo")) {
            SectorEntityToken betaToken = sector.getEntityById("unsc_cc_beta_gabriel");
            PlanetAPI betaGabriel = betaToken instanceof PlanetAPI
                    ? (PlanetAPI) betaToken : null;
            SectorEntityToken tributeJump = sector.getEntityById("unsc_cc_tribute_jump");
            SectorEntityToken inactiveGate = sector.getEntityById("unsc_cc_epsilon_eridani_gate");

            UNSCIndEvoIntegration.addEpsilonEridaniSupport(
                    system,
                    betaGabriel,
                    tributeJump,
                    inactiveGate,
                    INNER_BELT_DISTANCE);
        }

        // Clear hyperspace only after the final location is known, preventing
        // a stale cloud clearing at the provisional/pre-resolution coordinate.
        HyperspaceTerrainPlugin hyperspaceTerrainPlugin =
                (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor nebulaEditor = new NebulaEditor(hyperspaceTerrainPlugin);

        float minHyperspaceRadius = hyperspaceTerrainPlugin.getTileSize() * 2f;
        float maxHyperspaceRadius = system.getMaxRadiusInHyperspace();

        nebulaEditor.clearArc(
                system.getLocation().x,
                system.getLocation().y,
                0,
                maxHyperspaceRadius + minHyperspaceRadius * 0.5f,
                0f,
                360f);
        nebulaEditor.clearArc(
                system.getLocation().x,
                system.getLocation().y,
                0,
                maxHyperspaceRadius + minHyperspaceRadius,
                0f,
                360f,
                0.25f);

        system.getMemoryWithoutUpdate().set(AFTER_PROCGEN_FINALIZED_KEY, true);
    }
}
