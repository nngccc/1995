package za.co.target12

object GameConstants {
    const val CANVAS_WIDTH = 640f
    const val CANVAS_HEIGHT = 480f

    // Playfield boundaries
    const val BG = 55f
    const val OG = 430f
    const val LG = 50f
    const val RG = 582f

    // Target positions — index 5 is practice
    val TARGET_X = floatArrayOf(90f, 240f, 390f, 540f, 170f, 320f, 470f, 90f, 240f, 390f, 540f)
    val TARGET_Y = floatArrayOf(80f, 80f, 80f, 80f, 220f, 220f, 220f, 350f, 350f, 350f, 350f)
    val TARGET_LABELS = arrayOf("1", "2", "3", "4", "5", "", "6", "7", "8", "9", "10")
    const val TARGET_COUNT = 11
    const val PRACTICE_INDEX = 5
    const val TARGET_RADIUS = 50f
    val TARGET_RINGS = floatArrayOf(5f, 15f, 25f, 35f)

    // Crosshair
    const val CROSSHAIR_RADIUS = 55f
    const val CROSSHAIR_TICK = 10f
    const val CENTER_DOT_RADIUS = 2f

    // Initial center
    const val INITIAL_CX = 320f
    const val INITIAL_CY = 220f

    // Movement steps
    const val MOVE_SMALL = 10f
    const val MOVE_LARGE = 50f

    // Drift
    const val DRIFT_ACCEL = 0.06f
    const val DRIFT_SPRING = 0.005f
    const val DRIFT_MAX_SPEED = 0.5f
    const val DRIFT_DRAG = 0.95f

    // Heartbeat
    const val HEART_AMPLITUDE = 3.5f
    const val HEART_BPM_DEFAULT = 60f
    const val HEART_CHASE_UP = 0.0004f
    const val HEART_CHASE_DOWN = 0.0002f

    // Breathing
    const val BREATH_PERIOD = 5000f
    const val BREATH_AMP_Y = 25.0f
    const val BREATH_AMP_X = 4.0f
    const val BREATH_HOLD_MAX = 4000f
    const val BREATH_HOLD_AUTO_RELEASE = 10000f
    const val BREATH_RECOVERY_TIME = 2000f
    const val BREATH_GASP_DURATION = 400f

    // Scoring
    const val MAX_SCORING_SHOTS = 10
    const val MAX_TOTAL_SHOTS = 13

    // Shot marks
    const val SHOT_MARK_RADIUS = 5f

    // ECG
    const val ECG_X = 220f
    const val ECG_Y = 432f
    const val ECG_W = 200f
    const val ECG_H = 20f
    const val ECG_BUFFER_SIZE = 200
    const val ECG_SAMPLE_INTERVAL = 10f

    // Breath bar
    const val BAR_X = 185f
    const val BAR_Y = 432f
    const val BAR_W = 25f
    const val BAR_H = 20f

    // Flash
    const val FLASH_RADIUS = 60f

    // Intro
    val DEMO_SHOT_TIMES = longArrayOf(1500L, 2500L, 3500L)
    val DEMO_SHOT_X = floatArrayOf(340f, 320f, 320f)
    val DEMO_SHOT_Y = floatArrayOf(255f, 260f, 275f)
    val DEMO_TARGET_X = floatArrayOf(170f, 320f, 470f)
    const val DEMO_TARGET_Y = 275f
    const val INTRO_ADVANCE_MS = 5500L

    // Scorecard countdown
    const val SCORECARD_COUNTDOWN = 9

    // Joystick
    const val JOYSTICK_RADIUS = 180f
    const val JOYSTICK_MAX_SPEED = 3f

    // Overlay rects
    const val RESULTS_X = 130f
    const val RESULTS_Y = 80f
    const val RESULTS_W = 380f
    const val RESULTS_H = 340f
    const val HELP_X = 130f
    const val HELP_Y = 80f
    const val HELP_W = 380f
    const val HELP_H = 374f

    // Wind
    const val WIND_MIN_INTERVAL = 5000L
    const val WIND_MAX_INTERVAL = 30000L
    const val WIND_GUST_MIN_DURATION = 3000L
    const val WIND_GUST_MAX_DURATION = 15000L
    const val WIND_GUST_FADE_TIME = 200f

    // Windsock UI
    const val WINDSOCK_X = 545f
    const val WINDSOCK_Y = 25f
    const val WINDSOCK_W = 80f
    const val WINDSOCK_H = 40f
}
