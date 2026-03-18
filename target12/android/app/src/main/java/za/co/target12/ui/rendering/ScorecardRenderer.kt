package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameConstants
import za.co.target12.GameConstants.RONTES
import za.co.target12.GameConstants.SK
import za.co.target12.GameConstants.TARGETS
import za.co.target12.GameState
import za.co.target12.scoring.ScoringEngine
import kotlin.math.min

object ScorecardRenderer {

    private val targetFillPaint = Paint().apply {
        color = 0xFF808000.toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val ringPaint = Paint().apply {
        color = 0xFF000000.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = true
    }

    private val labelPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 12f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val shotPaint = Paint().apply {
        color = 0xFFAA0000.toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val textWhiteSerif = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.SERIF
        textSize = 11f
        isAntiAlias = true
    }

    private val textWhiteMono = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 11f
        isAntiAlias = true
    }

    private val textBlueMono = Paint().apply {
        color = 0xFF0000AA.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 12f
        isAntiAlias = true
    }

    private val textGreenMono = Paint().apply {
        color = 0xFF00AA00.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 12f
        isAntiAlias = true
    }

    private val titlePaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 20f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val practicePaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 11f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun draw(ds: DrawScope, gs: GameState) {
        val nc = ds.drawContext.canvas.nativeCanvas

        // Black background
        nc.drawColor(0xFF000000.toInt())

        // Draw all targets
        for (i in TARGETS.indices) {
            val t = TARGETS[i]
            drawTarget(nc, t.x, t.y)
            if (t.label.isNotEmpty()) {
                nc.drawText(t.label, t.x, t.y - 60f, labelPaint)
            }
        }

        // Practice label
        nc.drawText("PRACTICE SHOTS ONLY", 320f, 285f, practicePaint)

        // ECG chart
        EcgChartRenderer.draw(nc, 220f, 432f, 200f, 20f, gs.heartbeat)

        // Breath indicator
        drawBreathIndicator(nc, 185f, 432f, 25f, 20f, gs)

        // S.A.N.S.S.U. 01
        nc.drawText("S.A.N.S.S.U. 01", 320f, 460f, titlePaint)

        // Shot counts
        val shotsUsed = min(gs.aantal - 1, RONTES)
        val scoring = ScoringEngine.countScoringShots(gs.shotX, gs.shotY, gs.aantal)

        // Labels
        nc.drawText("NAME ........................", 40f, 460f, textWhiteSerif)
        nc.drawText("COMP ..................", 450f, 460f, textWhiteSerif)
        nc.drawText("Shots: $scoring/10", 40f, 208f, textWhiteSerif)
        nc.drawText("TOTAL", 40f, 220f, textWhiteSerif)
        nc.drawText("......", 40f, 235f, textWhiteSerif)
        nc.drawText("TEAM", 560f, 220f, textWhiteSerif)
        nc.drawText(".........", 555f, 235f, textWhiteSerif)

        // User data
        nc.drawText(gs.naam, 100f, 475f, textBlueMono)
        nc.drawText(gs.komp, 510f, 475f, textBlueMono)
        nc.drawText(gs.span, 558f, 248f, textBlueMono)

        // Score
        nc.drawText(gs.telling.toString(), 42f, 248f, textGreenMono)

        // Draw shots
        for (i in 1..RONTES) {
            if (gs.shotX[i] != 0f || gs.shotY[i] != 0f) {
                nc.drawCircle(gs.shotX[i], gs.shotY[i], SK, shotPaint)
            }
        }

        // Shot counter (right-aligned)
        val counterPaint = Paint(textWhiteMono).apply {
            textAlign = Paint.Align.RIGHT
        }
        nc.drawText("$shotsUsed/$RONTES", 630f, 15f, counterPaint)
    }

    fun drawTarget(nc: android.graphics.Canvas, x: Float, y: Float) {
        nc.drawCircle(x, y, 50f, targetFillPaint)
        for (r in intArrayOf(5, 15, 25, 35)) {
            nc.drawCircle(x, y, r.toFloat(), ringPaint)
        }
    }

    private fun drawBreathIndicator(nc: android.graphics.Canvas, x: Float, y: Float, w: Float, h: Float, gs: GameState) {
        // Background
        val bgPaint = Paint().apply { color = 0xFF000000.toInt(); style = Paint.Style.FILL }
        nc.drawRect(x, y - h / 2f, x + w, y + h / 2f, bgPaint)
        val borderPaint = Paint().apply {
            color = 0xFF003300.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }
        nc.drawRect(x, y - h / 2f, x + w, y + h / 2f, borderPaint)

        // Fill level from breathing displacement
        val fillLevel = (gs.breathing.currentDy / GameConstants.BREATH_AMPLITUDE_Y).coerceIn(0f, 1f)

        // Color based on state
        val barColor = when {
            gs.breathing.stress > 0.01f -> {
                val r = 255
                val g = (255 * (1f - gs.breathing.stress)).toInt().coerceIn(0, 255)
                android.graphics.Color.rgb(r, g, 0)
            }
            gs.breathing.recovering -> 0xFFFF00.toInt() or (0xFF shl 24)
            gs.breathing.holding -> 0x00FF00.toInt() or (0xFF shl 24)
            else -> 0x00AA00.toInt() or (0xFF shl 24)
        }

        val barH = fillLevel * (h - 2f)
        val fillPaint = Paint().apply { color = barColor; style = Paint.Style.FILL }
        nc.drawRect(x + 1f, y + h / 2f - 1f - barH, x + w - 1f, y + h / 2f - 1f, fillPaint)

        // Label when holding
        if (gs.breathing.holding) {
            val breathLabelPaint = Paint().apply {
                color = barColor
                typeface = Typeface.MONOSPACE
                textSize = 7f
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            nc.drawText("BREATH", x + w / 2f, y - h / 2f - 2f, breathLabelPaint)
        }
    }
}
