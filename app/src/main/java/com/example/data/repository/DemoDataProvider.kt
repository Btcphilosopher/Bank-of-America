package com.example.data.repository

import com.example.data.models.*

object DemoDataProvider {

    fun getInitialAccounts(): List<Account> {
        return listOf(
            Account(
                id = "chk_01",
                name = "Bank of America Advantage Checking",
                type = AccountType.CHECKING,
                category = AccountCategory.BANKING,
                accountNumberMasked = "•••• 4821",
                balance = 8421.55,
                availableBalance = 8421.55,
                interestRate = 0.05
            ),
            Account(
                id = "sav_01",
                name = "Bank of America Advantage Savings",
                type = AccountType.SAVINGS,
                category = AccountCategory.BANKING,
                accountNumberMasked = "•••• 9102",
                balance = 34820.00,
                availableBalance = 34820.00,
                interestRate = 4.25
            ),
            Account(
                id = "cd_01",
                name = "Bank of America 12-Month Featured CD",
                type = AccountType.CERTIFICATE_OF_DEPOSIT,
                category = AccountCategory.BANKING,
                accountNumberMasked = "•••• 3319",
                balance = 15000.00,
                availableBalance = 15000.00,
                interestRate = 5.10
            ),
            Account(
                id = "cc_01",
                name = "Customized Cash Rewards Visa Signature®",
                type = AccountType.CREDIT_CARD,
                category = AccountCategory.CREDIT,
                accountNumberMasked = "•••• 7741",
                balance = 2840.21, // Owed balance
                availableBalance = 17159.79, // $20,000 credit limit
                interestRate = 18.24
            ),
            Account(
                id = "inv_01",
                name = "Merrill® Guided Investing Portfolio",
                type = AccountType.BROKERAGE,
                category = AccountCategory.INVESTING,
                accountNumberMasked = "•••• 8820",
                balance = 128440.20,
                availableBalance = 6420.00, // Cash balance
                interestRate = 0.0
            ),
            Account(
                id = "401k_01",
                name = "Merrill® 401(k) Preferred Plan",
                type = AccountType.RETIREMENT_401K,
                category = AccountCategory.RETIREMENT,
                accountNumberMasked = "•••• 1109",
                balance = 241900.10,
                availableBalance = 0.0,
                interestRate = 0.0
            ),
            Account(
                id = "mort_01",
                name = "Bank of America Fixed Home Loan",
                type = AccountType.MORTGAGE,
                category = AccountCategory.LOANS,
                accountNumberMasked = "•••• 6204",
                balance = 285000.00, // Loan balance
                availableBalance = 0.0,
                interestRate = 3.875
            ),
            Account(
                id = "auto_01",
                name = "Bank of America Preferred Auto Loan",
                type = AccountType.AUTO_LOAN,
                category = AccountCategory.LOANS,
                accountNumberMasked = "•••• 5512",
                balance = 14200.00,
                availableBalance = 0.0,
                interestRate = 4.49
            )
        )
    }

