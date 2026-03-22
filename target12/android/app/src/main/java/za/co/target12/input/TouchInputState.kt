package za.co.target12.input

/** Tracks multi-touch state for joystick, fire, and breath-hold. Screen-pixel coords. */
class TouchInputState {
    // Joystick
    var joystickActive = false
    var joystickPointerId = -1L
    var joystickBaseX = 0f
    var joystickBaseY = 0f
    var joystickDx = 0f
    var joystickDy = 0f

    // Fire
    var firePressed = false
    var firePointerId = -1L
    var firePending = false

    // Breath hold
    var breathPressed = false
    var breathPointerId = -1L

    fun resetJoystick() {
        joystickActive = false
        joystickPointerId = -1L
        joystickDx = 0f
        joystickDy = 0f
    }

    fun releaseFire() {
        firePressed = false
        firePointerId = -1L
    }

    fun releaseBreath() {
        breathPressed = false
        breathPointerId = -1L
    }
}
