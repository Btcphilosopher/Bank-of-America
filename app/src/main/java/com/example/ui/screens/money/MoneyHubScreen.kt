package com.example.ui.screens.money

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import com.example.data.models.*
import com.example.data.repository.ApexRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun MoneyHubScreen(
    repository: ApexRepository,
    initialSubTab: Int = 0
) {
    var selectedSubTab by remember { mutableStateOf(initialSubTab) }
    val subTabs = listOf("Transactions", "Savings Goals", "Transfers", "Bill Pay", "Check Deposit")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Top Sub-Tab Bar
        ScrollableTabRow(
            selectedTabIndex = selectedSubTab,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            divider = {}
        ) {
            subTabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedSubTab == index,
                    onClick = { selectedSubTab = index },
                    text = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontWeight = if (selectedSubTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedSubTab) {
            0 -> TransactionsView(repository)
            1 -> SavingsGoalsView(repository)
            2 -> TransferHubView(repository)
            3 -> BillPayView(repository)
            4 -> CheckDepositView(repository)
        }
    }
}

// -------------------------------------------------------------
// 1. TRANSACTIONS VIEW
// -------------------------------------------------------------
@Composable
private fun TransactionsView(repository: ApexRepository) {
    val transactions by repository.transactions.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<TransactionCategory?>(null) }
    var selectedTxForCategoryCorrection by remember { mutableStateOf<Transaction?>(null) }

    val filtered = transactions.filter { tx ->
        val matchesSearch = tx.merchant.contains(searchQuery, ignoreCase = true) ||
                (tx.notes?.contains(searchQuery, ignoreCase = true) == true)
        val matchesCategory = selectedCategoryFilter == null || tx.category == selectedCategoryFilter
        matchesSearch && matchesCategory
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search transactions, merchants...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = null)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategoryFilter == null,
                        onClick = { selectedCategoryFilter = null },
                        label = { Text("All Categories") }
                    )
                }
                items(TransactionCategory.values()) { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat.name.replace("_", " ")) }
                    )
                }
            }
        }

        items(filtered) { tx ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedTxForCategoryCorrection = tx },
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (tx.category) {
                                    TransactionCategory.FOOD_AND_DINING -> Icons.Default.Restaurant
                                    TransactionCategory.INCOME -> Icons.Default.ArrowDownward
                                    TransactionCategory.SHOPPING -> Icons.Default.ShoppingBag
                                    TransactionCategory.UTILITIES -> Icons.Default.Bolt
                                    TransactionCategory.TRANSPORT -> Icons.Default.DirectionsCar
                                    TransactionCategory.SUBSCRIPTIONS -> Icons.Default.Subscriptions
                                    TransactionCategory.TRAVEL -> Icons.Default.Flight
                                    TransactionCategory.HEALTH -> Icons.Default.LocalHospital
                                    TransactionCategory.HOUSING -> Icons.Default.Home
                                    else -> Icons.Default.Receipt
                                },
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = tx.merchant,
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (tx.status == TransactionStatus.PENDING) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = AccentGold.copy(alpha = 0.2f)
                                    ) {
                                        Text(
                                            text = "PENDING",
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold),
                                            color = AccentGold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${tx.date} • ${tx.category.name.replace("_", " ")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = (if (tx.amount > 0) "+" else "") + Formatters.formatCurrency(tx.amount),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (tx.amount > 0) PositiveGreen else MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }
    }

    // Category Correction Dialog
    if (selectedTxForCategoryCorrection != null) {
        val tx = selectedTxForCategoryCorrection!!
        AlertDialog(
            onDismissRequest = { selectedTxForCategoryCorrection = null },
            title = { Text("Correct Category for ${tx.merchant}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Select a new category to recategorize this transaction:", style = MaterialTheme.typography.bodySmall)
                    TransactionCategory.values().forEach { cat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    repository.updateTransactionCategory(tx.id, cat)
                                    selectedTxForCategoryCorrection = null
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(selected = tx.category == cat, onClick = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(cat.name.replace("_", " "), style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedTxForCategoryCorrection = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 2. SAVINGS GOALS VIEW
// -------------------------------------------------------------
@Composable
private fun SavingsGoalsView(repository: ApexRepository) {
    val goals by repository.savingsGoals.collectAsState()
    var selectedGoalForDeposit by remember { mutableStateOf<SavingsGoal?>(null) }
    var addAmountText by remember { mutableStateOf("") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(goals) { goal ->
            val progress = (goal.currentAmount / goal.targetAmount).toFloat()
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = goal.title,
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Target: ${Formatters.formatCurrency(goal.targetAmount)} by ${goal.targetDate}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        CategoryBadge(category = goal.category, color = BankBlue)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = Formatters.formatCurrency(goal.currentAmount),
                            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = PositiveGreen)
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    ClipProgressMeter(progress = progress, color = PositiveGreen)

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = { selectedGoalForDeposit = goal },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Add Money to Goal")
                    }
                }
            }
        }
    }

    if (selectedGoalForDeposit != null) {
        val goal = selectedGoalForDeposit!!
        AlertDialog(
            onDismissRequest = { selectedGoalForDeposit = null },
            title = { Text("Contribute to ${goal.title}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter deposit amount from Advantage Checking:")
                    OutlinedTextField(
                        value = addAmountText,
                        onValueChange = { addAmountText = it },
                        label = { Text("Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amt = addAmountText.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            repository.addMoneyToGoal(goal.id, amt)
                            selectedGoalForDeposit = null
                            addAmountText = ""
                        }
                    }
                ) {
                    Text("Confirm Deposit")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedGoalForDeposit = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 3. TRANSFER HUB VIEW
// -------------------------------------------------------------
@Composable
private fun TransferHubView(repository: ApexRepository) {
    val accounts by repository.accounts.collectAsState()
    val p2pRecipients by repository.p2pRecipients.collectAsState()

    var transferType by remember { mutableStateOf("INTERNAL") } // INTERNAL, P2P, INTERNATIONAL
    var fromAccount by remember { mutableStateOf(accounts.firstOrNull()?.id ?: "") }
    var toAccount by remember { mutableStateOf(accounts.getOrNull(1)?.id ?: "") }
    var transferAmountText by remember { mutableStateOf("250.00") }
    var transferMemo by remember { mutableStateOf("Savings auto-allocation") }
    var confirmationCode by remember { mutableStateOf<String?>(null) }

    // P2P State
    var selectedRecipient by remember { mutableStateOf<P2PRecipient?>(p2pRecipients.firstOrNull()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(4.dp)
            ) {
                listOf("INTERNAL" to "Between Accounts", "P2P" to "Send P2P", "INTL" to "International").forEach { (typeKey, label) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (transferType == typeKey) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable { transferType = typeKey }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (transferType == typeKey) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 11.sp
                            ),
                            color = if (transferType == typeKey) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        if (transferType == "INTERNAL") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("FROM ACCOUNT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        AccountDropdownSelector(accounts = accounts, selectedId = fromAccount) { fromAccount = it }

                        Text("TO ACCOUNT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        AccountDropdownSelector(accounts = accounts, selectedId = toAccount) { toAccount = it }

                        OutlinedTextField(
                            value = transferAmountText,
                            onValueChange = { transferAmountText = it },
                            label = { Text("Transfer Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = transferMemo,
                            onValueChange = { transferMemo = it },
                            label = { Text("Memo / Note") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val amt = transferAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0 && fromAccount.isNotEmpty() && toAccount.isNotEmpty()) {
                                    val code = repository.executeTransfer(fromAccount, toAccount, amt, transferMemo)
                                    if (code.isNotEmpty()) confirmationCode = code
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Confirm Transfer")
                        }
                    }
                }
            }
        } else if (transferType == "P2P") {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Text("SELECT RECIPIENT", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            items(p2pRecipients) { recipient ->
                                val isSelected = selectedRecipient?.id == recipient.id
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                                        .clickable { selectedRecipient = recipient }
                                        .padding(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(CircleShape)
                                            .background(BankBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(recipient.avatarInitials, color = Color.White, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(recipient.name, style = MaterialTheme.typography.labelMedium)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = transferAmountText,
                            onValueChange = { transferAmountText = it },
                            label = { Text("Amount ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                val amt = transferAmountText.toDoubleOrNull() ?: 0.0
                                if (amt > 0 && selectedRecipient != null) {
                                    val code = repository.sendP2PPayment(selectedRecipient!!, amt, transferMemo)
                                    confirmationCode = code
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Send P2P Payment")
                        }
                    }
                }
            }
        } else {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = CardBorder()
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("INTERNATIONAL WIRE CALCULATOR", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        Text("Recipient Country: United Kingdom (GBP)", style = MaterialTheme.typography.bodyMedium)
                        Text("Guaranteed Rate: 1 USD = 0.7852 GBP", style = MaterialTheme.typography.bodySmall, color = PositiveGreen)
                        Text("Transfer Fee: $0.00 (Premier Relationship)", style = MaterialTheme.typography.bodySmall, color = BankBlue)

                        OutlinedTextField(
                            value = transferAmountText,
                            onValueChange = { transferAmountText = it },
                            label = { Text("Amount in USD ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )

                        val usd = transferAmountText.toDoubleOrNull() ?: 0.0
                        val gbp = usd * 0.7852

                        Text(
                            text = "Recipient Receives: £${String.format(Locale.US, "%.2f", gbp)} GBP",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = BankBlue)
                        )

                        Button(
                            onClick = {
                                confirmationCode = "INTL-" + (100000..999999).random()
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Review & Submit Wire")
                        }
                    }
                }
            }
        }
    }

    if (confirmationCode != null) {
        AlertDialog(
            onDismissRequest = { confirmationCode = null },
            title = { Text("Transfer Confirmed!") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Your transfer request was processed successfully.", style = MaterialTheme.typography.bodyMedium)
                    Text("Confirmation Number: $confirmationCode", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = BankBlue))
                    Text("Status: COMPLETED / PENDING CLEARANCE", style = MaterialTheme.typography.bodySmall, color = PositiveGreen)
                }
            },
            confirmButton = {
                Button(onClick = { confirmationCode = null }) {
                    Text("Done")
                }
            }
        )
    }
}

@Composable
private fun AccountDropdownSelector(
    accounts: List<Account>,
    selectedId: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val selectedAcc = accounts.find { it.id == selectedId } ?: accounts.firstOrNull()

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${selectedAcc?.name} (${selectedAcc?.accountNumberMasked}) - ${Formatters.formatCurrency(selectedAcc?.balance ?: 0.0)}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            accounts.forEach { acc ->
                DropdownMenuItem(
                    text = { Text("${acc.name} (${acc.accountNumberMasked}) - ${Formatters.formatCurrency(acc.balance)}") },
                    onClick = {
                        onSelect(acc.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 4. BILL PAY VIEW
// -------------------------------------------------------------
@Composable
private fun BillPayView(repository: ApexRepository) {
    val bills by repository.bills.collectAsState()
    var selectedBillForPay by remember { mutableStateOf<Bill?>(null) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(bills) { bill ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
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
                    Column {
                        Text(bill.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        Text("Due Date: ${bill.dueDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (bill.isAutoPay) {
                            Text("AutoPay Enabled", style = MaterialTheme.typography.labelSmall.copy(color = BankBlue, fontWeight = FontWeight.Bold))
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = Formatters.formatCurrency(bill.amount),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        if (bill.isPaid) {
                            Surface(shape = RoundedCornerShape(6.dp), color = PositiveGreen.copy(alpha = 0.15f)) {
                                Text("PAID", color = PositiveGreen, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                            }
                        } else {
                            Button(
                                onClick = { selectedBillForPay = bill },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Pay")
                            }
                        }
                    }
                }
            }
        }
    }

    if (selectedBillForPay != null) {
        val bill = selectedBillForPay!!
        AlertDialog(
            onDismissRequest = { selectedBillForPay = null },
            title = { Text("Confirm Bill Payment") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Payee: ${bill.name}")
                    Text("Amount: ${Formatters.formatCurrency(bill.amount)}")
                    Text("Payment Account: Advantage Checking ••••4821")
                    Text("Process Date: Immediate")
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.payBill(bill.id)
                        selectedBillForPay = null
                    }
                ) {
                    Text("Submit Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedBillForPay = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 5. CHECK DEPOSIT VIEW
// -------------------------------------------------------------
@Composable
private fun CheckDepositView(repository: ApexRepository) {
    var step by remember { mutableStateOf(1) } // 1: Front, 2: Back, 3: Amount, 4: Confirmed
    var amountText by remember { mutableStateOf("1250.00") }
    var confCode by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = CardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "MOBILE CHECK DEPOSIT",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            when (step) {
                1 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(44.dp), tint = BankBlue)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Align FRONT of check within frame", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Button(onClick = { step = 2 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Capture Front Image")
                    }
                }
                2 -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(44.dp), tint = BankBlue)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Align BACK of check (Endorsed 'For Mobile Deposit')", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    Button(onClick = { step = 3 }, modifier = Modifier.fillMaxWidth()) {
                        Text("Capture Back Image")
                    }
                }
                3 -> {
                    Text("Check Images Captured & AI Scanned Successfully", color = PositiveGreen, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = amountText,
                        onValueChange = { amountText = it },
                        label = { Text("Verified Check Amount ($)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            val amt = amountText.toDoubleOrNull() ?: 0.0
                            if (amt > 0) {
                                confCode = repository.processCheckDeposit(amt)
                                step = 4
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit Check for Deposit")
                    }
                }
                4 -> {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = PositiveGreen, modifier = Modifier.size(56.dp))
                    Text("Check Deposit Received!", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Confirmation Number: $confCode", style = MaterialTheme.typography.bodyMedium.copy(color = BankBlue, fontWeight = FontWeight.Bold))
                    Text("Funds credited immediately to Advantage Checking ••••4821.", style = MaterialTheme.typography.bodySmall)

                    Button(
                        onClick = { step = 1 },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Deposit Another Check")
                    }
                }
            }
        }
    }
}
