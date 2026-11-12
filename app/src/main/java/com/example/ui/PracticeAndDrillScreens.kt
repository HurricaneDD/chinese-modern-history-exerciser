package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.WrongRed

@Composable
fun PracticeModeScreen(
    uiState: QuizUiState,
    onSelectOption: (String) -> Unit,
    onNextQuestion: () -> Unit,
    onExitMidway: () -> Unit,
    onResumeSession: () -> Unit,
    onStartNewSession: () -> Unit,
    onGoToHome: () -> Unit
) {
    val control = uiState.control
    val totalInQueue = control.practiceQueueIds.size
    val currentQuestion = uiState.currentPracticeQuestion

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
                                "已半途退出随机刷题"
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

                        if (control.practiceIndex < totalInQueue && totalInQueue > 0) {
                            Button(
                                onClick = onResumeSession,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("practice_resume_button")
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = "继续刷题")
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("继续当前随机队列 (第 ${control.practiceIndex + 1}/$totalInQueue 题)")
                            }
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
                            Text("重新打乱全部 156 道题开始新刷题")
                        }

                        OutlinedButton(
                            onClick = onGoToHome,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("practice_back_home_button")
                        ) {
                            Text("返回首页")
                        }
                    }
                }
            }
            return
        }

        // Active random practice header
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "随机乱序进度：${control.practiceIndex + 1} / $totalInQueue",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "本轮对 ${control.practiceSessionCorrect} · 错 ${control.practiceSessionWrong}（实时记录至题库）",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = onStartNewSession,
                            modifier = Modifier.testTag("practice_reshuffle_top_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "重洗",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("重洗")
                        }
                        OutlinedButton(
                            onClick = onExitMidway,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = WrongRed),
                            modifier = Modifier.testTag("practice_exit_midway_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "半途退出",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("半途退出")
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = {
                        if (totalInQueue == 0) 0f else (control.practiceIndex + 1).toFloat() / totalInQueue.toFloat()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        if (currentQuestion != null) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                QuestionCardComponent(
                    question = currentQuestion,
                    selectedOption = control.practiceSelectedOption,
                    revealAnswer = control.practiceSelectedOption != null,
                    extraBadgeText = "随机乱序 #${control.practiceIndex + 1}",
                    onSelectOption = onSelectOption
                )
            }

            Surface(
                tonalElevation = 3.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = onNextQuestion,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("practice_next_button")
                    ) {
                        Text(
                            if (control.practiceSelectedOption == null) {
                                "跳过本题 / 下一题"
                            } else if (control.practiceIndex + 1 >= totalInQueue) {
                                "完成本轮刷题"
                            } else {
                                "下一题 (随机序列)"
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "下一题")
                    }
                }
            }
        }
    }
}

@Composable
fun WrongDrillModeScreen(
    uiState: QuizUiState,
    onSelectOption: (String) -> Unit,
    onNextWeightedQuestion: () -> Unit,
    onGoToPractice: () -> Unit,
    onGoToBank: () -> Unit
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
            // Empty state when no wrong questions exist yet
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
                            text = "错题专练模式只会从你有过做错记录的题目中随机抽取，且错误率越高的题目出现频率越高。你可以先前往「刷题模式」作答，或在「查看题库」中将易错题加入错题集。",
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
                            onClick = onGoToBank,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("drill_empty_go_bank")
                        ) {
                            Text("前往查看题库")
                        }
                    }
                }
            }
            return
        }

        // Calculate relative appearance probability of current question
        val totalWeight = wrongQuestions.sumOf { it.drillWeight }.coerceAtLeast(0.01)
        val currentProbPercent = ((currentQuestion.drillWeight / totalWeight) * 100.0).toInt().coerceAtLeast(1)

        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "错题专练",
                            tint = WrongRed
                        )
                        Text(
                            text = "错题池共 ${wrongQuestions.size} 道 · 按错误率加权抽题",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "本轮已练 ${control.drillSessionAnswered} 题 (再错 ${control.drillSessionWrongAgain})",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = "当前题错误率 ${currentQuestion.errorRatePercentage}%（写过 ${currentQuestion.totalAttempts} 遍 / 写错 ${currentQuestion.wrongAttempts} 遍）· 抽中权重占比约 $currentProbPercent%",
                    style = MaterialTheme.typography.bodyMedium,
                    color = WrongRed
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            QuestionCardComponent(
                question = currentQuestion,
                selectedOption = control.drillSelectedOption,
                revealAnswer = control.drillSelectedOption != null,
                extraBadgeText = "高频加权错题 · 错误率 ${currentQuestion.errorRatePercentage}%",
                onSelectOption = onSelectOption
            )
        }

        Surface(
            tonalElevation = 3.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Button(
                    onClick = onNextWeightedQuestion,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("drill_next_weighted_button")
                ) {
                    Icon(Icons.Default.Casino, contentDescription = "加权随机下一题")
                    Spacer(modifier = Modifier.width(8.dp))
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
