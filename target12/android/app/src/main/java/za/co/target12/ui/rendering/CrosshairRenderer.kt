package za.co.target12.ui.rendering

import android.graphics.Paint
import za.co.target12.GameConstants.GR

object CrosshairRenderer {

    private val strokePaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f
        isAntiAlias = true
    }

    private val dotPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun draw(nc: android.graphics.Canvas, x: Float, y: Float) {
        // Circle
        nc.drawCircle(x, y, GR, strokePaint)

        // Tick marks (10px extending both in/out from circle at cardinal points)
        val len = 10f
        nc.drawLine(x - GR - len, y, x - GR + len, y, strokePaint)
        nc.drawLine(x + GR - len, y, x + GR + len, y, strokePaint)
        nc.drawLine(x, y - GR - len, x, y - GR + len, strokePaint)
        nc.drawLine(x, y + GR - len, x, y + GR + len, strokePaint)

        // Center dot
        nc.drawCircle(x, y, 2f, dotPaint)
    }
}
