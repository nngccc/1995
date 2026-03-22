package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.GameConstants
import za.co.target12.GameState

object IntroRenderer {
    private val olive = Color(0xFF808000)
    private val shotColor = Color(0xFFAA0000)

    fun draw(scope: DrawScope, state: GameState, scale: Float, ox: Float, oy: Float) {
        scope.drawRect(Color.Black, Offset.Zero, scope.size)

        // Demo targets
        for (x in GameConstants.DEMO_TARGET_X) {
            val cx = x * scale + ox
            val cy = GameConstants.DEMO_TARGET_Y * scale + oy
            val r = GameConstants.TARGET_RADIUS * scale
            scope.drawCircle(olive, r, Offset(cx, cy))
            for (ring in GameConstants.TARGET_RINGS) {
                scope.drawCircle(Color.Black, ring * scale, Offset(cx, cy), style = Stroke(1f * scale))
            }
        }

        // Demo shots
        for (shot in state.demoShots) {
            scope.drawCircle(
                shotColor, GameConstants.SHOT_MARK_RADIUS * scale,
                Offset(shot.x * scale + ox, shot.y * scale + oy)
            )
        }

        // Text
        scope.drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas

            fun textPaint(colorInt: Int, size: Float, tf: Typeface, bold: Boolean = false): Paint {
                return Paint().apply {
                    color = colorInt
                    textSize = size * scale
                    typeface = if (bold) Typeface.create(tf, Typeface.BOLD) else tf
                    textAlign = Paint.Align.CENTER
                    isAntiAlias = true
                }
            }

            val centerX = 320f * scale + ox

            nc.drawText("SOUTH AFRICAN", centerX, 75f * scale + oy,
                textPaint(android.graphics.Color.rgb(0, 170, 0), 22f, Typeface.SERIF))
            nc.drawText("NATIONAL", centerX, 110f * scale + oy,
                textPaint(android.graphics.Color.rgb(255, 255, 0), 24f, Typeface.SERIF))
            nc.drawText("BISLEY SHOOTING 1995", centerX, 160f * scale + oy,
                textPaint(android.graphics.Color.rgb(170, 0, 0), 30f, Typeface.SERIF, bold = true))
            nc.drawText("(0.22\" CALIBRE)", centerX, 195f * scale + oy,
                textPaint(android.graphics.Color.rgb(0, 0, 170), 22f, Typeface.SERIF))
            nc.drawText("Press [6] for Help | Press any key to start", centerX, 450f * scale + oy,
                textPaint(android.graphics.Color.rgb(136, 136, 136), 12f, Typeface.SANS_SERIF))
        }
    }
}
