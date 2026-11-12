package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.math.roundToInt

@Entity(tableName = "questions")
data class QuestionEntity(
    @PrimaryKey val id: Int,
    val chapterNumber: Int,
    val chapterTitle: String,
    val questionNumber: Int,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctOption: String,
    val totalAttempts: Int = 0,
    val wrongAttempts: Int = 0,
    val lastPracticedAt: Long = 0L
) {
    val errorRate: Float
        get() = if (totalAttempts > 0) wrongAttempts.toFloat() / totalAttempts.toFloat() else 0f

    val errorRatePercentage: Float
        get() = if (totalAttempts > 0) (wrongAttempts.toFloat() * 1000f / totalAttempts.toFloat()).roundToInt() / 10f else 0f

    val isWrongQuestion: Boolean
        get() = wrongAttempts > 0

    fun optionTextFor(key: String): String = when (key.uppercase()) {
        "A" -> optionA
        "B" -> optionB
        "C" -> optionC
        "D" -> optionD
        else -> ""
    }

    /**
     * Weight used in Wrong-Question Drill Mode (错题专练模式):
     * Higher error rate -> significantly higher selection probability.
     */
    val drillWeight: Double
        get() {
            if (wrongAttempts <= 0) return 0.0
            val rate = errorRate.toDouble().coerceAtLeast(0.08)
            // Weight scales nonlinearly with errorRate so high-error-rate items appear much more frequently,
            // with a small tie-breaker bonus for higher wrongAttempts.
            return (rate * rate * 100.0) + (rate * 35.0) + (wrongAttempts.coerceAtMost(10) * 1.5)
        }
}
