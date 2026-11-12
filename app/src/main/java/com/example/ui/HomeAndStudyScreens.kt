package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.AutoAwesomeMotion
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.QuestionEntity
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenContainer
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedContainer

@Composable
fun HomeScreen(
    uiState: QuizUiState,
    onSelectStudy: () -> Unit,
    onSelectPractice: () -> Unit,
    onSelectWrongDrill: () -> Unit,
    onSelectBank: () -> Unit,
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
                text = "四大核心模式",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        item {
            ModeActionCard(
                title = "学习模式",
                subtitle = "按第一至十章单元与题号顺序逐题学习，支持背题显隐与选章跳题",
                badgeText = "共 10 章 · ${uiState.totalQuestionsCount} 题",
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

        item {
            ModeActionCard(
                title = "查看题库",
                subtitle = "展示所有题目写过的遍数、写错的遍数与错误率，支持专门展示错题及按错误率从高到低排序",
                badgeText = "全量检索 · 错题榜",
                icon = Icons.Default.LibraryBooks,
                accentColor = MaterialTheme.colorScheme.tertiary,
                testTag = "mode_card_bank",
                onClick = onSelectBank
            )
        }
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
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
                    Surface(
                        color = Color(0x33FFFFFF),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "${uiState.totalQuestionsCount} 题",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
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
    onSelectChapter: (Int?) -> Unit,
    onSelectOption: (String) -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onJumpToIndex: (Int) -> Unit,
    onToggleMemorizeMode: () -> Unit,
    onToggleRecordStats: () -> Unit
) {
    var showQuestionSheet by remember { mutableStateOf(false) }
    val studyList = uiState.studyQuestions
    val currentQuestion = uiState.currentStudyQuestion
    val currentIndex = uiState.control.studyIndex.coerceIn(0, (studyList.size - 1).coerceAtLeast(0))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("study_mode_screen")
    ) {
        // Chapter selector bar
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
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

        // Top controls: Progress + Memorize toggle + Sheet jumper
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "顺序进度：${if (studyList.isEmpty()) 0 else currentIndex + 1} / ${studyList.size}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(
                    selected = uiState.control.studyMemorizeMode,
                    onClick = onToggleMemorizeMode,
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Visibility,
                            contentDescription = "背题模式",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("直接看答案") },
                    modifier = Modifier.testTag("study_memorize_toggle")
                )
                FilterChip(
                    selected = false,
                    onClick = { showQuestionSheet = true },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.GridView,
                            contentDescription = "题号面板",
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    label = { Text("选题号") },
                    modifier = Modifier.testTag("study_open_sheet_button")
                )
            }
        }

        LinearProgressIndicator(
            progress = {
                if (studyList.isEmpty()) 0f else (currentIndex + 1).toFloat() / studyList.size.toFloat()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        )

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
                    selectedOption = uiState.control.studySelectedOption,
                    revealAnswer = uiState.control.studyMemorizeMode || uiState.control.studySelectedOption != null,
                    onSelectOption = onSelectOption
                )
            }

            // Bottom Prev / Next bar
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
                    OutlinedButton(
                        onClick = onPrevious,
                        enabled = currentIndex > 0,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("study_prev_button")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "上一题")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("上一题")
                    }
                    Button(
                        onClick = onNext,
                        enabled = currentIndex < studyList.size - 1,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("study_next_button")
                    ) {
                        Text("下一题")
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "下一题")
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
                    text = "选择单元与题号跳转",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 68.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.height(360.dp)
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun QuestionCardComponent(
    question: QuestionEntity,
    selectedOption: String?,
    revealAnswer: Boolean,
    extraBadgeText: String? = null,
    onSelectOption: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("active_question_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "${question.chapterTitle} · 第 ${question.questionNumber} 题",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "写过 ${question.totalAttempts} 遍 · 错 ${question.wrongAttempts} 遍 · 错误率 ${question.errorRatePercentage}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (question.wrongAttempts > 0) WrongRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                if (extraBadgeText != null) {
                    Surface(
                        color = WrongRedContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = extraBadgeText,
                            style = MaterialTheme.typography.labelMedium,
                            color = WrongRed,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Text(
                text = "${question.questionNumber}. ${question.questionText}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.testTag("question_stem_text")
            )

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
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = key,
                                    style = MaterialTheme.typography.labelLarge,
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
                            style = MaterialTheme.typography.bodyLarge,
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

            AnimatedVisibility(visible = revealAnswer) {
                Surface(
                    color = if (selectedOption == null || selectedOption == question.correctOption) {
                        CorrectGreenContainer.copy(alpha = 0.65f)
                    } else {
                        WrongRedContainer.copy(alpha = 0.65f)
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("answer_feedback_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
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
                            }
                        )
                        Column {
                            Text(
                                text = when {
                                    selectedOption == null -> "正确答案：${question.correctOption}（${question.optionTextFor(question.correctOption)}）"
                                    selectedOption == question.correctOption -> "回答正确！正确答案是 ${question.correctOption}"
                                    else -> "回答错误！你选择了 $selectedOption，正确答案是 ${question.correctOption}（${question.optionTextFor(question.correctOption)}）"
                                },
                                style = MaterialTheme.typography.titleMedium,
                                color = Color(0xFF1E1B18)
                            )
                        }
                    }
                }
            }
        }
    }
}
