package za.co.target12

object GameConstants {
    // Virtual canvas
    const val CANVAS_W = 640f
    const val CANVAS_H = 480f

    // Playfield boundaries
    const val BG = 55f   // top
    const val OG = 430f  // bottom
    const val LG = 50f   // left
    const val RG = 582f  // right

    // Crosshair
    const val GR = 55f   // circle radius
    const val SK = 5f    // shot dot radius

    // Movement
    const val XD = 10f   // arrow key move
    const val YW_MOVE = 10f
    const val XJ = 50f   // WASD jump
    const val YJ = 50f

    // Total shots
    const val RONTES = 13

    // Drift / hand shake
    const val DRIFT_MAX_SPEED = 0.5f
    const val DRIFT_ACCEL = 0.06f
    const val DRIFT_SPRING = 0.005f
    const val DRIFT_DRAG = 0.95f

    // Heartbeat
    const val HEART_AMPLITUDE = 3.5f
    const val RESTING_BPM = 60f

    // ECG chart
    const val ECG_CHART_WIDTH = 200
    const val ECG_CHART_TIMESPAN = 2000f

    // Breathing
    const val BREATH_PERIOD = 5000f
    const val BREATH_AMPLITUDE_Y = 25.0f
    const val BREATH_AMPLITUDE_X = 4.0f
    const val BREATH_HOLD_MAX = 4000f
    const val BREATH_RECOVERY_TIME = 2000f

    // Touch controls
    const val JOYSTICK_RADIUS = 60f
    const val JOYSTICK_MAX_SPEED = 3f
    const val FIRE_BTN_X = 570f
    const val FIRE_BTN_Y = 420f
    const val FIRE_BTN_R = 52f
    const val BREATH_BTN_X = 570f
    const val BREATH_BTN_Y = 300f
    const val BREATH_BTN_R = 44f

    // Target positions
    data class Target(val x: Float, val y: Float, val label: String)

    val TARGETS = arrayOf(
        Target(90f, 80f, "1"),
        Target(240f, 80f, "2"),
        Target(390f, 80f, "3"),
        Target(540f, 80f, "4"),
        Target(170f, 220f, "5"),
        Target(320f, 220f, ""),   // practice (index 5)
        Target(470f, 220f, "6"),
        Target(90f, 350f, "7"),
        Target(240f, 350f, "8"),
        Target(390f, 350f, "9"),
        Target(540f, 350f, "10"),
    )

    val SCORING_TARGETS = intArrayOf(0, 1, 2, 3, 4, 6, 7, 8, 9, 10)

    // Intro demo shot positions and timings
    val DEMO_SHOTS = arrayOf(
        Triple(1500L, 340f, 255f),
        Triple(2500L, 320f, 260f),
        Triple(3500L, 320f, 275f),
    )
    const val INTRO_AUTO_ADVANCE = 5500L

    // Scorecard countdown
    const val SCORECARD_COUNTDOWN_START = 9
}
