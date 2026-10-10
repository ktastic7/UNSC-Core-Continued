/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ktastic7.unsc.corecontinued.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;

import java.util.ArrayList;

public class UNSC_AddMarketplace {
    public static MarketAPI addMarketplace(
            String factionID,
            String marketID,
            SectorEntityToken primaryEntity,
            ArrayList<SectorEntityToken> connectedEntities,
            String name,
            int size,
            ArrayList<String> marketConditions,
            ArrayList<String> submarkets,
            ArrayList<String> industries,
            boolean withJunkAndChatter,
            boolean pirateMode) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", newMarket.getFaction().getTariffFraction());

        if (submarkets != null) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        if (marketConditions != null) {
            for (String condition : marketConditions) {
                newMarket.addCondition(condition);
            }
        }
        if (industries != null) {
            for (String industry : industries) {
                newMarket.addIndustry(industry);
            }

        }

        if (connectedEntities != null) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, withJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (connectedEntities != null) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        if (pirateMode) {
            newMarket.setEconGroup(newMarket.getId());
            newMarket.setHidden(true);
            primaryEntity.setSensorProfile(1f);
            primaryEntity.setDiscoverable(true);
            primaryEntity.getDetectedRangeMod().modifyFlat("gen", 5000f);
            newMarket.getMemoryWithoutUpdate().set(DecivTracker.NO_DECIV_KEY, true);
        } else {
            for (MarketConditionAPI mc : newMarket.getConditions()) {
                mc.setSurveyed(true);
            }
            newMarket.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        }

        newMarket.reapplyIndustries();
        return newMarket;
    }
}