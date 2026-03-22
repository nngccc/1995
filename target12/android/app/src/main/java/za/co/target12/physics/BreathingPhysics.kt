package za.co.target12.physics

import za.co.target12.GameConstants
import za.co.target12.GameState
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

object BreathingPhysics {

    /** Normal breathing waveform. t in 0..1. Returns (dx, dy). */
    private fun waveform(t: Float): Pair<Float, Float> {
        return when {
            t < 0.4f -> {
                val e = (1f - cos(t / 0.4f * PI.toFloat())) / 2f
                Pair(e * GameConstants.BREATH_AMP_X, e * GameConstants.BREATH_AMP_Y)
            }
            t < 0.5f -> Pair(GameConstants.BREATH_AMP_X, GameConstants.BREATH_AMP_Y)
            else -> {
                val e = (1f - cos((t - 0.5f) / 0.5f * PI.toFloat())) / 2f
                Pair((1f - e) * GameConstants.BREATH_AMP_X, (1f - e) * GameConstants.BREATH_AMP_Y)
            }
        }
    }

    fun startHold(state: GameState, currentTimeMs: Long) {
        if (state.breathHolding) return
        state.breathHolding = true
        state.breathRecovering = false
        state.breathHoldStart = currentTimeMs
        val t = (state.breathPhase % GameConstants.BREATH_PERIOD) / GameConstants.BREATH_PERIOD
        state.breathHoldPhase = t
        val (dx, dy) = waveform(t)
        state.breathHoldStartDx = dx
        state.breathHoldStartDy = dy
        state.breathHoldQuality = 1f - min(abs(t - 0.45f) / 0.45f, 1f)
        state.breathHoldRestDx = dx * 0.9f
        state.breathHoldRestDy = dy * (0.7f + 0.3f * state.breathHoldQuality)
    }

    fun releaseHold(state: GameState, currentTimeMs: Long) {
        if (!state.breathHolding) return
        state.breathHolding = false
        state.breathRecovering = true
        state.breathRecoverStart = currentTimeMs
        state.heartBPMTarget = GameConstants.HEART_BPM_DEFAULT
    }

    fun update(state: GameState, dt: Float, currentTimeMs: Long) {
        if (state.breathHolding) {
            val elapsed = (currentTimeMs - state.breathHoldStart).toFloat()

            // Auto-release after 10 seconds
            if (elapsed > GameConstants.BREATH_HOLD_AUTO_RELEASE) {
                releaseHold(state, currentTimeMs)
                return
            }

            // Settle phase (first 500ms)
            val settleProgress = min(1f, elapsed / 500f)
            val eased = settleProgress * settleProgress * (3f - 2f * settleProgress)
            state.breathDx = state.breathHoldStartDx + (state.breathHoldRestDx - state.breathHoldStartDx) * eased
            state.breathDy = state.breathHoldStartDy + (state.breathHoldRestDy - state.breathHoldStartDy) * eased

            // Over-hold stress
            if (elapsed > GameConstants.BREATH_HOLD_MAX) {
                state.breathStress = min(1f, (elapsed - GameConstants.BREATH_HOLD_MAX) / 2000f)

                // Slow circular wobble
                val t = currentTimeMs / 1000f
                val freq = t * 2f * PI.toFloat() * 2f
                state.breathDx += sin(freq) * state.breathStress * 4f
                state.breathDy += cos(freq * 0.7f) * state.breathStress * 4f * 1.2f

                // HR target with delayed onset
                val hrStress = max(0f, (state.breathStress - 0.25f) / 0.75f)
                state.heartBPMTarget = 60f + hrStress * 40f
            }
        } else if (state.breathRecovering) {
            val elapsed = (currentTimeMs - state.breathRecoverStart).toFloat()

            if (elapsed < GameConstants.BREATH_GASP_DURATION) {
                // Gasp phase
                val gaspT = elapsed / GameConstants.BREATH_GASP_DURATION
                state.breathDy = 8f * sin(gaspT * PI.toFloat())
                state.breathDx = 0f
            } else {
                // Exaggerated breathing
                val recProgress = min(1f, (elapsed - GameConstants.BREATH_GASP_DURATION) / GameConstants.BREATH_RECOVERY_TIME)
                val ampMul = 1.5f - 0.5f * recProgress
                state.breathStress *= (1f - recProgress)

                state.breathPhase += dt
                val t = (state.breathPhase % GameConstants.BREATH_PERIOD) / GameConstants.BREATH_PERIOD
                val (dx, dy) = waveform(t)
                state.breathDx = dx * ampMul
                state.breathDy = dy * ampMul

                if (recProgress >= 1f) {
                    state.breathRecovering = false
                    state.breathStress = 0f
                }
            }
        } else {
            // Normal breathing
            state.breathPhase += dt
            val t = (state.breathPhase % GameConstants.BREATH_PERIOD) / GameConstants.BREATH_PERIOD
            val (dx, dy) = waveform(t)
            state.breathDx = dx
            state.breathDy = dy
        }
    }
}
