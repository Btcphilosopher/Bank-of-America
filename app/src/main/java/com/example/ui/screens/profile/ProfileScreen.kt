package com.example.ui.screens.profile

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.FinancialDocument
import com.example.data.repository.ApexRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ProfileScreen(repository: ApexRepository) {
    var selectedProfileTab by remember { mutableStateOf(0) } // 0: Card Controls, 1: Credit Score, 2: Security, 3: Documents, 4: ATM Finder

    val isCardLocked by repository.isCardLocked.collectAsState()
    val isBiometricEnabled by repository.isBiometricEnabled.collectAsState()
    val isTwoFactorEnabled by repository.isTwoFactorEnabled.collectAsState()
    val creditScore by repository.creditScore.collectAsState()
    val documents by repository.documents.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        ScrollableTabRow(
            selectedTabIndex = selectedProfileTab,
            edgePadding = 0.dp,
            containerColor = Color.Transparent,
            contentColor = BankBlue,
            divider = {}
        ) {
            listOf("Cards", "Credit Score", "Security", "Documents", "ATM / Branch").forEachIndexed { index, title ->
                Tab(
                    selected = selectedProfileTab == index,
                    onClick = { selectedProfileTab = index },
                    text = { Text(title, fontWeight = FontWeight.Bold) }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (selectedProfileTab) {
            0 -> CardControlsView(isCardLocked, onToggleLock = { repository.toggleCardLock() })
            1 -> CreditScoreView(creditScore)
            2 -> SecurityCenterView(isBiometricEnabled, isTwoFactorEnabled, onToggleBio = { repository.setBiometric(it) }, onToggle2FA = { repository.setTwoFactor(it) })
            3 -> DocumentsView(documents)
            4 -> AtmBranchView()
        }
    }
}

// -------------------------------------------------------------
// 1. CARDS MANAGEMENT VIEW
// -------------------------------------------------------------
@Composable
private fun CardControlsView(isLocked: Boolean, onToggleLock: () -> Unit) {
    var showDigitalCardNumber by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Digital Card Visualizer
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                listOf(BofARed, BofADeepRed, BofANavy)
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("BANK OF AMERICA", style = MaterialTheme.typography.labelMedium.copy(color = Color.White, letterSpacing = 1.sp, fontWeight = FontWeight.ExtraBold))
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = PreferredGold.copy(alpha = 0.3f)
                                ) {
                                    Text("PLATINUM HONORS", modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 8.sp, fontWeight = FontWeight.Bold, color = PreferredGold))
                                }
                            }
                            Icon(Icons.Default.Contactless, contentDescription = null, tint = PreferredGold)
                        }

                        Text(
                            text = if (showDigitalCardNumber) "4532  8910  4421  7741" else "••••  ••••  ••••  7741",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold, color = Color.White, letterSpacing = 2.sp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CARDHOLDER", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp))
                                Text("TOM A. MITCHELL", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                            }
                            Column {
                                Text("EXPIRES", style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp))
                                Text("08/29", style = MaterialTheme.typography.bodyMedium.copy(color = Color.White, fontWeight = FontWeight.Bold))
                            }
                            Text("VISA", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold, color = Color.White))
                        }
                    }
                }
            }
        }

        // Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(if (isLocked) Icons.Default.Lock else Icons.Default.LockOpen, contentDescription = null, tint = if (isLocked) NegativeRed else PositiveGreen)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Lock Card", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text(if (isLocked) "Card is currently locked against new charges" else "Card is unlocked and active", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                        Switch(checked = isLocked, onCheckedChange = { onToggleLock() })
                    }

                    Divider()

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDigitalCardNumber = !showDigitalCardNumber },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = BankBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Show Digital Card Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.FlightTakeoff, contentDescription = null, tint = BankBlue)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Set Travel Notice", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. CREDIT SCORE VIEW
// -------------------------------------------------------------
@Composable
private fun CreditScoreView(creditScore: com.example.data.models.CreditScoreData) {
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
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("FICO CREDIT SCORE", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("${creditScore.score}", style = MaterialTheme.typography.headlineLarge.copy(fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = PositiveGreen))
                    Text("RATING: ${creditScore.rating}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PositiveGreen))

                    Spacer(modifier = Modifier.height(16.dp))
                    ClipProgressMeter(progress = (creditScore.score - 300) / 550f, color = PositiveGreen)
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("KEY CREDIT FACTORS", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    Text("Credit Utilization: ${creditScore.creditUtilizationPct}% (Excellent - under 30%)")
                    Text("Payment History: ${creditScore.paymentHistoryPct}% On-time")
                    Text("Average Account Age: ${creditScore.averageAccountAgeYears} years")
                    Text("Total Recent Inquiries: ${creditScore.totalInquiries}")
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. SECURITY CENTER VIEW
// -------------------------------------------------------------
@Composable
private fun SecurityCenterView(
    isBio: Boolean,
    is2FA: Boolean,
    onToggleBio: (Boolean) -> Unit,
    onToggle2FA: (Boolean) -> Unit
) {
    var showFraudAlertDemo by remember { mutableStateOf(false) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = CardBorder()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SECURITY STATUS: STRONG (94/100)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PositiveGreen))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Encrypted token vault active. Hardware biometric key installed.")
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
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Biometric Authentication (FaceID / Fingerprint)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Switch(checked = isBio, onCheckedChange = onToggleBio)
                    }

                    Divider()

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Two-Factor Authentication (2FA)", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Switch(checked = is2FA, onCheckedChange = onToggle2FA)
                    }

                    Divider()

                    Button(
                        onClick = { showFraudAlertDemo = true },
                        colors = ButtonDefaults.buttonColors(containerColor = NegativeRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Simulate Suspicious Fraud Alert")
                    }
                }
            }
        }
    }

    if (showFraudAlertDemo) {
        AlertDialog(
            onDismissRequest = { showFraudAlertDemo = false },
            title = { Text("SUSPICIOUS TRANSACTION DETECTED") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Amount: $842.17 at Online Merchant Electronics")
                    Text("Location: Unknown IP Address")
                    Text("Was this transaction made by you?")
                }
            },
            confirmButton = {
                Button(onClick = { showFraudAlertDemo = false }) {
                    Text("THIS WAS ME")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showFraudAlertDemo = false },
                    colors = ButtonDefaults.buttonColors(containerColor = NegativeRed)
                ) {
                    Text("THIS WAS NOT ME (FREEZE CARD)")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// 4. DOCUMENTS VIEW
// -------------------------------------------------------------
@Composable
private fun DocumentsView(documents: List<FinancialDocument>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(documents) { doc ->
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PictureAsPdf, contentDescription = null, tint = NegativeRed)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(doc.title, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                            Text("${doc.date} • ${doc.fileSize}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.Download, contentDescription = "Download")
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 5. ATM & BRANCH LOCATOR
// -------------------------------------------------------------
@Composable
private fun AtmBranchView() {
    val locations = listOf(
        Triple("Apex Financial Center - SF Downtown", "555 California St, San Francisco, CA", "0.3 mi • Open until 5:00 PM"),
        Triple("Market Street 24h ATM", "788 Market St, San Francisco, CA", "0.6 mi • 24/7 Drive-Thru ATM"),
        Triple("Financial District Branch", "101 Montgomery St, San Francisco, CA", "0.9 mi • Open until 6:00 PM")
    )

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        items(locations) { loc ->
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
                        Text(loc.first, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                        Text(loc.second, style = MaterialTheme.typography.bodySmall)
                        Text(loc.third, style = MaterialTheme.typography.bodySmall, color = PositiveGreen)
                    }
                    Button(onClick = {}, shape = RoundedCornerShape(8.dp)) {
                        Text("Directions")
                    }
                }
            }
        }
    }
}
