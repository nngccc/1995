package za.co.target12.physics

import za.co.target12.GameConstants
import za.co.target12.GameState
import kotlin.math.sqrt
import kotlin.random.Random

object DriftPhysics {
    fun update(state: GameState) {
        // 1. Random acceleration
        state.driftVx += (Random.nextFloat() - 0.5f) * GameConstants.DRIFT_ACCEL * 2f
        state.driftVy += (Random.nextFloat() - 0.5f) * GameConstants.DRIFT_ACCEL * 2f

        // 2. Spring force
        state.driftVx -= state.driftOx * GameConstants.DRIFT_SPRING
        state.driftVy -= state.driftOy * GameConstants.DRIFT_SPRING

        // 3. Speed clamp
        val maxSpeed = GameConstants.DRIFT_MAX_SPEED * (1f + state.breathStress * 2f)
        val speed = sqrt(state.driftVx * state.driftVx + state.driftVy * state.driftVy)
        if (speed > maxSpeed) {
            val scale = maxSpeed / speed
            state.driftVx *= scale
            state.driftVy *= scale
        }

        // 4. Drag
        state.driftVx *= GameConstants.DRIFT_DRAG
        state.driftVy *= GameConstants.DRIFT_DRAG

        // 5. Apply velocity (heartbeat added to Y only)
        state.driftOx += state.driftVx
        state.driftOy += state.driftVy + state.heartDy

        // Boundary clamp with reflection
        val sx = state.cx + state.driftOx + state.breathDx
        val sy = state.cy + state.driftOy + state.breathDy

        if (sx >= GameConstants.RG) {
            state.driftOx = GameConstants.RG - 1f - state.cx - state.breathDx
            state.driftVx = -state.driftVx
        }
        if (sx <= GameConstants.LG) {
            state.driftOx = GameConstants.LG + 1f - state.cx - state.breathDx
            state.driftVx = -state.driftVx
        }
        if (sy >= GameConstants.OG) {
            state.driftOy = GameConstants.OG - 1f - state.cy - state.breathDy
            state.driftVy = -state.driftVy
        }
        if (sy <= GameConstants.BG) {
            state.driftOy = GameConstants.BG + 1f - state.cy - state.breathDy
            state.driftVy = -state.driftVy
        }
    }
}
