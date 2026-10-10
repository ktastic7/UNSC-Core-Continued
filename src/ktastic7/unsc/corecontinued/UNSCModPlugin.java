package ktastic7.unsc.corecontinued;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import ktastic7.unsc.corecontinued.campaign.UNSCColonyNamer;
import ktastic7.unsc.corecontinued.world.UNSCWorldGen;

public class UNSCModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("unsc_cc");
        initUNSC();
    }

    private static void initUNSC() {
        new UNSCWorldGen().generate(Global.getSector());
    }

    @Override
    public void onNewGameAfterProcGen() {
        // Finalize the authored system only after sector procgen and every mod's
        // normal onNewGame() pass have had a chance to create their systems.
        // This allows Core to inspect the populated map before selecting its
        // final hyperspace position and creating hyperspace-facing jump links.
        UNSCWorldGen.finalizeAfterProcGen(Global.getSector());
    }

    @Override
    public void onGameLoad(boolean newGame) {
        // Keep the Core-owned system's presentation synchronized with the
        // supported Reborn coexistence rule on every load.
        UNSCWorldGen.syncEpsilonEridaniDisplayName(Global.getSector());
        if (!newGame) {
            UNSCWorldGen.logCoexistencePlacementSnapshot(Global.getSector());
        }

        // "Nex Gate": the colony-naming feature is only installed when
        // Nexerelin is actually enabled. Non-Nex games do no extra scanning.
        if (!Global.getSettings().getModManager().isModEnabled("nexerelin")) {
            return;
        }

        // Transient: re-added each load, but never serialized into the save.
        // All long-lived state is stored in market memory/sector persistentData.
        if (!Global.getSector().hasTransientScript(UNSCColonyNamer.class)) {
            Global.getSector().addTransientScript(new UNSCColonyNamer());
        }
    }
}
