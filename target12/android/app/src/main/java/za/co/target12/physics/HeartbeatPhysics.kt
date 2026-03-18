package za.co.target12.physics

import za.co.target12.GameConstants.ECG_CHART_TIMESPAN
import za.co.target12.GameConstants.ECG_CHART_WIDTH
import za.co.target12.GameConstants.HEART_AMPLITUDE
import za.co.target12.GameConstants.RESTING_BPM
import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sin

class HeartbeatPhysics {
    var bpm = RESTING_BPM
    var bpmTarget = RESTING_BPM
    var phase = 0f  // accumulated ms

    // ECG display buffer
    val ecgHistory = FloatArray(ECG_CHART_WIDTH)
    var ecgHead = 0
    var ecgAccumMs = 0f
    var ecgBeatPhase = 0f

    fun update(dt: Float, breathStress: Float): Float {
        // Heart rate chase
        val rate = if (bpmTarget > bpm) 0.0004f else 0.0002f
        bpm += (bpmTarget - bpm) * rate * dt

        phase += dt

        // Update ECG buffer
        ecgAccumMs += dt
        val sampleInterval = ECG_CHART_TIMESPAN / ECG_CHART_WIDTH
        while (ecgAccumMs >= sampleInterval) {
            ecgAccumMs -= sampleInterval
            ecgBeatPhase += sampleInterval / (60000f / bpm)
            ecgBeatPhase -= floor(ecgBeatPhase)
            ecgHistory[ecgHead] = ecgWaveform(ecgBeatPhase)
            ecgHead = (ecgHead + 1) % ECG_CHART_WIDTH
        }

        // Compute pulse displacement
        val period = 60000f / bpm
        val beatPos = (phase % period) / period

        var pulse = 0f
        if (beatPos < 0.08f) {
            pulse = sin(beatPos / 0.08f * PI.toFloat()).toFloat()
        } else if (beatPos >= 0.15f && beatPos < 0.22f) {
            pulse = 0.4f * sin((beatPos - 0.15f) / 0.07f * PI.toFloat()).toFloat()
        }

        val amp = HEART_AMPLITUDE * (1f + breathStress * 0.8f)
        return -pulse * amp
    }

    fun resetToResting() {
        bpm = RESTING_BPM
        bpmTarget = RESTING_BPM
    }

    companion object {
        fun ecgWaveform(t: Float): Float {
            if (t < 0.05f) return 0f
            if (t < 0.08f) return 0.15f * sin((t - 0.05f) / 0.03f * PI.toFloat()).toFloat()
            if (t < 0.12f) return 0f
            if (t < 0.14f) return -0.15f
            if (t < 0.18f) return sin((t - 0.14f) / 0.04f * PI.toFloat()).toFloat()
            if (t < 0.21f) return -0.25f
            if (t < 0.25f) return -0.25f * (1f - (t - 0.21f) / 0.04f)
            if (t < 0.35f) return 0f
            if (t < 0.45f) return 0.2f * sin((t - 0.35f) / 0.10f * PI.toFloat()).toFloat()
            return 0f
        }
    }
}
