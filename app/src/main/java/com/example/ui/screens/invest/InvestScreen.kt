package com.example.ui.screens.invest

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.InvestmentHolding
import com.example.data.repository.ApexRepository
import com.example.ui.components.*
import com.example.ui.theme.*
import java.util.Locale

@Composable
fun InvestScreen(repository: ApexRepository) {
    val holdings by repository.holdings.collectAsState()
    val retirementPlanner by repository.retirementPlanner.collectAsState()

    var selectedTimeframe by remember { mutableStateOf("1M") }
    val timeframes = listOf("1D", "1W", "1M", "3M", "1Y", "5Y", "MAX")

    var selectedHoldingForTrade by remember { mutableStateOf<InvestmentHolding?>(null) }
    var tradeTypeIsBuy by remember { mutableStateOf(true) }
    var tradeSharesText by remember { mutableStateOf("1.0") }
    var tradeMessage by remember { mutableStateOf<String?>(null) }

    // Retirement Simulator state
    var currentAge by remember { mutableStateOf(retirementPlanner.currentAge.toFloat()) }
    var targetAge by remember { mutableStateOf(retirementPlanner.targetRetirementAge.toFloat()) }
    var monthlyContribution by remember { mutableStateOf(retirementPlanner.monthlyContribution.toFloat()) }
    var returnRate by remember { mutableStateOf(retirementPlanner.expectedReturnRatePct.toFloat()) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Portfolio Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "MERRILL® GUIDED INVESTING",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp, color = BofANavy),
                            )
                            Text(
                                text = "A Bank of America Company",
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = PreferredGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, PreferredGold.copy(alpha = 0.4f))
                        ) {
                            Text(
                                text = "PLATINUM HONORS",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BofANavy)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatCurrency(repository.totalInvestments),
                        style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.TrendingUp, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "+$1,420.50 (+1.12%) Today",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = PositiveGreen)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Total Return: +21.6%",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timeframe Chart Canvas
                    PortfolioPerformanceChart()

                    Spacer(modifier = Modifier.height(14.dp))

                    // Timeframe Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        timeframes.forEach { tf ->
                            val isSelected = selectedTimeframe == tf
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) BankBlue else Color.Transparent)
                                    .clickable { selectedTimeframe = tf }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = tf,
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 11.sp
                                    ),
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Holdings Section
        item {
            SectionHeader(
                title = "PORTFOLIO HOLDINGS",
                subtitle = "Stocks, ETFs, Bonds & Money Market"
            )
        }

        items(holdings) { holding ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedHoldingForTrade = holding },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = holding.symbol,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            CategoryBadge(category = holding.assetClass.name, color = LightBankBlue)
                        }
                        Text(
                            text = "${holding.name} • ${holding.shares} shares",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Formatters.formatCurrency(holding.totalValue),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = Formatters.formatPct(holding.todayChangePct),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (holding.todayChangePct >= 0) PositiveGreen else NegativeRed
                            )
                        )
                    }
                }
            }
        }

        // Retirement Planner & Simulator Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "RETIREMENT PLANNER SIMULATOR",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Projected Output Box
                    val yearsToRetire = (targetAge - currentAge).coerceAtLeast(1f)
                    val totalMonths = yearsToRetire * 12
                    val r = (returnRate / 100) / 12
                    val fvInvestments = retirementPlanner.currentSavings * Math.pow(1 + r.toDouble(), totalMonths.toDouble())
                    val fvContributions = monthlyContribution * ((Math.pow(1 + r.toDouble(), totalMonths.toDouble()) - 1) / r)
                    val totalProjected = fvInvestments + fvContributions

                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BankBlue.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("PROJECTED RETIREMENT VALUE AT AGE ${targetAge.toInt()}", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), color = BankBlue)
                            Text(
                                text = Formatters.formatCurrency(totalProjected),
                                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold, color = BankBlue)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Estimated Monthly Retirement Income: ${Formatters.formatCurrency(totalProjected * 0.04 / 12)} / month (4% Rule)",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }

                    // Sliders
                    Column {
                        Text("Current Age: ${currentAge.toInt()} yrs", style = MaterialTheme.typography.bodySmall)
                        Slider(
                            value = currentAge,
                            onValueChange = { currentAge = it },
                            valueRange = 20f..60f
                        )
                    }

                    Column {
                        Text("Target Retirement Age: ${targetAge.toInt()} yrs", style = MaterialTheme.typography.bodySmall)
                        Slider(
                            value = targetAge,
                            onValueChange = { targetAge = it },
                            valueRange = 50f..75f
                        )
                    }

                    Column {
                        Text("Monthly Contribution: ${Formatters.formatCurrency(monthlyContribution.toDouble())}", style = MaterialTheme.typography.bodySmall)
                        Slider(
                            value = monthlyContribution,
                            onValueChange = { monthlyContribution = it },
                            valueRange = 200f..5000f,
                            steps = 48
                        )
                    }

                    Column {
                        Text("Expected Annual Return Rate: ${String.format(Locale.US, "%.1f", returnRate)}%", style = MaterialTheme.typography.bodySmall)
                        Slider(
                            value = returnRate,
                            onValueChange = { returnRate = it },
                            valueRange = 3f..12f
                        )
                    }
                }
            }
        }
    }

    // Trade Modal
    if (selectedHoldingForTrade != null) {
        val holding = selectedHoldingForTrade!!
        AlertDialog(
            onDismissRequest = { selectedHoldingForTrade = null },
            title = { Text("Trade ${holding.symbol} (${holding.name})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Current Price: ${Formatters.formatCurrency(holding.currentPrice)}")
                    Text("You own: ${holding.shares} shares (${Formatters.formatCurrency(holding.totalValue)})")

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (tradeTypeIsBuy) PositiveGreen else Color.Transparent)
                                .clickable { tradeTypeIsBuy = true }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("BUY", fontWeight = FontWeight.Bold, color = if (tradeTypeIsBuy) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!tradeTypeIsBuy) NegativeRed else Color.Transparent)
                                .clickable { tradeTypeIsBuy = false }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("SELL", fontWeight = FontWeight.Bold, color = if (!tradeTypeIsBuy) Color.White else MaterialTheme.colorScheme.onSurface)
                        }
                    }

                    OutlinedTextField(
                        value = tradeSharesText,
                        onValueChange = { tradeSharesText = it },
                        label = { Text("Shares") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    val shares = tradeSharesText.toDoubleOrNull() ?: 0.0
                    Text(
                        text = "Estimated Order Total: ${Formatters.formatCurrency(shares * holding.currentPrice)}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    if (tradeMessage != null) {
                        Text(tradeMessage!!, color = BankBlue, fontWeight = FontWeight.Bold)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val shares = tradeSharesText.toDoubleOrNull() ?: 0.0
                        if (shares > 0) {
                            val res = repository.executeTrade(holding.symbol, shares, tradeTypeIsBuy)
                            tradeMessage = "Order Executed! Ref: $res"
                        }
                    }
                ) {
                    Text("Submit Demo Trade")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedHoldingForTrade = null; tradeMessage = null }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
fun PortfolioPerformanceChart() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val points = listOf(
                Offset(0f, height * 0.8f),
                Offset(width * 0.15f, height * 0.7f),
                Offset(width * 0.3f, height * 0.75f),
                Offset(width * 0.45f, height * 0.5f),
                Offset(width * 0.6f, height * 0.55f),
                Offset(width * 0.75f, height * 0.3f),
                Offset(width * 0.9f, height * 0.35f),
                Offset(width, height * 0.15f)
            )

            val strokePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 1 until points.size) {
                    lineTo(points[i].x, points[i].y)
                }
            }

            val fillPath = Path().apply {
                addPath(strokePath)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }

            // Fill Gradient
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        PositiveGreen.copy(alpha = 0.3f),
                        Color.Transparent
                    )
                )
            )

            // Line
            drawPath(
                path = strokePath,
                color = PositiveGreen,
                style = Stroke(width = 6f)
            )
        }
    }
}
