package za.co.target12.ui.rendering

import za.co.target12.GameConstants.CANVAS_H
import za.co.target12.GameConstants.CANVAS_W

data class ScaleInfo(
    val scale: Float,
    val offsetX: Float,
    val offsetY: Float,
    val marginLeft: Float,
    val marginRight: Float,
    val canvasWidth: Float,
    val canvasHeight: Float,
    val screenWidth: Float,
    val screenHeight: Float,
) {
    val isNarrowMargin: Boolean get() = marginLeft < 80f
    val effectiveMarginWidth: Float get() = if (isNarrowMargin) 80f else marginLeft
}

fun computeScale(screenWidth: Float, screenHeight: Float): ScaleInfo {
    val targetAspect = CANVAS_W / CANVAS_H  // 4:3
    val screenAspect = screenWidth / screenHeight

    val w: Float
    val h: Float
    if (screenAspect > targetAspect) {
        // Screen is wider → fit by height
        h = screenHeight
        w = h * targetAspect
    } else {
        // Screen is taller → fit by width
        w = screenWidth
        h = w / targetAspect
    }

    val scale = w / CANVAS_W
    val offsetX = (screenWidth - w) / 2f
    val offsetY = (screenHeight - h) / 2f
    val marginLeft = offsetX
    val marginRight = offsetX
    return ScaleInfo(scale, offsetX, offsetY, marginLeft, marginRight, w, h, screenWidth, screenHeight)
}
