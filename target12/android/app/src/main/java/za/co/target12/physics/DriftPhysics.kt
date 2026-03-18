package za.co.target12.physics

import za.co.target12.GameConstants.DRIFT_ACCEL
import za.co.target12.GameConstants.DRIFT_DRAG
import za.co.target12.GameConstants.DRIFT_MAX_SPEED
import za.co.target12.GameConstants.DRIFT_SPRING
import kotlin.math.sqrt
import kotlin.random.Random

class DriftPhysics {
    var ox = 0f
    var oy = 0f
    var vx = 0f
    var vy = 0f

    fun update(breathStress: Float, heartDy: Float) {
        // Random acceleration
        vx += (Random.nextFloat() - 0.5f) * DRIFT_ACCEL * 2f
        vy += (Random.nextFloat() - 0.5f) * DRIFT_ACCEL * 2f

        // Spring force
        vx -= ox * DRIFT_SPRING
        vy -= oy * DRIFT_SPRING

        // Speed clamp — stress increases max speed
        val maxSpeed = DRIFT_MAX_SPEED * (1f + breathStress * 2f)
        val speed = sqrt(vx * vx + vy * vy)
        if (speed > maxSpeed) {
            vx = (vx / speed) * maxSpeed
            vy = (vy / speed) * maxSpeed
        }

        // Drag
        vx *= DRIFT_DRAG
        vy *= DRIFT_DRAG

        // Apply velocity (heartbeat added to Y)
        ox += vx
        oy += vy + heartDy
    }

    fun bounceIfNeeded(sightX: Float, sightY: Float, bg: Float, og: Float, lg: Float, rg: Float): Boolean {
        var bounced = false
        if (sightX >= rg || sightX <= lg) {
            vx = -vx
            if (sightX >= rg) ox = rg - 1f - (sightX - ox)  // adjust offset
            if (sightX <= lg) ox = lg + 1f - (sightX - ox)
            bounced = true
        }
        if (sightY >= og || sightY <= bg) {
            vy = -vy
            if (sightY >= og) oy = og - 1f - (sightY - oy)
            if (sightY <= bg) oy = bg + 1f - (sightY - oy)
            bounced = true
        }
        return bounced
    }

    fun reset() {
        ox = 0f; oy = 0f; vx = 0f; vy = 0f
    }
}
