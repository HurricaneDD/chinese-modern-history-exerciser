package com.example.data

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Suppress("UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL")
public class QuestionDao_Impl(
    private val __db: RoomDatabase,
) : QuestionDao {
    private val __insertAdapterOfQuestionEntity: EntityInsertAdapter<QuestionEntity> =
        object : EntityInsertAdapter<QuestionEntity>() {
            protected override fun createQuery(): String =
                "INSERT OR IGNORE INTO `questions` (`id`,`chapterNumber`,`chapterTitle`,`questionNumber`,`questionText`,`optionA`,`optionB`,`optionC`,`optionD`,`correctOption`,`totalAttempts`,`wrongAttempts`,`lastPracticedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

            protected override fun bind(statement: SQLiteStatement, entity: QuestionEntity) {
                statement.bindLong(1, entity.id.toLong())
                statement.bindLong(2, entity.chapterNumber.toLong())
                statement.bindText(3, entity.chapterTitle)
                statement.bindLong(4, entity.questionNumber.toLong())
                statement.bindText(5, entity.questionText)
                statement.bindText(6, entity.optionA)
                statement.bindText(7, entity.optionB)
                statement.bindText(8, entity.optionC)
                statement.bindText(9, entity.optionD)
                statement.bindText(10, entity.correctOption)
                statement.bindLong(11, entity.totalAttempts.toLong())
                statement.bindLong(12, entity.wrongAttempts.toLong())
                statement.bindLong(13, entity.lastPracticedAt)
            }
        }

    public override suspend fun insertAll(questions: List<QuestionEntity>): Unit =
        performSuspending(__db, false, true) { _connection ->
            __insertAdapterOfQuestionEntity.insert(_connection, questions)
        }

    public override fun getAllQuestionsFlow(): Flow<List<QuestionEntity>> {
        val _sql = "SELECT * FROM questions ORDER BY chapterNumber ASC, questionNumber ASC"
        return createFlow(__db, false, arrayOf("questions")) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
                val _columnIndexOfChapterNumber: Int = getColumnIndexOrThrow(_stmt, "chapterNumber")
                val _columnIndexOfChapterTitle: Int = getColumnIndexOrThrow(_stmt, "chapterTitle")
                val _columnIndexOfQuestionNumber: Int = getColumnIndexOrThrow(_stmt, "questionNumber")
                val _columnIndexOfQuestionText: Int = getColumnIndexOrThrow(_stmt, "questionText")
                val _columnIndexOfOptionA: Int = getColumnIndexOrThrow(_stmt, "optionA")
                val _columnIndexOfOptionB: Int = getColumnIndexOrThrow(_stmt, "optionB")
                val _columnIndexOfOptionC: Int = getColumnIndexOrThrow(_stmt, "optionC")
                val _columnIndexOfOptionD: Int = getColumnIndexOrThrow(_stmt, "optionD")
                val _columnIndexOfCorrectOption: Int = getColumnIndexOrThrow(_stmt, "correctOption")
                val _columnIndexOfTotalAttempts: Int = getColumnIndexOrThrow(_stmt, "totalAttempts")
                val _columnIndexOfWrongAttempts: Int = getColumnIndexOrThrow(_stmt, "wrongAttempts")
                val _columnIndexOfLastPracticedAt: Int = getColumnIndexOrThrow(_stmt, "lastPracticedAt")
                val _result: MutableList<QuestionEntity> = mutableListOf()
                while (_stmt.step()) {
                    val _item = QuestionEntity(
                        id = _stmt.getLong(_columnIndexOfId).toInt(),
                        chapterNumber = _stmt.getLong(_columnIndexOfChapterNumber).toInt(),
                        chapterTitle = _stmt.getText(_columnIndexOfChapterTitle),
                        questionNumber = _stmt.getLong(_columnIndexOfQuestionNumber).toInt(),
                        questionText = _stmt.getText(_columnIndexOfQuestionText),
                        optionA = _stmt.getText(_columnIndexOfOptionA),
                        optionB = _stmt.getText(_columnIndexOfOptionB),
                        optionC = _stmt.getText(_columnIndexOfOptionC),
                        optionD = _stmt.getText(_columnIndexOfOptionD),
                        correctOption = _stmt.getText(_columnIndexOfCorrectOption),
                        totalAttempts = _stmt.getLong(_columnIndexOfTotalAttempts).toInt(),
                        wrongAttempts = _stmt.getLong(_columnIndexOfWrongAttempts).toInt(),
                        lastPracticedAt = _stmt.getLong(_columnIndexOfLastPracticedAt)
                    )
                    _result.add(_item)
                }
                _result
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun getAllQuestionsOnce(): List<QuestionEntity> {
        val _sql = "SELECT * FROM questions ORDER BY chapterNumber ASC, questionNumber ASC"
        return performSuspending(__db, true, false) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
                val _columnIndexOfChapterNumber: Int = getColumnIndexOrThrow(_stmt, "chapterNumber")
                val _columnIndexOfChapterTitle: Int = getColumnIndexOrThrow(_stmt, "chapterTitle")
                val _columnIndexOfQuestionNumber: Int = getColumnIndexOrThrow(_stmt, "questionNumber")
                val _columnIndexOfQuestionText: Int = getColumnIndexOrThrow(_stmt, "questionText")
                val _columnIndexOfOptionA: Int = getColumnIndexOrThrow(_stmt, "optionA")
                val _columnIndexOfOptionB: Int = getColumnIndexOrThrow(_stmt, "optionB")
                val _columnIndexOfOptionC: Int = getColumnIndexOrThrow(_stmt, "optionC")
                val _columnIndexOfOptionD: Int = getColumnIndexOrThrow(_stmt, "optionD")
                val _columnIndexOfCorrectOption: Int = getColumnIndexOrThrow(_stmt, "correctOption")
                val _columnIndexOfTotalAttempts: Int = getColumnIndexOrThrow(_stmt, "totalAttempts")
                val _columnIndexOfWrongAttempts: Int = getColumnIndexOrThrow(_stmt, "wrongAttempts")
                val _columnIndexOfLastPracticedAt: Int = getColumnIndexOrThrow(_stmt, "lastPracticedAt")
                val _result: MutableList<QuestionEntity> = mutableListOf()
                while (_stmt.step()) {
                    val _item = QuestionEntity(
                        id = _stmt.getLong(_columnIndexOfId).toInt(),
                        chapterNumber = _stmt.getLong(_columnIndexOfChapterNumber).toInt(),
                        chapterTitle = _stmt.getText(_columnIndexOfChapterTitle),
                        questionNumber = _stmt.getLong(_columnIndexOfQuestionNumber).toInt(),
                        questionText = _stmt.getText(_columnIndexOfQuestionText),
                        optionA = _stmt.getText(_columnIndexOfOptionA),
                        optionB = _stmt.getText(_columnIndexOfOptionB),
                        optionC = _stmt.getText(_columnIndexOfOptionC),
                        optionD = _stmt.getText(_columnIndexOfOptionD),
                        correctOption = _stmt.getText(_columnIndexOfCorrectOption),
                        totalAttempts = _stmt.getLong(_columnIndexOfTotalAttempts).toInt(),
                        wrongAttempts = _stmt.getLong(_columnIndexOfWrongAttempts).toInt(),
                        lastPracticedAt = _stmt.getLong(_columnIndexOfLastPracticedAt)
                    )
                    _result.add(_item)
                }
                _result
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun getQuestionCount(): Int {
        val _sql = "SELECT COUNT(*) FROM questions"
        return performSuspending(__db, true, false) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                if (_stmt.step()) {
                    _stmt.getLong(0).toInt()
                } else {
                    0
                }
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun recordAnswer(
        questionId: Int,
        isWrong: Boolean,
        timestamp: Long,
    ) {
        val _sql = """
            UPDATE questions 
            SET totalAttempts = totalAttempts + 1,
                wrongAttempts = wrongAttempts + CASE WHEN ? THEN 1 ELSE 0 END,
                lastPracticedAt = ?
            WHERE id = ?
        """.trimIndent()
        return performSuspending(__db, false, true) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.bindLong(1, if (isWrong) 1L else 0L)
                _stmt.bindLong(2, timestamp)
                _stmt.bindLong(3, questionId.toLong())
                _stmt.step()
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun updateStats(
        questionId: Int,
        totalAttempts: Int,
        wrongAttempts: Int,
        timestamp: Long,
    ) {
        val _sql = """
            UPDATE questions 
            SET totalAttempts = ?,
                wrongAttempts = ?,
                lastPracticedAt = ?
            WHERE id = ?
        """.trimIndent()
        return performSuspending(__db, false, true) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.bindLong(1, totalAttempts.toLong())
                _stmt.bindLong(2, wrongAttempts.toLong())
                _stmt.bindLong(3, timestamp)
                _stmt.bindLong(4, questionId.toLong())
                _stmt.step()
            } finally {
                _stmt.close()
            }
        }
    }

    public override suspend fun resetAllStats() {
        val _sql = "UPDATE questions SET totalAttempts = 0, wrongAttempts = 0, lastPracticedAt = 0"
        return performSuspending(__db, false, true) { _connection ->
            val _stmt: SQLiteStatement = _connection.prepare(_sql)
            try {
                _stmt.step()
            } finally {
                _stmt.close()
            }
        }
    }

    public companion object {
        public fun getRequiredConverters(): List<KClass<*>> = emptyList()
    }
}
