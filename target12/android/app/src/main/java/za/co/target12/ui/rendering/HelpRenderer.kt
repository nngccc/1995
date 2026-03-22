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

object HelpRenderer {
    fun draw(scope: DrawScope, scale: Float, ox: Float, oy: Float) {
        // Overlay
        scope.drawRect(
            Color(0f, 0f, 100f / 255f, 0.95f),
            Offset(GameConstants.HELP_X * scale + ox, GameConstants.HELP_Y * scale + oy),
            Size(GameConstants.HELP_W * scale, GameConstants.HELP_H * scale)
        )

        scope.drawIntoCanvas { canvas ->
            val nc = canvas.nativeCanvas
            val x = 145f * scale + ox

            val greenPaint = Paint().apply {
                color = android.graphics.Color.rgb(0, 170, 0)
                textSize = 12f * scale; typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT; isAntiAlias = true
            }
            val yellowPaint = Paint().apply {
                color = android.graphics.Color.rgb(255, 255, 0)
                textSize = 12f * scale; typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT; isAntiAlias = true
            }
            val whitePaint = Paint().apply {
                color = android.graphics.Color.WHITE
                textSize = 12f * scale; typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT; isAntiAlias = true
            }
            val grayPaint = Paint().apply {
                color = android.graphics.Color.rgb(136, 136, 136)
                textSize = 11f * scale; typeface = Typeface.MONOSPACE
                textAlign = Paint.Align.LEFT; isAntiAlias = true
            }

            fun y(v: Float) = v * scale + oy
            val sp = 14f * scale

            nc.drawText("HELP - SA NATIONAL BISLEY SHOOTING .22", x, y(105f), greenPaint)
            nc.drawText("KEYS", x, y(130f), yellowPaint)

            nc.drawText("UP    = MOVE UP", x, y(150f), yellowPaint)
            nc.drawText("DOWN  = MOVE DOWN", x, y(150f) + sp, yellowPaint)
            nc.drawText("LEFT  = MOVE LEFT", x, y(150f) + sp * 2, yellowPaint)
            nc.drawText("RIGHT = MOVE RIGHT", x, y(150f) + sp * 3, yellowPaint)

            nc.drawText("W = BIG MOVE UP", x, y(206f), yellowPaint)
            nc.drawText("S = BIG MOVE DOWN", x, y(206f) + sp, yellowPaint)
            nc.drawText("A = BIG MOVE LEFT", x, y(206f) + sp * 2, yellowPaint)
            nc.drawText("D = BIG MOVE RIGHT", x, y(206f) + sp * 3, yellowPaint)

            nc.drawText("SHOOT = [ENTER / SPACE]", x, y(262f), yellowPaint)
            nc.drawText("HOLD BREATH = [SHIFT]", x, y(276f), yellowPaint)
            nc.drawText("EXIT = [ESC]", x, y(290f), yellowPaint)

            nc.drawText("FUNCTIONS", x, y(310f), whitePaint)
            nc.drawText("[1] ENTER NAME", x, y(330f), whitePaint)
            nc.drawText("[2] ENTER TEAM", x, y(330f) + sp, whitePaint)
            nc.drawText("[3] ENTER COMPETITION", x, y(330f) + sp * 2, whitePaint)
            nc.drawText("[5] RESTART", x, y(330f) + sp * 3, whitePaint)
            nc.drawText("[6] HELP", x, y(330f) + sp * 4, whitePaint)

            nc.drawText("Press any key to close", x, y(406f), grayPaint)
        }
    }
}
