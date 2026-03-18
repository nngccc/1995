package za.co.target12.physics

import za.co.target12.GameConstants.BREATH_AMPLITUDE_X
import za.co.target12.GameConstants.BREATH_AMPLITUDE_Y
import za.co.target12.GameConstants.BREATH_HOLD_MAX
import za.co.target12.GameConstants.BREATH_PERIOD
import za.co.target12.GameConstants.BREATH_RECOVERY_TIME
import za.co.target12.GameConstants.RESTING_BPM
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class BreathingPhysics {
    var phase = 0f
    var holding = false
    var holdStart = 0L
    var holdPhase = 0f
    var recovering = false
    var recoveryStart = 0L
    var stress = 0f

    var currentDx = 0f
    var currentDy = 0f

    private var holdStartDx = 0f
    private var holdStartDy = 0f
    private var holdRestDx = 0f
    private var holdRestDy = 0f
    private var holdQuality = 0f

    fun startHold(now: Long) {
        holding = true
        holdStart = now
        holdPhase = (phase % BREATH_PERIOD) / BREATH_PERIOD
        val bw = waveform(holdPhase)
        holdStartDx = bw.first
        holdStartDy = bw.second
        holdQuality = 1f - min(abs(holdPhase - 0.45f) / 0.45f, 1f)
        holdRestDx = holdStartDx * 0.9f
        holdRestDy = holdStartDy * (0.7f + 0.3f * holdQuality)
    }

    fun releaseHold(now: Long) {
        holding = false
        recovering = true
        recoveryStart = now
    }

    fun update(dt: Float, now: Long, heartbeat: HeartbeatPhysics) {
        phase += dt

        if (holding) {
            val holdElapsed = (now - holdStart).toFloat()
            if (holdElapsed > BREATH_HOLD_MAX) {
                stress = min(1f, (holdElapsed - BREATH_HOLD_MAX) / 2000f)
                val hrStress = max(0f, (stress - 0.25f) / 0.75f)
                heartbeat.bpmTarget = RESTING_BPM + hrStress * 40f
            } else {
                stress = 0f
            }

            // Settle from start to rest over 500ms
            val settleProgress = min(1f, holdElapsed / 500f)
            val eased = settleProgress * settleProgress * (3f - 2f * settleProgress)
            currentDx = holdStartDx + (holdRestDx - holdStartDx) * eased
            currentDy = holdStartDy + (holdRestDy - holdStartDy) * eased

            // Wobble when stressed
            if (stress > 0f) {
                val freq = (now / 1000.0 * 2.0 * PI * 2.0).toFloat()
                val amp = stress * 4f
                currentDx += sin(freq) * amp
                currentDy += (cos(freq * 0.7f) * amp * 1.2f).toFloat()
            }
        } else if (recovering) {
            val recElapsed = (now - recoveryStart).toFloat()
            if (recElapsed < 400f) {
                // Gasp phase
                val gaspT = recElapsed / 400f
                currentDy = 8f * sin(gaspT * PI.toFloat())
                currentDx = 0f
            } else {
                val adjustedElapsed = recElapsed - 400f
                val recProgress = min(1f, adjustedElapsed / BREATH_RECOVERY_TIME)
                val ampMul = 1.5f - 0.5f * recProgress
                stress *= (1f - recProgress)
                val breathT = (phase % BREATH_PERIOD) / BREATH_PERIOD
                val bw = waveform(breathT)
                currentDx = bw.first * ampMul
                currentDy = bw.second * ampMul
                if (recProgress >= 1f) {
                    recovering = false
                    stress = 0f
                }
            }
        } else {
            // Normal breathing
            val breathT = (phase % BREATH_PERIOD) / BREATH_PERIOD
            val bw = waveform(breathT)
            currentDx = bw.first
            currentDy = bw.second
        }

        // During normal breathing, target resting rate
        if (!holding) {
            heartbeat.bpmTarget = RESTING_BPM
        }
    }

    fun reset() {
        phase = 0f
        holding = false
        holdStart = 0L
        holdPhase = 0f
        recovering = false
        recoveryStart = 0L
        stress = 0f
        currentDx = 0f
        currentDy = 0f
        holdStartDx = 0f
        holdStartDy = 0f
        holdRestDx = 0f
        holdRestDy = 0f
        holdQuality = 0f
    }

    companion object {
        fun waveform(t: Float): Pair<Float, Float> {
            val dx: Float
            val dy: Float
            if (t < 0.4f) {
                val e = (1f - cos(t / 0.4f * PI.toFloat())) / 2f
                dy = e * BREATH_AMPLITUDE_Y
                dx = e * BREATH_AMPLITUDE_X
            } else if (t < 0.5f) {
                dy = BREATH_AMPLITUDE_Y
                dx = BREATH_AMPLITUDE_X
            } else {
                val e = (1f - cos((t - 0.5f) / 0.5f * PI.toFloat())) / 2f
                dy = (1f - e) * BREATH_AMPLITUDE_Y
                dx = (1f - e) * BREATH_AMPLITUDE_X
            }
            return Pair(dx, dy)
        }
    }
}
