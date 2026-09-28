package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.CloudSyncStatus
import com.example.ui.components.AddTransactionBottomSheet
import com.example.ui.components.EditBudgetDialog
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BudgetsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TransactionsScreen
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.WarningAmber
import com.example.ui.viewmodel.FinanceViewModel
import com.example.ui.viewmodel.ScreenTab
import com.example.ui.viewmodel.ThemeMode
import com.example.ui.viewmodel.FinanceUiState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = LocalContext.current.applicationContext as android.app.Application
            val viewModel: FinanceViewModel = viewModel(
                factory = FinanceViewModel.provideFactory(app)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            val isDarkTheme = when (uiState.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDarkTheme) {
                FinanztApp(viewModel, uiState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FinanztApp(viewModel: FinanceViewModel, uiState: FinanceUiState) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showSqlHelpDialog by remember { mutableStateOf(false) }
    var showUserMenu by remember { mutableStateOf(false) }

    // Show feedback messages
    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // If user is not logged in and hasn't chosen guest mode, present AuthScreen
    if (uiState.currentUser == null && !uiState.isGuestMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            AuthScreen(
                isLoading = uiState.isAuthLoading,
                errorMessage = uiState.authErrorMessage,
                onLogin = { email, pass -> viewModel.login(email, pass) },
                onSignup = { email, pass -> viewModel.signup(email, pass) },
                onContinueAsGuest = { viewModel.continueAsGuest() },
                modifier = Modifier.padding(innerPadding)
            )
        }
        return
    }

    // Handle back press to return to Dashboard
    BackHandler(enabled = uiState.currentTab != ScreenTab.DASHBOARD) {
        viewModel.setScreenTab(ScreenTab.DASHBOARD)
    }

    // Rotation animation for sync icon
    val infiniteTransition = rememberInfiniteTransition(label = "syncRotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Savings,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Meu DinDin",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = (-0.5).sp
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.syncWithSupabase() },
                        modifier = Modifier.testTag("topbar_sync_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sincronizar com Supabase",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = if (uiState.cloudSyncStatus == CloudSyncStatus.SYNCING)
                                Modifier.rotate(rotation) else Modifier
                        )
                    }

                    // Theme toggle
                    IconButton(
                        onClick = {
                            val nextMode = when (uiState.themeMode) {
                                ThemeMode.SYSTEM -> ThemeMode.DARK
                                ThemeMode.DARK -> ThemeMode.LIGHT
                                ThemeMode.LIGHT -> ThemeMode.SYSTEM
                            }
                            viewModel.setThemeMode(nextMode)
                        }
                    ) {
                        Icon(
                            imageVector = when (uiState.themeMode) {
                                ThemeMode.DARK -> Icons.Default.DarkMode
                                ThemeMode.LIGHT -> Icons.Default.LightMode
                                ThemeMode.SYSTEM -> Icons.Default.DarkMode // or some auto icon
                            },
                            contentDescription = "Alternar Tema",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    // User Profile menu
                    Box {
                        IconButton(
                            onClick = { showUserMenu = true },
                            modifier = Modifier.testTag("topbar_user_btn")
                        ) {
                            Icon(
                                imageVector = if (uiState.currentUser != null) Icons.Default.AccountCircle else Icons.Default.Person,
                                contentDescription = "Perfil do Usuário",
                                tint = if (uiState.currentUser != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        DropdownMenu(
                            expanded = showUserMenu,
                            onDismissRequest = { showUserMenu = false }
                        ) {
                            if (uiState.currentUser != null) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                                            ) {
                                                Text(
                                                    text = "Conectado como:",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                                if (uiState.currentUser!!.isAdmin) {
                                                    Surface(
                                                        shape = RoundedCornerShape(6.dp),
                                                        color = MaterialTheme.colorScheme.primary
                                                    ) {
                                                        Text(
                                                            text = "ADM",
                                                            style = MaterialTheme.typography.labelSmall.copy(
                                                                color = Color.White,
                                                                fontWeight = FontWeight.ExtraBold,
                                                                fontSize = 9.sp
                                                            ),
                                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = uiState.currentUser!!.email,
                                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                        }
                                    },
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = { Text("Sair da Conta", color = ExpenseRed) },
                                    leadingIcon = {
                                        Icon(Icons.Default.ExitToApp, contentDescription = null, tint = ExpenseRed)
                                    },
                                    onClick = {
                                        showUserMenu = false
                                        viewModel.logout()
                                    }
                                )
                            } else {
                                DropdownMenuItem(
                                    text = { Text("Fazer Login / Cadastro") },
                                    leadingIcon = {
                                        Icon(Icons.Default.Person, contentDescription = null)
                                    },
                                    onClick = {
                                        showUserMenu = false
                                        viewModel.logout() // resets guest mode to show AuthScreen
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },

        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_bottom_nav_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.DASHBOARD,
                    onClick = { viewModel.setScreenTab(ScreenTab.DASHBOARD) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == ScreenTab.DASHBOARD) Icons.Default.Home else Icons.Outlined.Home,
                            contentDescription = "Início"
                        )
                    },
                    label = { Text("Início") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_dashboard")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.STATS,
                    onClick = { viewModel.setScreenTab(ScreenTab.STATS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == ScreenTab.STATS) Icons.Default.PieChart else Icons.Outlined.PieChart,
                            contentDescription = "Gráficos"
                        )
                    },
                    label = { Text("Gráficos") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_stats")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.TRANSACTIONS,
                    onClick = { viewModel.setScreenTab(ScreenTab.TRANSACTIONS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == ScreenTab.TRANSACTIONS) Icons.Default.ReceiptLong else Icons.Outlined.ReceiptLong,
                            contentDescription = "Extrato"
                        )
                    },
                    label = { Text("Extrato") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_transactions")
                )

                NavigationBarItem(
                    selected = uiState.currentTab == ScreenTab.BUDGETS,
                    onClick = { viewModel.setScreenTab(ScreenTab.BUDGETS) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == ScreenTab.BUDGETS) Icons.Default.AccountBalanceWallet else Icons.Outlined.AccountBalanceWallet,
                            contentDescription = "Metas"
                        )
                    },
                    label = { Text("Metas") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.testTag("nav_tab_budgets")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = uiState.currentTab,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "ScreenTransition"
            ) { tab ->
                when (tab) {
                    ScreenTab.DASHBOARD -> DashboardScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onResetToCurrentMonth = { viewModel.resetToCurrentMonth() },
                        onAddIncome = { viewModel.openAddTransactionSheet(com.example.data.model.TransactionType.RECEITA) },
                        onAddExpense = { viewModel.openAddTransactionSheet(com.example.data.model.TransactionType.DESPESA) },
                        onEditTransaction = { viewModel.openEditTransactionSheet(it) },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) },
                        onNavigateToTab = { viewModel.setScreenTab(it) }
                    )

                    ScreenTab.STATS -> StatsScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onResetToCurrentMonth = { viewModel.resetToCurrentMonth() }
                    )

                    ScreenTab.TRANSACTIONS -> TransactionsScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onResetToCurrentMonth = { viewModel.resetToCurrentMonth() },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onFilterTypeChange = { viewModel.setFilterType(it) },
                        onFilterCategoryChange = { viewModel.setFilterCategoryId(it) },
                        onEditTransaction = { viewModel.openEditTransactionSheet(it) },
                        onDeleteTransaction = { viewModel.deleteTransaction(it) }
                    )

                    ScreenTab.BUDGETS -> BudgetsScreen(
                        uiState = uiState,
                        onPreviousMonth = { viewModel.previousMonth() },
                        onNextMonth = { viewModel.nextMonth() },
                        onResetToCurrentMonth = { viewModel.resetToCurrentMonth() },
                        onEditBudget = { viewModel.openEditBudgetDialog(it) }
                    )
                }
            }
        }
    }

    // Modal Bottom Sheet for Add/Edit Transaction
    if (uiState.isAddEditSheetOpen) {
        AddTransactionBottomSheet(
            editingTransaction = uiState.editingTransaction,
            defaultTransactionType = uiState.defaultTransactionType,
            onDismiss = { viewModel.closeAddEditSheet() },
            onSave = { id, title, amount, type, categoryId, timestamp, notes, recurrenceType, totalInstallments, isAmountTotal ->
                viewModel.saveTransaction(
                    id = id,
                    title = title,
                    amount = amount,
                    type = type,
                    categoryId = categoryId,
                    timestamp = timestamp,
                    notes = notes,
                    recurrenceType = recurrenceType,
                    totalInstallments = totalInstallments,
                    isAmountTotal = isAmountTotal
                )
            }
        )
    }

    // Dialog for Editing Category Budget Limit
    if (uiState.isEditBudgetDialogOpen && uiState.editingBudgetCategory != null) {
        val cat = uiState.editingBudgetCategory!!
        val currentLimit = uiState.budgetProgressList.find { it.category.id == cat.id }?.budgetLimit ?: 0.0
        EditBudgetDialog(
            category = cat,
            currentLimit = currentLimit,
            onDismiss = { viewModel.closeEditBudgetDialog() },
            onSave = { limit ->
                viewModel.saveCategoryBudget(cat.id, limit)
            },
            onDelete = {
                viewModel.deleteCategoryBudget(cat.id)
            }
        )
    }

    // Dialog with Supabase SQL setup script
    if (showSqlHelpDialog) {
        val sqlScript = """
CREATE TABLE IF NOT EXISTS public.transactions (
    id BIGSERIAL PRIMARY KEY,
    title TEXT NOT NULL,
    amount DOUBLE PRECISION NOT NULL,
    type TEXT NOT NULL,
    category_id TEXT NOT NULL,
    timestamp BIGINT NOT NULL,
    notes TEXT DEFAULT ''
);

CREATE TABLE IF NOT EXISTS public.budgets (
    category_id TEXT PRIMARY KEY,
    monthly_limit DOUBLE PRECISION NOT NULL
);

ALTER TABLE public.transactions ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Acesso publico transactions" ON public.transactions FOR ALL USING (true) WITH CHECK (true);

ALTER TABLE public.budgets ENABLE ROW LEVEL SECURITY;
CREATE POLICY "Acesso publico budgets" ON public.budgets FOR ALL USING (true) WITH CHECK (true);
        """.trimIndent()

        AlertDialog(
            onDismissRequest = { showSqlHelpDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Tabelas no Supabase", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = "O aplicativo está conectado ao seu Supabase! Para sincronizar as transações na nuvem, execute o seguinte comando no SQL Editor do seu projeto Supabase:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = sqlScript,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp
                            ),
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Supabase SQL", sqlScript)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "SQL copiado para a área de transferência!", Toast.LENGTH_SHORT).show()
                        showSqlHelpDialog = false
                    }
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar SQL")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSqlHelpDialog = false }) {
                    Text("Fechar")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
