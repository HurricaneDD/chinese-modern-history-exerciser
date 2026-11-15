package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAlert
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.QuestionEntity
import com.example.ui.theme.CorrectGreen
import com.example.ui.theme.CorrectGreenContainer
import com.example.ui.theme.WrongRed
import com.example.ui.theme.WrongRedContainer

@Composable
fun QuestionBankScreen(
    uiState: QuizUiState,
    onSetOnlyWrong: (Boolean) -> Unit,
    onSetSortOrder: (BankSortOrder) -> Unit,
    onSetChapterFilter: (Int?) -> Unit,
    onSetSearchQuery: (String) -> Unit,
    onMarkWrongOnce: (Int) -> Unit,
    onResetQuestionStats: (Int) -> Unit,
    onResetAllStats: () -> Unit
) {
    val control = uiState.control
    val questions = uiState.filteredBankQuestions

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("question_bank_screen")
    ) {
        // Search & Filter controls panel
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
                OutlinedTextField(
                    value = control.bankSearchQuery,
                    onValueChange = onSetSearchQuery,
                    placeholder = { Text("搜索题目关键字、选项或章节…") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "搜索") },
                    trailingIcon = {
                        if (control.bankSearchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSetSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "清除搜索")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("bank_search_input")
                )

                // All Questions vs Only Wrong Questions + Sort options
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChip(
                            selected = !control.bankOnlyWrong,
                            onClick = { onSetOnlyWrong(false) },
                            label = { Text("全部题目 (${uiState.totalQuestionsCount})") },
                            modifier = Modifier.testTag("bank_filter_all")
                        )
                    }
                    item {
                        FilterChip(
                            selected = control.bankOnlyWrong,
                            onClick = { onSetOnlyWrong(true) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.FilterList,
                                    contentDescription = "专门展示错题",
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text("专门展示错题 (${uiState.wrongQuestionsCount})") },
                            modifier = Modifier.testTag("bank_filter_only_wrong")
                        )
                    }
                    items(BankSortOrder.entries) { order ->
                        FilterChip(
                            selected = control.bankSortOrder == order,
                            onClick = { onSetSortOrder(order) },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Sort,
                                    contentDescription = order.label,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            label = { Text(order.label) },
                            modifier = Modifier.testTag("bank_sort_${order.name}")
                        )
                    }
                }

                // Chapter filter row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item {
                        FilterChip(
                            selected = control.bankChapterFilter == null,
                            onClick = { onSetChapterFilter(null) },
                            label = { Text("全章节") }
                        )
                    }
                    items((1..10).toList()) { chapter ->
                        FilterChip(
                            selected = control.bankChapterFilter == chapter,
                            onClick = { onSetChapterFilter(chapter) },
                            label = { Text("第${chapter}章") }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "当前展示 ${questions.size} 道题 · 排序：${control.bankSortOrder.label}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (uiState.totalAttemptsSum > 0) {
                        TextButton(
                            onClick = onResetAllStats,
                            modifier = Modifier.testTag("bank_reset_all_stats_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteSweep,
                                contentDescription = "清空全部统计",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("清空全部统计")
                        }
                    }
                }
            }
        }

        if (questions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (control.bankOnlyWrong) "当前筛选下暂无错题记录" else "未找到匹配的题目",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("bank_questions_list"),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(questions, key = { it.id }) { question ->
                    BankQuestionItemCard(
                        question = question,
                        onMarkWrongOnce = { onMarkWrongOnce(question.id) },
                        onResetStats = { onResetQuestionStats(question.id) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BankQuestionItemCard(
    question: QuestionEntity,
    onMarkWrongOnce: () -> Unit,
    onResetStats: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier
            .fillMaxWidth()
            .testTag("bank_item_card_${question.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp),
        border = BorderStroke(
            width = 1.dp,
            color = if (question.wrongAttempts > 0) {
                WrongRed.copy(alpha = 0.45f)
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top metadata & stats badges
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "第${question.chapterNumber}章 · 第${question.questionNumber}题",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // 写过的遍数
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "写过 ${question.totalAttempts} 遍",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // 写错的遍数
                Surface(
                    color = if (question.wrongAttempts > 0) WrongRedContainer else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "写错 ${question.wrongAttempts} 遍",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (question.wrongAttempts > 0) WrongRed else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (question.wrongAttempts > 0) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // 错误率
                Surface(
                    color = if (question.wrongAttempts > 0) WrongRed else MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "错误率 ${question.errorRatePercentage}%",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (question.wrongAttempts > 0) Color.White else MaterialTheme.colorScheme.onSecondaryContainer,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                androidx.compose.foundation.text.selection.SelectionContainer(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (expanded) "收起选项" else "展开选项",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }

            AnimatedVisibility(visible = expanded) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    val options = listOf(
                        "A" to question.optionA,
                        "B" to question.optionB,
                        "C" to question.optionC,
                        "D" to question.optionD
                    )
                    options.forEach { (key, text) ->
                        val isCorrect = question.correctOption.equals(key, ignoreCase = true)
                        Surface(
                            color = if (isCorrect) {
                                CorrectGreenContainer.copy(alpha = 0.7f)
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "$key. $text",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isCorrect) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCorrect) Color(0xFF064E3B) else MaterialTheme.colorScheme.onSurface
                                )
                                if (isCorrect) {
                                    Text(
                                        text = "正确答案",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = CorrectGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onMarkWrongOnce,
                            modifier = Modifier.testTag("bank_mark_wrong_${question.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AddAlert,
                                contentDescription = "记入错题",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("记错一次")
                        }
                        if (question.totalAttempts > 0) {
                            TextButton(
                                onClick = onResetStats,
                                modifier = Modifier.testTag("bank_reset_item_${question.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.RestartAlt,
                                    contentDescription = "清零本题记录",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("清零本题")
                            }
                        }
                    }
                }
            }
        }
    }
}
