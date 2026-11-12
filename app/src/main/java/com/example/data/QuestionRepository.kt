package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlin.random.Random

class QuestionRepository(private val questionDao: QuestionDao) {
    val allQuestionsFlow: Flow<List<QuestionEntity>> = questionDao.getAllQuestionsFlow()

    suspend fun ensureSeeded() {
        val count = questionDao.getQuestionCount()
        if (count < 156) {
            val allInitial = InitialQuestionsPart1.questions + InitialQuestionsPart2.questions
            questionDao.insertAll(allInitial)
        }
    }

    suspend fun recordAnswer(questionId: Int, isWrong: Boolean) {
        questionDao.recordAnswer(questionId = questionId, isWrong = isWrong)
    }

    suspend fun updateStats(questionId: Int, totalAttempts: Int, wrongAttempts: Int) {
        questionDao.updateStats(
            questionId = questionId,
            totalAttempts = totalAttempts,
            wrongAttempts = wrongAttempts
        )
    }

    suspend fun resetAllStats() {
        questionDao.resetAllStats()
    }

    companion object {
        /**
         * Weighted random selection for Wrong-Question Drill Mode (错题专练模式).
         * Questions with higher error rates have a higher probability of being picked.
         * Avoids picking `lastQuestionId` twice in a row when at least 2 wrong questions exist.
         */
        fun pickWeightedWrongQuestion(
            wrongQuestions: List<QuestionEntity>,
            lastQuestionId: Int? = null,
            random: Random = Random.Default
        ): QuestionEntity? {
            val eligible = wrongQuestions.filter { it.wrongAttempts > 0 }
            if (eligible.isEmpty()) return null
            if (eligible.size == 1) return eligible.first()

            val pool = eligible.filter { it.id != lastQuestionId }.ifEmpty { eligible }
            val totalWeight = pool.sumOf { it.drillWeight }
            if (totalWeight <= 0.0) return pool.random(random)

            var roll = random.nextDouble(totalWeight)
            for (item in pool) {
                roll -= item.drillWeight
                if (roll <= 0.0) {
                    return item
                }
            }
            return pool.last()
        }
    }
}
