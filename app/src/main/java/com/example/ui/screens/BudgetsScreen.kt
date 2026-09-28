package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.ui.components.BudgetProgressCard
import com.example.ui.components.MonthSelector
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.FinanceUiState
import com.example.util.CurrencyUtils

enum class BudgetFilter {
    ALL,
    CONFIGURED,
    EXCEEDED,
    AVAILABLE
}

@Composable
fun BudgetsScreen(
    uiState: FinanceUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    onEditBudget: (Category) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf(BudgetFilter.ALL) }

    val totalBudgetLimit = uiState.budgetProgressList.sumOf { it.budgetLimit }
    val totalBudgetSpent = uiState.budgetProgressList.filter { it.budgetLimit > 0 }.sumOf { it.spent }
    val totalBudgetPercentage = if (totalBudgetLimit > 0) ((totalBudgetSpent / totalBudgetLimit) * 100).toFloat() else 0f

    val exceededCategoriesCount = uiState.budgetProgressList.count { it.budgetLimit > 0 && it.spent > it.budgetLimit }
    val configuredBudgetsCount = uiState.budgetProgressList.count { it.budgetLimit > 0 }

    val filteredList = when (selectedFilter) {
        BudgetFilter.ALL -> uiState.budgetProgressList
        BudgetFilter.CONFIGURED -> uiState.budgetProgressList.filter { it.budgetLimit > 0 }
        BudgetFilter.EXCEEDED -> uiState.budgetProgressList.filter { it.budgetLimit > 0 && it.spent > it.budgetLimit }
        BudgetFilter.AVAILABLE -> uiState.budgetProgressList.filter { it.budgetLimit == 0.0 }
    }

    val totalProgressColor = when {
        totalBudgetPercentage >= 100f -> ExpenseRed
        totalBudgetPercentage >= 80f -> WarningAmber
        else -> IncomeGreen
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("budgets_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Month Selector
        item {
            MonthSelector(
                selectedYear = uiState.selectedYear,
                selectedMonth = uiState.selectedMonth,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth,
                onResetToCurrentMonth = onResetToCurrentMonth
            )
        }

        // Overall Budget Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Orçamento Global do Mês",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = totalProgressColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (totalBudgetLimit > 0) "${String.format("%.0f", totalBudgetPercentage)}%" else "Sem metas",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = totalProgressColor
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                text = "Gasto em categorias orçadas",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyUtils.format(totalBudgetSpent),
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.ExtraBold
                                ),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Limite global planejado",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = CurrencyUtils.format(totalBudgetLimit),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Global Progress Bar
                    LinearProgressIndicator(
                        progress = { if (totalBudgetLimit > 0) (totalBudgetPercentage / 100f).coerceIn(0f, 1f) else 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = totalProgressColor,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val remaining = totalBudgetLimit - totalBudgetSpent
                        Text(
                            text = if (totalBudgetLimit == 0.0) "Defina limites abaixo para acompanhar"
                            else if (remaining >= 0) "Resta: ${CurrencyUtils.format(remaining)}"
                            else "Ultrapassou: ${CurrencyUtils.format(-remaining)}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (remaining < 0) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )

                        Text(
                            text = "$configuredBudgetsCount categorias ativas",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Exceeded Alert if any
                    if (exceededCategoriesCount > 0) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = ExpenseRed.copy(alpha = 0.12f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = ExpenseRed,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "$exceededCategoriesCount categoria(s) excederam o limite estipulado!",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = ExpenseRed,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }

        // Filter chips row
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedFilter == BudgetFilter.ALL,
                        onClick = { selectedFilter = BudgetFilter.ALL },
                        label = { Text("Todas (${uiState.budgetProgressList.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                item {
                    FilterChip(
                        selected = selectedFilter == BudgetFilter.CONFIGURED,
                        onClick = { selectedFilter = BudgetFilter.CONFIGURED },
                        label = { Text("Com Metas ($configuredBudgetsCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }

                if (exceededCategoriesCount > 0) {
                    item {
                        FilterChip(
                            selected = selectedFilter == BudgetFilter.EXCEEDED,
                            onClick = { selectedFilter = BudgetFilter.EXCEEDED },
                            label = { Text("Excedidas ($exceededCategoriesCount)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                                selectedLabelColor = ExpenseRed
                            )
                        )
                    }
                }

                item {
                    val availableCount = uiState.budgetProgressList.count { it.budgetLimit == 0.0 }
                    FilterChip(
                        selected = selectedFilter == BudgetFilter.AVAILABLE,
                        onClick = { selectedFilter = BudgetFilter.AVAILABLE },
                        label = { Text("Sem Metas ($availableCount)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }
        }

        // Category Budgets Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progresso por Categoria",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = "Toque no lápis para alterar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Category Budget Cards List
        items(filteredList, key = { it.category.id }) { item ->
            BudgetProgressCard(
                progress = item,
                onEditBudget = { onEditBudget(item.category) }
            )
        }
    }
}
