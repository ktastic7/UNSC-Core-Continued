package ktastic7.unsc.corecontinued.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;

public class CHM_UNSC extends BaseHullMod {

	public static float AUTOFIRE_AIM = 0.25f;
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getAutofireAimAccuracy().modifyFlat(id, AUTOFIRE_AIM);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		
		return null;
	}

}
