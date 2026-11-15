package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedContainer

@Composable
fun PracticeModeScreen(
    uiState: QuizUiState,
    onSelectOption: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onExitMidway: () -> Unit,
    onStartNewSession: () -> Unit,
    onGoToHome: () -> Unit
) {
    val control = uiState.control
    val totalInQueue = control.practiceQueueIds.size
    val currentQuestion = uiState.currentPracticeQuestion
    var showExitConfirmDialog by androidx.compose.runtime.remember {
        androidx.compose.runtime.mutableStateOf(false)
    }

    if (control.practiceSessionActive && !control.practiceExitedSummary) {
        androidx.activity.compose.BackHandler {
            showExitConfirmDialog = true
        }
    }

    if (showExitConfirmDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showExitConfirmDialog = false },
            title = {
                Text(
                    text = "确认退出刷题",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "确定要退出当前刷题模式吗？目前已作答的 ${control.practiceSessionAnswered} 道题记录将会保留。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showExitConfirmDialog = false
                        onExitMidway()
                    },
                    modifier = Modifier.testTag("practice_confirm_exit_button")
                ) {
                    Text("确定退出")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showExitConfirmDialog = false },
                    modifier = Modifier.testTag("practice_cancel_exit_button")
                ) {
                    Text("继续刷题")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("practice_mode_screen")
    ) {
        if (control.practiceExitedSummary || !control.practiceSessionActive) {
            // Exited midway or finished summary card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_summary_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "刷题统计",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = if (control.practiceIndex + 1 >= totalInQueue && totalInQueue > 0 && control.practiceSessionAnswered >= totalInQueue) {
                                "本轮随机刷题已完成"
                            } else {
                                "已退出随机刷题"
                            },
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "本轮已作答的题目对错数据均已实时记录至题库统计中。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            SummaryStatBox(
                                label = "本轮已答",
                                value = "${control.practiceSessionAnswered} 题",
                                modifier = Modifier.weight(1f)
                            )
                            SummaryStatBox(
                                label = "答对",
                                value = "${control.practiceSessionCorrect} 题",
                                modifier = Modifier.weight(1f)
                            )
                            SummaryStatBox(
                                label = "答错",
                                value = "${control.practiceSessionWrong} 题",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = onGoToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("practice_back_home_button")
                        ) {
                            Text("完成并返回首页")
                        }

                        OutlinedButton(
                            onClick = onStartNewSession,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("practice_restart_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "重新洗牌")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("重新打乱全部题目开始新一轮")
                        }
                    }
                }
            }
            return
        }

        // Ultra-compact active random practice header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "刷题模式 · 随机第 ${control.practiceIndex + 1} / $totalInQueue 题",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "本轮答对 ${control.practiceSessionCorrect} · 答错 ${control.practiceSessionWrong}（实时记录数据）",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Surface(
                        onClick = { showExitConfirmDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        color = WrongRedContainer,
                        modifier = Modifier.testTag("practice_exit_midway_button")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "退出",
                                tint = WrongRed,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "退出",
                                style = MaterialTheme.typography.labelLarge,
                                color = WrongRed
                            )
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = {
                        if (totalInQueue == 0) 0f else (control.practiceIndex + 1).toFloat() / totalInQueue.toFloat()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                )
            }
        }

        if (currentQuestion != null) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                QuestionCardComponent(
                    question = currentQuestion,
                    selectedOption = control.practiceSelectedOption,
                    revealAnswer = control.practiceSelectedOption != null,
                    showFeedbackBanner = false,
                    onSelectOption = onSelectOption,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Button(
                        onClick = onNextQuestion,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("practice_next_button")
                    ) {
                        Text(
                            if (control.practiceSelectedOption == null) {
                                "跳过本题 / 下一题"
                            } else if (control.practiceIndex + 1 >= totalInQueue) {
                                "完成本轮刷题"
                            } else {
                                "下一题"
                            }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "下一题",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WrongDrillModeScreen(
    uiState: QuizUiState,
    onExitMode: () -> Unit,
    onSelectOption: (String) -> Unit,
    onNextWeightedQuestion: () -> Unit,
    onGoToPractice: () -> Unit
) {
    val wrongQuestions = uiState.wrongQuestions
    val currentQuestion = uiState.currentDrillQuestion ?: wrongQuestions.firstOrNull()
    val control = uiState.control

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("wrong_drill_screen")
    ) {
        if (wrongQuestions.isEmpty() || currentQuestion == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("wrong_drill_empty_card"),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircleOutline,
                            contentDescription = "暂无错题",
                            tint = CorrectGreen,
                            modifier = Modifier.size(56.dp)
                        )
                        Text(
                            text = "当前暂无错题记录",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "错题专练模式只会从你有过做错记录的题目中随机抽取，且错误率越高的题目出现频率越高。你可以先前往「刷题模式」作答。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onGoToPractice,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("drill_empty_go_practice")
                        ) {
                            Icon(Icons.Default.Shuffle, contentDescription = "前往刷题模式")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("前往刷题模式")
                        }
                        OutlinedButton(
                            onClick = onExitMode,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("drill_empty_back_home")
                        ) {
                            Text("返回首页")
                        }
                    }
                }
            }
            return
        }

        val totalWeight = wrongQuestions.sumOf { it.drillWeight }.coerceAtLeast(0.01)
        val currentProbPercent = ((currentQuestion.drillWeight / totalWeight) * 100.0).toInt().coerceAtLeast(1)

        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onExitMode,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("drill_exit_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "退出错题专练"
                        )
                    }
                    Column {
                        Text(
                            text = "错题专练 · 错题池 ${wrongQuestions.size} 道",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "本题错误率 ${currentQuestion.errorRatePercentage}% · 权重占比 ~$currentProbPercent%",
                            style = MaterialTheme.typography.labelMedium,
                            color = WrongRed
                        )
                    }
                }

                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.padding(end = 6.dp)
                ) {
                    Text(
                        text = "已练 ${control.drillSessionAnswered} (再错 ${control.drillSessionWrongAgain})",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            QuestionCardComponent(
                question = currentQuestion,
                selectedOption = control.drillSelectedOption,
                revealAnswer = control.drillSelectedOption != null,
                extraBadgeText = "错误率 ${currentQuestion.errorRatePercentage}%（写 ${currentQuestion.totalAttempts} 错 ${currentQuestion.wrongAttempts}）",
                onSelectOption = onSelectOption,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = onNextWeightedQuestion,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("drill_next_weighted_button")
                ) {
                    Icon(
                        Icons.Default.Casino,
                        contentDescription = "加权随机下一题",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("抽取下一道错题（错误率越高出现概率越高）")
                }
            }
        }
    }
}

@Composable
private fun SummaryStatBox(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
