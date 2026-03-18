package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import za.co.target12.GameState

object ResultsRenderer {

    private val overlayPaint = Paint().apply {
        color = 0xF2000064.toInt()
        style = Paint.Style.FILL
    }

    private val titlePaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.SERIF
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val scoreLabelPaint = Paint().apply {
        color = 0xFF00AA00.toInt()
        typeface = Typeface.SERIF
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val scoreValuePaint = Paint().apply {
        color = 0xFFFFFF00.toInt()
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 64f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val percentPaint = Paint().apply {
        color = 0xFFFFFF00.toInt()
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 48f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val optionPaint = Paint().apply {
        color = 0xFFAA0000.toInt()
        typeface = Typeface.SERIF
        textSize = 16f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun draw(nc: android.graphics.Canvas, gs: GameState) {
        nc.drawRect(130f, 80f, 510f, 420f, overlayPaint)

        nc.drawText("YOUR ROUNDS ARE DONE!", 320f, 130f, titlePaint)
        nc.drawText("SCORE", 300f, 175f, scoreLabelPaint)

        val scoreX = if (gs.telling == 100) 220f else 260f
        nc.drawText(gs.telling.toString(), scoreX, 260f, scoreValuePaint)
        nc.drawText("%", 400f, 250f, percentPaint)

        nc.drawText("OPTIONS", 320f, 300f, optionPaint)
        nc.drawText("[9]   VIEW SCORECARD (10s)", 320f, 330f, optionPaint)
        nc.drawText("[5]   SHOOT AGAIN", 320f, 355f, optionPaint)
        nc.drawText("[ESC] EXIT", 320f, 380f, optionPaint)
    }
}
