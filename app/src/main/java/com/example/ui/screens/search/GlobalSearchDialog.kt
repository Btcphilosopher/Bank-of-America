package com.example.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.repository.ApexRepository
import com.example.ui.components.CardBorder
import com.example.ui.components.Formatters

@Composable
fun GlobalSearchDialog(
    repository: ApexRepository,
    onDismiss: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val transactions by repository.transactions.collectAsState()
    val accounts by repository.accounts.collectAsState()
    val bills by repository.bills.collectAsState()
    val rewards by repository.rewards.collectAsState()

    val matchedTx = if (searchQuery.isBlank()) emptyList() else transactions.filter {
        it.merchant.contains(searchQuery, ignoreCase = true) || (it.notes?.contains(searchQuery, ignoreCase = true) == true)
    }

    val matchedAcc = if (searchQuery.isBlank()) emptyList() else accounts.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val matchedBills = if (searchQuery.isBlank()) emptyList() else bills.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    val matchedRewards = if (searchQuery.isBlank()) emptyList() else rewards.filter {
        it.merchant.contains(searchQuery, ignoreCase = true) || it.offerTitle.contains(searchQuery, ignoreCase = true)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight(0.82f),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Global Financial Search", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search Amazon, Whole Foods, Bills, Accounts...") },
                    leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (matchedTx.isNotEmpty()) {
                        item { Text("TRANSACTIONS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
                        items(matchedTx) { tx ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = CardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(tx.merchant, fontWeight = FontWeight.Bold)
                                    Text(Formatters.formatCurrency(tx.amount))
                                }
                            }
                        }
                    }

                    if (matchedAcc.isNotEmpty()) {
                        item { Text("ACCOUNTS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
                        items(matchedAcc) { acc ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = CardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(acc.name, fontWeight = FontWeight.Bold)
                                    Text(Formatters.formatCurrency(acc.balance))
                                }
                            }
                        }
                    }

                    if (matchedBills.isNotEmpty()) {
                        item { Text("BILLS", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)) }
                        items(matchedBills) { bill ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                border = CardBorder()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(bill.name, fontWeight = FontWeight.Bold)
                                    Text(Formatters.formatCurrency(bill.amount))
                                }
                            }
                        }
                    }

                    if (searchQuery.isNotBlank() && matchedTx.isEmpty() && matchedAcc.isEmpty() && matchedBills.isEmpty() && matchedRewards.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No matching financial records found.", style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }
            }
        }
    }
}
