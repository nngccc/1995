package za.co.target12.ui.rendering

import android.graphics.RadialGradient
import android.graphics.Shader
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameConstants
import za.co.target12.GameState

object MuzzleFlashRenderer {
    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        if (state.flashAlpha <= 0f) return

        val a = state.flashAlpha
        val sx = state.flashX * scale + ox
        val sy = state.flashY * scale + oy
        val r = GameConstants.FLASH_RADIUS * scale

        // Radial gradient
        scope.drawIntoCanvas { canvas ->
            val gradient = RadialGradient(
                sx, sy, r,
                intArrayOf(
                    android.graphics.Color.argb((a * 255).toInt(), 255, 255, 255),
                    android.graphics.Color.argb((a * 0.7f * 255).toInt(), 255, 255, 100),
                    android.graphics.Color.argb((a * 0.3f * 255).toInt(), 255, 160, 0),
                    android.graphics.Color.argb(0, 255, 160, 0)
                ),
                floatArrayOf(0f, 0.3f, 0.7f, 1f),
                Shader.TileMode.CLAMP
            )
            val paint = android.graphics.Paint().apply { shader = gradient }
            canvas.nativeCanvas.drawCircle(sx, sy, r, paint)
        }

        // Full-screen white overlay
        scope.drawRect(
            Color(1f, 1f, 1f, a * 0.15f),
            Offset.Zero, scope.size
        )
    }
}
