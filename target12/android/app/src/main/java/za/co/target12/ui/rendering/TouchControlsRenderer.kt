package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import za.co.target12.GameConstants.JOYSTICK_RADIUS
import za.co.target12.input.TouchInputState

object TouchControlsRenderer {

    /**
     * Draw touch controls in screen coordinates (not canvas coordinates).
     * Call this AFTER nc.restore() so drawing is in screen space.
     */
    fun draw(nc: android.graphics.Canvas, touch: TouchInputState) {
        // Joystick
        if (touch.joystickPointerId != null) {
            val ringPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 6f
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
            nc.drawCircle(touch.joystickBaseX + touch.joystickDx, touch.joystickBaseY + touch.joystickDy, 48f, thumbPaint)
        } else {
            // Idle hint at margin center — visible like other buttons
            val hintX = touch.joystickHintScreenX
            val hintY = touch.joystickHintScreenY
            val hintFillPaint = Paint().apply {
                color = 0xFF0000AA.toInt()
                style = Paint.Style.FILL
                alpha = 102 // 0.40
                isAntiAlias = true
            }
            nc.drawCircle(hintX, hintY, 138f, hintFillPaint)
            val hintRingPaint = Paint().apply {
                color = 0xFF6464FF.toInt()
                style = Paint.Style.STROKE
                strokeWidth = 6f
                alpha = 140 // 0.55
                isAntiAlias = true
            }
            nc.drawCircle(hintX, hintY, 138f, hintRingPaint)
            val hintTextPaint = Paint().apply {
                color = 0xFFFFFFFF.toInt()
                typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
                textSize = 30f
                textAlign = Paint.Align.CENTER
                alpha = 217 // 0.85
                isAntiAlias = true
            }
            nc.drawText("MOVE", hintX, hintY + 9f, hintTextPaint)
        }

        // Fire button (right margin)
        val fireX = touch.fireBtnScreenX
        val fireY = touch.fireBtnScreenY
        val fireR = touch.fireBtnRadius
        val firePressed = touch.firePointerId != null
        val fireFillPaint = Paint().apply {
            color = if (firePressed) 0xFFFF5555.toInt() else 0xFFAA0000.toInt()
            style = Paint.Style.FILL
            alpha = if (firePressed) 191 else 102
            isAntiAlias = true
        }
        nc.drawCircle(fireX, fireY, fireR, fireFillPaint)
        val fireStrokePaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 6f
            alpha = if (firePressed) 230 else 140
            isAntiAlias = true
        }
        nc.drawCircle(fireX, fireY, fireR, fireStrokePaint)
        val fireTextPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textSize = 39f
            textAlign = Paint.Align.CENTER
            alpha = if (firePressed) 255 else 217
            isAntiAlias = true
        }
        nc.drawText("FIRE", fireX, fireY + 15f, fireTextPaint)

        // Breath-hold button (left margin)
        val breathX = touch.breathBtnScreenX
        val breathY = touch.breathBtnScreenY
        val breathR = touch.breathBtnRadius
        val breathPressed = touch.breathPointerId != null
        val breathFillPaint = Paint().apply {
            color = if (breathPressed) 0xFF00FF00.toInt() else 0xFF006600.toInt()
            style = Paint.Style.FILL
            alpha = if (breathPressed) 191 else 102
            isAntiAlias = true
        }
        nc.drawCircle(breathX, breathY, breathR, breathFillPaint)
        val breathStrokePaint = Paint().apply {
            color = 0xFF00FF00.toInt()
            style = Paint.Style.STROKE
            strokeWidth = 6f
            alpha = if (breathPressed) 230 else 140
            isAntiAlias = true
        }
        nc.drawCircle(breathX, breathY, breathR, breathStrokePaint)
        val breathTextPaint = Paint().apply {
            color = 0xFFFFFFFF.toInt()
            typeface = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD)
            textSize = 33f
            textAlign = Paint.Align.CENTER
            alpha = if (breathPressed) 255 else 217
            isAntiAlias = true
        }
        nc.drawText("HOLD", breathX, breathY - 9f, breathTextPaint)
        nc.drawText("BREATH", breathX, breathY + 33f, breathTextPaint)
    }
}
