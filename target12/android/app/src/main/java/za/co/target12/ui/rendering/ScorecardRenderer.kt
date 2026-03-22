package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameConstants
import za.co.target12.GameState

object ScorecardRenderer {
    private val olive = Color(0xFF808000)
    private val shotColor = Color(0xFFAA0000)

    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        // Black background
        scope.drawRect(Color.Black, Offset.Zero, scope.size)

        // Draw targets
        for (i in 0 until GameConstants.TARGET_COUNT) {
            val cx = GameConstants.TARGET_X[i] * scale + ox
            val cy = GameConstants.TARGET_Y[i] * scale + oy
            val r = GameConstants.TARGET_RADIUS * scale
            scope.drawCircle(olive, r, Offset(cx, cy))
            for (ring in GameConstants.TARGET_RINGS) {
                scope.drawCircle(Color.Black, ring * scale, Offset(cx, cy), style = Stroke(1f * scale))
            }
        }

        // Shot marks
        for (shot in state.shots) {
            scope.drawCircle(
                shotColor, GameConstants.SHOT_MARK_RADIUS * scale,
                Offset(shot.x * scale + ox, shot.y * scale + oy)
            )
        }

        // Text labels
        scope.drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas

            // Target number labels
            val labelPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 12f * scale
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            for (i in 0 until GameConstants.TARGET_COUNT) {
                val label = GameConstants.TARGET_LABELS[i]
                if (label.isNotEmpty()) {
                    nc.drawText(
                        label,
                        GameConstants.TARGET_X[i] * scale + ox,
                        (GameConstants.TARGET_Y[i] - 60f) * scale + oy,
                        labelPaint
                    )
                }
            }
            // Practice label
            val practicePaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11f * scale
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            nc.drawText("PRACTICE SHOTS ONLY", 320f * scale + ox, 285f * scale + oy, practicePaint)

            // Shot counter (scoring)
            val serifPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11f * scale
                typeface = Typeface.SERIF
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            nc.drawText("Shots: ${state.scoringShots}/10", 40f * scale + ox, 208f * scale + oy, serifPaint)

            // Shot counter (total) right-aligned
            val totalPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11f * scale
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.RIGHT
                isAntiAlias = true
            }
            nc.drawText("${state.totalShots}/13", 630f * scale + ox, 15f * scale + oy, totalPaint)

            // TOTAL section
            nc.drawText("TOTAL", 40f * scale + ox, 220f * scale + oy, serifPaint)
            nc.drawText("......", 40f * scale + ox, 235f * scale + oy, serifPaint)
            val scorePaint = Paint().apply {
                color = android.graphics.Color.rgb(0, 170, 0)
                textSize = 12f * scale
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            nc.drawText("${state.score}", 42f * scale + ox, 248f * scale + oy, scorePaint)

            // TEAM section
            val teamLabelPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 11f * scale
                typeface = Typeface.SERIF
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            nc.drawText("TEAM", 560f * scale + ox, 220f * scale + oy, teamLabelPaint)
            nc.drawText(".........", 555f * scale + ox, 235f * scale + oy, teamLabelPaint)
            val teamValPaint = Paint().apply {
                color = android.graphics.Color.rgb(0, 0, 170)
                textSize = 12f * scale
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            nc.drawText(state.playerTeam, 558f * scale + ox, 248f * scale + oy, teamValPaint)

            // NAME
            nc.drawText("NAME ........................", 40f * scale + ox, 460f * scale + oy, serifPaint)
            val nameValPaint = Paint().apply {
                color = android.graphics.Color.rgb(0, 0, 170)
                textSize = 12f * scale
                typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT
                isAntiAlias = true
            }
            nc.drawText(state.playerName, 100f * scale + ox, 475f * scale + oy, nameValPaint)

            // S.A.N.S.S.U.
            val orgPaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 20f * scale
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            nc.drawText("S.A.N.S.S.U. 01", 320f * scale + ox, 460f * scale + oy, orgPaint)

            // COMP
            nc.drawText("COMP ..................", 450f * scale + ox, 460f * scale + oy, serifPaint)
            nc.drawText(state.playerComp, 510f * scale + ox, 475f * scale + oy, nameValPaint)
        }
    }
}
