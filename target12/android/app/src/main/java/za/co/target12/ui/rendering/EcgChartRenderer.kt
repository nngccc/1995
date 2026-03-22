package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameConstants
import za.co.target12.GameState

object EcgChartRenderer {

    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        drawEcg(scope, state, scale, ox, oy)
        drawBreathBar(scope, state, scale, ox, oy)
    }

    private fun drawEcg(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        val x = GameConstants.ECG_X * scale + ox
        val top = (GameConstants.ECG_Y - GameConstants.ECG_H / 2f) * scale + oy
        val w = GameConstants.ECG_W * scale
        val h = GameConstants.ECG_H * scale
        val centerY = GameConstants.ECG_Y * scale + oy

        // Background
        scope.drawRect(Color.Black, Offset(x, top), Size(w, h))
        scope.drawRect(Color(0xFF330000), Offset(x, top), Size(w, h), style = Stroke(1f * scale))

        // Trace
        val path = Path()
        var first = true
        for (i in 0 until GameConstants.ECG_BUFFER_SIZE) {
            val bufIdx = (state.ecgIndex + i) % GameConstants.ECG_BUFFER_SIZE
            val value = state.ecgBuffer[bufIdx]
            val px = x + i.toFloat() / GameConstants.ECG_BUFFER_SIZE * w
            val py = centerY - value * (h / 2f) * 0.85f
            if (first) { path.moveTo(px, py); first = false }
            else path.lineTo(px, py)
        }
        scope.drawPath(path, Color.Red, style = Stroke(1.5f * scale))
    }

    private fun drawBreathBar(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        val x = GameConstants.BAR_X * scale + ox
        val top = (GameConstants.BAR_Y - GameConstants.BAR_H / 2f) * scale + oy
        val w = GameConstants.BAR_W * scale
        val h = GameConstants.BAR_H * scale

        // Background
        scope.drawRect(Color.Black, Offset(x, top), Size(w, h))
        scope.drawRect(Color(0xFF003300), Offset(x, top), Size(w, h), style = Stroke(1f * scale))

        // Fill level
        val fillLevel = (state.breathDy / GameConstants.BREATH_AMP_Y).coerceIn(0f, 1f)
        val barColor = when {
            state.breathStress > 0.01f -> Color(1f, 1f * (1f - state.breathStress), 0f)
            state.breathRecovering -> Color.Yellow
            state.breathHolding -> Color(0f, 1f, 0f)
            else -> Color(0f, 0.67f, 0f)
        }

        if (fillLevel > 0f) {
            val fillH = h * fillLevel
            scope.drawRect(barColor, Offset(x, top + h - fillH), Size(w, fillH))
        }

        // "BREATH" label when holding
        if (state.breathHolding) {
            scope.drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = android.graphics.Color.argb(
                        255,
                        (barColor.red * 255).toInt(),
                        (barColor.green * 255).toInt(),
                        (barColor.blue * 255).toInt()
                    )
                    textSize = 7f * scale
                    typeface = Typeface.MONOSPACE
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
                canvas.nativeCanvas.drawText("BREATH", x + w / 2f, top - 2f * scale, paint)
            }
        }
    }
}
