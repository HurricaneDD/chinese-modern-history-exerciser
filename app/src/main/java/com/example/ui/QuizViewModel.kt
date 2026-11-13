package com.example.ui

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.QuestionEntity
import com.example.data.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppTab {
    HOME,
    STUDY,
    PRACTICE,
    WRONG_DRILL,
    QUESTION_BANK
}

enum class BankSortOrder(val label: String) {
    SEQUENTIAL("按章节与题号顺序"),
    ERROR_RATE_DESC("按错误率从高到低"),
    WRONG_COUNT_DESC("按写错遍数从高到低"),
    ATTEMPTS_DESC("按写过遍数从高到低")
}

data class InternalControlState(
    val currentTab: AppTab = AppTab.HOME,
    // Study Mode (学习模式：按单元和题号顺序，支持进度记忆与进入询问)
    val showStudyEntryDialog: Boolean = false,
    val savedStudyChapterFilter: Int? = null,
    val savedStudyIndex: Int = 0,
    val studyChapterFilter: Int? = null, // null = all chapters in order
    val studyIndex: Int = 0,
    val studySelectedOption: String? = null,
    val studyMemorizeMode: Boolean = false,
    // Practice Mode (刷题模式：随机打乱，记录数据，可半途退出)
    val practiceQueueIds: List<Int> = emptyList(),
    val practiceIndex: Int = 0,
    val practiceSelectedOption: String? = null,
    val practiceSessionActive: Boolean = false,
    val practiceExitedSummary: Boolean = false,
    val practiceSessionAnswered: Int = 0,
    val practiceSessionCorrect: Int = 0,
    val practiceSessionWrong: Int = 0,
    // Wrong-Question Drill Mode (错题专练模式：加权随机错题，再次写错记录)
    val drillCurrentQuestionId: Int? = null,
    val drillSelectedOption: String? = null,
    val drillSessionAnswered: Int = 0,
    val drillSessionWrongAgain: Int = 0,
    val drillSessionCorrect: Int = 0,
    // Question Bank Mode (查看题库：展示遍数/错数/错误率，专门展示错题，按错误率排序)
    val bankOnlyWrong: Boolean = false,
    val bankSortOrder: BankSortOrder = BankSortOrder.SEQUENTIAL,
    val bankChapterFilter: Int? = null,
    val bankSearchQuery: String = ""
)

