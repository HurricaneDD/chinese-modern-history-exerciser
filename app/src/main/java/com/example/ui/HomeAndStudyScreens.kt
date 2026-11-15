package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuestionEntity
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenContainer
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedContainer

@Composable
fun HomeScreen(
    uiState: QuizUiState,
    onSelectStudy: () -> Unit,
    onDismissStudyDialog: () -> Unit,
    onStartStudyFromBeginning: () -> Unit,
    onContinueLastStudy: () -> Unit,
    onSelectPractice: () -> Unit,
    onSelectWrongDrill: () -> Unit,
    onOpenWrongBankSorted: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            HeroBannerCard(
                uiState = uiState,
                onOpenWrongBankSorted = onOpenWrongBankSorted
            )
        }

        item {
            Text(
                text = "选择练习模式",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            ModeActionCard(
                title = "学习模式",
                subtitle = "按第一至十章单元与题号顺序逐题学习，支持进度记忆、背题显隐与选章跳题",
                badgeText = "共 10 章 · 顺序精读",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                accentColor = MaterialTheme.colorScheme.primary,
                testTag = "mode_card_study",
                onClick = onSelectStudy
            )
        }

        item {
            ModeActionCard(
                title = "刷题模式",
                subtitle = "题库全部题目随机打乱顺序作答，实时记录对错遍数，支持随时半途退出",
                badgeText = "已累计作答 ${uiState.totalAttemptsSum} 次",
                icon = Icons.Default.Shuffle,
                accentColor = MaterialTheme.colorScheme.secondary,
                testTag = "mode_card_practice",
                onClick = onSelectPractice
            )
        }

        item {
            ModeActionCard(
                title = "错题专练模式",
                subtitle = "仅从错题集中随机抽题，错误率越高的题目出现频率越高，再次写错实时记录",
                badgeText = "当前错题 ${uiState.wrongQuestionsCount} 道",
                icon = Icons.Default.Psychology,
                accentColor = WrongRed,
                testTag = "mode_card_wrong_drill",
                onClick = onSelectWrongDrill
            )
        }
    }

    if (uiState.control.showStudyEntryDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = onDismissStudyDialog,
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.MenuBook,
                    contentDescription = "学习模式",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "进入学习模式",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "上次学习进度：${uiState.savedStudyProgressLabel}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "请选择从头开始学习，或继续上次学习的进度：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = onContinueLastStudy,
                    modifier = Modifier.testTag("study_dialog_continue_button")
                ) {
                    Text("继续上次学习的进度")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = onStartStudyFromBeginning,
                    modifier = Modifier.testTag("study_dialog_restart_button")
                ) {
                    Text("从头开始")
                }
            }
        )
    }
}

@Composable
private fun HeroBannerCard(
    uiState: QuizUiState,
    onOpenWrongBankSorted: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF8E1414),
                            Color(0xFF5A0B0B)
                        )
                    )
                )
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Column {
                    Text(
                        text = "中国近现代史纲要",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFFFF8E1)
                    )
                    Text(
                        text = "按章节精选单选合集 · 实时错题加权精练",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFFFE0B2)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatMiniPill(
                        label = "已刷题目",
                        value = "${uiState.attemptedQuestionsCount}/${uiState.totalQuestionsCount}",
                        modifier = Modifier.weight(1f)
                    )
                    StatMiniPill(
                        label = "累计刷题",
                        value = "${uiState.totalAttemptsSum} 遍",
                        modifier = Modifier.weight(1f)
                    )
                    StatMiniPill(
                        label = "累计错题",
                        value = "${uiState.wrongQuestionsCount} 道",
                        modifier = Modifier.weight(1f)
                    )
                    StatMiniPill(
                        label = "总正确率",
                        value = "${uiState.overallAccuracyPercent}%",
                        modifier = Modifier.weight(1f)
                    )
                }

                if (uiState.wrongQuestionsCount > 0) {
                    Surface(
                        onClick = onOpenWrongBankSorted,
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x26FFFFFF),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("hero_wrong_shortcut")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = "错题排行",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "你有 ${uiState.wrongQuestionsCount} 道错题，点击按错误率从高到低查看",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White
                                )
                            }
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "前往",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMiniPill(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        color = Color(0x22FFFFFF),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFFE0B2)
            )
        }
    }
}

