package com.diegowmenezes.pagoapp.ui.reports

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.diegowmenezes.pagoapp.domain.model.CategorySummary
import com.diegowmenezes.pagoapp.domain.model.MonthlySummary
import com.diegowmenezes.pagoapp.domain.model.RecipientSummary
import com.diegowmenezes.pagoapp.ui.components.EmptyStateView
import com.diegowmenezes.pagoapp.ui.components.formatCents
import com.diegowmenezes.pagoapp.ui.components.toCategoryLabel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Relatorios") }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            is ReportsUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is ReportsUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (uiState as ReportsUiState.Error).message,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.loadReports() }) {
                            Text("Tentar novamente")
                        }
                    }
                }
            }
            is ReportsUiState.Success -> {
                val data = (uiState as ReportsUiState.Success).data
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
                ) {
                    item {
                        MonthlyTrendSection(
                            monthlyTrend = data.monthlyTrend
                        )
                    }
                    item {
                        TopRecipientsSection(
                            topRecipients = data.topRecipients
                        )
                    }
                    item {
                        SpendingByCategorySection(
                            spendingByCategory = data.spendingByCategory
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthlyTrendSection(monthlyTrend: List<MonthlySummary>) {
    if (monthlyTrend.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.BarChart,
            message = "Sem dados mensais disponiveis",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        return
    }

    val maxAmount = monthlyTrend.flatMap { listOf(it.incomeCents, it.expenseCents) }
        .maxOfOrNull { it } ?: 0L

    Column {
        Text(
            text = "Tendencia Mensal",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Receitas", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelSmall)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Despesas", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        monthlyTrend.forEach { month ->
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = month.month,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = month.incomeCents.formatCents(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            if (maxAmount > 0) (month.incomeCents.toFloat() / maxAmount.toFloat())
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = month.expenseCents.formatCents(),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    LinearProgressIndicator(
                        progress = {
                            if (maxAmount > 0) (month.expenseCents.toFloat() / maxAmount.toFloat())
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = MaterialTheme.colorScheme.error,
                        trackColor = MaterialTheme.colorScheme.errorContainer,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun TopRecipientsSection(topRecipients: List<RecipientSummary>) {
    if (topRecipients.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.Person,
            message = "Sem destinatarios frequentes",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        return
    }

    val maxAmount = topRecipients.maxOfOrNull { it.totalAmountCents } ?: 0L

    Column {
        Text(
            text = "Destinatarios Frequentes",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        topRecipients.forEachIndexed { index, recipient ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.width(32.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = recipient.recipientName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = {
                                if (maxAmount > 0) (recipient.totalAmountCents.toFloat() / maxAmount.toFloat())
                                else 0f
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp),
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${recipient.transactionCount} transacoes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = recipient.totalAmountCents.formatCents(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun SpendingByCategorySection(spendingByCategory: List<CategorySummary>) {
    if (spendingByCategory.isEmpty()) {
        EmptyStateView(
            icon = Icons.Filled.BarChart,
            message = "Sem dados por categoria",
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        return
    }

    val totalCents = spendingByCategory.sumOf { it.totalAmountCents }
    val maxCents = spendingByCategory.maxOfOrNull { it.totalAmountCents } ?: 0L

    val categoryColors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFF44336),
        Color(0xFFFF9800),
        Color(0xFF9C27B0),
        Color(0xFF607D8B),
        Color(0xFF795548)
    )

    Column {
        Text(
            text = "Gastos por Categoria",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (totalCents > 0) {
            DonutChart(
                segments = spendingByCategory.mapIndexed { index, summary ->
                    DonutSegment(
                        fraction = summary.totalAmountCents.toFloat() / totalCents.toFloat(),
                        color = categoryColors[index % categoryColors.size]
                    )
                },
                centerText = totalCents.formatCents(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        spendingByCategory.forEachIndexed { index, summary ->
            val color = categoryColors[index % categoryColors.size]
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Canvas(modifier = Modifier.size(12.dp)) {
                    drawCircle(color = color)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = summary.category.toCategoryLabel(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = summary.totalAmountCents.formatCents(),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (maxCents > 0) (summary.totalAmountCents.toFloat() / maxCents.toFloat())
                            else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp),
                        color = color,
                        trackColor = color.copy(alpha = 0.2f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

private data class DonutSegment(
    val fraction: Float,
    val color: Color
)

@Composable
private fun DonutChart(
    segments: List<DonutSegment>,
    centerText: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(160.dp)) {
            val strokeWidth = 28.dp.toPx()
            val radius = (size.minDimension - strokeWidth) / 2f
            val topLeft = Offset(
                x = (size.width - radius * 2) / 2f,
                y = (size.height - radius * 2) / 2f
            )
            val arcSize = Size(radius * 2, radius * 2)

            var startAngle = -90f
            segments.forEach { segment ->
                val sweepAngle = segment.fraction * 360f
                drawArc(
                    color = segment.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth)
                )
                startAngle += sweepAngle
            }
        }
        Text(
            text = centerText,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}