data class QuizUiState(
    val isLoading: Boolean = true,
    val allQuestions: List<QuestionEntity> = emptyList(),
    val control: InternalControlState = InternalControlState()
) {
    val totalQuestionsCount: Int get() = allQuestions.size
    val attemptedQuestionsCount: Int get() = allQuestions.count { it.totalAttempts > 0 }
    val wrongQuestions: List<QuestionEntity> get() = allQuestions.filter { it.wrongAttempts > 0 }
    val wrongQuestionsCount: Int get() = wrongQuestions.size
    val totalAttemptsSum: Int get() = allQuestions.sumOf { it.totalAttempts }
    val totalWrongSum: Int get() = allQuestions.sumOf { it.wrongAttempts }
    val overallAccuracyPercent: Int
        get() = if (totalAttemptsSum > 0) {
            (((totalAttemptsSum - totalWrongSum) * 100f) / totalAttemptsSum).toInt()
        } else 0

    // Study Mode derived list
    val studyQuestions: List<QuestionEntity>
        get() = if (control.studyChapterFilter == null) {
            allQuestions
        } else {
            allQuestions.filter { it.chapterNumber == control.studyChapterFilter }
        }

    val currentStudyQuestion: QuestionEntity?
        get() = studyQuestions.getOrNull(control.studyIndex.coerceIn(0, (studyQuestions.size - 1).coerceAtLeast(0)))

    // Saved study progress description for the entry prompt dialog
    val savedStudyProgressLabel: String
        get() {
            val chapterPool = if (control.savedStudyChapterFilter == null) {
                allQuestions
            } else {
                allQuestions.filter { it.chapterNumber == control.savedStudyChapterFilter }
            }
            val safeIdx = control.savedStudyIndex.coerceIn(0, (chapterPool.size - 1).coerceAtLeast(0))
            val q = chapterPool.getOrNull(safeIdx)
            val scopeText = control.savedStudyChapterFilter?.let { "第${it}章" } ?: "全部章节"
            return if (q != null) {
                "$scopeText · 第 ${safeIdx + 1}/${chapterPool.size} 题（第${q.chapterNumber}章第${q.questionNumber}题）"
            } else {
                "$scopeText · 第 ${safeIdx + 1} 题"
            }
        }

    // Practice Mode derived question
    private val questionById: Map<Int, QuestionEntity>
        get() = allQuestions.associateBy { it.id }

    val currentPracticeQuestion: QuestionEntity?
        get() = control.practiceQueueIds.getOrNull(control.practiceIndex)?.let { questionById[it] }

    // Wrong Drill derived question
    val currentDrillQuestion: QuestionEntity?
        get() = control.drillCurrentQuestionId?.let { questionById[it] }

    // Question Bank filtered and sorted list
    val filteredBankQuestions: List<QuestionEntity>
        get() {
            val query = control.bankSearchQuery.trim()
            val base = allQuestions.filter { q ->
                val matchesWrong = !control.bankOnlyWrong || q.wrongAttempts > 0
                val matchesChapter = control.bankChapterFilter == null || q.chapterNumber == control.bankChapterFilter
                val matchesQuery = query.isEmpty() ||
                    q.questionText.contains(query, ignoreCase = true) ||
                    q.optionA.contains(query, ignoreCase = true) ||
                    q.optionB.contains(query, ignoreCase = true) ||
                    q.optionC.contains(query, ignoreCase = true) ||
                    q.optionD.contains(query, ignoreCase = true) ||
                    q.chapterTitle.contains(query, ignoreCase = true)
                matchesWrong && matchesChapter && matchesQuery
            }
            return when (control.bankSortOrder) {
                BankSortOrder.SEQUENTIAL -> base.sortedWith(
                    compareBy<QuestionEntity> { it.chapterNumber }.thenBy { it.questionNumber }
                )
                BankSortOrder.ERROR_RATE_DESC -> base.sortedWith(
                    compareByDescending<QuestionEntity> { it.errorRate }
                        .thenByDescending { it.wrongAttempts }
                        .thenByDescending { it.totalAttempts }
                        .thenBy { it.chapterNumber }
                        .thenBy { it.questionNumber }
                )
                BankSortOrder.WRONG_COUNT_DESC -> base.sortedWith(
                    compareByDescending<QuestionEntity> { it.wrongAttempts }
                        .thenByDescending { it.errorRate }
                        .thenBy { it.chapterNumber }
                        .thenBy { it.questionNumber }
                )
                BankSortOrder.ATTEMPTS_DESC -> base.sortedWith(
                    compareByDescending<QuestionEntity> { it.totalAttempts }
                        .thenByDescending { it.errorRate }
                        .thenBy { it.chapterNumber }
                        .thenBy { it.questionNumber }
                )
            }
        }
}

