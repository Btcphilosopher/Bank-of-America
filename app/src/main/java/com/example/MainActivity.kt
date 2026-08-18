package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.models.Account
import com.example.data.repository.ApexRepository
import com.example.ui.components.*
import com.example.ui.screens.assistant.AssistantDialog
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.invest.InvestScreen
import com.example.ui.screens.money.MoneyHubScreen
import com.example.ui.screens.plan.PlanScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.GlobalSearchDialog
import com.example.ui.theme.ApexFinancialTheme

class MainActivity : ComponentActivity() {

    private val repository = ApexRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ApexFinancialTheme {
                var currentTab by remember { mutableStateOf(NavTab.HOME) }
                var moneyHubInitialSubTab by remember { mutableStateOf(0) }

                var isSearchOpen by remember { mutableStateOf(false) }
                var isAssistantOpen by remember { mutableStateOf(false) }
                var selectedAccountDetail by remember { mutableStateOf<Account?>(null) }

                val activeWorkspace by repository.activeWorkspace.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopHeader(
                            userName = "Tom",
                            workspace = activeWorkspace,
                            onWorkspaceChange = { repository.setWorkspace(it) },
                            onSearchClick = { isSearchOpen = true },
                            onAssistantClick = { isAssistantOpen = true },
                            onNotificationClick = { currentTab = NavTab.PROFILE },
                            onSecurityClick = { currentTab = NavTab.PROFILE },
                            unreadNotificationCount = 2
                        )
                    },
                    bottomBar = {
                        BottomNavBar(
                            currentTab = currentTab,
                            onTabSelected = { currentTab = it }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentTab) {
                            NavTab.HOME -> HomeScreen(
                                repository = repository,
                                onNavigateToTab = { currentTab = it },
                                onAccountClick = { selectedAccountDetail = it },
                                onTransferClick = {
                                    moneyHubInitialSubTab = 2
                                    currentTab = NavTab.MONEY
                                },
                                onPayBillClick = {
                                    moneyHubInitialSubTab = 3
                                    currentTab = NavTab.MONEY
                                },
                                onDepositClick = {
                                    moneyHubInitialSubTab = 4
                                    currentTab = NavTab.MONEY
                                },
                                onSendMoneyClick = {
                                    moneyHubInitialSubTab = 2
                                    currentTab = NavTab.MONEY
                                }
                            )

                            NavTab.MONEY -> MoneyHubScreen(
                                repository = repository,
                                initialSubTab = moneyHubInitialSubTab
                            )

                            NavTab.INVEST -> InvestScreen(repository = repository)

                            NavTab.PLAN -> PlanScreen(repository = repository)

                            NavTab.PROFILE -> ProfileScreen(repository = repository)
                        }

                        // Global Search Dialog
                        if (isSearchOpen) {
                            GlobalSearchDialog(
                                repository = repository,
                                onDismiss = { isSearchOpen = false }
                            )
                        }

                        // AI Financial Assistant Dialog
                        if (isAssistantOpen) {
                            AssistantDialog(
                                repository = repository,
                                onDismiss = { isAssistantOpen = false }
                            )
                        }

                        // Account Detail Modal
                        if (selectedAccountDetail != null) {
                            val acc = selectedAccountDetail!!
                            AlertDialog(
                                onDismissRequest = { selectedAccountDetail = null },
                                title = { Text("${acc.name} Details") },
                                text = {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Account Number: ${acc.accountNumberMasked}")
                                        Text("Account Type: ${acc.type.name}")
                                        Text("Current Balance: ${Formatters.formatCurrency(acc.balance)}", fontWeight = FontWeight.Bold)
                                        Text("Available Balance: ${Formatters.formatCurrency(acc.availableBalance)}")
                                        if (acc.interestRate > 0) {
                                            Text("Annual Percentage Yield (APY): ${acc.interestRate}%")
                                        }
                                        Text("Currency: ${acc.currency}")
                                    }
                                },
                                confirmButton = {
                                    Button(onClick = { selectedAccountDetail = null }) {
                                        Text("Close")
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
