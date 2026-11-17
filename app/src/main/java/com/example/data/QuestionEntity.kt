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
     * Higher error rate -> strictly higher probability of being drawn earlier in a cycle.
     */
    val drillWeight: Double
        get() {
            if (wrongAttempts <= 0) return 0.0
            val rate = errorRate.toDouble().coerceIn(0.001, 1.0)
            // Monotonically increasing with errorRate; wrongAttempts only breaks exact ties.
            return (rate * rate * 100.0) + (rate * 25.0) + (wrongAttempts.coerceAtMost(100) * 1e-6)
        }
}
