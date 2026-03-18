package za.co.target12.scoring

import za.co.target12.GameConstants.RONTES
import za.co.target12.GameConstants.SCORING_TARGETS
import za.co.target12.GameConstants.TARGETS
import kotlin.math.abs
import kotlin.math.round
import kotlin.math.sqrt

object ScoringEngine {

    fun calculateScore(shotX: FloatArray, shotY: FloatArray, shotCount: Int): Int {
        var total = 0
        for (ti in SCORING_TARGETS) {
            val tx = TARGETS[ti].x
            val ty = TARGETS[ti].y
            var minDist = 60f

            for (s in 1..minOf(shotCount, RONTES)) {
                if (shotX[s] == 0f && shotY[s] == 0f) continue
                val dx = abs(tx - shotX[s])
                val dy = abs(ty - shotY[s])
                val dist = round(sqrt(dx * dx + dy * dy))
                if (dist < minDist) minDist = dist
            }

            total += when {
                minDist <= 11f -> 10
                minDist <= 21f -> 9
                minDist <= 30f -> 8
                minDist <= 40f -> 7
                minDist <= 56f -> 6
                else -> 0
            }
        }
        return total
    }

    fun countScoringShots(shotX: FloatArray, shotY: FloatArray, currentShot: Int): Int {
        var count = 0
        for (s in 1 until currentShot) {
            if (shotX[s] == 0f && shotY[s] == 0f) continue
            var minDist = Float.MAX_VALUE
            var closestIdx = -1
            for (ti in TARGETS.indices) {
                val dx = TARGETS[ti].x - shotX[s]
                val dy = TARGETS[ti].y - shotY[s]
                val dist = dx * dx + dy * dy
                if (dist < minDist) {
                    minDist = dist
                    closestIdx = ti
                }
            }
            if (closestIdx != 5) count++
        }
        return count
    }
}
