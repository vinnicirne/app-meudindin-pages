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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.model.Category
import com.example.data.model.TransactionType
import com.example.ui.components.MonthSelector
import com.example.ui.components.TransactionItem
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.FilterType
import com.example.ui.viewmodel.FinanceUiState
import com.example.util.CurrencyUtils

@Composable
fun TransactionsScreen(
    uiState: FinanceUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onResetToCurrentMonth: () -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterTypeChange: (FilterType) -> Unit,
    onFilterCategoryChange: (String?) -> Unit,
    onEditTransaction: (TransactionEntity) -> Unit,
    onDeleteTransaction: (TransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var transactionToDelete by remember { mutableStateOf<TransactionEntity?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("transactions_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
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

        // Search Field
        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Pesquisar por título, nota ou categoria...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "Pesquisar")
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Limpar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_transactions_input")
            )
        }

        // Type Filter Tabs (Todas, Receitas, Despesas)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.filterType == FilterType.ALL,
                        onClick = { onFilterTypeChange(FilterType.ALL) },
                        label = { Text("Todas") },
                        modifier = Modifier.testTag("filter_all_chip")
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterType == FilterType.RECEITAS,
                        onClick = { onFilterTypeChange(FilterType.RECEITAS) },
                        label = { Text("Receitas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                            selectedLabelColor = IncomeGreen
                        ),
                        modifier = Modifier.testTag("filter_income_chip")
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterType == FilterType.DESPESAS,
                        onClick = { onFilterTypeChange(FilterType.DESPESAS) },
                        label = { Text("Despesas") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ExpenseRed.copy(alpha = 0.2f),
                            selectedLabelColor = ExpenseRed
                        ),
                        modifier = Modifier.testTag("filter_expense_chip")
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterType == FilterType.PARCELADAS,
                        onClick = { onFilterTypeChange(FilterType.PARCELADAS) },
                        label = { Text("Parceladas (Cartão/Carnê)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF6366F1).copy(alpha = 0.2f),
                            selectedLabelColor = Color(0xFF4F46E5)
                        ),
                        modifier = Modifier.testTag("filter_installments_chip")
                    )
                }
                item {
                    FilterChip(
                        selected = uiState.filterType == FilterType.RECORRENTES,
                        onClick = { onFilterTypeChange(FilterType.RECORRENTES) },
                        label = { Text("Recorrentes (Salário/Fixas)") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                            selectedLabelColor = IncomeGreen
                        ),
                        modifier = Modifier.testTag("filter_recurring_chip")
                    )
                }
            }
        }

        // Horizontal Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAllCategories = uiState.filterCategoryId == null
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { onFilterCategoryChange(null) }
                            .testTag("cat_filter_all"),
                        color = if (isAllCategories) MaterialTheme.colorScheme.primaryContainer
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Text(
                            text = "Todas Categorias",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isAllCategories) FontWeight.Bold else FontWeight.Medium,
                                color = if (isAllCategories) MaterialTheme.colorScheme.onPrimaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                items(Category.ALL_CATEGORIES) { cat ->
                    val isSelected = uiState.filterCategoryId == cat.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                onFilterCategoryChange(if (isSelected) null else cat.id)
                            }
                            .testTag("cat_filter_${cat.id}"),
                        color = if (isSelected) cat.color.copy(alpha = 0.25f)
                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(cat.color)
                            )
                            Text(
                                text = cat.name,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) cat.color else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }

        // Summary of Filtered Items
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${uiState.filteredTransactions.size} registro(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                var filteredSum = 0.0
                uiState.filteredTransactions.forEach {
                    if (it.transactionType == TransactionType.RECEITA) filteredSum += it.amount
                    else filteredSum -= it.amount
                }
                Text(
                    text = "Líquido: ${CurrencyUtils.format(filteredSum)}",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (filteredSum >= 0) IncomeGreen else ExpenseRed
                    )
                )
            }
        }

        // Filtered List
        if (uiState.filteredTransactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.size(40.dp)
                        )
                        Text(
                            text = "Nenhuma transação encontrada",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "Tente alterar os filtros ou o mês selecionado",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        } else {
            items(uiState.filteredTransactions, key = { it.id }) { tx ->
                TransactionItem(
                    transaction = tx,
                    onEdit = { onEditTransaction(tx) },
                    onDelete = { transactionToDelete = tx }
                )
            }
        }
    }

    // Delete confirmation dialog
    transactionToDelete?.let { tx ->
        AlertDialog(
            onDismissRequest = { transactionToDelete = null },
            title = { Text("Excluir Transação") },
            text = {
                Text("Deseja realmente excluir \"${tx.title}\" no valor de ${CurrencyUtils.format(tx.amount)}?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteTransaction(tx)
                        transactionToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed),
                    modifier = Modifier.testTag("confirm_delete_btn")
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { transactionToDelete = null },
                    modifier = Modifier.testTag("cancel_delete_btn")
                ) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }
}
