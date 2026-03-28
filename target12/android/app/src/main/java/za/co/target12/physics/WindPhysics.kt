package za.co.target12.physics

import za.co.target12.GameConstants
import za.co.target12.GameState
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object WindPhysics {
    fun updateWindGust(state: GameState, now: Long) {
        // Check if it's time to start a new gust
        if (now >= state.nextGustTime && state.gustStartTime == null) {
            state.gustStartTime = now
            state.gustDuration = (Math.random() * (GameConstants.WIND_GUST_MAX_DURATION - GameConstants.WIND_GUST_MIN_DURATION)).toLong() + GameConstants.WIND_GUST_MIN_DURATION

            // CRITICAL: Set gustEndTime when gust starts
            state.gustEndTime = state.gustStartTime!! + state.gustDuration

            // Gust amplitude based on base wind strength
            state.gustStrength = when {
                state.baseWindStrength <= 3 -> {
                    (Math.random() * (2.2f - 1.8f) + 1.8f).toFloat()
                }
                state.baseWindStrength <= 6 -> {
                    (Math.random() * (1.8f - 1.5f) + 1.5f).toFloat()
                }
                else -> {
                    (Math.random() * (1.5f - 1.3f) + 1.3f).toFloat()
                }
            }

            // Direction variance ±15°
            state.gustDirectionDelta = ((Math.random() - 0.5) * 30).toFloat()

            // Schedule next gust
            val delayToNextGust = (Math.random() * (GameConstants.WIND_MAX_INTERVAL - GameConstants.WIND_MIN_INTERVAL)).toLong() + GameConstants.WIND_MIN_INTERVAL
            state.nextGustTime = state.gustEndTime!! + delayToNextGust
        }

        // Check if gust should end
        if (state.gustStartTime != null && now >= state.gustEndTime!!) {
            state.gustStartTime = null
            state.gustEndTime = null
        }
    }

    fun calculateWindDeflection(state: GameState, sx: Float, sy: Float, now: Long): Pair<Float, Float> {
        // Get effective wind (base + gust if active)
        var effectiveStrength = state.baseWindStrength
        var effectiveAngle = state.baseWindAngle

        if (state.gustStartTime != null) {
            val gustElapsedMs = now - state.gustStartTime!!
            val gustElapsedSec = gustElapsedMs.toFloat() / 1000f
            val gustProgress = (gustElapsedMs.toFloat() / state.gustDuration.toFloat()).coerceAtMost(1f)

            // Smooth easing: fade in first 200ms, fade out last 200ms
            var gustMultiplier = 1f
            if (gustProgress < GameConstants.WIND_GUST_FADE_TIME / state.gustDuration) {
                val fade = (gustProgress * state.gustDuration) / GameConstants.WIND_GUST_FADE_TIME
                gustMultiplier = fade * fade * (3 - 2 * fade) // Cubic smoothstep
            } else if (gustProgress > 1 - (GameConstants.WIND_GUST_FADE_TIME / state.gustDuration)) {
                val fade = ((1 - gustProgress) * state.gustDuration) / GameConstants.WIND_GUST_FADE_TIME
                gustMultiplier = fade * fade * (3 - 2 * fade)
            }

            // Continuous strength oscillation (±30% variation @ 0.1 Hz)
            val oscillation = kotlin.math.sin(gustElapsedSec * 2 * Math.PI * 0.1f).toFloat() * 0.3f
            val instantGustStrength = state.gustStrength * (1 + oscillation)

            effectiveStrength = state.baseWindStrength * (1 + (instantGustStrength - 1) * gustMultiplier)
            effectiveAngle = state.baseWindAngle + state.gustDirectionDelta * gustMultiplier
        }

        // Calculate deflection based on distance from center
        val dist = sqrt((sx - 320) * (sx - 320) + (sy - 220) * (sy - 220))
        val distanceFactor = 1 + (dist / 500)

        val angleRad = Math.toRadians(effectiveAngle.toDouble())
        val deflectionX = (effectiveStrength * sin(angleRad) * distanceFactor).toFloat()
        val deflectionY = (-effectiveStrength * cos(angleRad) * distanceFactor).toFloat()

        return Pair(deflectionX, deflectionY)
    }
}
