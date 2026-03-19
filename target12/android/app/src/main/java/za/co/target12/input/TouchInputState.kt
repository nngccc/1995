package za.co.target12.input

import za.co.target12.GameConstants.JOYSTICK_RADIUS

class TouchInputState {
    // Joystick
    var joystickPointerId: Int? = null
    var joystickBaseX = 0f
    var joystickBaseY = 0f
    var joystickDx = 0f
    var joystickDy = 0f

    // Fire button
    var firePointerId: Int? = null
    var fireJustPressed = false

    // Breath-hold button
    var breathPointerId: Int? = null
    var breathJustPressed = false
    var breathJustReleased = false

    // Margin layout info (set by GameScreen on each frame/resize)
    var marginWidth = 0f
    var leftMarginLeft = 0f    // screen X of left margin start
    var leftMarginRight = 0f   // screen X of left margin end (= canvas left)
    var rightMarginLeft = 0f   // screen X of right margin start (= canvas right)
    var rightMarginRight = 0f  // screen X of right margin end
    var screenHeight = 0f
    var isNarrowMargin = false

    // Button centers in screen coordinates (computed from margin layout)
    val fireBtnScreenX: Float get() = rightMarginLeft + marginWidth / 2f
    val fireBtnScreenY: Float get() = screenHeight * 0.75f
    val fireBtnRadius: Float get() = 156f

    val breathBtnScreenX: Float get() = leftMarginLeft + marginWidth / 2f
    val breathBtnScreenY: Float get() = screenHeight * 0.35f
    val breathBtnRadius: Float get() = 132f

    val joystickHintScreenX: Float get() = leftMarginLeft + marginWidth / 2f
    val joystickHintScreenY: Float get() = screenHeight * 0.65f

    fun updateLayout(scaleMarginLeft: Float, scaleMarginRight: Float, scaleMarginWidth: Float,
                     scaleScreenHeight: Float, narrow: Boolean) {
        leftMarginLeft = 0f
        leftMarginRight = scaleMarginLeft
        rightMarginLeft = scaleMarginRight
        rightMarginRight = scaleMarginRight + scaleMarginWidth
        marginWidth = if (narrow) 80f else scaleMarginWidth
        screenHeight = scaleScreenHeight
        isNarrowMargin = narrow
    }

    fun onPointerDown(id: Int, screenX: Float, screenY: Float) {
        // Fire button (right margin)
        if (firePointerId == null && ptInCircle(screenX, screenY, fireBtnScreenX, fireBtnScreenY, fireBtnRadius)) {
            firePointerId = id
            fireJustPressed = true
            return
        }

        // Breath-hold button (left margin, checked before joystick catch-all)
        if (breathPointerId == null && ptInCircle(screenX, screenY, breathBtnScreenX, breathBtnScreenY, breathBtnRadius)) {
            breathPointerId = id
            breathJustPressed = true
            return
        }

        // Joystick zone (left margin area)
        val inLeftMargin = if (isNarrowMargin) {
            screenX < leftMarginRight + 80f
        } else {
            screenX < leftMarginRight
        }
        if (joystickPointerId == null && inLeftMargin) {
            joystickPointerId = id
            joystickBaseX = screenX
            joystickBaseY = screenY
            joystickDx = 0f
            joystickDy = 0f
        }
    }

    fun onPointerMove(id: Int, screenX: Float, screenY: Float) {
        if (id != joystickPointerId) return
        var dx = screenX - joystickBaseX
        var dy = screenY - joystickBaseY
        val dist = kotlin.math.sqrt(dx * dx + dy * dy)
        if (dist > JOYSTICK_RADIUS) {
            dx = dx / dist * JOYSTICK_RADIUS
            dy = dy / dist * JOYSTICK_RADIUS
        }
        joystickDx = dx
        joystickDy = dy
    }

    fun onPointerUp(id: Int) {
        if (id == joystickPointerId) {
            joystickPointerId = null
            joystickDx = 0f
            joystickDy = 0f
        }
        if (id == firePointerId) {
            firePointerId = null
        }
        if (id == breathPointerId) {
            breathPointerId = null
            breathJustReleased = true
        }
    }

    fun consumeEvents() {
        fireJustPressed = false
        breathJustPressed = false
        breathJustReleased = false
    }

    private fun ptInCircle(px: Float, py: Float, cx: Float, cy: Float, r: Float): Boolean {
        val dx = px - cx
        val dy = py - cy
        return dx * dx + dy * dy <= r * r
    }
}
