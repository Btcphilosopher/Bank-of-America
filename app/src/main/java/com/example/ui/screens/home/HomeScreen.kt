package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Account
import com.example.data.models.AccountCategory
import com.example.data.repository.ApexRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: ApexRepository,
    onNavigateToTab: (NavTab) -> Unit,
    onAccountClick: (Account) -> Unit,
    onTransferClick: () -> Unit,
    onPayBillClick: () -> Unit,
    onDepositClick: () -> Unit,
    onSendMoneyClick: () -> Unit
) {
    val accounts by repository.accounts.collectAsState()
    val totalCash = repository.totalAvailableCash
    val netWorth = repository.netWorth
    val monthlySpending = repository.monthlySpending

    var selectedAccountCategoryFilter by remember { mutableStateOf<AccountCategory?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Bank of America Preferred Rewards Insight Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = BofANavy)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(PreferredGold.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Preferred Rewards",
                            tint = PreferredGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PREFERRED REWARDS • PLATINUM HONORS",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = PreferredGold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "You're earning a 75% credit card rewards bonus + 0.20% relationship rate boost on Advantage Savings.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Financial Hero Overview Header
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
                    Text(
                        text = "TOTAL AVAILABLE CASH",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatCurrency(totalCash),
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "NET WORTH",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Formatters.formatCurrency(netWorth),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            )
                        }

                        Divider(
                            modifier = Modifier
                                .height(36.dp)
                                .width(1.dp),
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        )

                        Column {
                            Text(
                                text = "MONTHLY SPENDING",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = Formatters.formatCurrency(monthlySpending),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }

        // Quick Actions Grid
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    QuickActionButton(
                        icon = Icons.Outlined.SwapHoriz,
                        label = "Transfer",
                        onClick = onTransferClick
                    )
                    QuickActionButton(
                        icon = Icons.Outlined.ReceiptLong,
                        label = "Pay Bills",
                        onClick = onPayBillClick
                    )
                    QuickActionButton(
                        icon = Icons.Outlined.CameraAlt,
                        label = "Deposit",
                        onClick = onDepositClick
                    )
                    QuickActionButton(
                        icon = Icons.Outlined.FlashOn,
                        label = "Zelle®",
                        onClick = onSendMoneyClick
                    )
                }
            }
        }

        // Financial Health Summary Panel
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    HealthScoreMeter(scorePct = 0.84f, ratingText = "EXCELLENT (84/100)")
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(PositiveGreen.copy(alpha = 0.08f))
                            .padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.TrendingUp,
                            contentDescription = null,
                            tint = PositiveGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "TOP OPPORTUNITY",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = PositiveGreen,
                                    fontSize = 10.sp
                                )
                            )
                            Text(
                                text = "Your savings balance is growing faster than your monthly spending (+14.2% YoY).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Accounts Category Header & Filters
        item {
            Column {
                SectionHeader(
                    title = "ACCOUNTS & BALANCES",
                    subtitle = "Checking, Savings, Investments, Cards & Loans",
                    actionText = "All Accounts",
                    onActionClick = { onNavigateToTab(NavTab.MONEY) }
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(vertical = 4.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedAccountCategoryFilter == null,
                            onClick = { selectedAccountCategoryFilter = null },
                            label = { Text("All Accounts") }
                        )
                    }
                    items(AccountCategory.values()) { cat ->
                        FilterChip(
                            selected = selectedAccountCategoryFilter == cat,
                            onClick = { selectedAccountCategoryFilter = cat },
                            label = { Text(cat.name.replace("_", " ")) }
                        )
                    }
                }
            }
        }

        // Account Cards List
        val filteredAccounts = if (selectedAccountCategoryFilter != null) {
            accounts.filter { it.category == selectedAccountCategoryFilter }
        } else {
            accounts
        }

        items(filteredAccounts) { acc ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAccountClick(acc) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    when (acc.category) {
                                        AccountCategory.BANKING -> BankBlue.copy(alpha = 0.15f)
                                        AccountCategory.CREDIT -> NegativeRed.copy(alpha = 0.15f)
                                        AccountCategory.INVESTING -> AccentGold.copy(alpha = 0.15f)
                                        AccountCategory.RETIREMENT -> LightBankBlue.copy(alpha = 0.15f)
                                        AccountCategory.LOANS -> Color.Gray.copy(alpha = 0.15f)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (acc.category) {
                                    AccountCategory.BANKING -> Icons.Default.AccountBalance
                                    AccountCategory.CREDIT -> Icons.Default.CreditCard
                                    AccountCategory.INVESTING -> Icons.Default.ShowChart
                                    AccountCategory.RETIREMENT -> Icons.Default.Savings
                                    AccountCategory.LOANS -> Icons.Default.Home
                                },
                                contentDescription = null,
                                tint = when (acc.category) {
                                    AccountCategory.BANKING -> BankBlue
                                    AccountCategory.CREDIT -> NegativeRed
                                    AccountCategory.INVESTING -> AccentGold
                                    AccountCategory.RETIREMENT -> LightBankBlue
                                    AccountCategory.LOANS -> MaterialTheme.colorScheme.onSurface
                                },
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Text(
                                text = acc.name,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${acc.accountNumberMasked} • ${acc.type.name.replace("_", " ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Formatters.formatCurrency(acc.balance),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = if (acc.category == AccountCategory.CREDIT || acc.category == AccountCategory.LOANS) NegativeRed else MaterialTheme.colorScheme.onSurface
                        )
                        if (acc.interestRate > 0) {
                            Text(
                                text = "${acc.interestRate}% APY",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                color = PositiveGreen
                            )
                        }
                    }
                }
            }
        }
    }
}
