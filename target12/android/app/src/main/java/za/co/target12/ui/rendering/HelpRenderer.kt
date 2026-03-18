package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface

object HelpRenderer {

    private val overlayPaint = Paint().apply {
        color = 0xF2000064.toInt()  // rgba(0,0,100,0.95)
        style = Paint.Style.FILL
    }

    private val greenMonoPaint = Paint().apply {
        color = 0xFF00AA00.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 12f
        isAntiAlias = true
    }

    private val yellowMonoPaint = Paint().apply {
        color = 0xFFFFFF00.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 12f
        isAntiAlias = true
    }

    private val whiteMonoPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 12f
        isAntiAlias = true
    }

    private val grayMonoPaint = Paint().apply {
        color = 0xFF888888.toInt()
        typeface = Typeface.MONOSPACE
        textSize = 11f
        isAntiAlias = true
    }

    fun draw(nc: android.graphics.Canvas) {
        nc.drawRect(130f, 80f, 510f, 454f, overlayPaint)

        nc.drawText("HELP - SA NATIONAL BISLEY SHOOTING .22", 145f, 105f, greenMonoPaint)

        nc.drawText("TOUCH CONTROLS", 145f, 130f, yellowMonoPaint)

        var ly = 155f
        val lines = arrayOf(
            "LEFT SIDE      = JOYSTICK (AIM)",
            "RIGHT BOTTOM   = FIRE BUTTON",
            "RIGHT MIDDLE   = HOLD BREATH",
        )
        for (line in lines) {
            nc.drawText(line, 145f, ly, yellowMonoPaint)
            ly += 16f
        }

        ly += 10f
        nc.drawText("TIPS", 145f, ly, yellowMonoPaint)
        ly += 18f

        val tips = arrayOf(
            "Hold breath near exhale pause",
            "for steadiest aim",
            "",
            "Fire while holding breath",
            "Don't hold too long!",
        )
        for (tip in tips) {
            if (tip.isNotEmpty()) {
                nc.drawText(tip, 145f, ly, yellowMonoPaint)
            }
            ly += 14f
        }

        ly += 10f
        nc.drawText("FUNCTIONS", 145f, ly, whiteMonoPaint)
        ly += 20f

        val funcs = arrayOf(
            "NAME        = [1]",
            "TEAM        = [2]",
            "COMPETITION = [3]",
            "SCORE       = [4]",
            "",
            "RESTART     = [5]",
            "HELP        = [6]",
        )
        for (func in funcs) {
            if (func.isNotEmpty()) {
                nc.drawText(func, 145f, ly, whiteMonoPaint)
            }
            ly += 14f
        }

        ly += 10f
        nc.drawText("Tap anywhere to close", 145f, ly, grayMonoPaint)
    }
}
