package ktastic7.unsc.corecontinued.weapons;

import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

/**
 * Frame-rate-independent spool logic for the M606 Goalkeeper PD chaingun.
 *
 * The original script reduced its refire scalar by 0.13 every rendered frame
 * and held spool state for 30 frames. That made behavior vary with FPS and it
 * accidentally used the ship's Energy RoF multiplier even though the weapon is
 * ballistic. This implementation preserves the old ~60 FPS timing baseline:
 * 0.13/frame becomes 7.8/second and 30 frames becomes a 0.5 second spool hold.
 */
public class UNSC_GoalkeeperSpool implements EveryFrameWeaponEffectPlugin {

    private static final float REFIRE_REDUCTION_PER_SECOND = 7.8f;
    private static final float UNSPOOL_DELAY_SECONDS = 0.5f;
    private static final float MIN_COOLDOWN_SECONDS = 0.025f;

    private float refire = 1f;
    private float unspoolTimer = 0f;
    private float baseRefire = 0f;
    private boolean initialized = false;
    private ShipAPI ship;

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (engine == null || engine.isPaused() || weapon == null) return;

        if (!initialized) {
            initialized = true;
            ship = weapon.getShip();
            baseRefire = weapon.getCooldown();
        }

        if (ship == null) return;

        if (weapon.isFiring()) {
            // Preserve the old behavior of staying fully spooled for roughly
            // half a second after the weapon stops firing.
            unspoolTimer = UNSPOOL_DELAY_SECONDS;

            if (weapon.getChargeLevel() >= 1f) {
                // Equivalent to the old 0.13-per-frame ramp at a 60 FPS baseline,
                // but now tied to elapsed combat time instead of rendered frames.
                refire = Math.max(0f, refire - REFIRE_REDUCTION_PER_SECOND * amount);

                float ballisticRoF = ship.getMutableStats().getBallisticRoFMult().computeMultMod();
                if (ballisticRoF <= 0f) ballisticRoF = 1f;

                float cooldown = Math.max(MIN_COOLDOWN_SECONDS, refire * baseRefire);
                weapon.setRemainingCooldownTo(cooldown / ballisticRoF);
            }
        } else if (unspoolTimer > 0f) {
            unspoolTimer = Math.max(0f, unspoolTimer - amount);
        } else {
            refire = 1f;
        }
    }
}
