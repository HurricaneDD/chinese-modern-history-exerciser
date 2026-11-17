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
        data class CyclePickResult(
            val question: QuestionEntity?,
            val cycleNumber: Int,
            val seenIdsAfterTransition: Set<Int>
        )

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

        /**
         * Periodic (cyclic) weighted random selection without replacement within each cycle.
         * Every wrong question is drawn once per cycle; within a cycle, questions with higher
         * error rates have a strictly higher probability of being drawn earlier.
         *
         * @param wrongQuestions All current wrong questions (wrongAttempts > 0).
         * @param seenIdsInCycle Question IDs already completed in the current cycle.
         * @param currentCycleNumber Current 1-based cycle index.
         * @param completedQuestionId Optional ID of the question just finished/skipped to mark as seen.
         * @param random Random instance for deterministic testing.
         */
        fun pickCycleWrongQuestion(
            wrongQuestions: List<QuestionEntity>,
            seenIdsInCycle: Set<Int>,
            currentCycleNumber: Int,
            completedQuestionId: Int? = null,
            random: Random = Random.Default
        ): CyclePickResult {
            val eligibleAll = wrongQuestions.filter { it.wrongAttempts > 0 }
            if (eligibleAll.isEmpty()) {
                return CyclePickResult(
                    question = null,
                    cycleNumber = 1,
                    seenIdsAfterTransition = emptySet()
                )
            }

            val validIds = eligibleAll.map { it.id }.toSet()
            val updatedSeen = buildSet {
                addAll(seenIdsInCycle.intersect(validIds))
                if (completedQuestionId != null && completedQuestionId in validIds) {
                    add(completedQuestionId)
                }
            }

            val remaining = eligibleAll.filter { it.id !in updatedSeen }
            return if (remaining.isEmpty()) {
                val nextCycle = currentCycleNumber.coerceAtLeast(1) + 1
                val picked = pickWeightedWrongQuestion(
                    wrongQuestions = eligibleAll,
                    lastQuestionId = completedQuestionId,
                    random = random
                )
                CyclePickResult(
                    question = picked,
                    cycleNumber = nextCycle,
                    seenIdsAfterTransition = emptySet()
                )
            } else {
                val picked = pickWeightedWrongQuestion(
                    wrongQuestions = remaining,
                    lastQuestionId = completedQuestionId,
                    random = random
                )
                CyclePickResult(
                    question = picked,
                    cycleNumber = currentCycleNumber.coerceAtLeast(1),
                    seenIdsAfterTransition = updatedSeen
                )
            }
        }
    }
}
