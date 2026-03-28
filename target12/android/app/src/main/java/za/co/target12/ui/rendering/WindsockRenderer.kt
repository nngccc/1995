package za.co.target12.ui.rendering

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import za.co.target12.GameConstants
import za.co.target12.GameState
import kotlin.math.cos
import kotlin.math.sin

object WindsockRenderer {
    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        with(scope) {
            val wx = (GameConstants.WINDSOCK_X + ox) * scale
            val wy = (GameConstants.WINDSOCK_Y + oy) * scale
            val ww = GameConstants.WINDSOCK_W * scale
            val wh = GameConstants.WINDSOCK_H * scale
            val cx = wx
            val cy = wy

            // Background box (semi-transparent dark)
            drawRect(
                color = Color.Black.copy(alpha = 0.6f),
                topLeft = Offset(wx - ww / 2, wy - wh / 2),
                size = Size(ww, wh)
            )

            // Border (blue)
            drawRect(
                color = Color(0f, 0.67f, 1f, 1f),
                topLeft = Offset(wx - ww / 2, wy - wh / 2),
                size = Size(ww, wh),
                style = Stroke(width = 2f * scale)
            )

            // Compass rose - circle
            drawCircle(
                color = Color(0.4f, 0.4f, 0.4f),
                radius = 12 * scale,
                center = Offset(cx, cy),
                style = Stroke(width = 1f * scale)
            )

            // Cardinal marks (N, S, E, W)
            val markLen = 6 * scale
            val grayColor = Color(0.4f, 0.4f, 0.4f)
            val lineWidth = 1f * scale

            // N (up)
            drawLine(grayColor, Offset(cx, cy - 12 * scale), Offset(cx, cy - 12 * scale - markLen), lineWidth)
            // S (down)
            drawLine(grayColor, Offset(cx, cy + 12 * scale), Offset(cx, cy + 12 * scale + markLen), lineWidth)
            // E (right)
            drawLine(grayColor, Offset(cx + 12 * scale, cy), Offset(cx + 12 * scale + markLen, cy), lineWidth)
            // W (left)
            drawLine(grayColor, Offset(cx - 12 * scale, cy), Offset(cx - 12 * scale - markLen, cy), lineWidth)

            // Calculate effective wind (base + active gust)
            var effectiveAngle = state.baseWindAngle
            var effectiveStrength = state.baseWindStrength
            if (state.gustStartTime != null) {
                val now = System.currentTimeMillis()
                val gustElapsedMs = now - state.gustStartTime!!
                val gustElapsedSec = gustElapsedMs.toFloat() / 1000f
                val gustProgress = (gustElapsedMs.toFloat() / state.gustDuration.toFloat()).coerceAtMost(1f)

                // Apply easing for fade in/out
                var gustMultiplier = 1f
                if (gustProgress < GameConstants.WIND_GUST_FADE_TIME / state.gustDuration) {
                    val fade = (gustProgress * state.gustDuration) / GameConstants.WIND_GUST_FADE_TIME
                    gustMultiplier = fade * fade * (3 - 2 * fade)
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

            // Wind arrow (using effective wind angle and strength for length)
            val baseArrowLen = 8 * scale
            val arrowLen = kotlin.math.min(50 * scale, baseArrowLen * effectiveStrength) // Scale with strength, max 50px
            val angleRad = Math.toRadians(effectiveAngle.toDouble())
            val arrowX = cx + arrowLen * sin(angleRad).toFloat()
            val arrowY = cy - arrowLen * cos(angleRad).toFloat()

            // Arrow main line
            drawLine(
                Color.White,
                Offset(cx, cy),
                Offset(arrowX, arrowY),
                2f * scale
            )

            // Arrow head (scales with arrow length, uses same trig as arrow direction)
            val headLen = kotlin.math.max(3 * scale, kotlin.math.min(8 * scale, arrowLen / 5))
            val headAngle = Math.PI / 6
            val headX1 = arrowX - headLen * sin(angleRad - headAngle).toFloat()
            val headY1 = arrowY + headLen * cos(angleRad - headAngle).toFloat()
            val headX2 = arrowX - headLen * sin(angleRad + headAngle).toFloat()
            val headY2 = arrowY + headLen * cos(angleRad + headAngle).toFloat()

            drawLine(Color.White, Offset(arrowX, arrowY), Offset(headX1, headY1), 2f * scale)
            drawLine(Color.White, Offset(arrowX, arrowY), Offset(headX2, headY2), 2f * scale)
        }
    }
}
