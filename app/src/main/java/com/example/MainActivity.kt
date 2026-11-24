package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.AppDatabase
import com.example.data.QuestionRepository
import com.example.ui.AppTab
import com.example.ui.HomeScreen
import com.example.ui.PracticeModeScreen
import com.example.ui.QuestionBankScreen
import com.example.ui.QuizUiState
import com.example.ui.QuizViewModel
import com.example.ui.StudyModeScreen
import com.example.ui.WrongDrillModeScreen
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getInstance(applicationContext)
        val repository = QuestionRepository(database.questionDao())
        val prefs = getSharedPreferences("modern_history_quiz_prefs", MODE_PRIVATE)

        setContent {
            MyApplicationTheme {
                val quizViewModel: QuizViewModel = viewModel(
                    factory = QuizViewModel.Factory(repository, prefs)
                )
                val uiState by quizViewModel.uiState.collectAsStateWithLifecycle()

                ModernHistoryQuizApp(
                    uiState = uiState,
                    viewModel = quizViewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernHistoryQuizApp(
    uiState: QuizUiState,
    viewModel: QuizViewModel
) {
    val currentTab = uiState.control.currentTab
    val isMainTab = currentTab == AppTab.HOME || currentTab == AppTab.QUESTION_BANK

    if (currentTab != AppTab.HOME) {
        BackHandler {
            if (currentTab == AppTab.PRACTICE && uiState.control.practiceSessionActive) {
                viewModel.exitPracticeMidway()
            } else {
                viewModel.exitActiveModeToHome()
            }
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (isMainTab) {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = when (currentTab) {
                                AppTab.QUESTION_BANK -> "查看题库 · 统计与错题榜"
                                else -> "近现代史纲要刷题"
                            },
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
        },
        bottomBar = {
            if (isMainTab) {
                NavigationBar(
                    modifier = Modifier.testTag("bottom_navigation_bar")
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppTab.HOME,
                        onClick = { viewModel.selectMainTab(AppTab.HOME) },
                        icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                        label = { Text("首页") },
                        modifier = Modifier.testTag("nav_tab_home")
                    )
                    NavigationBarItem(
                        selected = currentTab == AppTab.QUESTION_BANK,
                        onClick = { viewModel.selectMainTab(AppTab.QUESTION_BANK) },
                        icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "查看题库") },
                        label = { Text("查看题库") },
                        modifier = Modifier.testTag("nav_tab_bank")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                when (currentTab) {
                    AppTab.HOME -> HomeScreen(
                        uiState = uiState,
                        onSelectStudy = { viewModel.requestEnterStudyMode() },
                        onDismissStudyDialog = { viewModel.dismissStudyEntryDialog() },
                        onStartStudyFromBeginning = { viewModel.enterStudyModeFromStart() },
                        onContinueLastStudy = { viewModel.enterStudyModeContinue() },
                        onSelectPractice = { viewModel.startNewPracticeSession() },
                        onSelectWrongDrill = { viewModel.enterWrongDrillMode() },
                        onOpenWrongBankSorted = { viewModel.openBankWithWrongFilterSortedByErrorRate() }
                    )

                    AppTab.STUDY -> StudyModeScreen(
                        uiState = uiState,
                        onExitMode = { viewModel.exitActiveModeToHome() },
                        onSelectChapter = viewModel::setStudyChapter,
                        onSelectOption = viewModel::selectStudyOption,
                        onNext = viewModel::nextStudyQuestion,
                        onPrevious = viewModel::previousStudyQuestion,
                        onJumpToIndex = viewModel::jumpToStudyIndex,
                        onToggleMemorizeMode = viewModel::toggleStudyMemorizeMode
                    )

                    AppTab.PRACTICE -> PracticeModeScreen(
                        uiState = uiState,
                        onSelectOption = viewModel::selectPracticeOption,
                        onNextQuestion = viewModel::nextPracticeQuestion,
                        onExitMidway = viewModel::exitPracticeMidway,
                        onStartNewSession = viewModel::startNewPracticeSession,
                        onGoToHome = { viewModel.exitActiveModeToHome() }
                    )

                    AppTab.WRONG_DRILL -> WrongDrillModeScreen(
                        uiState = uiState,
                        onExitMode = { viewModel.exitActiveModeToHome() },
                        onSelectOption = viewModel::selectDrillOption,
                        onNextWeightedQuestion = viewModel::startOrNextWrongDrillQuestion,
                        onGoToPractice = { viewModel.startNewPracticeSession() }
                    )

                    AppTab.QUESTION_BANK -> QuestionBankScreen(
                        uiState = uiState,
                        onSetOnlyWrong = viewModel::setBankOnlyWrong,
                        onSetSortOrder = viewModel::setBankSortOrder,
                        onSetChapterFilter = viewModel::setBankChapterFilter,
                        onSetSearchQuery = viewModel::setBankSearchQuery,
                        onMarkWrongOnce = viewModel::markQuestionWrongOnce,
                        onDecrementWrongOnce = viewModel::decrementQuestionWrongOnce,
                        onResetQuestionStats = viewModel::resetSingleQuestionStats
                    )
                }
            }
        }
    }
}
