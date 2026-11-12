package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions ORDER BY chapterNumber ASC, questionNumber ASC")
    fun getAllQuestionsFlow(): Flow<List<QuestionEntity>>

    @Query("SELECT * FROM questions ORDER BY chapterNumber ASC, questionNumber ASC")
    suspend fun getAllQuestionsOnce(): List<QuestionEntity>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getQuestionCount(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(questions: List<QuestionEntity>)

    @Query(
        """
        UPDATE questions 
        SET totalAttempts = totalAttempts + 1,
            wrongAttempts = wrongAttempts + CASE WHEN :isWrong THEN 1 ELSE 0 END,
            lastPracticedAt = :timestamp
        WHERE id = :questionId
        """
    )
    suspend fun recordAnswer(questionId: Int, isWrong: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query(
        """
        UPDATE questions 
        SET totalAttempts = :totalAttempts,
            wrongAttempts = :wrongAttempts,
            lastPracticedAt = :timestamp
        WHERE id = :questionId
        """
    )
    suspend fun updateStats(questionId: Int, totalAttempts: Int, wrongAttempts: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE questions SET totalAttempts = 0, wrongAttempts = 0, lastPracticedAt = 0")
    suspend fun resetAllStats()
}
