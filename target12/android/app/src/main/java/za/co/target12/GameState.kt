package za.co.target12

import androidx.compose.runtime.mutableLongStateOf
import za.co.target12.GameConstants.BG
import za.co.target12.GameConstants.INTRO_AUTO_ADVANCE
import za.co.target12.GameConstants.JOYSTICK_MAX_SPEED
import za.co.target12.GameConstants.JOYSTICK_RADIUS
import za.co.target12.GameConstants.LG
import za.co.target12.GameConstants.OG
import za.co.target12.GameConstants.RG
import za.co.target12.GameConstants.RONTES
import za.co.target12.GameConstants.SCORECARD_COUNTDOWN_START
import za.co.target12.audio.GameAudio
import za.co.target12.input.TouchInputState
import za.co.target12.physics.BreathingPhysics
import za.co.target12.physics.DriftPhysics
import za.co.target12.physics.HeartbeatPhysics
import za.co.target12.scoring.ScoringEngine

enum class GameScreen {
    INTRO, SHOOTING, HELP, INPUT_NAME, INPUT_TEAM, INPUT_COMP, RESULTS, SCORECARD
}

class GameState {
    // Compose recomposition trigger
    val frameCounter = mutableLongStateOf(0L)

    var screen = GameScreen.INTRO
    var stateEntryTime = 0L

    // Player data (persists across restarts)
    var naam = ""
    var span = ""
    var komp = ""

    // Scoring
    var telling = 0       // score
    var aantal = 1        // current shot (1-based)
    val shotX = FloatArray(RONTES + 1)
    val shotY = FloatArray(RONTES + 1)

    // Aim center
    var cx = 320f
    var cy = 220f

    // Physics
    val drift = DriftPhysics()
    val heartbeat = HeartbeatPhysics()
    val breathing = BreathingPhysics()

    // Touch
    val touch = TouchInputState()

    // Audio
    val audio = GameAudio()

    // Muzzle flash
    var flashAlpha = 0f
    var flashX = 0f
    var flashY = 0f

    // Intro animation
    var introStep = 0
    var introTimer = 0L

    // Scorecard countdown
    var scorecardCountdown = SCORECARD_COUNTDOWN_START
    var scorecardTimer = 0L

    // Sight position (computed each frame)
    var sightX = 320f
    var sightY = 220f

    fun initAudio() {
        audio.init()
    }

    fun releaseAudio() {
        audio.release()
    }

    fun update(dt: Float, now: Long) {
        when (screen) {
            GameScreen.INTRO -> updateIntro(now)
            GameScreen.SHOOTING, GameScreen.INPUT_NAME, GameScreen.INPUT_TEAM, GameScreen.INPUT_COMP -> updateShooting(dt, now)
            GameScreen.SCORECARD -> updateScorecard(now)
            else -> {}
        }

        // Flash decay
        if (flashAlpha > 0f) {
            flashAlpha -= dt / 100f
            if (flashAlpha < 0f) flashAlpha = 0f
        }

        frameCounter.longValue++
    }

    private fun updateIntro(now: Long) {
        val elapsed = now - introTimer
        introStep = when {
            elapsed >= 3500L -> 3
            elapsed >= 2500L -> 2
            elapsed >= 1500L -> 1
            else -> 0
        }
        // Play fire sounds at demo shot times
        for (i in GameConstants.DEMO_SHOTS.indices) {
            val shotTime = GameConstants.DEMO_SHOTS[i].first
            val prevElapsed = elapsed - 16 // approximate one frame ago
            if (prevElapsed < shotTime && elapsed >= shotTime) {
                audio.playFire()
            }
        }
        if (elapsed >= INTRO_AUTO_ADVANCE) {
            resetGame()
            screen = GameScreen.SHOOTING
        }
    }

    private fun updateShooting(dt: Float, now: Long) {
        // Process touch events
        if (touch.fireJustPressed && screen == GameScreen.SHOOTING) {
            fire(now)
        }
        if (touch.breathJustPressed && !breathing.holding) {
            breathing.startHold(now)
        }
        if (touch.breathJustReleased && breathing.holding) {
            breathing.releaseHold(now)
        }
        touch.consumeEvents()

        // Apply joystick
        if (touch.joystickPointerId != null) {
            cx += (touch.joystickDx / JOYSTICK_RADIUS) * JOYSTICK_MAX_SPEED
            cy += (touch.joystickDy / JOYSTICK_RADIUS) * JOYSTICK_MAX_SPEED
            clampCenter()
        }

        // Update physics
        breathing.update(dt, now, heartbeat)
        val heartDy = heartbeat.update(dt, breathing.stress)
        drift.update(breathing.stress, heartDy)

        // Compute sight position
        sightX = cx + drift.ox + breathing.currentDx
        sightY = cy + drift.oy + breathing.currentDy

        // Bounce drift if sight out of bounds
        if (drift.bounceIfNeeded(sightX, sightY, BG, OG, LG, RG)) {
            audio.playBump()
        }

        // Recompute after bounce
        sightX = cx + drift.ox + breathing.currentDx
        sightY = cy + drift.oy + breathing.currentDy
    }

    private fun updateScorecard(now: Long) {
        val elapsed = now - scorecardTimer
        scorecardCountdown = SCORECARD_COUNTDOWN_START - (elapsed / 1000).toInt()
        if (scorecardCountdown < 0) {
            screen = GameScreen.RESULTS
        }
    }

    fun fire(@Suppress("UNUSED_PARAMETER") now: Long) {
        if (aantal > RONTES) return
        shotX[aantal] = sightX
        shotY[aantal] = sightY
        audio.playFire()
        flashAlpha = 1f
        flashX = sightX
        flashY = sightY
        aantal++
        telling = ScoringEngine.calculateScore(shotX, shotY, aantal - 1)
        if (aantal > RONTES || ScoringEngine.countScoringShots(shotX, shotY, aantal) >= 10) {
            screen = GameScreen.RESULTS
        }
    }

    fun clampCenter(): Boolean {
        var bounced = false
        if (cx >= RG) { cx = RG - 1; bounced = true }
        if (cx <= LG) { cx = LG + 1; bounced = true }
        if (cy >= OG) { cy = OG - 1; bounced = true }
        if (cy <= BG) { cy = BG + 1; bounced = true }
        if (bounced) audio.playBoundary()
        return bounced
    }

    fun resetGame() {
        aantal = 1
        telling = 0
        cx = 320f
        cy = 220f
        drift.reset()
        breathing.reset()
        heartbeat.resetToResting()
        flashAlpha = 0f
        for (i in shotX.indices) { shotX[i] = 0f; shotY[i] = 0f }
    }

    fun startIntro(now: Long) {
        screen = GameScreen.INTRO
        introStep = 0
        introTimer = now
    }

    fun viewScorecard(now: Long) {
        screen = GameScreen.SCORECARD
        scorecardCountdown = SCORECARD_COUNTDOWN_START
        scorecardTimer = now
    }

    fun shootAgain() {
        resetGame()
        screen = GameScreen.SHOOTING
    }

    fun exitToIntro(now: Long) {
        resetGame()
        startIntro(now)
    }
}
