package com.example.ui.screens.plan

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.ApexRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun PlanScreen(repository: ApexRepository) {
    val mortgage = repository.mortgage.collectAsState().value
    val monthlySpending = repository.monthlySpending
    val totalAssets = repository.totalAssets
    val totalLiabilities = repository.totalLiabilities
    val netWorth = repository.netWorth

    var selectedPlanTab by remember { mutableStateOf(0) } // 0: Budget & Subscriptions, 1: Life Goals, 2: Net Worth

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        TabRow(
            selectedTabIndex = selectedPlanTab,
            containerColor = Color.Transparent,
            contentColor = BankBlue
        ) {
            Tab(
                selected = selectedPlanTab == 0,
                onClick = { selectedPlanTab = 0 },
                text = { Text("Budget", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedPlanTab == 1,
                onClick = { selectedPlanTab = 1 },
                text = { Text("Life Plan®", fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedPlanTab == 2,
                onClick = { selectedPlanTab = 2 },
                text = { Text("Net Worth", fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedPlanTab) {
            0 -> BudgetAndSubscriptionsView(monthlySpending)
            1 -> LifeGoalsView(repository)
            2 -> NetWorthView(totalAssets, totalLiabilities, netWorth, mortgage)
        }
    }
}

@Composable
private fun BudgetAndSubscriptionsView(monthlySpending: Double) {
    val monthlyIncome = 8500.00
    val remaining = monthlyIncome - monthlySpending

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Income vs Expense Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("MONTHLY CASH FLOW & BUDGET", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("INCOME", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatCurrency(monthlyIncome), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PositiveGreen))
                        }
                        Column {
                            Text("EXPENSES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatCurrency(monthlySpending), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NegativeRed))
                        }
                        Column {
                            Text("REMAINING", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatCurrency(remaining), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BankBlue))
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    ClipProgressMeter(progress = (monthlySpending / monthlyIncome).toFloat(), color = BankBlue)
                }
            }
        }

        // Subscriptions Section
        item {
            SectionHeader(
                title = "DETECTED SUBSCRIPTIONS",
                subtitle = "Recurring Monthly Services ($314.97 / month)"
            )
        }

        val subscriptions = listOf(
            Triple("Equinox Fitness", "$260.00 / mo", "Renews Sep 01"),
            Triple("Netflix Premium", "$22.99 / mo", "Renews Aug 28"),
            Triple("Spotify Family", "$16.99 / mo", "Renews Aug 25"),
            Triple("Amazon Prime", "$14.99 / mo", "Renews Sep 05")
        )

        items(subscriptions) { sub ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Text(sub.first, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(sub.third, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Text(sub.second, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = BankBlue))
                }
            }
        }
    }
}

@Composable
private fun LifeGoalsView(repository: ApexRepository) {
    val goals by repository.savingsGoals.collectAsState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(goals) { goal ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(goal.title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        CategoryBadge(category = "ON TRACK", color = PositiveGreen)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Target Date: ${goal.targetDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)

                    Spacer(modifier = Modifier.height(10.dp))
                    ClipProgressMeter(progress = (goal.currentAmount / goal.targetAmount).toFloat(), color = BankBlue)
                }
            }
        }
    }
}

@Composable
private fun NetWorthView(
    totalAssets: Double,
    totalLiabilities: Double,
    netWorth: Double,
    mortgage: com.example.data.models.MortgageData
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("TOTAL NET WORTH", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp))
                    Text(Formatters.formatCurrency(netWorth), style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.ExtraBold, color = BankBlue))

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("TOTAL ASSETS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatCurrency(totalAssets), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PositiveGreen))
                        }
                        Column {
                            Text("TOTAL LIABILITIES", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(Formatters.formatCurrency(totalLiabilities), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = NegativeRed))
                        }
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("HOME EQUITY & MORTGAGE", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estimated Home Market Value: ${Formatters.formatCurrency(mortgage.estimatedHomeValue)}")
                    Text("Current Mortgage Balance: ${Formatters.formatCurrency(mortgage.currentBalance)}")
                    Text("Net Home Equity: ${Formatters.formatCurrency(mortgage.estimatedEquity)}", fontWeight = FontWeight.Bold, color = PositiveGreen)
                }
            }
        }
    }
}
