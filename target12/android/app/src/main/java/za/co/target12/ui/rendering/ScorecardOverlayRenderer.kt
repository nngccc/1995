package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameState

object ScorecardOverlayRenderer {
    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        // Box at (290, 400), 60×35
        scope.drawRect(
            Color(0f, 0f, 100f / 255f, 0.9f),
            Offset(290f * scale + ox, 400f * scale + oy),
            Size(60f * scale, 35f * scale)
        )

        scope.drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 22f * scale
                typeface = Typeface.SERIF
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            canvas.nativeCanvas.drawText(
                "${state.scorecardCountdown}",
                320f * scale + ox,
                425f * scale + oy,
                paint
            )
        }
    }
}
