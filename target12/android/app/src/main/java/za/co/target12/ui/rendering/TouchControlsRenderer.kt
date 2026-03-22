package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import za.co.target12.input.TouchInputState
import kotlin.math.sqrt

object TouchControlsRenderer {

    fun draw(scope: DrawScope, touchState: TouchInputState, scaleInfo: ScaleInfo) {
        val lm = scaleInfo.leftMarginEnd
        val rm = scaleInfo.rightMarginStart
        val sh = scaleInfo.scaledHeight + scaleInfo.offsetY * 2f

        if (lm < 10f) return // no margin to draw in

        drawJoystick(scope, touchState, lm, sh)
        drawBreathButton(scope, touchState, rm, rm + scaleInfo.offsetX, sh)
        drawFireButton(scope, touchState, rm, rm + scaleInfo.offsetX, sh)
    }

    private fun drawBreathButton(scope: DrawScope, ts: TouchInputState, rightStart: Float, screenW: Float, screenH: Float) {
        val marginW = screenW - rightStart
        if (marginW < 10f) return
        val cx = rightStart + marginW / 2f
        val cy = screenH * 0.50f
        val r = 132f.coerceAtMost(marginW / 2f - 4f)
        val pressed = ts.breathPressed

        val fillAlpha = if (pressed) 0.75f else 0.40f
        val strokeAlpha = if (pressed) 0.90f else 0.55f
        val labelAlpha = if (pressed) 1.0f else 0.85f

        scope.drawCircle(Color(0f, if (pressed) 1f else 0.4f, 0f, fillAlpha), r, Offset(cx, cy))
        scope.drawCircle(Color(0f, 1f, 0f, strokeAlpha), r, Offset(cx, cy), style = Stroke(6f))

        scope.drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = android.graphics.Color.argb((labelAlpha * 255).toInt(), 255, 255, 255)
                textSize = 33f; typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER; isAntiAlias = true
            }
            canvas.nativeCanvas.drawText("HOLD", cx, cy - 13f, paint)
            canvas.nativeCanvas.drawText("BREATH", cx, cy + 27f, paint)
        }
    }

    private fun drawJoystick(scope: DrawScope, ts: TouchInputState, marginEnd: Float, screenH: Float) {
        val cx = marginEnd / 2f
        val cy = screenH * 0.65f
        val r = 138f.coerceAtMost(marginEnd / 2f - 4f)

        if (!ts.joystickActive) {
            scope.drawCircle(Color(0f, 0f, 170f / 255f, 0.40f), r, Offset(cx, cy))
            scope.drawCircle(Color(100f / 255f, 100f / 255f, 1f, 0.55f), r, Offset(cx, cy), style = Stroke(6f))
            scope.drawIntoCanvas { canvas ->
                val paint = Paint().apply {
                    color = android.graphics.Color.argb((0.85f * 255).toInt(), 255, 255, 255)
                    textSize = 30f; typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                    textAlign = Paint.Align.CENTER; isAntiAlias = true
                }
                canvas.nativeCanvas.drawText("MOVE", cx, cy + 12f, paint)
            }
        } else {
            val base = Offset(ts.joystickBaseX, ts.joystickBaseY)
            scope.drawCircle(Color(1f, 1f, 1f, 0.35f), r, base, style = Stroke(6f))
            val thumbX = ts.joystickBaseX + ts.joystickDx
            val thumbY = ts.joystickBaseY + ts.joystickDy
            scope.drawCircle(Color(1f, 1f, 1f, 0.60f), 48f, Offset(thumbX, thumbY))
        }
    }

    private fun drawFireButton(
        scope: DrawScope, ts: TouchInputState,
        rightStart: Float, screenW: Float, screenH: Float
    ) {
        val marginW = screenW - rightStart
        if (marginW < 10f) return
        val cx = rightStart + marginW / 2f
        val cy = screenH * 0.75f
        val r = 156f.coerceAtMost(marginW / 2f - 4f)
        val pressed = ts.firePressed

        val fillAlpha = if (pressed) 0.75f else 0.40f
        val strokeAlpha = if (pressed) 0.90f else 0.55f
        val labelAlpha = if (pressed) 1.0f else 0.85f

        scope.drawCircle(
            Color(if (pressed) 1f else 0.67f, if (pressed) 0.33f else 0f, 0f, fillAlpha),
            r, Offset(cx, cy)
        )
        scope.drawCircle(Color(1f, 1f, 1f, strokeAlpha), r, Offset(cx, cy), style = Stroke(6f))

        scope.drawIntoCanvas { canvas ->
            val paint = Paint().apply {
                color = android.graphics.Color.argb((labelAlpha * 255).toInt(), 255, 255, 255)
                textSize = 39f; typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textAlign = Paint.Align.CENTER; isAntiAlias = true
            }
            canvas.nativeCanvas.drawText("FIRE", cx, cy + 15f, paint)
        }
    }
}
