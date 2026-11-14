package com.example.data

import androidx.room.InvalidationTracker
import androidx.room.RoomOpenDelegate
import androidx.room.migration.AutoMigrationSpec
import androidx.room.migration.Migration
import androidx.room.util.TableInfo
import androidx.room.util.TableInfo.Companion.read
import androidx.room.util.dropFtsSyncTriggers
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL")
public class AppDatabase_Impl : AppDatabase() {
    private val _questionDao: Lazy<QuestionDao> = lazy {
        QuestionDao_Impl(this)
    }

    protected override fun createOpenDelegate(): RoomOpenDelegate {
        val _openDelegate: RoomOpenDelegate = object : RoomOpenDelegate(
            1,
            "f862d051c898e3f40a56312bdaeee3d4",
            "b6ded2529b6c7f3225f5d4f7846ac4fb"
        ) {
            public override fun createAllTables(connection: SQLiteConnection) {
                connection.execSQL("CREATE TABLE IF NOT EXISTS `questions` (`id` INTEGER NOT NULL, `chapterNumber` INTEGER NOT NULL, `chapterTitle` TEXT NOT NULL, `questionNumber` INTEGER NOT NULL, `questionText` TEXT NOT NULL, `optionA` TEXT NOT NULL, `optionB` TEXT NOT NULL, `optionC` TEXT NOT NULL, `optionD` TEXT NOT NULL, `correctOption` TEXT NOT NULL, `totalAttempts` INTEGER NOT NULL, `wrongAttempts` INTEGER NOT NULL, `lastPracticedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))")
                connection.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)")
                connection.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, 'f862d051c898e3f40a56312bdaeee3d4')")
            }

            public override fun dropAllTables(connection: SQLiteConnection) {
                connection.execSQL("DROP TABLE IF EXISTS `questions`")
            }

            public override fun onCreate(connection: SQLiteConnection) {
            }

            public override fun onOpen(connection: SQLiteConnection) {
                internalInitInvalidationTracker(connection)
            }

            public override fun onPreMigrate(connection: SQLiteConnection) {
                dropFtsSyncTriggers(connection)
            }

            public override fun onPostMigrate(connection: SQLiteConnection) {
            }

            public override fun onValidateSchema(connection: SQLiteConnection): RoomOpenDelegate.ValidationResult {
                val _columnsQuestions: MutableMap<String, TableInfo.Column> = mutableMapOf()
                _columnsQuestions["id"] = TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["chapterNumber"] = TableInfo.Column("chapterNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["chapterTitle"] = TableInfo.Column("chapterTitle", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["questionNumber"] = TableInfo.Column("questionNumber", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["questionText"] = TableInfo.Column("questionText", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["optionA"] = TableInfo.Column("optionA", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["optionB"] = TableInfo.Column("optionB", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["optionC"] = TableInfo.Column("optionC", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["optionD"] = TableInfo.Column("optionD", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["correctOption"] = TableInfo.Column("correctOption", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["totalAttempts"] = TableInfo.Column("totalAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["wrongAttempts"] = TableInfo.Column("wrongAttempts", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                _columnsQuestions["lastPracticedAt"] = TableInfo.Column("lastPracticedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY)
                val _foreignKeysQuestions: MutableSet<TableInfo.ForeignKey> = mutableSetOf()
                val _indicesQuestions: MutableSet<TableInfo.Index> = mutableSetOf()
                val _infoQuestions = TableInfo("questions", _columnsQuestions, _foreignKeysQuestions, _indicesQuestions)
                val _existingQuestions: TableInfo = read(connection, "questions")
                if (_infoQuestions != _existingQuestions) {
                    return RoomOpenDelegate.ValidationResult(
                        false,
                        """
                        |questions(com.example.data.QuestionEntity).
                        | Expected:
                        |""".trimMargin() + _infoQuestions + """
                        |
                        | Found:
                        |""".trimMargin() + _existingQuestions
                    )
                }
                return RoomOpenDelegate.ValidationResult(true, null)
            }
        }
        return _openDelegate
    }

    protected override fun createInvalidationTracker(): InvalidationTracker {
        val _shadowTablesMap: MutableMap<String, String> = mutableMapOf()
        val _viewTables: MutableMap<String, Set<String>> = mutableMapOf()
        return InvalidationTracker(this, _shadowTablesMap, _viewTables, "questions")
    }

    public override fun clearAllTables() {
        super.performClear(false, "questions")
    }

    protected override fun getRequiredTypeConverterClasses(): Map<KClass<*>, List<KClass<*>>> {
        val _typeConvertersMap: MutableMap<KClass<*>, List<KClass<*>>> = mutableMapOf()
        _typeConvertersMap[QuestionDao::class] = QuestionDao_Impl.getRequiredConverters()
        return _typeConvertersMap
    }

    public override fun getRequiredAutoMigrationSpecClasses(): Set<KClass<out AutoMigrationSpec>> {
        return mutableSetOf()
    }

    public override fun createAutoMigrations(
        autoMigrationSpecs: Map<KClass<out AutoMigrationSpec>, AutoMigrationSpec>
    ): List<Migration> {
        return mutableListOf()
    }

    public override fun questionDao(): QuestionDao = _questionDao.value
}
