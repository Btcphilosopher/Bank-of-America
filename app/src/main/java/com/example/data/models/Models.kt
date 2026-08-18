package com.example.data.models

import androidx.annotation.DrawableRes

enum class AccountType {
    CHECKING,
    SAVINGS,
    MONEY_MARKET,
    CERTIFICATE_OF_DEPOSIT,
    CREDIT_CARD,
    MORTGAGE,
    AUTO_LOAN,
    PERSONAL_LOAN,
    BROKERAGE,
    RETIREMENT_401K,
    ROTH_IRA
}

enum class AccountCategory {
    BANKING,
    CREDIT,
    INVESTING,
    RETIREMENT,
    LOANS
}

data class Account(
    val id: String,
    val name: String,
    val type: AccountType,
    val category: AccountCategory,
    val accountNumberMasked: String,
    val balance: Double,
    val availableBalance: Double,
    val interestRate: Double = 0.0,
    val currency: String = "USD",
    val isBusinessAccount: Boolean = false
)

enum class TransactionCategory {
    HOUSING,
    FOOD_AND_DINING,
    TRANSPORT,
    SHOPPING,
    ENTERTAINMENT,
    UTILITIES,
    TRAVEL,
    HEALTH,
    SUBSCRIPTIONS,
    INCOME,
    TRANSFER,
    INVESTMENT,
    OTHER
}

enum class TransactionStatus {
    PENDING,
    COMPLETED
}

data class Transaction(
    val id: String,
    val accountId: String,
    val merchant: String,
    val category: TransactionCategory,
    val amount: Double, // Negative for spending, positive for deposit/income
    val date: String, // e.g., "2026-08-17"
    val status: TransactionStatus = TransactionStatus.COMPLETED,
    val isRecurring: Boolean = false,
    val notes: String? = null,
    val iconName: String = "store"
)

data class Bill(
    val id: String,
    val name: String,
    val category: TransactionCategory,
    val amount: Double,
    val dueDate: String,
    val isPaid: Boolean = false,
    val isAutoPay: Boolean = true,
    val accountId: String = "chk_01"
)

data class SavingsGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val targetDate: String,
    val category: String,
    val isAutoSaveEnabled: Boolean = true
)

enum class AssetClass {
    STOCKS,
    BONDS,
    ETFS,
    MONEY_MARKET,
    CRYPTO
}

data class InvestmentHolding(
    val symbol: String,
    val name: String,
    val shares: Double,
    val avgPrice: Double,
    val currentPrice: Double,
    val todayChangePct: Double,
    val assetClass: AssetClass
) {
    val totalValue: Double get() = shares * currentPrice
    val totalReturn: Double get() = (currentPrice - avgPrice) * shares
    val totalReturnPct: Double get() = if (avgPrice > 0) ((currentPrice - avgPrice) / avgPrice) * 100 else 0.0
}

data class RewardOffer(
    val id: String,
    val merchant: String,
    val offerTitle: String,
    val cashbackPct: Double,
    val expirationDate: String,
    val isActivated: Boolean = false,
    val category: String = "Shopping"
)

data class P2PRecipient(
    val id: String,
    val name: String,
    val handle: String,
    val email: String,
    val phone: String,
    val isFavorite: Boolean = false,
    val avatarInitials: String
)

data class CreditScoreData(
    val score: Int = 784,
    val maxScore: Int = 850,
    val rating: String = "EXCELLENT",
    val creditUtilizationPct: Int = 12,
    val paymentHistoryPct: Int = 100,
    val averageAccountAgeYears: Double = 6.4,
    val totalInquiries: Int = 1,
    val openAccountsCount: Int = 7
)

data class RetirementPlannerData(
    val currentAge: Int = 32,
    val targetRetirementAge: Int = 65,
    val currentSavings: Double = 241900.10,
    val monthlyContribution: Double = 1850.0,
    val expectedReturnRatePct: Double = 7.5,
    val targetMonthlyIncome: Double = 8500.0,
    val employerMatchPct: Double = 5.0
)

data class MortgageData(
    val originalPrincipal: Double = 350000.00,
    val currentBalance: Double = 285000.00,
    val interestRatePct: Double = 3.875,
    val monthlyPayment: Double = 2150.00,
    val principalPart: Double = 820.00,
    val interestPart: Double = 918.75,
    val escrowPart: Double = 411.25,
    val estimatedHomeValue: Double = 465000.00
) {
    val estimatedEquity: Double get() = estimatedHomeValue - currentBalance
}

data class FinancialDocument(
    val id: String,
    val title: String,
    val category: String, // "STATEMENTS", "TAX", "INVESTMENT", "LOANS"
    val date: String,
    val fileSize: String,
    val isRead: Boolean = true
)

data class FinancialNotification(
    val id: String,
    val title: String,
    val message: String,
    val date: String,
    val type: String, // "PAYMENT", "SECURITY", "DEPOSIT", "REWARD"
    val isRead: Boolean = false
)

enum class MessageSender {
    USER,
    ASSISTANT
}

data class AssistantActionCard(
    val type: String, // "PAY_BILL", "TRANSFER", "INSIGHT"
    val title: String,
    val details: Map<String, String>,
    val amount: Double? = null,
    val isConfirmed: Boolean = false
)

data class AssistantMessage(
    val id: String,
    val text: String,
    val sender: MessageSender,
    val timestamp: String,
    val actionCard: AssistantActionCard? = null
)