@Composable
private fun ModeActionCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: ImageVector,
    accentColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                color = accentColor.copy(alpha = 0.14f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.size(56.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = accentColor,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Surface(
                        color = accentColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = badgeText,
                            style = MaterialTheme.typography.labelMedium,
                            color = accentColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "进入$title",
                tint = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyModeScreen(
    uiState: QuizUiState,
    onExitMode: () -> Unit,
    onSelectChapter: (Int?) -> Unit,
    onSelectOption: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onJumpToIndex: (Int) -> Unit,
    onToggleMemorizeMode: () -> Unit
) {
    var showQuestionSheet by remember { mutableStateOf(false) }
    val studyList = uiState.studyQuestions
    val currentQuestion = uiState.currentStudyQuestion
    val currentIndex = uiState.control.studyIndex.coerceIn(0, (studyList.size - 1).coerceAtLeast(0))
    val chapterLabel = uiState.control.studyChapterFilter?.let { "第${it}章" } ?: "全章节"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("study_mode_screen")
    ) {
        // Ultra-compact top bar merging exit, progress, memorize toggle, and chapter/question picker
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
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
                                .testTag("study_exit_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "退出学习模式"
                            )
                        }
                        Column {
                            Text(
                                text = "学习模式 · $chapterLabel",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "进度 ${if (studyList.isEmpty()) 0 else currentIndex + 1} / ${studyList.size}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 6.dp)
                    ) {
                        Surface(
                            onClick = onToggleMemorizeMode,
                            shape = RoundedCornerShape(10.dp),
                            color = if (uiState.control.studyMemorizeMode) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier.testTag("study_memorize_toggle")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "背题",
                                    tint = if (uiState.control.studyMemorizeMode) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    },
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "背题",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (uiState.control.studyMemorizeMode) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }

                        Surface(
                            onClick = { showQuestionSheet = true },
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            modifier = Modifier.testTag("study_open_sheet_button")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.GridView,
                                    contentDescription = "选章与题号",
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.size(15.dp)
                                )
                                Text(
                                    text = "选章/题号",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }

                LinearProgressIndicator(
                    progress = {
                        if (studyList.isEmpty()) 0f else (currentIndex + 1).toFloat() / studyList.size.toFloat()
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
                    selectedOption = uiState.control.studySelectedOption,
                    revealAnswer = uiState.control.studyMemorizeMode || uiState.control.studySelectedOption != null,
                    showFeedbackBanner = false,
                    onSelectOption = onSelectOption,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Compact bottom Prev / Next bar
            Surface(
                tonalElevation = 2.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = currentIndex > 0,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("study_prev_button")
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "上一题",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("上一题")
                    }
                    Button(
                        onClick = onNext,
                        enabled = currentIndex < studyList.size - 1,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("study_next_button")
                    ) {
                        Text("下一题")
                        Spacer(modifier = Modifier.width(4.dp))
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

    if (showQuestionSheet) {
        ModalBottomSheet(
            onDismissRequest = { showQuestionSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "筛选章节与跳转题号",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 12.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.control.studyChapterFilter == null,
                            onClick = { onSelectChapter(null) },
                            label = { Text("全部章节 (156)") },
                            modifier = Modifier.testTag("study_chapter_all")
                        )
                    }
                    items((1..10).toList()) { chapter ->
                        FilterChip(
                            selected = uiState.control.studyChapterFilter == chapter,
                            onClick = { onSelectChapter(chapter) },
                            label = { Text("第${chapter}章") },
                            modifier = Modifier.testTag("study_chapter_$chapter")
                        )
                    }
                }
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 68.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(340.dp)
                ) {
                    itemsIndexed(studyList) { index, q ->
                        val isCurrent = index == currentIndex
                        Surface(
                            onClick = {
                                onJumpToIndex(index)
                                showQuestionSheet = false
                            },
                            shape = RoundedCornerShape(12.dp),
                            color = if (isCurrent) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "${q.chapterNumber}-${q.questionNumber}",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = if (isCurrent) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuestionCardComponent(
    question: QuestionEntity,
    selectedOption: String?,
    revealAnswer: Boolean,
    showFeedbackBanner: Boolean = true,
    extraBadgeText: String? = null,
    onSelectOption: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("active_question_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section: Chapter & question number, full-width stats bar below it, then question stem
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${question.chapterTitle} · 第${question.questionNumber}题",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                // Full-width horizontal bar for "刷 x 遍 错 x 遍" directly beneath chapter and question number
                Surface(
                    color = if (extraBadgeText != null || question.wrongAttempts > 0) {
                        WrongRedContainer.copy(alpha = 0.65f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("question_stats_bar")
                ) {
                    Text(
                        text = extraBadgeText
                            ?: "刷 ${question.totalAttempts} 遍 · 错 ${question.wrongAttempts} 遍 · 错误率 ${question.errorRatePercentage}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (question.wrongAttempts > 0 || extraBadgeText != null) {
                            WrongRed
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }

                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(
                        text = "${question.questionNumber}. ${question.questionText}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = 20.sp,
                            lineHeight = 28.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(top = 2.dp, bottom = 2.dp)
                            .testTag("question_stem_text")
                    )
                }
            }

            // Middle section: Large, clear options A, B, C, D that fit on screen without scrolling
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val options = listOf(
                    "A" to question.optionA,
                    "B" to question.optionB,
                    "C" to question.optionC,
                    "D" to question.optionD
                )

                options.forEach { (key, text) ->
                    val isSelected = selectedOption == key
                    val isCorrect = question.correctOption.equals(key, ignoreCase = true)

                    val containerColor = when {
                        revealAnswer && isCorrect -> CorrectGreenContainer
                        revealAnswer && isSelected && !isCorrect -> WrongRedContainer
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                    }

                    val borderColor = when {
                        revealAnswer && isCorrect -> CorrectGreen
                        revealAnswer && isSelected && !isCorrect -> WrongRed
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    }

                    val contentColor = when {
                        revealAnswer && isCorrect -> Color(0xFF064E3B)
                        revealAnswer && isSelected && !isCorrect -> Color(0xFF7F1D1D)
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Surface(
                        onClick = { onSelectOption(key) },
                        shape = RoundedCornerShape(14.dp),
                        color = containerColor,
                        border = BorderStroke(1.5.dp, borderColor),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("option_button_$key")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = when {
                                    revealAnswer && isCorrect -> CorrectGreen
                                    revealAnswer && isSelected && !isCorrect -> WrongRed
                                    else -> MaterialTheme.colorScheme.surface
                                },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = key,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (revealAnswer && (isCorrect || isSelected)) {
                                            Color.White
                                        } else {
                                            MaterialTheme.colorScheme.onSurface
                                        }
                                    )
                                }
                            }

                            Text(
                                text = text,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontSize = 17.sp,
                                    lineHeight = 23.sp
                                ),
                                fontWeight = if (revealAnswer && isCorrect) FontWeight.SemiBold else FontWeight.Normal,
                                color = contentColor,
                                modifier = Modifier.weight(1f)
                            )

                            if (revealAnswer && isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "正确选项",
                                    tint = CorrectGreen
                                )
                            } else if (revealAnswer && isSelected && !isCorrect) {
                                Icon(
                                    imageVector = Icons.Default.Cancel,
                                    contentDescription = "错误选项",
                                    tint = WrongRed
                                )
                            }
                        }
                    }
                }
            }

            // Bottom feedback banner (omitted in Study Mode per user preference)
            if (showFeedbackBanner) {
                AnimatedVisibility(visible = revealAnswer) {
                    Surface(
                        color = if (selectedOption == null || selectedOption == question.correctOption) {
                            CorrectGreenContainer.copy(alpha = 0.75f)
                        } else {
                            WrongRedContainer.copy(alpha = 0.75f)
                        },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("answer_feedback_banner")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = if (selectedOption == null || selectedOption == question.correctOption) {
                                    Icons.Default.CheckCircle
                                } else {
                                    Icons.Default.ErrorOutline
                                },
                                contentDescription = "答案提示",
                                tint = if (selectedOption == null || selectedOption == question.correctOption) {
                                    CorrectGreen
                                } else {
                                    WrongRed
                                },
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = when {
                                    selectedOption == null -> "正确答案：${question.correctOption}（${question.optionTextFor(question.correctOption)}）"
                                    selectedOption == question.correctOption -> "回答正确！标准答案：${question.correctOption}"
                                    else -> "回答错误（已选 $selectedOption）· 正确答案：${question.correctOption}"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E1B18)
                            )
                        }
                    }
                }
            }
        }
    }
}
