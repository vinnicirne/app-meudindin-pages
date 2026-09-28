package com.example.util

import java.text.NumberFormat
import java.util.Locale

object CurrencyUtils {
    private val ptBrLocale = Locale("pt", "BR")
    private val currencyFormat = NumberFormat.getCurrencyInstance(ptBrLocale)

    fun format(amount: Double): String {
        return currencyFormat.format(amount)
    }

    fun formatWithSign(amount: Double, isIncome: Boolean): String {
        val formatted = currencyFormat.format(amount)
        return if (isIncome) "+ $formatted" else "- $formatted"
    }

    fun parseAmount(text: String): Double? {
        val clean = text.replace("[^\\d,.]".toRegex(), "")
            .replace(".", "")
            .replace(",", ".")
        return clean.toDoubleOrNull()
    }
}
