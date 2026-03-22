package za.co.target12.ui.rendering

import za.co.target12.GameConstants

data class ScaleInfo(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val scaledWidth: Float,
    val scaledHeight: Float
) {
    val leftMarginEnd: Float get() = offsetX
    val rightMarginStart: Float get() = offsetX + scaledWidth

    fun isLeftMargin(screenX: Float) = screenX < leftMarginEnd
    fun isRightMargin(screenX: Float) = screenX > rightMarginStart
    fun isOnCanvas(screenX: Float) = screenX in leftMarginEnd..rightMarginStart

    fun toCanvasX(screenX: Float) = (screenX - offsetX) / scale
    fun toCanvasY(screenY: Float) = (screenY - offsetY) / scale
}

object CanvasScaler {
    fun computeScale(screenW: Float, screenH: Float): ScaleInfo {
        val aspect = GameConstants.CANVAS_WIDTH / GameConstants.CANVAS_HEIGHT
        val viewAspect = screenW / screenH

        val sw: Float; val sh: Float
        if (viewAspect > aspect) {
            sh = screenH; sw = sh * aspect
        } else {
            sw = screenW; sh = sw / aspect
        }

        return ScaleInfo(
            scale = sw / GameConstants.CANVAS_WIDTH,
            offsetX = (screenW - sw) / 2f,
            offsetY = (screenH - sh) / 2f,
            scaledWidth = sw,
            scaledHeight = sh
        )
    }
}
