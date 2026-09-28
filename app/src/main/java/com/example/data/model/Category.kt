package com.example.data.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.HomeWork
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: String,
    val name: String,
    val iconKey: String,
    val color: Color,
    val type: TransactionType? = null // null means available for both
) {
    val icon: ImageVector
        get() = when (iconKey) {
            "restaurant" -> Icons.Default.Restaurant
            "shopping" -> Icons.Default.ShoppingCart
            "home" -> Icons.Default.Home
            "car" -> Icons.Default.DirectionsCar
            "health" -> Icons.Default.LocalHospital
            "leisure" -> Icons.Default.SportsEsports
            "school" -> Icons.Default.School
            "money" -> Icons.Default.AttachMoney
            "work" -> Icons.Default.Work
            "invest" -> Icons.Default.TrendingUp
            "card" -> Icons.Default.CreditCard
            "carne" -> Icons.Default.Receipt
            "loan" -> Icons.Default.AccountBalance
            "pension" -> Icons.Default.FamilyRestroom
            "rent" -> Icons.Default.HomeWork
            else -> Icons.Default.Category
        }

    companion object {
        val ALL_CATEGORIES = listOf(
            // Despesas comuns
            Category("cat_alimentacao", "Alimentação", "restaurant", Color(0xFFF59E0B), TransactionType.DESPESA),
            Category("cat_mercado", "Supermercado", "shopping", Color(0xFF10B981), TransactionType.DESPESA),
            Category("cat_moradia", "Moradia", "home", Color(0xFF3B82F6), TransactionType.DESPESA),
            Category("cat_transporte", "Transporte", "car", Color(0xFF8B5CF6), TransactionType.DESPESA),
            Category("cat_saude", "Saúde", "health", Color(0xFFEF4444), TransactionType.DESPESA),
            Category("cat_lazer", "Lazer", "leisure", Color(0xFFEC4899), TransactionType.DESPESA),
            Category("cat_educacao", "Educação", "school", Color(0xFF06B6D4), TransactionType.DESPESA),
            // Despesas recorrentes & parceladas
            Category("cat_cartao", "Cartão de Crédito", "card", Color(0xFF6366F1), TransactionType.DESPESA),
            Category("cat_carne", "Carnê / Crediário", "carne", Color(0xFFD97706), TransactionType.DESPESA),
            Category("cat_emprestimo", "Empréstimos", "loan", Color(0xFFDC2626), TransactionType.DESPESA),

            // Receitas recorrentes & avulsas
            Category("cat_salario", "Salário Mensal", "money", Color(0xFF10B981), TransactionType.RECEITA),
            Category("cat_pensao", "Pensão / Benefício", "pension", Color(0xFF059669), TransactionType.RECEITA),
            Category("cat_aluguel_rec", "Aluguel Recebido", "rent", Color(0xFF0D9488), TransactionType.RECEITA),
            Category("cat_investimentos", "Rendimentos", "invest", Color(0xFF0284C7), TransactionType.RECEITA),
            Category("cat_freelance", "Freelance / Extra", "work", Color(0xFF14B8A6), TransactionType.RECEITA),

            // Outros
            Category("cat_outros", "Outros", "category", Color(0xFF64748B), null)
        )

        fun findById(id: String): Category {
            return ALL_CATEGORIES.find { it.id == id } ?: Category(
                id = id,
                name = "Outros",
                iconKey = "category",
                color = Color(0xFF64748B),
                type = null
            )
        }
    }
}
