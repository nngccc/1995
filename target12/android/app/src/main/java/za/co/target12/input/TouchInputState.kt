package za.co.target12.input

import za.co.target12.GameConstants.BREATH_BTN_R
import za.co.target12.GameConstants.BREATH_BTN_X
import za.co.target12.GameConstants.BREATH_BTN_Y
import za.co.target12.GameConstants.FIRE_BTN_R
import za.co.target12.GameConstants.FIRE_BTN_X
import za.co.target12.GameConstants.FIRE_BTN_Y
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

    fun onPointerDown(id: Int, canvasX: Float, canvasY: Float) {
        // Fire button
        if (firePointerId == null && ptInCircle(canvasX, canvasY, FIRE_BTN_X, FIRE_BTN_Y, FIRE_BTN_R)) {
            firePointerId = id
            fireJustPressed = true
            return
        }

        // Breath-hold button
        if (breathPointerId == null && ptInCircle(canvasX, canvasY, BREATH_BTN_X, BREATH_BTN_Y, BREATH_BTN_R)) {
            breathPointerId = id
            breathJustPressed = true
            return
        }

        // Joystick zone (left half)
        if (joystickPointerId == null && canvasX < 320f) {
            joystickPointerId = id
            joystickBaseX = canvasX
            joystickBaseY = canvasY
            joystickDx = 0f
            joystickDy = 0f
        }
    }

    fun onPointerMove(id: Int, canvasX: Float, canvasY: Float) {
        if (id != joystickPointerId) return
        var dx = canvasX - joystickBaseX
        var dy = canvasY - joystickBaseY
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
