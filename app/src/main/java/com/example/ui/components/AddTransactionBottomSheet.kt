package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TransactionEntity
import com.example.data.model.Category
import com.example.data.model.TransactionType
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.util.CurrencyUtils
import com.example.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddTransactionBottomSheet(
    editingTransaction: TransactionEntity?,
    defaultTransactionType: TransactionType = TransactionType.DESPESA,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        title: String,
        amount: Double,
        type: TransactionType,
        categoryId: String,
        timestamp: Long,
        notes: String,
        recurrenceType: String,
        totalInstallments: Int,
        isAmountTotal: Boolean
    ) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var type by remember {
        mutableStateOf(editingTransaction?.transactionType ?: defaultTransactionType)
    }

    var title by remember {
        mutableStateOf(editingTransaction?.title ?: "")
    }

    var amountText by remember {
        mutableStateOf(editingTransaction?.amount?.let { String.format("%.2f", it).replace(',', '.') } ?: "")
    }

    var selectedCategoryId by remember {
        mutableStateOf(
            editingTransaction?.categoryId ?: if (type == TransactionType.RECEITA) "cat_salario" else "cat_alimentacao"
        )
    }

    var timestamp by remember {
        mutableLongStateOf(editingTransaction?.timestamp ?: System.currentTimeMillis())
    }

    var notes by remember {
        mutableStateOf(editingTransaction?.notes ?: "")
    }

    // Recurrence & Installment States
    var recurrenceMode by remember {
        mutableStateOf(
            when (editingTransaction?.recurrenceType) {
                "PARCELADA" -> "PARCELADA"
                "FIXA" -> "FIXA"
                else -> "UNICA"
            }
        )
    }

    var totalInstallments by remember {
        mutableIntStateOf(if (editingTransaction != null && editingTransaction.totalInstallments > 1) editingTransaction.totalInstallments else 2)
    }

    var installmentText by remember {
        mutableStateOf(if (editingTransaction != null && editingTransaction.totalInstallments > 1) editingTransaction.totalInstallments.toString() else "2")
    }

    var fixedRecurringMonths by remember {
        mutableIntStateOf(12) // Default 12 months for salary / pension
    }

    var isAmountTotal by remember {
        mutableStateOf(false) // false = amount is per installment; true = total divided
    }

    var hasAttemptedSave by remember { mutableStateOf(false) }

    val categoriesForType = remember(type) {
        Category.ALL_CATEGORIES.filter {
            it.type == null || it.type == type
        }
    }
    
    var isCategoryDropdownExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(type) {
        if (categoriesForType.none { it.id == selectedCategoryId }) {
            categoriesForType.firstOrNull()?.let {
                selectedCategoryId = it.id
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = Modifier.testTag("add_transaction_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (editingTransaction == null) "Nova Transação" else "Editar Transação",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("bottom_sheet_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Type Segment (Receita vs Despesa)
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Despesa Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (type == TransactionType.DESPESA) ExpenseRed
                                else Color.Transparent
                            )
                            .clickable { type = TransactionType.DESPESA }
                            .padding(vertical = 10.dp)
                            .testTag("type_despesa_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Despesa",
                            fontWeight = FontWeight.Bold,
                            color = if (type == TransactionType.DESPESA) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Receita Tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (type == TransactionType.RECEITA) IncomeGreen
                                else Color.Transparent
                            )
                            .clickable { type = TransactionType.RECEITA }
                            .padding(vertical = 10.dp)
                            .testTag("type_receita_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Receita",
                            fontWeight = FontWeight.Bold,
                            color = if (type == TransactionType.RECEITA) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Frequência & Recorrência Selector
            if (editingTransaction == null) {
                Text(
                    text = "Frequência / Tipo de Lançamento",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Única
                    FilterChip(
                        selected = recurrenceMode == "UNICA",
                        onClick = { recurrenceMode = "UNICA" },
                        label = { Text("Única") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    // Parcelada (Cartão, Carnê, Empréstimo)
                    if (type == TransactionType.DESPESA) {
                        FilterChip(
                            selected = recurrenceMode == "PARCELADA",
                            onClick = {
                                recurrenceMode = "PARCELADA"
                                if (selectedCategoryId !in listOf("cat_cartao", "cat_carne", "cat_emprestimo")) {
                                    selectedCategoryId = "cat_cartao"
                                }
                            },
                            label = { Text("Parcelada") },
                            leadingIcon = {
                                Icon(Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(14.dp))
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF6366F1).copy(alpha = 0.2f),
                                selectedLabelColor = Color(0xFF4F46E5)
                            ),
                            modifier = Modifier.weight(1.2f)
                        )
                    }

                    // Fixa / Recorrente (Salário, Pensão, Aluguel)
                    FilterChip(
                        selected = recurrenceMode == "FIXA",
                        onClick = {
                            recurrenceMode = "FIXA"
                            if (type == TransactionType.RECEITA && selectedCategoryId !in listOf("cat_salario", "cat_pensao", "cat_aluguel_rec")) {
                                selectedCategoryId = "cat_salario"
                            }
                        },
                        label = { Text("Fixa Mensal") },
                        leadingIcon = {
                            Icon(Icons.Default.Repeat, contentDescription = null, modifier = Modifier.size(14.dp))
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = IncomeGreen.copy(alpha = 0.2f),
                            selectedLabelColor = IncomeGreen
                        ),
                        modifier = Modifier.weight(1.2f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section for Parcelada configuration
                AnimatedVisibility(visible = recurrenceMode == "PARCELADA") {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CreditCard,
                                    contentDescription = null,
                                    tint = Color(0xFF4F46E5),
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Configuração do Parcelamento (Cartão, Carnê ou Empréstimo)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Número de parcelas:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Campo de digitação livre do número de parcelas
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = installmentText,
                                    onValueChange = { raw ->
                                        // Aceita apenas dígitos, limita a 3 chars
                                        val digits = raw.filter { it.isDigit() }.take(3)
                                        installmentText = digits
                                        val parsed = digits.toIntOrNull() ?: 0
                                        if (parsed in 1..360) totalInstallments = parsed
                                    },
                                    suffix = { Text("x", fontWeight = FontWeight.Bold, color = Color(0xFF6366F1)) },
                                    placeholder = { Text("Ex: 12") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.width(110.dp)
                                )
                                Text(
                                    text = if (totalInstallments > 1) "parcela(s) mensais" else "parcela",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Atalhos rápidos
                            val installmentPresets = listOf(2, 3, 4, 6, 10, 12, 24, 48)
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                installmentPresets.forEach { count ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (totalInstallments == count) Color(0xFF6366F1)
                                        else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable {
                                                totalInstallments = count
                                                installmentText = count.toString()
                                            }
                                    ) {
                                        Text(
                                            text = "${count}x",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (totalInstallments == count) Color.White else MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Valor é total ou por parcela?
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (!isAmountTotal) Color(0xFF6366F1).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    border = if (!isAmountTotal) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1)) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { isAmountTotal = false }
                                ) {
                                    Text(
                                        text = "Valor por parcela",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isAmountTotal) Color(0xFF6366F1).copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface,
                                    border = if (isAmountTotal) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1)) else null,
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable { isAmountTotal = true }
                                ) {
                                    Text(
                                        text = "Valor total da compra",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                        modifier = Modifier.padding(8.dp),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            // Live explanation
                            val currentAmount = amountText.toDoubleOrNull() ?: 0.0
                            if (currentAmount > 0.0) {
                                Spacer(modifier = Modifier.height(10.dp))
                                val installmentVal = if (isAmountTotal) currentAmount / totalInstallments else currentAmount
                                val totalVal = if (isAmountTotal) currentAmount else currentAmount * totalInstallments
                                Text(
                                    text = "→ Serão geradas $totalInstallments parcelas de ${CurrencyUtils.format(installmentVal)} mensais (Total: ${CurrencyUtils.format(totalVal)})",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF4F46E5)
                                    )
                                )
                            }
                        }
                    }
                }

                // Section for Fixa Mensal configuration
                AnimatedVisibility(visible = recurrenceMode == "FIXA") {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Repeat,
                                    contentDescription = null,
                                    tint = IncomeGreen,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Lançamento Recorrente (Salário, Pensão ou Contas Fixas)",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "Programar mensalmente por:",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            val monthsPresets = listOf(3, 6, 12, 24)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                monthsPresets.forEach { months ->
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = if (fixedRecurringMonths == months) IncomeGreen
                                        else MaterialTheme.colorScheme.surface,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { fixedRecurringMonths = months }
                                    ) {
                                        Text(
                                            text = "$months meses",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = if (fixedRecurringMonths == months) Color.White else MaterialTheme.colorScheme.onSurface
                                            ),
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            }

                            val currentAmount = amountText.toDoubleOrNull() ?: 0.0
                            if (currentAmount > 0.0) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "→ Será agendado ${CurrencyUtils.format(currentAmount)} todo mês pelos próximos $fixedRecurringMonths meses.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = IncomeGreen
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Amount Input
            Text(
                text = if (recurrenceMode == "PARCELADA" && isAmountTotal) "Valor Total da Compra (R$)"
                else if (recurrenceMode == "PARCELADA") "Valor por Parcela (R$)"
                else "Valor (R$)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it.replace(',', '.') },
                placeholder = { Text("0.00") },
                prefix = {
                    Text(
                        text = "R$ ",
                        fontWeight = FontWeight.Bold,
                        color = if (type == TransactionType.RECEITA) IncomeGreen else ExpenseRed
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                isError = hasAttemptedSave && (amountText.toDoubleOrNull() ?: 0.0) <= 0.0,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_amount_input"),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title / Description
            Text(
                text = "Descrição / Título",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                placeholder = {
                    Text(
                        when {
                            recurrenceMode == "PARCELADA" -> "Ex: Smartphone, Notebook, Carnê Loja..."
                            type == TransactionType.RECEITA -> "Ex: Salário Mensal, Pensão Alimentícia..."
                            else -> "Ex: Supermercado, Aluguel..."
                        }
                    )
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Title, contentDescription = null)
                },
                singleLine = true,
                isError = hasAttemptedSave && title.isBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_title_input"),
                shape = RoundedCornerShape(14.dp)
            )

            // Quick suggestion chips based on mode
            Spacer(modifier = Modifier.height(8.dp))
            val suggestions = when {
                recurrenceMode == "PARCELADA" -> listOf("Compra Parcelada", "Notebook", "Smartphone", "Carnê Móveis", "Empréstimo Caixa")
                recurrenceMode == "FIXA" && type == TransactionType.RECEITA -> listOf("Salário Mensal", "Pensão Alimentícia", "Pensão Aposentadoria", "Aluguel Recebido", "Benefício INSS")
                recurrenceMode == "FIXA" && type == TransactionType.DESPESA -> listOf("Aluguel Moradia", "Plano de Saúde", "Condomínio", "Academia", "Internet")
                type == TransactionType.RECEITA -> listOf("Salário", "Freelance", "Rendimentos", "Venda", "Reembolso")
                else -> listOf("Supermercado", "Combustível", "Restaurante", "Farmácia", "Lazer", "Uber")
            }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                suggestions.forEach { suggestion ->
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                title = suggestion
                                // Auto-select matching category
                                when (suggestion) {
                                    "Salário", "Salário Mensal" -> selectedCategoryId = "cat_salario"
                                    "Pensão Alimentícia", "Pensão Aposentadoria", "Benefício INSS" -> selectedCategoryId = "cat_pensao"
                                    "Aluguel Recebido" -> selectedCategoryId = "cat_aluguel_rec"
                                    "Compra Parcelada", "Smartphone", "Notebook" -> selectedCategoryId = "cat_cartao"
                                    "Carnê Móveis" -> selectedCategoryId = "cat_carne"
                                    "Empréstimo Caixa" -> selectedCategoryId = "cat_emprestimo"
                                }
                            }
                    ) {
                        Text(
                            text = suggestion,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Category Picker
            Text(
                text = "Categoria",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))

            val selectedCategory = categoriesForType.find { it.id == selectedCategoryId } ?: categoriesForType.firstOrNull()
            
            androidx.compose.material3.ExposedDropdownMenuBox(
                expanded = isCategoryDropdownExpanded,
                onExpandedChange = { isCategoryDropdownExpanded = !isCategoryDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = {
                        selectedCategory?.let {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = null,
                                tint = it.color,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    },
                    trailingIcon = {
                        androidx.compose.material3.ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryDropdownExpanded)
                    },
                    colors = androidx.compose.material3.ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                ExposedDropdownMenu(
                    expanded = isCategoryDropdownExpanded,
                    onDismissRequest = { isCategoryDropdownExpanded = false }
                ) {
                    categoriesForType.forEach { cat ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = { 
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Icon(
                                        imageVector = cat.icon,
                                        contentDescription = null,
                                        tint = cat.color,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = cat.name,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = if (cat.id == selectedCategoryId) FontWeight.Bold else FontWeight.Normal
                                        )
                                    )
                                }
                            },
                            onClick = {
                                selectedCategoryId = cat.id
                                isCategoryDropdownExpanded = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Date Selection
            Text(
                text = if (recurrenceMode == "PARCELADA") "Data da 1ª Parcela" else "Data de Início",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Today chip
                val isToday = DateUtils.isToday(timestamp)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isToday) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { timestamp = System.currentTimeMillis() }
                ) {
                    Text(
                        text = "Hoje",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                // Yesterday chip
                val isYesterday = DateUtils.isYesterday(timestamp)
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = if (isYesterday) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            val cal = Calendar.getInstance()
                            cal.add(Calendar.DAY_OF_YEAR, -1)
                            timestamp = cal.timeInMillis
                        }
                ) {
                    Text(
                        text = "Ontem",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isYesterday) FontWeight.Bold else FontWeight.Normal,
                            color = if (isYesterday) MaterialTheme.colorScheme.onPrimaryContainer
                            else MaterialTheme.colorScheme.onSurface
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }

                Text(
                    text = DateUtils.formatDate(timestamp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(start = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Notes
            Text(
                text = "Observações (Opcional)",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text("Ex: Cartão Nubank, Carnê Magazine, etc.") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Note, contentDescription = null)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tx_notes_input"),
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(26.dp))

            // Save Button
            Button(
                onClick = {
                    hasAttemptedSave = true
                    val parsedAmount = amountText.toDoubleOrNull()
                    if (parsedAmount != null && parsedAmount > 0.0 && title.isNotBlank()) {
                        val installmentsCount = when (recurrenceMode) {
                            "PARCELADA" -> totalInstallments
                            "FIXA" -> fixedRecurringMonths
                            else -> 1
                        }

                        onSave(
                            editingTransaction?.id ?: 0L,
                            title.trim(),
                            parsedAmount,
                            type,
                            selectedCategoryId,
                            timestamp,
                            notes.trim(),
                            recurrenceMode,
                            installmentsCount,
                            isAmountTotal
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_transaction_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (type == TransactionType.RECEITA) IncomeGreen else MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = when {
                        editingTransaction != null -> "Atualizar Transação"
                        recurrenceMode == "PARCELADA" -> "Gerar $totalInstallments Parcelas"
                        recurrenceMode == "FIXA" -> "Programar $fixedRecurringMonths Meses"
                        else -> "Salvar Transação"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}
