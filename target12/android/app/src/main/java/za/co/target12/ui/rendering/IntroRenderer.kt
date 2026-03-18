package za.co.target12.ui.rendering

import android.graphics.Paint
import android.graphics.Typeface
import za.co.target12.GameState

object IntroRenderer {

    private val greenPaint = Paint().apply {
        color = 0xFF00AA00.toInt()
        typeface = Typeface.SERIF
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val yellowPaint = Paint().apply {
        color = 0xFFFFFF00.toInt()
        typeface = Typeface.SERIF
        textSize = 24f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val redPaint = Paint().apply {
        color = 0xFFAA0000.toInt()
        typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        textSize = 30f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val bluePaint = Paint().apply {
        color = 0xFF0000AA.toInt()
        typeface = Typeface.SERIF
        textSize = 22f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val creditsPaint = Paint().apply {
        color = 0xFFFFFFFF.toInt()
        typeface = Typeface.SANS_SERIF
        textSize = 14f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val hintPaint = Paint().apply {
        color = 0xFF888888.toInt()
        typeface = Typeface.SANS_SERIF
        textSize = 12f
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
    }

    private val shotPaint = Paint().apply {
        color = 0xFFAA0000.toInt()
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    fun draw(nc: android.graphics.Canvas, gs: GameState) {
        nc.drawColor(0xFF000000.toInt())

        nc.drawText("SOUTH AFRICAN", 320f, 75f, greenPaint)
        nc.drawText("NATIONAL", 320f, 110f, yellowPaint)
        nc.drawText("BISLEY SHOOTING 1995", 320f, 160f, redPaint)
        nc.drawText("(0.22\" CALIBRE)", 320f, 195f, bluePaint)

        // Three demo targets
        for (tx in intArrayOf(170, 320, 470)) {
            ScorecardRenderer.drawTarget(nc, tx.toFloat(), 275f)
        }

        // Demo shots
        if (gs.introStep >= 1) nc.drawCircle(340f, 255f, 5f, shotPaint)
        if (gs.introStep >= 2) nc.drawCircle(320f, 260f, 5f, shotPaint)
        if (gs.introStep >= 3) nc.drawCircle(320f, 275f, 5f, shotPaint)

        nc.drawText("PROGRAMMING - NICO GERBER", 320f, 420f, creditsPaint)
        nc.drawText("Press [6] for Help  |  Press any key to start", 320f, 450f, hintPaint)
    }
}
