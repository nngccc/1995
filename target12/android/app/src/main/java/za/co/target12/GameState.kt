package za.co.target12

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

enum class GamePhase {
    INTRO, SHOOTING, HELP, INPUT_NAME, INPUT_TEAM, INPUT_COMP, RESULTS, SCORECARD
}

data class Shot(val x: Float, val y: Float)

class GameState {
    var phase by mutableStateOf(GamePhase.INTRO)

    // Center point (player-controlled)
    var cx by mutableFloatStateOf(GameConstants.INITIAL_CX)
    var cy by mutableFloatStateOf(GameConstants.INITIAL_CY)

    // Drift
    var driftOx = 0f
    var driftOy = 0f
    var driftVx = 0f
    var driftVy = 0f

    // Heartbeat
    var heartBPM = GameConstants.HEART_BPM_DEFAULT
    var heartBPMTarget = GameConstants.HEART_BPM_DEFAULT
    var heartPhase = 0f   // ms accumulated
    var heartDy = 0f

    // Breathing
    var breathPhase = 0f  // ms accumulated
    var breathDx = 0f
    var breathDy = 0f
    var breathHolding = false
    var breathHoldStart = 0L
    var breathHoldPhase = 0f
    var breathHoldStartDx = 0f
    var breathHoldStartDy = 0f
    var breathHoldRestDx = 0f
    var breathHoldRestDy = 0f
    var breathHoldQuality = 0f
    var breathStress = 0f
    var breathRecovering = false
    var breathRecoverStart = 0L

    // Wind (session-level, initialized at game start)
    var baseWindStrength = 0f
    var baseWindAngle = 0f
    var nextGustTime = 0L
    var gustStartTime: Long? = null
    var gustEndTime: Long? = null
    var gustStrength = 0f
    var gustDirectionDelta = 0f
    var gustDuration = 0L

    // ECG
    val ecgBuffer = FloatArray(GameConstants.ECG_BUFFER_SIZE)
    var ecgIndex = 0
    var ecgBeatPhase = 0f
    var ecgAccumMs = 0f

    // Shots
    val shots = mutableStateListOf<Shot>()
    var scoringShots by mutableIntStateOf(0)
    var totalShots by mutableIntStateOf(0)
    var score by mutableIntStateOf(0)

    // Flash
    var flashAlpha by mutableFloatStateOf(0f)
    var flashX = 0f
    var flashY = 0f

    // Player data (persists across reset)
    var playerName by mutableStateOf("")
    var playerTeam by mutableStateOf("")
    var playerComp by mutableStateOf("")

    // Intro
    var introStartTime = 0L
    var demoShotsFired = 0
    val demoShots = mutableStateListOf<Shot>()

    // Scorecard countdown
    var scorecardCountdown by mutableIntStateOf(GameConstants.SCORECARD_COUNTDOWN)
    var scorecardLastTick = 0L

    // Frame timing
    var lastFrameTime = 0L

    val sightX: Float get() = cx + driftOx + breathDx
    val sightY: Float get() = cy + driftOy + breathDy

    fun resetGame() {
        phase = GamePhase.SHOOTING
        cx = GameConstants.INITIAL_CX
        cy = GameConstants.INITIAL_CY
        driftOx = 0f; driftOy = 0f; driftVx = 0f; driftVy = 0f
        heartBPM = GameConstants.HEART_BPM_DEFAULT
        heartBPMTarget = GameConstants.HEART_BPM_DEFAULT
        heartPhase = 0f; heartDy = 0f
        breathPhase = 0f; breathDx = 0f; breathDy = 0f
        breathHolding = false; breathStress = 0f
        breathRecovering = false
        breathHoldQuality = 0f
        ecgBuffer.fill(0f); ecgIndex = 0; ecgBeatPhase = 0f; ecgAccumMs = 0f
        shots.clear()
        scoringShots = 0; totalShots = 0; score = 0
        flashAlpha = 0f
        // Reset wind gust state (keep base wind for session)
        nextGustTime = System.currentTimeMillis() + (Math.random() * (GameConstants.WIND_MAX_INTERVAL - GameConstants.WIND_MIN_INTERVAL)).toLong() + GameConstants.WIND_MIN_INTERVAL
        gustStartTime = null
        gustEndTime = null
    }
}
