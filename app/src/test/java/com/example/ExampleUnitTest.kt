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
}
