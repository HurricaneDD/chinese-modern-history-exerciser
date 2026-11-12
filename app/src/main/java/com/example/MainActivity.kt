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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shuffle
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

        setContent {
            MyApplicationTheme {
                val quizViewModel: QuizViewModel = viewModel(
                    factory = QuizViewModel.Factory(repository)
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

    if (currentTab != AppTab.HOME) {
        BackHandler {
            viewModel.selectTab(AppTab.HOME)
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = when (currentTab) {
                            AppTab.HOME -> "近现代史纲要刷题"
                            AppTab.STUDY -> "学习模式 · 顺序研读"
                            AppTab.PRACTICE -> "刷题模式 · 随机打乱"
                            AppTab.WRONG_DRILL -> "错题专练 · 加权高频"
                            AppTab.QUESTION_BANK -> "查看题库 · 统计与错题榜"
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
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.testTag("bottom_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = currentTab == AppTab.HOME,
                    onClick = { viewModel.selectTab(AppTab.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "首页") },
                    label = { Text("首页") },
                    modifier = Modifier.testTag("nav_tab_home")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.STUDY,
                    onClick = { viewModel.selectTab(AppTab.STUDY) },
                    icon = { Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = "学习模式") },
                    label = { Text("学习模式") },
                    modifier = Modifier.testTag("nav_tab_study")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.PRACTICE,
                    onClick = { viewModel.selectTab(AppTab.PRACTICE) },
                    icon = { Icon(Icons.Default.Shuffle, contentDescription = "刷题模式") },
                    label = { Text("刷题模式") },
                    modifier = Modifier.testTag("nav_tab_practice")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.WRONG_DRILL,
                    onClick = { viewModel.selectTab(AppTab.WRONG_DRILL) },
                    icon = { Icon(Icons.Default.Psychology, contentDescription = "错题专练") },
                    label = { Text("错题专练") },
                    modifier = Modifier.testTag("nav_tab_wrong_drill")
                )
                NavigationBarItem(
                    selected = currentTab == AppTab.QUESTION_BANK,
                    onClick = { viewModel.selectTab(AppTab.QUESTION_BANK) },
                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = "查看题库") },
                    label = { Text("查看题库") },
                    modifier = Modifier.testTag("nav_tab_bank")
                )
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
                        onSelectStudy = { viewModel.selectTab(AppTab.STUDY) },
                        onSelectPractice = { viewModel.selectTab(AppTab.PRACTICE) },
                        onSelectWrongDrill = { viewModel.selectTab(AppTab.WRONG_DRILL) },
                        onSelectBank = { viewModel.selectTab(AppTab.QUESTION_BANK) },
                        onOpenWrongBankSorted = { viewModel.openBankWithWrongFilterSortedByErrorRate() }
                    )

                    AppTab.STUDY -> StudyModeScreen(
                        uiState = uiState,
                        onSelectChapter = viewModel::setStudyChapter,
                        onSelectOption = viewModel::selectStudyOption,
                        onNext = viewModel::nextStudyQuestion,
                        onPrevious = viewModel::previousStudyQuestion,
                        onJumpToIndex = viewModel::jumpToStudyIndex,
                        onToggleMemorizeMode = viewModel::toggleStudyMemorizeMode,
                        onToggleRecordStats = viewModel::toggleStudyRecordStats
                    )

                    AppTab.PRACTICE -> PracticeModeScreen(
                        uiState = uiState,
                        onSelectOption = viewModel::selectPracticeOption,
                        onNextQuestion = viewModel::nextPracticeQuestion,
                        onExitMidway = viewModel::exitPracticeMidway,
                        onResumeSession = viewModel::resumePracticeSession,
                        onStartNewSession = viewModel::startNewPracticeSession,
                        onGoToHome = { viewModel.selectTab(AppTab.HOME) }
                    )

                    AppTab.WRONG_DRILL -> WrongDrillModeScreen(
                        uiState = uiState,
                        onSelectOption = viewModel::selectDrillOption,
                        onNextWeightedQuestion = viewModel::startOrNextWrongDrillQuestion,
                        onGoToPractice = { viewModel.selectTab(AppTab.PRACTICE) },
                        onGoToBank = { viewModel.selectTab(AppTab.QUESTION_BANK) }
                    )

                    AppTab.QUESTION_BANK -> QuestionBankScreen(
                        uiState = uiState,
                        onSetOnlyWrong = viewModel::setBankOnlyWrong,
                        onSetSortOrder = viewModel::setBankSortOrder,
                        onSetChapterFilter = viewModel::setBankChapterFilter,
                        onSetSearchQuery = viewModel::setBankSearchQuery,
                        onMarkWrongOnce = viewModel::markQuestionWrongOnce,
                        onResetQuestionStats = viewModel::resetSingleQuestionStats,
                        onResetAllStats = viewModel::resetAllStats
                    )
                }
            }
        }
    }
}
