package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameConstants
import za.co.target12.GameState

object ResultsRenderer {
    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        scope.drawRect(
            Color(0f, 0f, 100f / 255f, 0.95f),
            Offset(GameConstants.RESULTS_X * scale + ox, GameConstants.RESULTS_Y * scale + oy),
            Size(GameConstants.RESULTS_W * scale, GameConstants.RESULTS_H * scale)
        )

        scope.drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas
            val cx = 320f * scale + ox
            fun y(v: Float) = v * scale + oy

            val whitePaint = Paint().apply {
                color = android.graphics.Color.WHITE; textSize = 22f * scale
                typeface = Typeface.SERIF; textAlign = Paint.Align.CENTER; isAntiAlias = true
            }
            nc.drawText("YOUR ROUNDS ARE DONE!", cx, y(130f), whitePaint)

            val greenPaint = Paint().apply {
                color = android.graphics.Color.rgb(0, 170, 0); textSize = 22f * scale
                typeface = Typeface.SERIF; textAlign = Paint.Align.CENTER; isAntiAlias = true
            }
            nc.drawText("SCORE", 300f * scale + ox, y(175f), greenPaint)

            val scoreX = if (state.score >= 100) 220f else 260f
            val scorePaint = Paint().apply {
                color = android.graphics.Color.rgb(255, 255, 0); textSize = 64f * scale
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.LEFT; isAntiAlias = true
            }
            nc.drawText("${state.score}", scoreX * scale + ox, y(260f), scorePaint)

            val pctPaint = Paint().apply {
                color = android.graphics.Color.rgb(255, 255, 0); textSize = 48f * scale
                typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
                textAlign = Paint.Align.LEFT; isAntiAlias = true
            }
            nc.drawText("%", 400f * scale + ox, y(250f), pctPaint)

            val redPaint = Paint().apply {
                color = android.graphics.Color.rgb(170, 0, 0); textSize = 16f * scale
                typeface = Typeface.SERIF; textAlign = Paint.Align.CENTER; isAntiAlias = true
            }
            nc.drawText("OPTIONS", cx, y(300f), redPaint)
            nc.drawText("[9]   VIEW SCORECARD (10s)", cx, y(330f), redPaint)
            nc.drawText("[5]   SHOOT AGAIN", cx, y(355f), redPaint)
            nc.drawText("[ESC] EXIT", cx, y(380f), redPaint)
        }
    }
}
