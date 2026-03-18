package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface

object ScorecardOverlayRenderer {

    private val boxPaint = Paint().apply {
        color = 0xE6000064.toInt()  // rgba(0,0,100,0.9)
        style = Paint.Style.FILL
    }

    private val numberPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.SERIF
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    fun draw(nc: android.graphics.Canvas, countdown: Int) {
        nc.drawRect(290f, 400f, 350f, 435f, boxPaint)
        nc.drawText(countdown.toString(), 320f, 425f, numberPaint)
    }
}
