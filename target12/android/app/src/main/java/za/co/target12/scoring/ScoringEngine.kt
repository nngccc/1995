package za.co.target12.scoring

import za.co.target12.GameConstants
import za.co.target12.GameState
import za.co.target12.Shot
import kotlin.math.round
import kotlin.math.sqrt

object ScoringEngine {

    /** Returns true if round has ended. */
    fun processShot(state: GameState): Boolean {
        val shot = Shot(state.sightX, state.sightY)
        state.shots.add(shot)
        state.totalShots++

        // Determine nearest target
        val nearestIdx = nearestTarget(shot)
        if (nearestIdx != GameConstants.PRACTICE_INDEX) {
            state.scoringShots++
        }

        // Recalculate score
        state.score = computeScore(state)

        // Round ends?
        return state.scoringShots >= GameConstants.MAX_SCORING_SHOTS ||
                state.totalShots >= GameConstants.MAX_TOTAL_SHOTS
    }

    fun nearestTarget(shot: Shot): Int {
        var minDist = Float.MAX_VALUE
        var idx = 0
        for (i in 0 until GameConstants.TARGET_COUNT) {
            val dx = shot.x - GameConstants.TARGET_X[i]
            val dy = shot.y - GameConstants.TARGET_Y[i]
            val d = dx * dx + dy * dy
            if (d < minDist) { minDist = d; idx = i }
        }
        return idx
    }

    fun computeScore(state: GameState): Int {
        var total = 0
        for (i in 0 until GameConstants.TARGET_COUNT) {
            if (i == GameConstants.PRACTICE_INDEX) continue
            var minDist = Float.MAX_VALUE
            for (shot in state.shots) {
                val dx = shot.x - GameConstants.TARGET_X[i]
                val dy = shot.y - GameConstants.TARGET_Y[i]
                val d = sqrt(dx * dx + dy * dy)
                if (d < minDist) minDist = d
            }
            total += distanceToPoints(round(minDist).toInt())
        }
        return total
    }

    private fun distanceToPoints(dist: Int): Int = when {
        dist <= 11 -> 10
        dist <= 21 -> 9
        dist <= 30 -> 8
        dist <= 40 -> 7
        dist <= 56 -> 6
        else -> 0
    }
}
