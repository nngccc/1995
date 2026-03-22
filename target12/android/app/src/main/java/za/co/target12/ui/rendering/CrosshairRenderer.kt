package za.co.target12.ui.rendering

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import za.co.target12.GameConstants
import za.co.target12.GameState

object CrosshairRenderer {
    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        val sx = state.sightX * scale + ox
        val sy = state.sightY * scale + oy
        val r = GameConstants.CROSSHAIR_RADIUS * scale
        val tick = GameConstants.CROSSHAIR_TICK * scale

        // Circle
        scope.drawCircle(Color.White, r, Offset(sx, sy), style = Stroke(1f * scale))

        // Tick marks at cardinal points (inward + outward)
        val dirs = arrayOf(
            Offset(-1f, 0f), Offset(1f, 0f), Offset(0f, -1f), Offset(0f, 1f)
        )
        for (d in dirs) {
            val innerStart = Offset(sx + d.x * (r - tick), sy + d.y * (r - tick))
            val outerEnd = Offset(sx + d.x * (r + tick), sy + d.y * (r + tick))
            scope.drawLine(Color.White, innerStart, outerEnd, strokeWidth = 1f * scale)
        }

        // Center dot
        scope.drawCircle(Color.White, GameConstants.CENTER_DOT_RADIUS * scale, Offset(sx, sy))
    }
}