class QuizViewModel(
    private val repository: QuestionRepository,
    private val prefs: SharedPreferences? = null
) : ViewModel() {
    companion object {
        private const val KEY_SAVED_STUDY_CHAPTER = "saved_study_chapter"
        private const val KEY_SAVED_STUDY_INDEX = "saved_study_index"
    }

    private val initialSavedChapter: Int? = prefs?.getInt(KEY_SAVED_STUDY_CHAPTER, -1)?.takeIf { it in 1..10 }
    private val initialSavedIndex: Int = prefs?.getInt(KEY_SAVED_STUDY_INDEX, 0)?.coerceAtLeast(0) ?: 0

    private val controlState = MutableStateFlow(
        InternalControlState(
            savedStudyChapterFilter = initialSavedChapter,
            savedStudyIndex = initialSavedIndex
        )
    )

    val uiState: StateFlow<QuizUiState> = combine(
        repository.allQuestionsFlow,
        controlState
    ) { questions, control ->
        QuizUiState(
            isLoading = questions.isEmpty(),
            allQuestions = questions,
            control = control
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = QuizUiState()
    )

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    private var autoAdvanceJob: kotlinx.coroutines.Job? = null

    private fun cancelAutoAdvance() {
        autoAdvanceJob?.cancel()
        autoAdvanceJob = null
    }

    private fun persistStudyProgress(chapter: Int?, index: Int) {
        prefs?.edit()
            ?.putInt(KEY_SAVED_STUDY_CHAPTER, chapter ?: -1)
            ?.putInt(KEY_SAVED_STUDY_INDEX, index)
            ?.apply()
    }

    /**
     * Bottom navigation only switches between HOME and QUESTION_BANK.
     */
    fun selectMainTab(tab: AppTab) {
        if (tab != AppTab.HOME && tab != AppTab.QUESTION_BANK) return
        cancelAutoAdvance()
        controlState.update { state ->
            state.copy(
                currentTab = tab,
                showStudyEntryDialog = false,
                practiceQueueIds = emptyList(),
                practiceSessionActive = false,
                practiceExitedSummary = false,
                drillCurrentQuestionId = null,
                drillSelectedOption = null
            )
        }
    }

    /**
     * Exits whichever active mode (Study / Practice / Wrong Drill) is currently running
     * and returns cleanly to HOME while preserving saved study progress.
     */
    fun exitActiveModeToHome() {
        cancelAutoAdvance()
        controlState.update { state ->
            state.copy(
                currentTab = AppTab.HOME,
                showStudyEntryDialog = false,
                studySelectedOption = null,
                practiceQueueIds = emptyList(),
                practiceIndex = 0,
                practiceSelectedOption = null,
                practiceSessionActive = false,
                practiceExitedSummary = false,
                practiceSessionAnswered = 0,
                practiceSessionCorrect = 0,
                practiceSessionWrong = 0,
                drillCurrentQuestionId = null,
                drillSelectedOption = null,
                drillSessionAnswered = 0,
                drillSessionWrongAgain = 0,
                drillSessionCorrect = 0
            )
        }
    }

    // ---------------- Study Mode Actions (学习模式：含记忆与询问弹窗) ----------------
    fun requestEnterStudyMode() {
        controlState.update { it.copy(showStudyEntryDialog = true) }
    }

    fun dismissStudyEntryDialog() {
        controlState.update { it.copy(showStudyEntryDialog = false) }
    }

    fun enterStudyModeFromStart() {
        cancelAutoAdvance()
        persistStudyProgress(chapter = null, index = 0)
        controlState.update {
            it.copy(
                currentTab = AppTab.STUDY,
                showStudyEntryDialog = false,
                savedStudyChapterFilter = null,
                savedStudyIndex = 0,
                studyChapterFilter = null,
                studyIndex = 0,
                studySelectedOption = null,
                studyMemorizeMode = false,
                practiceQueueIds = emptyList(),
                practiceSessionActive = false,
                practiceExitedSummary = false,
                drillCurrentQuestionId = null
            )
        }
    }

    fun enterStudyModeContinue() {
        cancelAutoAdvance()
        controlState.update {
            it.copy(
                currentTab = AppTab.STUDY,
                showStudyEntryDialog = false,
                studyChapterFilter = it.savedStudyChapterFilter,
                studyIndex = it.savedStudyIndex,
                studySelectedOption = null,
                practiceQueueIds = emptyList(),
                practiceSessionActive = false,
                practiceExitedSummary = false,
                drillCurrentQuestionId = null
            )
        }
    }

    fun setStudyChapter(chapter: Int?) {
        cancelAutoAdvance()
        persistStudyProgress(chapter = chapter, index = 0)
        controlState.update {
            it.copy(
                studyChapterFilter = chapter,
                studyIndex = 0,
                savedStudyChapterFilter = chapter,
                savedStudyIndex = 0,
                studySelectedOption = null
            )
        }
    }

    fun jumpToStudyIndex(index: Int) {
        cancelAutoAdvance()
        val maxIndex = (uiState.value.studyQuestions.size - 1).coerceAtLeast(0)
        val clamped = index.coerceIn(0, maxIndex)
        persistStudyProgress(chapter = controlState.value.studyChapterFilter, index = clamped)
        controlState.update {
            it.copy(
                studyIndex = clamped,
                savedStudyIndex = clamped,
                studySelectedOption = null
            )
        }
    }

    fun nextStudyQuestion() {
        cancelAutoAdvance()
        val list = uiState.value.studyQuestions
        if (list.isEmpty()) return
        val nextIdx = (controlState.value.studyIndex + 1).coerceAtMost(list.size - 1)
        persistStudyProgress(chapter = controlState.value.studyChapterFilter, index = nextIdx)
        controlState.update {
            it.copy(
                studyIndex = nextIdx,
                savedStudyIndex = nextIdx,
                studySelectedOption = null
            )
        }
    }

    fun previousStudyQuestion() {
        cancelAutoAdvance()
        val prevIdx = (controlState.value.studyIndex - 1).coerceAtLeast(0)
        persistStudyProgress(chapter = controlState.value.studyChapterFilter, index = prevIdx)
        controlState.update {
            it.copy(
                studyIndex = prevIdx,
                savedStudyIndex = prevIdx,
                studySelectedOption = null
            )
        }
    }

    fun toggleStudyMemorizeMode() {
        controlState.update { it.copy(studyMemorizeMode = !it.studyMemorizeMode) }
    }

    fun selectStudyOption(option: String) {
        val currentControl = controlState.value
        if (currentControl.studySelectedOption != null) return
        val question = uiState.value.currentStudyQuestion ?: return
        val isCorrect = option.equals(question.correctOption, ignoreCase = true)
        controlState.update { it.copy(studySelectedOption = option) }

        if (isCorrect) {
            cancelAutoAdvance()
            autoAdvanceJob = viewModelScope.launch {
                kotlinx.coroutines.delay(260L)
                val list = uiState.value.studyQuestions
                if (controlState.value.studyIndex < list.size - 1) {
                    nextStudyQuestion()
                }
            }
        }
    }

    // ---------------- Practice Mode Actions (刷题模式) ----------------
    fun startNewPracticeSession() {
        cancelAutoAdvance()
        val allIds = uiState.value.allQuestions.map { it.id }.shuffled()
        controlState.update {
            it.copy(
                currentTab = AppTab.PRACTICE,
                showStudyEntryDialog = false,
                practiceQueueIds = allIds,
                practiceIndex = 0,
                practiceSelectedOption = null,
                practiceSessionActive = true,
                practiceExitedSummary = false,
                practiceSessionAnswered = 0,
                practiceSessionCorrect = 0,
                practiceSessionWrong = 0,
                drillCurrentQuestionId = null
            )
        }
    }

    fun selectPracticeOption(option: String) {
        val state = controlState.value
        if (state.practiceSelectedOption != null || !state.practiceSessionActive) return
        val question = uiState.value.currentPracticeQuestion ?: return
        val isWrong = !option.equals(question.correctOption, ignoreCase = true)

        controlState.update {
            it.copy(
                practiceSelectedOption = option,
                practiceSessionAnswered = it.practiceSessionAnswered + 1,
                practiceSessionCorrect = it.practiceSessionCorrect + if (isWrong) 0 else 1,
                practiceSessionWrong = it.practiceSessionWrong + if (isWrong) 1 else 0
            )
        }
        viewModelScope.launch {
            repository.recordAnswer(questionId = question.id, isWrong = isWrong)
        }

        if (!isWrong) {
            cancelAutoAdvance()
            autoAdvanceJob = viewModelScope.launch {
                kotlinx.coroutines.delay(260L)
                nextPracticeQuestion()
            }
        }
    }

    fun nextPracticeQuestion() {
        cancelAutoAdvance()
        val state = controlState.value
        val total = state.practiceQueueIds.size
        if (total == 0) return
        if (state.practiceIndex + 1 >= total) {
            controlState.update {
                it.copy(
                    practiceSessionActive = false,
                    practiceExitedSummary = true,
                    practiceSelectedOption = null
                )
            }
        } else {
            controlState.update {
                it.copy(
                    practiceIndex = it.practiceIndex + 1,
                    practiceSelectedOption = null
                )
            }
        }
    }

    fun exitPracticeMidway() {
        cancelAutoAdvance()
        controlState.update {
            it.copy(
                practiceSessionActive = false,
                practiceExitedSummary = true
            )
        }
    }

    // ---------------- Wrong-Question Drill Mode Actions (错题专练模式) ----------------
    fun enterWrongDrillMode() {
        cancelAutoAdvance()
        val wrongList = uiState.value.wrongQuestions
        val picked = QuestionRepository.pickWeightedWrongQuestion(wrongQuestions = wrongList)
        controlState.update {
            it.copy(
                currentTab = AppTab.WRONG_DRILL,
                showStudyEntryDialog = false,
                drillCurrentQuestionId = picked?.id,
                drillSelectedOption = null,
                drillSessionAnswered = 0,
                drillSessionWrongAgain = 0,
                drillSessionCorrect = 0,
                practiceQueueIds = emptyList(),
                practiceSessionActive = false,
                practiceExitedSummary = false
            )
        }
    }

    fun startOrNextWrongDrillQuestion() {
        cancelAutoAdvance()
        val wrongList = uiState.value.wrongQuestions
        val currentId = controlState.value.drillCurrentQuestionId
        val picked = QuestionRepository.pickWeightedWrongQuestion(
            wrongQuestions = wrongList,
            lastQuestionId = currentId
        )
        controlState.update {
            it.copy(
                currentTab = AppTab.WRONG_DRILL,
                drillCurrentQuestionId = picked?.id,
                drillSelectedOption = null
            )
        }
    }

    fun selectDrillOption(option: String) {
        val state = controlState.value
        if (state.drillSelectedOption != null) return
        val question = uiState.value.currentDrillQuestion ?: return
        val isWrong = !option.equals(question.correctOption, ignoreCase = true)

        controlState.update {
            it.copy(
                drillSelectedOption = option,
                drillSessionAnswered = it.drillSessionAnswered + 1,
                drillSessionWrongAgain = it.drillSessionWrongAgain + if (isWrong) 1 else 0,
                drillSessionCorrect = it.drillSessionCorrect + if (isWrong) 0 else 1
            )
        }
        viewModelScope.launch {
            repository.recordAnswer(questionId = question.id, isWrong = isWrong)
        }

        if (!isWrong) {
            cancelAutoAdvance()
            autoAdvanceJob = viewModelScope.launch {
                kotlinx.coroutines.delay(260L)
                startOrNextWrongDrillQuestion()
            }
        }
    }

    // ---------------- Question Bank Actions (查看题库) ----------------
    fun openBankWithWrongFilterSortedByErrorRate() {
        controlState.update {
            it.copy(
                currentTab = AppTab.QUESTION_BANK,
                bankOnlyWrong = true,
                bankSortOrder = BankSortOrder.ERROR_RATE_DESC,
                bankChapterFilter = null
            )
        }
    }

    fun setBankOnlyWrong(onlyWrong: Boolean) {
        controlState.update {
            it.copy(
                bankOnlyWrong = onlyWrong,
                bankSortOrder = if (onlyWrong && it.bankSortOrder == BankSortOrder.SEQUENTIAL) {
                    BankSortOrder.ERROR_RATE_DESC
                } else {
                    it.bankSortOrder
                }
            )
        }
    }

    fun setBankSortOrder(sortOrder: BankSortOrder) {
        controlState.update { it.copy(bankSortOrder = sortOrder) }
    }

    fun setBankChapterFilter(chapter: Int?) {
        controlState.update { it.copy(bankChapterFilter = chapter) }
    }

    fun setBankSearchQuery(query: String) {
        controlState.update { it.copy(bankSearchQuery = query) }
    }

    fun markQuestionWrongOnce(questionId: Int) {
        viewModelScope.launch {
            repository.recordAnswer(questionId = questionId, isWrong = true)
        }
    }

    fun resetSingleQuestionStats(questionId: Int) {
        viewModelScope.launch {
            repository.updateStats(questionId = questionId, totalAttempts = 0, wrongAttempts = 0)
        }
    }

    fun resetAllStats() {
        viewModelScope.launch {
            repository.resetAllStats()
            controlState.update {
                it.copy(
                    drillCurrentQuestionId = null,
                    drillSelectedOption = null,
                    drillSessionAnswered = 0,
                    drillSessionWrongAgain = 0,
                    drillSessionCorrect = 0
                )
            }
        }
    }

    class Factory(
        private val repository: QuestionRepository,
        private val prefs: SharedPreferences? = null
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return QuizViewModel(repository, prefs) as T
        }
    }
}
