package za.co.target12.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerId
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntSize
import kotlinx.coroutines.android.awaitFrame
import za.co.target12.*
import za.co.target12.audio.GameAudio
import za.co.target12.input.TouchInputState
import za.co.target12.physics.BreathingPhysics
import za.co.target12.physics.DriftPhysics
import za.co.target12.physics.HeartbeatPhysics
import za.co.target12.scoring.ScoringEngine
import za.co.target12.ui.rendering.*
import kotlin.math.sqrt

@Composable
fun GameScreen() {
    val state = remember { GameState() }
    val touchState = remember { TouchInputState() }
    var screenSize by remember { mutableStateOf(IntSize(1, 1)) }
    var frameCount by remember { mutableIntStateOf(0) }

    val scaleInfo = remember(screenSize) {
        CanvasScaler.computeScale(screenSize.width.toFloat(), screenSize.height.toFloat())
    }

    // Intro timer
    LaunchedEffect(state.phase) {
        if (state.phase == GamePhase.INTRO) {
            state.introStartTime = System.currentTimeMillis()
            state.demoShotsFired = 0
            state.demoShots.clear()
        }
    }

    // Main game loop
    LaunchedEffect(Unit) {
        state.lastFrameTime = System.nanoTime()
        while (true) {
            awaitFrame()
            val now = System.nanoTime()
            val dt = (now - state.lastFrameTime) / 1_000_000f
            state.lastFrameTime = now
            val currentTimeMs = System.currentTimeMillis()

            when (state.phase) {
                GamePhase.INTRO -> {
                    val elapsed = currentTimeMs - state.introStartTime
                    for (i in GameConstants.DEMO_SHOT_TIMES.indices) {
                        if (state.demoShotsFired <= i && elapsed >= GameConstants.DEMO_SHOT_TIMES[i]) {
                            state.demoShots.add(Shot(GameConstants.DEMO_SHOT_X[i], GameConstants.DEMO_SHOT_Y[i]))
                            state.demoShotsFired = i + 1
                            GameAudio.playFireSound()
                        }
                    }
                    if (elapsed >= GameConstants.INTRO_ADVANCE_MS) {
                        state.resetGame()
                    }
                }

                GamePhase.SHOOTING -> {
                    // Joystick movement
                    if (touchState.joystickActive) {
                        state.cx += (touchState.joystickDx / GameConstants.JOYSTICK_RADIUS) * GameConstants.JOYSTICK_MAX_SPEED
                        state.cy += (touchState.joystickDy / GameConstants.JOYSTICK_RADIUS) * GameConstants.JOYSTICK_MAX_SPEED
                        clampCenter(state)
                    }

                    // Pending fire from touch
                    if (touchState.firePending) {
                        touchState.firePending = false
                        fireShot(state, currentTimeMs, touchState)
                    }

                    // Breath hold from touch
                    if (touchState.breathPressed && !state.breathHolding) {
                        BreathingPhysics.startHold(state, currentTimeMs)
                    } else if (!touchState.breathPressed && state.breathHolding) {
                        BreathingPhysics.releaseHold(state, currentTimeMs)
                    }

                    // Physics
                    HeartbeatPhysics.update(state, dt)
                    BreathingPhysics.update(state, dt, currentTimeMs)
                    // Sync touch state if physics auto-released breath hold
                    if (!state.breathHolding && touchState.breathPressed) {
                        touchState.breathPressed = false
                    }
                    DriftPhysics.update(state)

                    // Flash decay
                    if (state.flashAlpha > 0f) {
                        state.flashAlpha -= dt / 100f
                        if (state.flashAlpha < 0f) state.flashAlpha = 0f
                    }
                }

                GamePhase.SCORECARD -> {
                    if (currentTimeMs - state.scorecardLastTick >= 1000L) {
                        state.scorecardCountdown--
                        state.scorecardLastTick = currentTimeMs
                        if (state.scorecardCountdown < 0) {
                            state.phase = GamePhase.RESULTS
                        }
                    }
                }

                GamePhase.INPUT_NAME, GamePhase.INPUT_TEAM, GamePhase.INPUT_COMP -> {
                    // Drift continues during input
                    HeartbeatPhysics.update(state, dt)
                    BreathingPhysics.update(state, dt, currentTimeMs)
                    DriftPhysics.update(state)
                }

                else -> { /* HELP, RESULTS: no physics */ }
            }

            frameCount++
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .onSizeChanged { screenSize = it }
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                // Single unified pointer handler — routes by zone
                .pointerInput(state.phase) {
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val currentTimeMs = System.currentTimeMillis()

                            for (change in event.changes) {
                                val x = change.position.x
                                val y = change.position.y
                                val id = change.id.value

                                when (state.phase) {
                                    GamePhase.SHOOTING -> {
                                        if (change.pressed) {
                                            if (scaleInfo.isLeftMargin(x)) {
                                                // Left margin: joystick (entire area)
                                                if (!touchState.joystickActive) {
                                                    touchState.joystickActive = true
                                                    touchState.joystickPointerId = id
                                                    touchState.joystickBaseX = x
                                                    touchState.joystickBaseY = y
                                                }
                                                if (touchState.joystickPointerId == id) {
                                                    touchState.joystickDx = x - touchState.joystickBaseX
                                                    touchState.joystickDy = y - touchState.joystickBaseY
                                                    val dist = sqrt(
                                                        touchState.joystickDx * touchState.joystickDx +
                                                        touchState.joystickDy * touchState.joystickDy
                                                    )
                                                    if (dist > GameConstants.JOYSTICK_RADIUS) {
                                                        val s = GameConstants.JOYSTICK_RADIUS / dist
                                                        touchState.joystickDx *= s
                                                        touchState.joystickDy *= s
                                                    }
                                                }
                                            } else if (scaleInfo.isRightMargin(x)) {
                                                // Right margin: upper = breath toggle, lower = fire
                                                val screenH = scaleInfo.scaledHeight + scaleInfo.offsetY * 2f
                                                if (y < screenH * 0.625f) {
                                                    // Breath hold toggle (on initial press only)
                                                    if (change.previousPressed != change.pressed) {
                                                        touchState.breathPressed = !touchState.breathPressed
                                                    }
                                                } else {
                                                    // Fire button
                                                    if (!touchState.firePressed) {
                                                        touchState.firePressed = true
                                                        touchState.firePointerId = id
                                                        touchState.firePending = true
                                                    }
                                                }
                                            } else {
                                                // Canvas area — fire shot on initial press
                                                if (change.previousPressed != change.pressed && change.pressed) {
                                                    fireShot(state, currentTimeMs)
                                                }
                                            }
                                        } else {
                                            // Released
                                            if (id == touchState.joystickPointerId) touchState.resetJoystick()
                                            if (id == touchState.firePointerId) touchState.releaseFire()
                                        }
                                    }

                                    GamePhase.INTRO -> {
                                        // Tap on canvas advances to shooting
                                        if (change.pressed && !change.previousPressed) {
                                            state.resetGame()
                                        }
                                    }

                                    GamePhase.HELP -> {
                                        if (change.pressed && !change.previousPressed) {
                                            state.phase = GamePhase.SHOOTING
                                        }
                                    }

                                    GamePhase.SCORECARD -> {
                                        if (change.pressed && !change.previousPressed) {
                                            state.phase = GamePhase.RESULTS
                                        }
                                    }

                                    GamePhase.RESULTS -> {
                                        if (change.pressed && !change.previousPressed && scaleInfo.isOnCanvas(x)) {
                                            val vy = scaleInfo.toCanvasY(y)
                                            when {
                                                vy in 315f..345f -> {
                                                    state.scorecardCountdown = GameConstants.SCORECARD_COUNTDOWN
                                                    state.scorecardLastTick = System.currentTimeMillis()
                                                    state.phase = GamePhase.SCORECARD
                                                }
                                                vy in 340f..370f -> state.resetGame()
                                                vy in 365f..395f -> state.phase = GamePhase.INTRO
                                            }
                                        }
                                    }

                                    else -> {}
                                }
                            }
                        }
                    }
                }
        ) {
            frameCount.let { _ ->
                val scale = scaleInfo.scale
                val ox = scaleInfo.offsetX
                val oy = scaleInfo.offsetY

                when (state.phase) {
                    GamePhase.INTRO -> {
                        IntroRenderer.draw(this, state, scale, ox, oy)
                    }

                    GamePhase.SHOOTING, GamePhase.INPUT_NAME, GamePhase.INPUT_TEAM, GamePhase.INPUT_COMP -> {
                        ScorecardRenderer.draw(this, state, scale, ox, oy)
                        CrosshairRenderer.draw(this, state, scale, ox, oy)
                        MuzzleFlashRenderer.draw(this, state, scale, ox, oy)
                        EcgChartRenderer.draw(this, state, scale, ox, oy)
                        if (state.phase == GamePhase.SHOOTING) {
                            TouchControlsRenderer.draw(this, touchState, scaleInfo)
                        }
                    }

                    GamePhase.HELP -> {
                        ScorecardRenderer.draw(this, state, scale, ox, oy)
                        CrosshairRenderer.draw(this, state, scale, ox, oy)
                        HelpRenderer.draw(this, scale, ox, oy)
                    }

                    GamePhase.RESULTS -> {
                        ScorecardRenderer.draw(this, state, scale, ox, oy)
                        ResultsRenderer.draw(this, state, scale, ox, oy)
                    }

                    GamePhase.SCORECARD -> {
                        ScorecardRenderer.draw(this, state, scale, ox, oy)
                        ScorecardOverlayRenderer.draw(this, state, scale, ox, oy)
                    }
                }
            }
        }

        // Input dialog overlay
        if (state.phase in listOf(GamePhase.INPUT_NAME, GamePhase.INPUT_TEAM, GamePhase.INPUT_COMP)) {
            InputDialog(state = state, onDone = { state.phase = GamePhase.SHOOTING })
        }
    }
}

private fun clampCenter(state: GameState) {
    var clamped = false
    if (state.cx >= GameConstants.RG) { state.cx = GameConstants.RG - 1f; clamped = true }
    if (state.cx <= GameConstants.LG) { state.cx = GameConstants.LG + 1f; clamped = true }
    if (state.cy >= GameConstants.OG) { state.cy = GameConstants.OG - 1f; clamped = true }
    if (state.cy <= GameConstants.BG) { state.cy = GameConstants.BG + 1f; clamped = true }
    if (clamped) GameAudio.playBoundarySound()
}

private fun fireShot(state: GameState, currentTimeMs: Long, touchState: TouchInputState? = null) {
    if (state.phase != GamePhase.SHOOTING) return
    state.flashX = state.sightX
    state.flashY = state.sightY
    state.flashAlpha = 1.0f
    GameAudio.playFireSound()
    // Auto-release breath hold on fire
    if (state.breathHolding) {
        BreathingPhysics.releaseHold(state, currentTimeMs)
        touchState?.releaseBreath()
    }
    if (ScoringEngine.processShot(state)) {
        state.phase = GamePhase.RESULTS
    }
}