    fun getInitialTransactions(): List<Transaction> {
        return listOf(
            Transaction("tx_01", "chk_01", "Whole Foods Market", TransactionCategory.FOOD_AND_DINING, -82.41, "2026-08-17", TransactionStatus.COMPLETED, false, "Weekly organic groceries"),
            Transaction("tx_02", "chk_01", "Starbucks Coffee", TransactionCategory.FOOD_AND_DINING, -7.85, "2026-08-17", TransactionStatus.COMPLETED, false, "Iced Latte & Pastry"),
            Transaction("tx_03", "chk_01", "Apex Employer Payroll - Direct Deposit", TransactionCategory.INCOME, 4250.00, "2026-08-15", TransactionStatus.COMPLETED, true, "Bi-weekly Salary"),
            Transaction("tx_04", "cc_01", "Apple Store Online", TransactionCategory.SHOPPING, -199.00, "2026-08-16", TransactionStatus.PENDING, false, "AirPods Pro Care"),
            Transaction("tx_05", "chk_01", "ConEdison Electric & Gas", TransactionCategory.UTILITIES, -124.00, "2026-08-14", TransactionStatus.COMPLETED, true, "Monthly Electric Bill"),
            Transaction("tx_06", "cc_01", "Chevron Gas Station", TransactionCategory.TRANSPORT, -48.50, "2026-08-14", TransactionStatus.COMPLETED, false, "Fuel Fill-up"),
            Transaction("tx_07", "chk_01", "Netflix Subscription", TransactionCategory.SUBSCRIPTIONS, -22.99, "2026-08-12", TransactionStatus.COMPLETED, true, "Premium Tier"),
            Transaction("tx_08", "chk_01", "Spotify Family", TransactionCategory.SUBSCRIPTIONS, -16.99, "2026-08-11", TransactionStatus.COMPLETED, true, "Music Streaming"),
            Transaction("tx_09", "chk_01", "Zelle Transfer from Sarah Miller", TransactionCategory.TRANSFER, 150.00, "2026-08-10", TransactionStatus.COMPLETED, false, "Dinner Split"),
            Transaction("tx_10", "cc_01", "Delta Air Lines", TransactionCategory.TRAVEL, -420.80, "2026-08-08", TransactionStatus.COMPLETED, false, "Roundtrip Flight to SF"),
            Transaction("tx_11", "cc_01", "Marriott Hotels", TransactionCategory.TRAVEL, -289.00, "2026-08-08", TransactionStatus.COMPLETED, false, "Hotel Stay"),
            Transaction("tx_12", "chk_01", "Equinox Fitness Club", TransactionCategory.HEALTH, -260.00, "2026-08-01", TransactionStatus.COMPLETED, true, "Monthly Gym Access"),
            Transaction("tx_13", "chk_01", "Home Depot", TransactionCategory.HOUSING, -142.30, "2026-08-02", TransactionStatus.COMPLETED, false, "Garden & Tools"),
            Transaction("tx_14", "chk_01", "Target Department Store", TransactionCategory.SHOPPING, -94.15, "2026-08-03", TransactionStatus.COMPLETED, false, "Home Supplies"),
            Transaction("tx_15", "chk_01", "Uber Ride Services", TransactionCategory.TRANSPORT, -34.20, "2026-08-05", TransactionStatus.COMPLETED, false, "Airport Ride"),
            Transaction("tx_16", "inv_01", "Dividend Payout - Vanguard S&P 500 ETF", TransactionCategory.INVESTMENT, 342.50, "2026-08-01", TransactionStatus.COMPLETED, false, "Q3 Dividend"),
            Transaction("tx_17", "chk_01", "Mortgage Payment Escrow", TransactionCategory.HOUSING, -2150.00, "2026-08-01", TransactionStatus.COMPLETED, true, "Monthly Mortgage"),
            Transaction("tx_18", "cc_01", "Amazon.com Shopping", TransactionCategory.SHOPPING, -82.31, "2026-08-04", TransactionStatus.COMPLETED, false, "Electronics & Books"),
            Transaction("tx_19", "cc_01", "Amazon Prime", TransactionCategory.SUBSCRIPTIONS, -14.99, "2026-08-01", TransactionStatus.COMPLETED, true, "Prime Membership"),
            Transaction("tx_20", "chk_01", "CVS Pharmacy", TransactionCategory.HEALTH, -31.40, "2026-08-06", TransactionStatus.COMPLETED, false, "Prescription & Healthcare")
        )
    }

    fun getInitialBills(): List<Bill> {
        return listOf(
            Bill("b_01", "ConEdison Electric & Gas", TransactionCategory.UTILITIES, 124.00, "2026-08-25", isPaid = false, isAutoPay = true, accountId = "chk_01"),
            Bill("b_02", "Apex Mortgage Servicing", TransactionCategory.HOUSING, 2150.00, "2026-09-01", isPaid = false, isAutoPay = true, accountId = "chk_01"),
            Bill("b_03", "Custom Cash Visa Minimum Payment", TransactionCategory.TRANSFER, 85.00, "2026-08-28", isPaid = false, isAutoPay = false, accountId = "chk_01"),
            Bill("b_04", "Verizon Wireless Family", TransactionCategory.UTILITIES, 160.00, "2026-08-29", isPaid = false, isAutoPay = true, accountId = "chk_01"),
            Bill("b_05", "Preferred Auto Financing", TransactionCategory.TRANSPORT, 385.00, "2026-09-05", isPaid = false, isAutoPay = true, accountId = "chk_01"),
            Bill("b_06", "State Farm Auto & Home Insurance", TransactionCategory.HOUSING, 210.00, "2026-09-10", isPaid = false, isAutoPay = true, accountId = "chk_01"),
            Bill("b_07", "Equinox Fitness Membership", TransactionCategory.HEALTH, 260.00, "2026-09-01", isPaid = false, isAutoPay = true, accountId = "chk_01")
        )
    }

    fun getInitialSavingsGoals(): List<SavingsGoal> {
        return listOf(
            SavingsGoal("sg_01", "Emergency Cash Cushion", 50000.00, 34820.00, "2027-01-01", "Security", isAutoSaveEnabled = true),
            SavingsGoal("sg_02", "Custom Home Renovation", 25000.00, 14200.00, "2027-06-15", "House", isAutoSaveEnabled = true),
            SavingsGoal("sg_03", "European Summer Tour", 10000.00, 6800.00, "2027-07-01", "Travel", isAutoSaveEnabled = false),
            SavingsGoal("sg_04", "New EV Purchase Fund", 40000.00, 18500.00, "2028-03-31", "Car", isAutoSaveEnabled = true)
        )
    }

