package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ExpenseRed
import com.example.ui.theme.IncomeGreen
import com.example.ui.viewmodel.MonthlyBarData
import kotlin.math.max

@Composable
fun MonthlyBarChart(
    history: List<MonthlyBarData>,
    modifier: Modifier = Modifier
) {
    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(history) {
        animProgress.snapTo(0f)
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("monthly_bar_chart_card"),
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
                    text = "Evolução Mensal (6 Meses)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )

                // Legends
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(IncomeGreen)
                        )
                        Text(
                            text = "Receitas",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ExpenseRed)
                        )
                        Text(
                            text = "Despesas",
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (history.isEmpty() || history.all { it.income == 0.0 && it.expense == 0.0 }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Dados insuficientes para histórico mensal",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val maxVal = history.maxOfOrNull { max(it.income, it.expense) } ?: 1.0
                val safeMax = if (maxVal > 0) maxVal * 1.15 else 1000.0

                // Canvas Chart
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("bar_chart_canvas")
                ) {
                    val availableWidth = size.width
                    val availableHeight = size.height
                    val groupCount = history.size
                    val groupWidth = availableWidth / groupCount
                    val barWidth = (groupWidth * 0.28f).coerceAtMost(16.dp.toPx())
                    val barSpacing = 4.dp.toPx()
                    val cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())

                    // Baseline
                    drawLine(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        start = Offset(0f, availableHeight),
                        end = Offset(availableWidth, availableHeight),
                        strokeWidth = 1.dp.toPx()
                    )

                    val progress = animProgress.value

                    history.forEachIndexed { index, data ->
                        val groupCenterX = index * groupWidth + (groupWidth / 2f)

                        // Income Bar (Left of group center)
                        val incomeHeight = ((data.income / safeMax) * availableHeight * progress).toFloat()
                            .coerceAtLeast(0f)
                        val incomeLeft = groupCenterX - barWidth - (barSpacing / 2f)
                        val incomeTop = availableHeight - incomeHeight

                        if (incomeHeight > 0) {
                            drawRoundRect(
                                color = IncomeGreen,
                                topLeft = Offset(incomeLeft, incomeTop),
                                size = Size(barWidth, incomeHeight),
                                cornerRadius = cornerRadius
                            )
                        }

                        // Expense Bar (Right of group center)
                        val expenseHeight = ((data.expense / safeMax) * availableHeight * progress).toFloat()
                            .coerceAtLeast(0f)
                        val expenseLeft = groupCenterX + (barSpacing / 2f)
                        val expenseTop = availableHeight - expenseHeight

                        if (expenseHeight > 0) {
                            drawRoundRect(
                                color = ExpenseRed,
                                topLeft = Offset(expenseLeft, expenseTop),
                                size = Size(barWidth, expenseHeight),
                                cornerRadius = cornerRadius
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // X-Axis Month labels
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    history.forEach { item ->
                        Text(
                            text = item.monthName,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
