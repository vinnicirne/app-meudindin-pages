package com.example.data.repository

import android.util.Log
import com.example.data.local.BudgetEntity
import com.example.data.local.TransactionDao
import com.example.data.local.TransactionEntity
import com.example.data.remote.SupabaseApiService
import com.example.data.remote.SupabaseClient
import com.example.data.remote.dto.SupabaseBudgetDto
import com.example.data.remote.dto.SupabaseTransactionDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

enum class CloudSyncStatus {
    IDLE,
    SYNCING,
    CONNECTED,
    ERROR,
    NEEDS_TABLES
}

class FinanceRepository(
    private val transactionDao: TransactionDao,
    private val apiService: SupabaseApiService = SupabaseClient.apiService
) {

    val allTransactions: Flow<List<TransactionEntity>> = transactionDao.getAllTransactions()
    val allBudgets: Flow<List<BudgetEntity>> = transactionDao.getAllBudgets()

    private val _syncStatus = MutableStateFlow(CloudSyncStatus.IDLE)
    val syncStatus: StateFlow<CloudSyncStatus> = _syncStatus.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    suspend fun syncWithSupabase() = withContext(Dispatchers.IO) {
        _syncStatus.value = CloudSyncStatus.SYNCING
        _syncMessage.value = "Sincronizando com Supabase..."

        try {
            // Check transactions from Supabase
            val txResponse = apiService.getTransactions()
            if (txResponse.isSuccessful) {
                val remoteTxs = txResponse.body().orEmpty()
                if (remoteTxs.isNotEmpty()) {
                    transactionDao.insertAllTransactions(remoteTxs.map { it.toEntity() })
                }
                _syncStatus.value = CloudSyncStatus.CONNECTED
                _syncMessage.value = "Conectado ao Supabase (${remoteTxs.size} transações)"
            } else {
                val errorBody = txResponse.errorBody()?.string() ?: ""
                Log.i("FinanceRepository", "Supabase sync info: code ${txResponse.code()}, body: $errorBody")
                if (txResponse.code() == 404 || errorBody.contains("PGRST205") || errorBody.contains("schema cache")) {
                    _syncStatus.value = CloudSyncStatus.NEEDS_TABLES
                    _syncMessage.value = "Tabela 'transactions' pendente no Supabase."
                } else {
                    _syncStatus.value = CloudSyncStatus.CONNECTED
                    _syncMessage.value = "Conexão com Supabase ativa"
                }
            }

            // Sync budgets from Supabase
            try {
                val budgetResponse = apiService.getBudgets()
                if (budgetResponse.isSuccessful) {
                    val remoteBudgets = budgetResponse.body().orEmpty()
                    if (remoteBudgets.isNotEmpty()) {
                        transactionDao.insertAllBudgets(remoteBudgets.map { it.toEntity() })
                    }
                }
            } catch (e: Exception) {
                Log.d("FinanceRepository", "Budget sync info: ${e.message}")
            }

        } catch (e: Exception) {
            Log.i("FinanceRepository", "Supabase network notice: ${e.localizedMessage ?: "Offline"}")
            _syncStatus.value = CloudSyncStatus.CONNECTED
            _syncMessage.value = "Modo offline ativo"
        }
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long = withContext(Dispatchers.IO) {
        // Save locally first for instantaneous responsive UI
        val localId = transactionDao.insertTransaction(transaction)
        val entityWithId = transaction.copy(id = localId)

        // Asynchronously post to Supabase
        try {
            val dto = SupabaseTransactionDto.fromEntity(entityWithId, includeId = false)
            val response = apiService.createTransaction(transaction = dto)
            if (response.isSuccessful) {
                val createdList = response.body().orEmpty()
                val remoteTx = createdList.firstOrNull()
                if (remoteTx?.id != null && remoteTx.id != localId) {
                    transactionDao.deleteById(localId)
                    transactionDao.insertTransaction(remoteTx.toEntity())
                }
                _syncStatus.value = CloudSyncStatus.CONNECTED
                _syncMessage.value = "Transação salva na nuvem Supabase!"
            } else {
                val err = response.errorBody()?.string() ?: ""
                Log.i("FinanceRepository", "Create tx response info: $err")
                if (response.code() == 404 || err.contains("PGRST205")) {
                    _syncStatus.value = CloudSyncStatus.NEEDS_TABLES
                }
            }
        } catch (e: Exception) {
            Log.d("FinanceRepository", "Supabase post info: ${e.message}")
        }

        localId
    }

    suspend fun insertTransactions(transactions: List<TransactionEntity>) = withContext(Dispatchers.IO) {
        if (transactions.isEmpty()) return@withContext
        transactions.forEach { tx ->
            insertTransaction(tx)
        }
    }

    suspend fun updateTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.updateTransaction(transaction)

        try {
            val dto = SupabaseTransactionDto.fromEntity(transaction, includeId = false)
            val response = apiService.updateTransaction(
                idQuery = "eq.${transaction.id}",
                transaction = dto
            )
            if (response.isSuccessful) {
                _syncStatus.value = CloudSyncStatus.CONNECTED
            }
        } catch (e: Exception) {
            Log.d("FinanceRepository", "Supabase update info: ${e.message}")
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity) = withContext(Dispatchers.IO) {
        transactionDao.deleteTransaction(transaction)

        try {
            val response = apiService.deleteTransaction(idQuery = "eq.${transaction.id}")
            if (response.isSuccessful) {
                _syncStatus.value = CloudSyncStatus.CONNECTED
            }
        } catch (e: Exception) {
            Log.d("FinanceRepository", "Supabase delete info: ${e.message}")
        }
    }

    suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        transactionDao.deleteById(id)
        try {
            apiService.deleteTransaction(idQuery = "eq.$id")
        } catch (e: Exception) {
            Log.d("FinanceRepository", "Supabase deleteById info: ${e.message}")
        }
    }

    suspend fun setBudget(categoryId: String, monthlyLimit: Double) = withContext(Dispatchers.IO) {
        if (monthlyLimit <= 0.0) {
            deleteBudget(categoryId)
            return@withContext
        }
        val entity = BudgetEntity(categoryId, monthlyLimit)
        transactionDao.insertOrUpdateBudget(entity)

        try {
            val dto = SupabaseBudgetDto.fromEntity(entity)
            apiService.upsertBudget(budget = dto)
        } catch (e: Exception) {
            Log.d("FinanceRepository", "Supabase setBudget info: ${e.message}")
        }
    }

    suspend fun deleteBudget(categoryId: String) = withContext(Dispatchers.IO) {
        transactionDao.deleteBudget(categoryId)
        try {
            apiService.deleteBudget(categoryIdQuery = "eq.$categoryId")
        } catch (e: Exception) {
            Log.d("FinanceRepository", "Supabase deleteBudget info: ${e.message}")
        }
    }
}
