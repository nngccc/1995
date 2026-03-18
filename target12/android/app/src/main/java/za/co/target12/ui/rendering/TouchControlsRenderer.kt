package za.co.target12.ui.rendering

import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Typeface
import za.co.target12.GameConstants.BREATH_BTN_R
import za.co.target12.GameConstants.BREATH_BTN_X
import za.co.target12.GameConstants.BREATH_BTN_Y
import za.co.target12.GameConstants.FIRE_BTN_R
import za.co.target12.GameConstants.FIRE_BTN_X
import za.co.target12.GameConstants.FIRE_BTN_Y
import za.co.target12.GameConstants.JOYSTICK_RADIUS
import za.co.target12.input.TouchInputState

object TouchControlsRenderer {

    fun draw(nc: android.graphics.Canvas, touch: TouchInputState) {
        // Joystick
        if (touch.joystickPointerId != null) {
            val ringPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 2f
                alpha = 89 // 0.35
                isAntiAlias = true
            }
            nc.drawCircle(touch.joystickBaseX, touch.joystickBaseY, JOYSTICK_RADIUS, ringPaint)
            val thumbPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                style = Paint.Style.FILL
                alpha = 153 // 0.60
                isAntiAlias = true
            }
            nc.drawCircle(touch.joystickBaseX + touch.joystickDx, touch.joystickBaseY + touch.joystickDy, 16f, thumbPaint)
        } else {
            // Idle hint
            val hintRingPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 1f
                alpha = 31 // 0.12
                pathEffect = DashPathEffect(floatArrayOf(4f, 6f), 0f)
                isAntiAlias = true
            }
            nc.drawCircle(110f, 390f, 46f, hintRingPaint)
            val hintTextPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                typeface = Typeface.MONOSPACE
                textSize = 10f
                textAlign = Paint.Align.CENTER
                alpha = 31
                isAntiAlias = true
            }
            nc.drawText("MOVE", 110f, 393f, hintTextPaint)
        }

        // Fire button
        val firePressed = touch.firePointerId != null
        val fireFillPaint = Paint().apply {
            color = if (firePressed) 0xFFFF5555.toInt() else 0xFFAA0000.toInt()
            style = Paint.Style.FILL
            alpha = if (firePressed) 191 else 102
            isAntiAlias = true
        }
        nc.drawCircle(FIRE_BTN_X, FIRE_BTN_Y, FIRE_BTN_R, fireFillPaint)
        val fireStrokePaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = if (firePressed) 230 else 140
            isAntiAlias = true
        }
        nc.drawCircle(FIRE_BTN_X, FIRE_BTN_Y, FIRE_BTN_R, fireStrokePaint)
        val fireTextPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textSize = 13f
            textAlign = Paint.Align.CENTER
            alpha = if (firePressed) 255 else 217
            isAntiAlias = true
        }
        nc.drawText("FIRE", FIRE_BTN_X, FIRE_BTN_Y + 5f, fireTextPaint)

        // Breath-hold button
        val breathPressed = touch.breathPointerId != null
        val breathFillPaint = Paint().apply {
            color = if (breathPressed) 0xFF00FF00.toInt() else 0xFF006600.toInt()
            style = Paint.Style.FILL
            alpha = if (breathPressed) 191 else 102
            isAntiAlias = true
        }
        nc.drawCircle(BREATH_BTN_X, BREATH_BTN_Y, BREATH_BTN_R, breathFillPaint)
        val breathStrokePaint = Paint().apply {
            color = 0xFF00FF00.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 2f
            alpha = if (breathPressed) 230 else 140
            isAntiAlias = true
        }
        nc.drawCircle(BREATH_BTN_X, BREATH_BTN_Y, BREATH_BTN_R, breathStrokePaint)
        val breathTextPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textSize = 11f
            textAlign = Paint.Align.CENTER
            alpha = if (breathPressed) 255 else 217
            isAntiAlias = true
        }
        nc.drawText("HOLD", BREATH_BTN_X, BREATH_BTN_Y - 3f, breathTextPaint)
        nc.drawText("BREATH", BREATH_BTN_X, BREATH_BTN_Y + 11f, breathTextPaint)
    }
}
