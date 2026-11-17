package com.example

import com.example.data.InitialQuestionsPart1
import com.example.data.InitialQuestionsPart2
import com.example.data.QuestionRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class ExampleUnitTest {
    @Test
    fun initialQuestions_containsAll156Questionsacross10Chapters() {
        val all = InitialQuestionsPart1.questions + InitialQuestionsPart2.questions
        assertEquals(156, all.size)
        val chapters = all.map { it.chapterNumber }.distinct().sorted()
        assertEquals((1..10).toList(), chapters)
    }

    @Test
    fun weightedWrongQuestionSelection_prefersHigherErrorRate() {
        val sample = InitialQuestionsPart1.questions.take(2)
        val highErrorQuestion = sample[0].copy(id = 1, totalAttempts = 10, wrongAttempts = 9) // 90% error rate
        val lowErrorQuestion = sample[1].copy(id = 2, totalAttempts = 10, wrongAttempts = 1)  // 10% error rate
        val pool = listOf(highErrorQuestion, lowErrorQuestion)

        val random = Random(42)
        var highPickedCount = 0
        repeat(500) {
            val picked = QuestionRepository.pickWeightedWrongQuestion(pool, lastQuestionId = null, random = random)
            if (picked?.id == 1) highPickedCount++
        }
        // High error rate question should be selected significantly more often than low error rate question
        assertTrue("Expected highErrorQuestion to be picked majority of times, got $highPickedCount/500", highPickedCount > 350)
    }

    @Test
    fun cyclicWrongQuestionSelection_coversAllOncePerCycleAndPrioritizesHigherErrorRateEarlier() {
        val sample = InitialQuestionsPart1.questions.take(4)
        val qHigh = sample[0].copy(id = 1, totalAttempts = 10, wrongAttempts = 9)   // 90%
        val qMidHigh = sample[1].copy(id = 2, totalAttempts = 10, wrongAttempts = 6) // 60%
        val qMidLow = sample[2].copy(id = 3, totalAttempts = 10, wrongAttempts = 3)  // 30%
        val qLow = sample[3].copy(id = 4, totalAttempts = 10, wrongAttempts = 1)     // 10%
        val pool = listOf(qHigh, qMidHigh, qMidLow, qLow)

        val random = Random(12345)
        var highFirstCount = 0
        var lowFirstCount = 0

        repeat(200) {
            var seen = emptySet<Int>()
            var cycle = 1
            var lastId: Int? = null
            val cycleDrawnIds = mutableListOf<Int>()

            repeat(pool.size) {
                val res = QuestionRepository.pickCycleWrongQuestion(
                    wrongQuestions = pool,
                    seenIdsInCycle = seen,
                    currentCycleNumber = cycle,
                    completedQuestionId = lastId,
                    random = random
                )
                val picked = res.question!!
                cycleDrawnIds.add(picked.id)
                lastId = picked.id
                seen = res.seenIdsAfterTransition
                cycle = res.cycleNumber
            }

            // Each cycle must cover every wrong question exactly once
            assertEquals(pool.map { it.id }.toSet(), cycleDrawnIds.toSet())
            if (cycleDrawnIds.first() == qHigh.id) highFirstCount++
            if (cycleDrawnIds.first() == qLow.id) lowFirstCount++

            // Next draw after completing all 4 starts cycle 2
            val nextCycleRes = QuestionRepository.pickCycleWrongQuestion(
                wrongQuestions = pool,
                seenIdsInCycle = seen,
                currentCycleNumber = cycle,
                completedQuestionId = lastId,
                random = random
            )
            assertEquals(2, nextCycleRes.cycleNumber)
            assertTrue(nextCycleRes.question != null)
        }

        assertTrue(
            "Expected highest error-rate question to be drawn first much more often than lowest ($highFirstCount vs $lowFirstCount)",
            highFirstCount > lowFirstCount * 5
        )
    }
}
