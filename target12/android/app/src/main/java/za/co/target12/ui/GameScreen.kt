package za.co.target12.ui

import android.view.MotionEvent
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import za.co.target12.GameConstants.CANVAS_H
import za.co.target12.GameConstants.CANVAS_W
import za.co.target12.GameScreen as GS
import za.co.target12.GameState
import za.co.target12.ui.rendering.CrosshairRenderer
import za.co.target12.ui.rendering.HelpRenderer
import za.co.target12.ui.rendering.IntroRenderer
import za.co.target12.ui.rendering.MuzzleFlashRenderer
import za.co.target12.ui.rendering.ResultsRenderer
import za.co.target12.ui.rendering.ScorecardOverlayRenderer
import za.co.target12.ui.rendering.ScorecardRenderer
import za.co.target12.ui.rendering.TouchControlsRenderer
import za.co.target12.ui.rendering.computeScale

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun GameScreen(gs: GameState) {
    // Game loop
    LaunchedEffect(Unit) {
        var lastFrameNanos = 0L
        while (true) {
            val frameTimeNanos = withFrameNanos { it }
            if (lastFrameNanos == 0L) {
                lastFrameNanos = frameTimeNanos
                continue
            }
            val dtMs = (frameTimeNanos - lastFrameNanos) / 1_000_000f
            lastFrameNanos = frameTimeNanos
            val now = System.currentTimeMillis()
            gs.update(dtMs.coerceAtMost(50f), now)
        }
    }

    // Back button handling
    BackHandler {
        val now = System.currentTimeMillis()
        when (gs.screen) {
            GS.SHOOTING -> gs.exitToIntro(now)
            GS.HELP -> gs.screen = GS.SHOOTING
            GS.RESULTS -> gs.exitToIntro(now)
            GS.SCORECARD -> gs.screen = GS.RESULTS
            GS.INPUT_NAME, GS.INPUT_TEAM, GS.INPUT_COMP -> gs.screen = GS.SHOOTING
            GS.INTRO -> {}
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center,
    ) {
        val density = LocalDensity.current
        val screenWidthPx = with(density) { maxWidth.toPx() }
        val screenHeightPx = with(density) { maxHeight.toPx() }
        val scaleInfo = computeScale(screenWidthPx, screenHeightPx)

        val canvasWidthDp = with(density) { (CANVAS_W * scaleInfo.scale).toDp() }
        val canvasHeightDp = with(density) { (CANVAS_H * scaleInfo.scale).toDp() }

        // Read frame counter to trigger recomposition
        val frame = gs.frameCounter.longValue

        Box(
            modifier = Modifier
                .width(canvasWidthDp)
                .height(canvasHeightDp)
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInteropFilter { event ->
                        handleTouchEvent(event, gs, scaleInfo.scale)
                        true
                    }
            ) {
                @Suppress("UNUSED_EXPRESSION")
                frame

                val nc = drawContext.canvas.nativeCanvas
                nc.save()
                nc.scale(scaleInfo.scale, scaleInfo.scale)

                when (gs.screen) {
                    GS.INTRO -> IntroRenderer.draw(nc, gs)

                    GS.SHOOTING, GS.INPUT_NAME, GS.INPUT_TEAM, GS.INPUT_COMP -> {
                        ScorecardRenderer.draw(this, gs)
                        CrosshairRenderer.draw(nc, gs.sightX, gs.sightY)
                        TouchControlsRenderer.draw(nc, gs.touch)
                        MuzzleFlashRenderer.draw(nc, gs.flashX, gs.flashY, gs.flashAlpha, CANVAS_W, CANVAS_H)
                    }

                    GS.HELP -> {
                        ScorecardRenderer.draw(this, gs)
                        CrosshairRenderer.draw(nc, gs.sightX, gs.sightY)
                        HelpRenderer.draw(nc)
                    }

                    GS.RESULTS -> {
                        ScorecardRenderer.draw(this, gs)
                        ResultsRenderer.draw(nc, gs)
                    }

                    GS.SCORECARD -> {
                        ScorecardRenderer.draw(this, gs)
                        ScorecardOverlayRenderer.draw(nc, gs.scorecardCountdown)
                    }
                }

                nc.restore()
            }

            // Input dialog overlays (rendered as Compose, on top of Canvas)
            when (gs.screen) {
                GS.INPUT_NAME -> InputDialog(
                    label = "ENTER YOUR NAME",
                    maxLength = 15,
                    onConfirm = { gs.naam = it; gs.screen = GS.SHOOTING },
                    onCancel = { gs.screen = GS.SHOOTING },
                )
                GS.INPUT_TEAM -> InputDialog(
                    label = "ENTER YOUR TEAM",
                    maxLength = 6,
                    onConfirm = { gs.span = it; gs.screen = GS.SHOOTING },
                    onCancel = { gs.screen = GS.SHOOTING },
                )
                GS.INPUT_COMP -> InputDialog(
                    label = "ENTER THE COMPETITION",
                    maxLength = 11,
                    onConfirm = { gs.komp = it; gs.screen = GS.SHOOTING },
                    onCancel = { gs.screen = GS.SHOOTING },
                )
                else -> {}
            }
        }
    }
}

private fun handleTouchEvent(event: MotionEvent, gs: GameState, scale: Float) {
    val actionIndex = event.actionIndex
    val pointerId = event.getPointerId(actionIndex)
    val canvasX = event.getX(actionIndex) / scale
    val canvasY = event.getY(actionIndex) / scale

    when (event.actionMasked) {
        MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
            when (gs.screen) {
                GS.SHOOTING -> gs.touch.onPointerDown(pointerId, canvasX, canvasY)
                GS.HELP -> gs.screen = GS.SHOOTING
                GS.SCORECARD -> gs.screen = GS.RESULTS
                GS.RESULTS -> handleResultsTap(gs, canvasX, canvasY)
                GS.INTRO -> {}
                else -> {}
            }
        }
        MotionEvent.ACTION_MOVE -> {
            for (i in 0 until event.pointerCount) {
                val id = event.getPointerId(i)
                val x = event.getX(i) / scale
                val y = event.getY(i) / scale
                gs.touch.onPointerMove(id, x, y)
            }
        }
        MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP, MotionEvent.ACTION_CANCEL -> {
            gs.touch.onPointerUp(pointerId)
        }
    }
}

private fun handleResultsTap(gs: GameState, canvasX: Float, canvasY: Float) {
    val now = System.currentTimeMillis()
    if (canvasX in 130f..510f) {
        when {
            canvasY in 315f..345f -> gs.viewScorecard(now)
            canvasY in 340f..370f -> gs.shootAgain()
            canvasY in 365f..395f -> gs.exitToIntro(now)
        }
    }
}
