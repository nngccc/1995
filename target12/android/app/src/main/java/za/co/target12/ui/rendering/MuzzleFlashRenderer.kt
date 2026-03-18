package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader

object MuzzleFlashRenderer {

    fun draw(nc: android.graphics.Canvas, x: Float, y: Float, alpha: Float, canvasW: Float, canvasH: Float) {
        if (alpha <= 0f) return

        // Full-screen white overlay
        val overlayPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            this.alpha = (alpha * 0.15f * 255f).toInt().coerceIn(0, 255)
            style = Paint.Style.FILL
        }
        nc.drawRect(0f, 0f, canvasW, canvasH, overlayPaint)

        // Radial gradient centered on shot position
        val a = alpha
        val colors = intArrayOf(
            colorWithAlpha(255, 255, 255, a),
            colorWithAlpha(255, 255, 100, a * 0.7f),
            colorWithAlpha(255, 160, 0, a * 0.3f),
            colorWithAlpha(255, 160, 0, 0f),
        )
        val stops = floatArrayOf(0f, 0.3f, 0.7f, 1f)

        val gradient = RadialGradient(x, y, 60f, colors, stops, Shader.TileMode.CLAMP)
        val flashPaint = Paint().apply {
            shader = gradient
            style = Paint.Style.FILL
        }
        nc.drawCircle(x, y, 60f, flashPaint)
    }

    private fun colorWithAlpha(r: Int, g: Int, b: Int, a: Float): Int {
        val ai = (a * 255f).toInt().coerceIn(0, 255)
        return (ai shl 24) or (r shl 16) or (g shl 8) or b
    }
}