    fun getInitialHoldings(): List<InvestmentHolding> {
        return listOf(
            InvestmentHolding("VOO", "Vanguard S&P 500 ETF", 145.0, 420.00, 512.30, 1.24, AssetClass.ETFS),
            InvestmentHolding("AAPL", "Apple Inc.", 85.0, 175.50, 224.50, 0.85, AssetClass.STOCKS),
            InvestmentHolding("MSFT", "Microsoft Corporation", 60.0, 310.00, 448.20, -0.42, AssetClass.STOCKS),
            InvestmentHolding("NVDA", "NVIDIA Corporation", 120.0, 92.00, 128.40, 3.15, AssetClass.STOCKS),
            InvestmentHolding("BND", "Vanguard Total Bond Market ETF", 300.0, 74.50, 72.80, 0.12, AssetClass.BONDS),
            InvestmentHolding("VMFXX", "Vanguard Federal Money Market", 6420.0, 1.00, 1.00, 0.00, AssetClass.MONEY_MARKET)
        )
    }

    fun getInitialRewards(): List<RewardOffer> {
        return listOf(
            RewardOffer("ro_01", "Whole Foods Market", "5% Cash Back on Organic Groceries", 5.0, "2026-08-31", isActivated = true, category = "Groceries"),
            RewardOffer("ro_02", "Chevron & Texaco", "3% Cash Back on Fuel & EV Charging", 3.0, "2026-09-15", isActivated = true, category = "Transport"),
            RewardOffer("ro_03", "Starbucks", "$5 Bonus Cash on $25 reload", 10.0, "2026-08-25", isActivated = false, category = "Dining"),
            RewardOffer("ro_04", "Marriott Bonvoy", "10,000 Bonus Points on 2 Night Stay", 8.0, "2026-10-01", isActivated = false, category = "Travel"),
            RewardOffer("ro_05", "Apple Store", "3% Unlimited Daily Cash Back", 3.0, "2026-12-31", isActivated = true, category = "Electronics")
        )
    }

    fun getInitialP2PRecipients(): List<P2PRecipient> {
        return listOf(
            P2PRecipient("p_01", "Sarah Miller", "@sarah_m", "sarah.m@example.com", "(555) 234-5678", isFavorite = true, avatarInitials = "SM"),
            P2PRecipient("p_02", "David Chen", "@dchen_tech", "david.c@example.com", "(555) 876-5432", isFavorite = true, avatarInitials = "DC"),
            P2PRecipient("p_03", "Elena Rostova", "@elena_r", "elena.r@example.com", "(555) 345-6789", isFavorite = false, avatarInitials = "ER"),
            P2PRecipient("p_04", "Marcus Johnson", "@marcus_j", "marcus.j@example.com", "(555) 901-2345", isFavorite = true, avatarInitials = "MJ")
        )
    }

    fun getInitialDocuments(): List<FinancialDocument> {
        return listOf(
            FinancialDocument("doc_01", "July 2026 Advantage Checking Statement", "STATEMENTS", "2026-08-01", "1.2 MB", isRead = true),
            FinancialDocument("doc_02", "2025 Form 1099-INT Tax Statement", "TAX", "2026-01-31", "480 KB", isRead = true),
            FinancialDocument("doc_03", "Q2 2026 Managed Investment Summary", "INVESTMENT", "2026-07-05", "2.8 MB", isRead = false),
            FinancialDocument("doc_04", "Annual Mortgage Escrow Analysis 2026", "LOAN", "2026-06-12", "890 KB", isRead = true),
            FinancialDocument("doc_05", "Security Privacy & Regulation E Disclosure", "NOTICES", "2026-05-01", "340 KB", isRead = true)
        )
    }

    fun getInitialNotifications(): List<FinancialNotification> {
        return listOf(
            FinancialNotification("n_01", "Direct Deposit Received", "Your payroll deposit of $4,250.00 from Apex Employer Payroll was posted to Checking.", "2026-08-15 06:30 AM", "DEPOSIT", isRead = false),
            FinancialNotification("n_02", "Upcoming Bill Alert", "ConEdison Electric bill for $124.00 is scheduled for AutoPay on Aug 25.", "2026-08-16 09:00 AM", "PAYMENT", isRead = false),
            FinancialNotification("n_03", "Security Login Verification", "Successful biometric sign-in from iPhone 16 Pro in San Francisco, CA.", "2026-08-17 08:12 AM", "SECURITY", isRead = true),
            FinancialNotification("n_04", "Cashback Reward Activated", "5% Grocery cashback activated for Whole Foods purchases.", "2026-08-14 02:15 PM", "REWARD", isRead = true)
        )
    }

    fun getInitialAssistantMessages(): List<AssistantMessage> {
        return listOf(
            AssistantMessage(
                id = "m_01",
                text = "Hello Tom! I'm Erica®, your Bank of America virtual assistant. I can help you review account balances, transfer money with Zelle®, schedule bill payments, check your Preferred Rewards status, or manage your Merrill® investments. What can I do for you today?",
                sender = MessageSender.ASSISTANT,
                timestamp = "08:15 AM"
            )
        )
    }
}
