package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.BudgetEntity
import com.example.data.local.TransactionEntity
import com.example.data.local.UserSession
import com.example.data.model.Category
import com.example.data.model.TransactionType
import com.example.data.repository.AuthRepository
import com.example.data.repository.CloudSyncStatus
import com.example.data.repository.FinanceRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

enum class ScreenTab {
    DASHBOARD,
    STATS,
    TRANSACTIONS,
    BUDGETS
}

enum class FilterType {
    ALL,
    RECEITAS,
    DESPESAS,
    PARCELADAS,
    RECORRENTES
}

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

data class CategoryBreakdown(
    val category: Category,
    val totalAmount: Double,
    val percentage: Float // 0..100
)

data class MonthlyBarData(
    val monthName: String,
    val monthIndex: Int,
    val year: Int,
    val income: Double,
    val expense: Double
)

data class CategoryBudgetProgress(
    val category: Category,
    val spent: Double,
    val budgetLimit: Double,
    val percentage: Float
)

data class FinanceUiState(
    val currentTab: ScreenTab = ScreenTab.DASHBOARD,
    val selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR),
    val selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH), // 0-based
    val totalBalanceAllTime: Double = 0.0,
    val monthlyIncome: Double = 0.0,
    val monthlyExpense: Double = 0.0,
    val monthlyBalance: Double = 0.0,
    val monthlySavingsRate: Float = 0f,
    val monthlyTransactions: List<TransactionEntity> = emptyList(),
    val filteredTransactions: List<TransactionEntity> = emptyList(),
    val categoryBreakdown: List<CategoryBreakdown> = emptyList(),
    val sixMonthsHistory: List<MonthlyBarData> = emptyList(),
    val budgetProgressList: List<CategoryBudgetProgress> = emptyList(),
    val searchQuery: String = "",
    val filterType: FilterType = FilterType.ALL,
    val filterCategoryId: String? = null,
    val isAddEditSheetOpen: Boolean = false,
    val defaultTransactionType: TransactionType = TransactionType.DESPESA,
    val editingTransaction: TransactionEntity? = null,
    val isEditBudgetDialogOpen: Boolean = false,
    val editingBudgetCategory: Category? = null,
    val userMessage: String? = null,
    val cloudSyncStatus: CloudSyncStatus = CloudSyncStatus.IDLE,
    val cloudSyncMessage: String? = null,
    val currentUser: UserSession? = null,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null,
    val isGuestMode: Boolean = false,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class FinanceViewModel(
    application: Application,
    private val repository: FinanceRepository,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        // Observe auth user session
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
                if (user != null) {
                    syncWithSupabase()
                }
            }
        }

        // Observe local room transactions and budgets
        viewModelScope.launch {
            combine(
                repository.allTransactions,
                repository.allBudgets,
                _uiState
            ) { transactions, budgets, state ->
                computeState(transactions, budgets, state)
            }.collect { newState ->
                _uiState.value = newState
            }
        }

        // Observe Supabase Cloud Sync Status
        viewModelScope.launch {
            repository.syncStatus.collect { status ->
                _uiState.update { it.copy(cloudSyncStatus = status) }
            }
        }

        viewModelScope.launch {
            repository.syncMessage.collect { msg ->
                _uiState.update { it.copy(cloudSyncMessage = msg) }
            }
        }

        // Trigger initial sync
        syncWithSupabase()
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = authRepository.login(email, pass)
            result.fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            currentUser = session,
                            authErrorMessage = null,
                            isGuestMode = false
                        )
                    }
                    showUserMessage("Bem-vindo(a), ${session.email}!")
                    syncWithSupabase()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authErrorMessage = err.message ?: "Erro ao entrar"
                        )
                    }
                }
            )
        }
    }

    fun signup(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
            val result = authRepository.signup(email, pass)
            result.fold(
                onSuccess = { session ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            currentUser = session,
                            authErrorMessage = null,
                            isGuestMode = false
                        )
                    }
                    showUserMessage("Conta criada com sucesso!")
                    syncWithSupabase()
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            isAuthLoading = false,
                            authErrorMessage = err.message ?: "Erro ao criar conta"
                        )
                    }
                }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update {
                it.copy(currentUser = null, isGuestMode = false)
            }
            showUserMessage("Sessão encerrada.")
        }
    }

    fun continueAsGuest() {
        _uiState.update { it.copy(isGuestMode = true, authErrorMessage = null) }
    }

    fun syncWithSupabase() {
        viewModelScope.launch {
            repository.syncWithSupabase()
        }
    }

    private fun computeState(
        allTransactions: List<TransactionEntity>,
        budgets: List<BudgetEntity>,
        currentState: FinanceUiState
    ): FinanceUiState {
        val selectedYear = currentState.selectedYear
        val selectedMonth = currentState.selectedMonth

        // All-time balance
        var totalBalance = 0.0
        allTransactions.forEach { tx ->
            if (tx.transactionType == TransactionType.RECEITA) {
                totalBalance += tx.amount
            } else {
                totalBalance -= tx.amount
            }
        }

        // Transactions for selected month
        val startMonthTime = DateUtils.getStartOfMonth(selectedYear, selectedMonth)
        val endMonthTime = DateUtils.getEndOfOfMonth(selectedYear, selectedMonth)

        val monthTransactions = allTransactions.filter {
            it.timestamp in startMonthTime..endMonthTime
        }

        var mIncome = 0.0
        var mExpense = 0.0
        monthTransactions.forEach { tx ->
            if (tx.transactionType == TransactionType.RECEITA) {
                mIncome += tx.amount
            } else {
                mExpense += tx.amount
            }
        }

        val mBalance = mIncome - mExpense
        val savingsRate = if (mIncome > 0) {
            (((mIncome - mExpense) / mIncome) * 100).toFloat().coerceIn(-100f, 100f)
        } else 0f

        // Category breakdown for expenses
        val expenseTransactions = monthTransactions.filter { it.transactionType == TransactionType.DESPESA }
        val categoryExpenses = mutableMapOf<String, Double>()
        expenseTransactions.forEach { tx ->
            categoryExpenses[tx.categoryId] = (categoryExpenses[tx.categoryId] ?: 0.0) + tx.amount
        }

        val breakdown = categoryExpenses.map { (catId, amount) ->
            val percentage = if (mExpense > 0) ((amount / mExpense) * 100).toFloat() else 0f
            CategoryBreakdown(
                category = Category.findById(catId),
                totalAmount = amount,
                percentage = percentage
            )
        }.sortedByDescending { it.totalAmount }

        // 6-month historical data
        val historyList = mutableListOf<MonthlyBarData>()
        val cal = Calendar.getInstance()
        cal.set(Calendar.YEAR, selectedYear)
        cal.set(Calendar.MONTH, selectedMonth)

        for (i in 5 downTo 0) {
            val histCal = Calendar.getInstance()
            histCal.timeInMillis = cal.timeInMillis
            histCal.add(Calendar.MONTH, -i)
            val hYear = histCal.get(Calendar.YEAR)
            val hMonth = histCal.get(Calendar.MONTH)

            val hStart = DateUtils.getStartOfMonth(hYear, hMonth)
            val hEnd = DateUtils.getEndOfOfMonth(hYear, hMonth)

            var hIncome = 0.0
            var hExpense = 0.0
            allTransactions.forEach { tx ->
                if (tx.timestamp in hStart..hEnd) {
                    if (tx.transactionType == TransactionType.RECEITA) {
                        hIncome += tx.amount
                    } else {
                        hExpense += tx.amount
                    }
                }
            }

            historyList.add(
                MonthlyBarData(
                    monthName = DateUtils.getShortMonthName(hMonth),
                    monthIndex = hMonth,
                    year = hYear,
                    income = hIncome,
                    expense = hExpense
                )
            )
        }

        // Budget progress
        val budgetMap = budgets.associate { it.categoryId to it.monthlyLimit }
        val defaultExpenseCategories = Category.ALL_CATEGORIES.filter {
            it.type == TransactionType.DESPESA || it.type == null
        }

        val budgetProgress = defaultExpenseCategories.map { category ->
            val spent = categoryExpenses[category.id] ?: 0.0
            val limit = budgetMap[category.id] ?: 0.0
            val percentage = if (limit > 0) ((spent / limit) * 100).toFloat() else 0f
            CategoryBudgetProgress(
                category = category,
                spent = spent,
                budgetLimit = limit,
                percentage = percentage
            )
        }

        // Apply filters for transactions tab
        val query = currentState.searchQuery.trim().lowercase()
        val filtered = monthTransactions.filter { tx ->
            val matchesQuery = query.isEmpty() ||
                    tx.title.lowercase().contains(query) ||
                    tx.notes.lowercase().contains(query) ||
                    Category.findById(tx.categoryId).name.lowercase().contains(query)

            val matchesType = when (currentState.filterType) {
                FilterType.ALL -> true
                FilterType.RECEITAS -> tx.transactionType == TransactionType.RECEITA
                FilterType.DESPESAS -> tx.transactionType == TransactionType.DESPESA
                FilterType.PARCELADAS -> tx.isInstallment || tx.categoryId in listOf("cat_cartao", "cat_carne", "cat_emprestimo")
                FilterType.RECORRENTES -> tx.isFixedRecurring || tx.categoryId in listOf("cat_salario", "cat_pensao", "cat_aluguel_rec")
            }

            val matchesCategory = currentState.filterCategoryId == null ||
                    tx.categoryId == currentState.filterCategoryId

            matchesQuery && matchesType && matchesCategory
        }

        return currentState.copy(
            totalBalanceAllTime = totalBalance,
            monthlyIncome = mIncome,
            monthlyExpense = mExpense,
            monthlyBalance = mBalance,
            monthlySavingsRate = savingsRate,
            monthlyTransactions = monthTransactions,
            filteredTransactions = filtered,
            categoryBreakdown = breakdown,
            sixMonthsHistory = historyList,
            budgetProgressList = budgetProgress
        )
    }

    fun setScreenTab(tab: ScreenTab) {
        _uiState.update { it.copy(currentTab = tab) }
    }

    fun nextMonth() {
        _uiState.update { state ->
            var newMonth = state.selectedMonth + 1
            var newYear = state.selectedYear
            if (newMonth > 11) {
                newMonth = 0
                newYear += 1
            }
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
    }

    fun previousMonth() {
        _uiState.update { state ->
            var newMonth = state.selectedMonth - 1
            var newYear = state.selectedYear
            if (newMonth < 0) {
                newMonth = 11
                newYear -= 1
            }
            state.copy(selectedMonth = newMonth, selectedYear = newYear)
        }
    }

    fun resetToCurrentMonth() {
        val now = Calendar.getInstance()
        _uiState.update {
            it.copy(
                selectedYear = now.get(Calendar.YEAR),
                selectedMonth = now.get(Calendar.MONTH)
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun setFilterType(filterType: FilterType) {
        _uiState.update { it.copy(filterType = filterType) }
    }

    fun setFilterCategoryId(catId: String?) {
        _uiState.update { it.copy(filterCategoryId = catId) }
    }

    fun setThemeMode(mode: ThemeMode) {
        _uiState.update { it.copy(themeMode = mode) }
    }

    fun openAddTransactionSheet(defaultType: TransactionType = TransactionType.DESPESA) {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = true,
                defaultTransactionType = defaultType,
                editingTransaction = null
            )
        }
    }

    fun openEditTransactionSheet(transaction: TransactionEntity) {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = true,
                editingTransaction = transaction
            )
        }
    }

    fun closeAddEditSheet() {
        _uiState.update {
            it.copy(
                isAddEditSheetOpen = false,
                editingTransaction = null
            )
        }
    }

    fun saveTransaction(
        id: Long = 0,
        title: String,
        amount: Double,
        type: TransactionType,
        categoryId: String,
        timestamp: Long,
        notes: String,
        recurrenceType: String = "UNICA",
        totalInstallments: Int = 1,
        isAmountTotal: Boolean = false
    ) {
        viewModelScope.launch {
            // EDIÇÃO de transação existente: apenas atualiza, nunca regera parcelas
            if (id != 0L) {
                val entity = TransactionEntity(
                    id = id,
                    title = title.ifBlank { "Sem título" },
                    amount = amount,
                    type = type.name,
                    categoryId = categoryId,
                    timestamp = timestamp,
                    notes = notes,
                    recurrenceType = recurrenceType,
                    installmentNumber = 1,
                    totalInstallments = totalInstallments
                )
                repository.updateTransaction(entity)
                showUserMessage("Transação atualizada!")
                closeAddEditSheet()
                return@launch
            }

            // CRIAÇÃO — transação única ou sem parcelamento
            if (recurrenceType == "UNICA" || totalInstallments <= 1) {
                val entity = TransactionEntity(
                    id = 0L,
                    title = title.ifBlank { "Sem título" },
                    amount = amount,
                    type = type.name,
                    categoryId = categoryId,
                    timestamp = timestamp,
                    notes = notes,
                    recurrenceType = "UNICA",
                    installmentNumber = 1,
                    totalInstallments = 1
                )
                repository.insertTransaction(entity)
                showUserMessage("Transação salva e sincronizada!")
                closeAddEditSheet()
                return@launch
            }

            // CRIAÇÃO — parcelada ou fixa mensal: gera N entidades, uma por mês
            // Cada parcela tem seu próprio timestamp (mês correspondente) e valor por parcela
            val seriesId = java.util.UUID.randomUUID().toString()
            val entities = mutableListOf<TransactionEntity>()
            val cal = Calendar.getInstance()
            cal.timeInMillis = timestamp

            val perInstallmentAmount = if (recurrenceType == "PARCELADA" && isAmountTotal) {
                amount / totalInstallments  // divide o total pelas parcelas
            } else {
                amount  // valor já é por parcela
            }

            for (i in 1..totalInstallments) {
                val txTitle = when (recurrenceType) {
                    "PARCELADA" -> "$title ($i/$totalInstallments)"
                    "FIXA" -> "$title (Recorrente)"
                    else -> title
                }

                entities.add(
                    TransactionEntity(
                        id = 0L,
                        title = txTitle,
                        amount = perInstallmentAmount,      // valor da PARCELA, não o total
                        type = type.name,
                        categoryId = categoryId,
                        timestamp = cal.timeInMillis,       // cada parcela no seu próprio mês
                        notes = notes,
                        recurrenceType = recurrenceType,
                        installmentNumber = i,
                        totalInstallments = totalInstallments,
                        seriesId = seriesId
                    )
                )

                cal.add(Calendar.MONTH, 1)  // avança 1 mês para a próxima parcela
            }

            repository.insertTransactions(entities)
            val msg = if (recurrenceType == "PARCELADA") {
                "$totalInstallments parcelas de ${
                    com.example.util.CurrencyUtils.format(perInstallmentAmount)
                } geradas com sucesso!"
            } else {
                "$totalInstallments meses recorrentes programados!"
            }
            showUserMessage(msg)
            closeAddEditSheet()
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
            showUserMessage("Transação excluída.")
        }
    }

    fun openEditBudgetDialog(category: Category) {
        _uiState.update {
            it.copy(
                isEditBudgetDialogOpen = true,
                editingBudgetCategory = category
            )
        }
    }

    fun closeEditBudgetDialog() {
        _uiState.update {
            it.copy(
                isEditBudgetDialogOpen = false,
                editingBudgetCategory = null
            )
        }
    }

    fun saveCategoryBudget(categoryId: String, limit: Double) {
        viewModelScope.launch {
            repository.setBudget(categoryId, limit)
            if (limit > 0) {
                showUserMessage("Meta de orçamento salva e sincronizada!")
            } else {
                showUserMessage("Meta de orçamento removida.")
            }
            closeEditBudgetDialog()
        }
    }

    fun deleteCategoryBudget(categoryId: String) {
        viewModelScope.launch {
            repository.deleteBudget(categoryId)
            showUserMessage("Meta de orçamento removida.")
            closeEditBudgetDialog()
        }
    }

    private fun showUserMessage(message: String) {
        _uiState.update { it.copy(userMessage = message) }
    }

    fun clearUserMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    companion object {
        fun provideFactory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val db = AppDatabase.getInstance(application)
                    val repository = FinanceRepository(db.transactionDao())
                    val authRepo = AuthRepository.getInstance(application)
                    return FinanceViewModel(application, repository, authRepo) as T
                }
            }
    }
}
