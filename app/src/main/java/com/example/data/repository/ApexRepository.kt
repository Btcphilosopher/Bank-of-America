package com.example.data.repository

import com.example.data.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class ApexRepository {

    private val _accounts = MutableStateFlow(DemoDataProvider.getInitialAccounts())
    val accounts: StateFlow<List<Account>> = _accounts.asStateFlow()

    private val _transactions = MutableStateFlow(DemoDataProvider.getInitialTransactions())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _bills = MutableStateFlow(DemoDataProvider.getInitialBills())
    val bills: StateFlow<List<Bill>> = _bills.asStateFlow()

    private val _savingsGoals = MutableStateFlow(DemoDataProvider.getInitialSavingsGoals())
    val savingsGoals: StateFlow<List<SavingsGoal>> = _savingsGoals.asStateFlow()

    private val _holdings = MutableStateFlow(DemoDataProvider.getInitialHoldings())
    val holdings: StateFlow<List<InvestmentHolding>> = _holdings.asStateFlow()

    private val _rewards = MutableStateFlow(DemoDataProvider.getInitialRewards())
    val rewards: StateFlow<List<RewardOffer>> = _rewards.asStateFlow()

    private val _p2pRecipients = MutableStateFlow(DemoDataProvider.getInitialP2PRecipients())
    val p2pRecipients: StateFlow<List<P2PRecipient>> = _p2pRecipients.asStateFlow()

    private val _documents = MutableStateFlow(DemoDataProvider.getInitialDocuments())
    val documents: StateFlow<List<FinancialDocument>> = _documents.asStateFlow()

    private val _notifications = MutableStateFlow(DemoDataProvider.getInitialNotifications())
    val notifications: StateFlow<List<FinancialNotification>> = _notifications.asStateFlow()

    private val _assistantMessages = MutableStateFlow(DemoDataProvider.getInitialAssistantMessages())
    val assistantMessages: StateFlow<List<AssistantMessage>> = _assistantMessages.asStateFlow()

    private val _creditScore = MutableStateFlow(CreditScoreData())
    val creditScore: StateFlow<CreditScoreData> = _creditScore.asStateFlow()

    private val _retirementPlanner = MutableStateFlow(RetirementPlannerData())
    val retirementPlanner: StateFlow<RetirementPlannerData> = _retirementPlanner.asStateFlow()

    private val _mortgage = MutableStateFlow(MortgageData())
    val mortgage: StateFlow<MortgageData> = _mortgage.asStateFlow()

    // Card States
    private val _isCardLocked = MutableStateFlow(false)
    val isCardLocked: StateFlow<Boolean> = _isCardLocked.asStateFlow()

    private val _isBiometricEnabled = MutableStateFlow(true)
    val isBiometricEnabled: StateFlow<Boolean> = _isBiometricEnabled.asStateFlow()

    private val _isTwoFactorEnabled = MutableStateFlow(true)
    val isTwoFactorEnabled: StateFlow<Boolean> = _isTwoFactorEnabled.asStateFlow()

    private val _activeWorkspace = MutableStateFlow("PERSONAL") // PERSONAL or BUSINESS
    val activeWorkspace: StateFlow<String> = _activeWorkspace.asStateFlow()

    // Financial Overview helper getters
    val totalAvailableCash: Double
        get() = _accounts.value
            .filter { it.category == AccountCategory.BANKING }
            .sumOf { it.availableBalance }

    val totalInvestments: Double
        get() = _accounts.value
            .filter { it.category == AccountCategory.INVESTING || it.category == AccountCategory.RETIREMENT }
            .sumOf { it.balance }

    val totalLiabilities: Double
        get() = _accounts.value
            .filter { it.category == AccountCategory.CREDIT || it.category == AccountCategory.LOANS }
            .sumOf { it.balance }

    val totalAssets: Double
        get() = totalAvailableCash + totalInvestments + _mortgage.value.estimatedHomeValue

    val netWorth: Double
        get() = totalAssets - totalLiabilities

    val monthlySpending: Double
        get() = _transactions.value
            .filter { it.amount < 0 && it.category != TransactionCategory.TRANSFER }
            .sumOf { kotlin.math.abs(it.amount) }

    fun setWorkspace(workspace: String) {
        _activeWorkspace.value = workspace
    }

    fun toggleCardLock() {
        _isCardLocked.value = !_isCardLocked.value
    }

    fun setBiometric(enabled: Boolean) {
        _isBiometricEnabled.value = enabled
    }

    fun setTwoFactor(enabled: Boolean) {
        _isTwoFactorEnabled.value = enabled
    }

    // Execute Money Transfer
    fun executeTransfer(fromAccountId: String, toAccountId: String, amount: Double, memo: String): String {
        val currentAccs = _accounts.value.toMutableList()
        val fromIdx = currentAccs.indexOfFirst { it.id == fromAccountId }
        val toIdx = currentAccs.indexOfFirst { it.id == toAccountId }

        if (fromIdx != -1 && toIdx != -1 && currentAccs[fromIdx].availableBalance >= amount) {
            val from = currentAccs[fromIdx]
            val to = currentAccs[toIdx]

            currentAccs[fromIdx] = from.copy(
                balance = from.balance - amount,
                availableBalance = from.availableBalance - amount
            )
            currentAccs[toIdx] = to.copy(
                balance = to.balance + amount,
                availableBalance = to.availableBalance + amount
            )

            _accounts.value = currentAccs

            // Add transactions
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val tx1 = Transaction(
                id = "tx_" + System.currentTimeMillis(),
                accountId = fromAccountId,
                merchant = "Transfer to ${to.name}",
                category = TransactionCategory.TRANSFER,
                amount = -amount,
                date = dateStr,
                status = TransactionStatus.COMPLETED,
                notes = memo
            )
            val tx2 = Transaction(
                id = "tx_" + (System.currentTimeMillis() + 1),
                accountId = toAccountId,
                merchant = "Transfer from ${from.name}",
                category = TransactionCategory.TRANSFER,
                amount = amount,
                date = dateStr,
                status = TransactionStatus.COMPLETED,
                notes = memo
            )

            val currentTx = _transactions.value.toMutableList()
            currentTx.add(0, tx1)
            currentTx.add(0, tx2)
            _transactions.value = currentTx

            val confCode = "TRF-" + (100000..999999).random()
            return confCode
        }
        return ""
    }

    // Pay Bill
    fun payBill(billId: String): Boolean {
        val currentBills = _bills.value.toMutableList()
        val idx = currentBills.indexOfFirst { it.id == billId }
        if (idx != -1) {
            val bill = currentBills[idx]
            if (bill.isPaid) return true

            currentBills[idx] = bill.copy(isPaid = true)
            _bills.value = currentBills

            // Deduct from checking
            val accs = _accounts.value.toMutableList()
            AccDeduct(accs, bill.accountId, bill.amount)
            _accounts.value = accs

            // Add transaction
            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val tx = Transaction(
                id = "tx_" + System.currentTimeMillis(),
                accountId = bill.accountId,
                merchant = bill.name,
                category = bill.category,
                amount = -bill.amount,
                date = dateStr,
                status = TransactionStatus.COMPLETED,
                notes = "Bill Payment"
            )
            val currentTx = _transactions.value.toMutableList()
            currentTx.add(0, tx)
            _transactions.value = currentTx

            return true
        }
        return false
    }

    // Process Mobile Check Deposit
    fun processCheckDeposit(amount: Double, accountId: String = "chk_01"): String {
        val accs = _accounts.value.toMutableList()
        val idx = accs.indexOfFirst { it.id == accountId }
        if (idx != -1) {
            val acc = accs[idx]
            accs[idx] = acc.copy(
                balance = acc.balance + amount,
                availableBalance = acc.availableBalance + amount
            )
            _accounts.value = accs

            val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val tx = Transaction(
                id = "tx_" + System.currentTimeMillis(),
                accountId = accountId,
                merchant = "Mobile Check Deposit",
                category = TransactionCategory.INCOME,
                amount = amount,
                date = dateStr,
                status = TransactionStatus.COMPLETED,
                notes = "Verified & Cleared"
            )
            val currentTx = _transactions.value.toMutableList()
            currentTx.add(0, tx)
            _transactions.value = currentTx

            val conf = "CHK-" + (100000..999999).random()
            return conf
        }
        return ""
    }

    // Savings Goal Addition
    fun addMoneyToGoal(goalId: String, amount: Double): Boolean {
        val goals = _savingsGoals.value.toMutableList()
        val idx = goals.indexOfFirst { it.id == goalId }
        if (idx != -1) {
            val g = goals[idx]
            goals[idx] = g.copy(currentAmount = g.currentAmount + amount)
            _savingsGoals.value = goals

            // Deduct from checking
            val accs = _accounts.value.toMutableList()
            AccDeduct(accs, "chk_01", amount)
            _accounts.value = accs
            return true
        }
        return false
    }

    // Activate Reward Offer
    fun toggleRewardOffer(offerId: String) {
        val current = _rewards.value.toMutableList()
        val idx = current.indexOfFirst { it.id == offerId }
        if (idx != -1) {
            val offer = current[idx]
            current[idx] = offer.copy(isActivated = !offer.isActivated)
            _rewards.value = current
        }
    }

    // Update Transaction Category
    fun updateTransactionCategory(txId: String, newCategory: TransactionCategory) {
        val current = _transactions.value.toMutableList()
        val idx = current.indexOfFirst { it.id == txId }
        if (idx != -1) {
            current[idx] = current[idx].copy(category = newCategory)
            _transactions.value = current
        }
    }

    // Send P2P Payment
    fun sendP2PPayment(recipient: P2PRecipient, amount: Double, note: String): String {
        val accs = _accounts.value.toMutableList()
        AccDeduct(accs, "chk_01", amount)
        _accounts.value = accs

        val dateStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val tx = Transaction(
            id = "tx_" + System.currentTimeMillis(),
            accountId = "chk_01",
            merchant = "P2P Payment to ${recipient.name} (${recipient.handle})",
            category = TransactionCategory.TRANSFER,
            amount = -amount,
            date = dateStr,
            status = TransactionStatus.COMPLETED,
            notes = note
        )
        val currentTx = _transactions.value.toMutableList()
        currentTx.add(0, tx)
        _transactions.value = currentTx

        return "P2P-" + (100000..999999).random()
    }

    // Buy/Sell Stock or ETF
    fun executeTrade(symbol: String, sharesToTrade: Double, isBuy: Boolean): String {
        val currentHoldings = _holdings.value.toMutableList()
        val idx = currentHoldings.indexOfFirst { it.symbol == symbol }
        if (idx != -1) {
            val holding = currentHoldings[idx]
            val totalTradeValue = sharesToTrade * holding.currentPrice

            val accs = _accounts.value.toMutableList()
            val invAccIdx = accs.indexOfFirst { it.id == "inv_01" }

            if (isBuy) {
                if (invAccIdx != -1 && accs[invAccIdx].availableBalance >= totalTradeValue) {
                    accs[invAccIdx] = accs[invAccIdx].copy(
                        availableBalance = accs[invAccIdx].availableBalance - totalTradeValue,
                        balance = accs[invAccIdx].balance + totalTradeValue
                    )
                    currentHoldings[idx] = holding.copy(shares = holding.shares + sharesToTrade)
                } else {
                    return "INSUFFICIENT_FUNDS"
                }
            } else {
                if (holding.shares >= sharesToTrade) {
                    currentHoldings[idx] = holding.copy(shares = holding.shares - sharesToTrade)
                    if (invAccIdx != -1) {
                        accs[invAccIdx] = accs[invAccIdx].copy(
                            availableBalance = accs[invAccIdx].availableBalance + totalTradeValue
                        )
                    }
                } else {
                    return "INSUFFICIENT_SHARES"
                }
            }
            _accounts.value = accs
            _holdings.value = currentHoldings
            return "TRADE-" + (100000..999999).random()
        }
        return "ERROR"
    }

    // AI Assistant Query Engine
    fun askAssistant(query: String) {
        val currentMsgs = _assistantMessages.value.toMutableList()
        val timeStr = SimpleDateFormat("hh:mm a", Locale.US).format(Date())

        currentMsgs.add(AssistantMessage("m_" + System.currentTimeMillis(), query, MessageSender.USER, timeStr))

        val lower = query.lowercase()
        var responseText = ""
        var actionCard: AssistantActionCard? = null

        when {
            lower.contains("electricity") || lower.contains("electric") || lower.contains("conedison") -> {
                val bill = _bills.value.find { it.name.contains("ConEdison", ignoreCase = true) }
                if (bill != null) {
                    responseText = "Your ConEdison electric bill is $${String.format(Locale.US, "%.2f", bill.amount)}, due on ${bill.dueDate}. Would you like to pay it now?"
                    actionCard = AssistantActionCard(
                        type = "PAY_BILL",
                        title = "ConEdison Electric & Gas",
                        details = mapOf("Due Date" to bill.dueDate, "Account" to "Advantage Checking ••••4821"),
                        amount = bill.amount
                    )
                } else {
                    responseText = "All electricity bills are currently up to date!"
                }
            }
            lower.contains("spend") || lower.contains("spent") || lower.contains("restaurant") || lower.contains("food") -> {
                val foodTotal = _transactions.value
                    .filter { it.category == TransactionCategory.FOOD_AND_DINING && it.amount < 0 }
                    .sumOf { kotlin.math.abs(it.amount) }
                responseText = "This month, you've spent $${String.format(Locale.US, "%.2f", foodTotal)} on Food & Dining across Whole Foods, Starbucks, and restaurants."
                actionCard = AssistantActionCard(
                    type = "INSIGHT",
                    title = "Monthly Food & Dining Summary",
                    details = mapOf("Category" to "Food & Dining", "Total Transactions" to "2", "Budget Impact" to "Well within $1,200 limit")
                )
            }
            lower.contains("credit card") || lower.contains("card due") -> {
                val cc = _accounts.value.find { it.id == "cc_01" }
                responseText = "Your Custom Cash Visa balance is $${String.format(Locale.US, "%.2f", cc?.balance ?: 0.0)}. The minimum payment of $85.00 is due on August 28."
            }
            lower.contains("portfolio") || lower.contains("invest") || lower.contains("stock") -> {
                responseText = "Your total investment portfolio value is $${String.format(Locale.US, "%.2f", totalInvestments)}. Top holdings include Vanguard S&P 500 ETF (VOO) and Apple Inc. (AAPL)."
            }
            lower.contains("save") || lower.contains("savings") -> {
                val sav = _accounts.value.find { it.id == "sav_01" }
                responseText = "You have $${String.format(Locale.US, "%.2f", sav?.balance ?: 0.0)} in Savings earning 4.25% APY, plus $34,820 towards your Emergency Cash Cushion goal."
            }
            else -> {
                responseText = "I've analyzed your financial data. Your available cash is $${String.format(Locale.US, "%.2f", totalAvailableCash)} and your Net Worth stands at $${String.format(Locale.US, "%.2f", netWorth)}. Let me know if you want to transfer money, review bills, or check investments."
            }
        }

        currentMsgs.add(
            AssistantMessage(
                id = "m_" + (System.currentTimeMillis() + 1),
                text = responseText,
                sender = MessageSender.ASSISTANT,
                timestamp = timeStr,
                actionCard = actionCard
            )
        )

        _assistantMessages.value = currentMsgs
    }

    private fun AccDeduct(accs: MutableList<Account>, accountId: String, amount: Double) {
        val idx = accs.indexOfFirst { it.id == accountId }
        if (idx != -1) {
            val acc = accs[idx]
            accs[idx] = acc.copy(
                balance = acc.balance - amount,
                availableBalance = acc.availableBalance - amount
            )
        }
    }
}
