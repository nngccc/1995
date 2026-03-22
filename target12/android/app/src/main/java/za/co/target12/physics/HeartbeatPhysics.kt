package za.co.target12.physics

import za.co.target12.GameConstants
import za.co.target12.GameState
import kotlin.math.PI
import kotlin.math.sin

object HeartbeatPhysics {

    /** Update heartbeat pulse and BPM chase. dt in ms. */
    fun update(state: GameState, dt: Float) {
        // BPM chase (asymmetric)
        val rate = if (state.heartBPMTarget > state.heartBPM)
            GameConstants.HEART_CHASE_UP else GameConstants.HEART_CHASE_DOWN
        state.heartBPM += (state.heartBPMTarget - state.heartBPM) * rate * dt

        // Phase accumulation
        val period = 60000f / state.heartBPM
        state.heartPhase += dt
        if (state.heartPhase >= period) state.heartPhase -= period

        val beatPos = state.heartPhase / period  // 0..1

        // Double-bump waveform
        val pulse = when {
            beatPos < 0.08f -> sin(beatPos / 0.08f * PI.toFloat())
            beatPos in 0.15f..0.22f -> 0.4f * sin((beatPos - 0.15f) / 0.07f * PI.toFloat())
            else -> 0f
        }

        val amplitude = GameConstants.HEART_AMPLITUDE * (1f + state.breathStress * 0.8f)
        state.heartDy = -pulse * amplitude

        // ECG sampling
        state.ecgAccumMs += dt
        while (state.ecgAccumMs >= GameConstants.ECG_SAMPLE_INTERVAL) {
            state.ecgAccumMs -= GameConstants.ECG_SAMPLE_INTERVAL
            val samplePeriod = 60000f / state.heartBPM
            state.ecgBeatPhase += GameConstants.ECG_SAMPLE_INTERVAL / samplePeriod
            if (state.ecgBeatPhase >= 1f) state.ecgBeatPhase -= 1f
            state.ecgBuffer[state.ecgIndex] = ecgWaveform(state.ecgBeatPhase)
            state.ecgIndex = (state.ecgIndex + 1) % GameConstants.ECG_BUFFER_SIZE
        }
    }

    /** ECG waveform for display: P/QRS/T complex */
    fun ecgWaveform(t: Float): Float = when {
        t < 0.05f -> 0f
        t < 0.08f -> 0.15f * sin((t - 0.05f) / 0.03f * PI.toFloat())
        t < 0.12f -> 0f
        t < 0.14f -> -0.15f
        t < 0.18f -> sin((t - 0.14f) / 0.04f * PI.toFloat())
        t < 0.21f -> -0.25f
        t < 0.25f -> -0.25f * (1f - (t - 0.21f) / 0.04f)
        t < 0.35f -> 0f
        t < 0.45f -> 0.2f * sin((t - 0.35f) / 0.10f * PI.toFloat())
        else -> 0f
    }
}
