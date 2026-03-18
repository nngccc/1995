package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Path
import za.co.target12.GameConstants.ECG_CHART_WIDTH
import za.co.target12.physics.HeartbeatPhysics

object EcgChartRenderer {

    private val bgPaint = Paint().apply {
        color = 0xFF000000.toInt()
        style = Paint.Style.FILL
    }

    private val borderPaint = Paint().apply {
        color = 0xFF330000.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1f
    }

    private val tracePaint = Paint().apply {
        color = 0xFFFF0000.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 1.5f
        isAntiAlias = true
    }

    private val path = Path()

    fun draw(nc: android.graphics.Canvas, x: Float, y: Float, w: Float, h: Float, heartbeat: HeartbeatPhysics) {
        nc.drawRect(x, y - h / 2f, x + w, y + h / 2f, bgPaint)
        nc.drawRect(x, y - h / 2f, x + w, y + h / 2f, borderPaint)

        path.reset()
        for (i in 0 until ECG_CHART_WIDTH) {
            val bufIdx = (heartbeat.ecgHead + i) % ECG_CHART_WIDTH
            val value = heartbeat.ecgHistory[bufIdx]
            val px = x + i
            val py = y - value * (h / 2f) * 0.85f
            if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
        }
        nc.drawPath(path, tracePaint)
    }
}
