package com.ndejje.momocalc

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

enum class Network { MTN, AIRTEL }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoMoAppTheme {
                MoMoApp()
            }
        }
    }
}

@Composable
fun MoMoApp() {
    var selectedNetwork by remember { mutableStateOf(Network.MTN) }
    val backgroundColor by animateColorAsState(
        if (selectedNetwork == Network.MTN) MtnYellow.copy(alpha = 0.05f) 
        else AirtelRed.copy(alpha = 0.05f), label = "bg"
    )

    Surface(modifier = Modifier.fillMaxSize(), color = backgroundColor) {
        Scaffold(
            topBar = { MoMoTopBar() }
        ) { innerPadding ->
            MoMoCalcScreen(
                network = selectedNetwork,
                onNetworkChange = { selectedNetwork = it },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
fun MoMoCalcScreen(
    network: Network,
    onNetworkChange: (Network) -> Unit,
    modifier: Modifier = Modifier
) {
    var amountInput by remember { mutableStateOf("") }
    var isMaxMode by remember { mutableStateOf(false) }
    val history = remember { mutableStateListOf<String>() }
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val numericAmount = amountInput.toDoubleOrNull() ?: 0.0
    val isError = amountInput.isNotEmpty() && amountInput.toDoubleOrNull() == null

    // Logic for Tiered Fees
    val feeVal = if (isMaxMode) 0.0 else calculateFee(numericAmount, network)
    val amountToReceive = if (isMaxMode) calculateMaxCash(numericAmount, network) else numericAmount
    val totalDeduction = if (isMaxMode) numericAmount else (numericAmount + feeVal)
    val displayFee = if (isMaxMode) (numericAmount - amountToReceive) else feeVal

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Network Switcher
        NetworkToggle(network, onNetworkChange)

        // 2. Money Visual
        Text(text = if (network == Network.MTN) "🟡" else "🔴", fontSize = 40.sp)

        // 3. Max Withdrawal Switch
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(stringResource(R.string.max_withdrawal_label), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
                Text(stringResource(R.string.max_withdrawal_desc), style = MaterialTheme.typography.bodySmall)
            }
            Spacer(Modifier.width(16.dp))
            Switch(
                checked = isMaxMode,
                onCheckedChange = { isMaxMode = it; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
                colors = SwitchDefaults.colors(checkedThumbColor = if (network == Network.MTN) Color.Black else Color.White)
            )
        }

        // 4. Input Field with Live Comma Formatting
        HoistedAmountInput(
            amount = amountInput,
            onAmountChange = { amountInput = it.take(9) },
            onClear = { amountInput = ""; haptic.performHapticFeedback(HapticFeedbackType.LongPress) },
            label = if (isMaxMode) "Enter Total Account Balance" else stringResource(R.string.enter_amount),
            network = network
        )

        // 5. Results Section
        AnimatedVisibility(
            visible = amountInput.isNotEmpty() && !isError && numericAmount >= 500,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FeeCard(
                    amount = formatCurrency(amountToReceive),
                    fee = formatCurrency(displayFee),
                    total = formatCurrency(totalDeduction)
                )
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = {
                        val shareMsg = context.getString(R.string.share_text, formatCurrency(amountToReceive), formatCurrency(displayFee), formatCurrency(totalDeduction))
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareMsg)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share via"))
                        if (!history.contains(amountInput)) history.add(0, amountInput)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (network == Network.MTN) MtnYellow else AirtelRed, contentColor = Color.Black)
                ) {
                    Text("Share Breakdown")
                }
            }
        }

        // 6. Recent History
        if (history.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.recent_calculations), style = MaterialTheme.typography.titleSmall)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history.take(3)) { item ->
                    Card(
                        onClick = { amountInput = item },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(formatCurrency(item.toDoubleOrNull() ?: 0.0), modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
fun NetworkToggle(current: Network, onNetworkChange: (Network) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NetworkButton("MTN", current == Network.MTN, MtnYellow) { onNetworkChange(Network.MTN) }
        NetworkButton("Airtel", current == Network.AIRTEL, AirtelRed) { onNetworkChange(Network.AIRTEL) }
    }
}

@Composable
fun NetworkButton(label: String, selected: Boolean, color: Color, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (selected) color else Color.LightGray.copy(alpha = 0.2f))
    ) {
        Text(label, modifier = Modifier.padding(8.dp).fillMaxWidth(), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HoistedAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    onClear: () -> Unit,
    label: String,
    network: Network
) {
    TextField(
        value = amount,
        onValueChange = { if (it.all { char -> char.isDigit() }) onAmountChange(it) },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        shape = MaterialTheme.shapes.small,
        trailingIcon = {
            if (amount.isNotEmpty()) {
                IconButton(onClick = onClear) { Icon(Icons.Default.Clear, contentDescription = null) }
            }
        },
        visualTransformation = CurrencyVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = if (network == Network.MTN) MtnYellow else AirtelRed,
            focusedLabelColor = if (network == Network.MTN) Color.Black else AirtelRed
        )
    )
}

// --- UTILS ---

fun formatCurrency(amount: Double): String {
    val locale = Locale.Builder().setLanguage("en").setRegion("UG").build()
    val formatter = NumberFormat.getCurrencyInstance(locale)
    return formatter.format(amount).replace("UGX", "UGX ")
}

class CurrencyVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        if (originalText.isEmpty()) return TransformedText(text, OffsetMapping.Identity)
        
        val formatted = NumberFormat.getInstance(Locale.US).format(originalText.toLongOrNull() ?: 0L)
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val textBefore = originalText.substring(0, offset)
                val formattedBefore = NumberFormat.getInstance(Locale.US).format(textBefore.toLongOrNull() ?: 0L)
                return if (textBefore.isEmpty()) 0 else formattedBefore.length
            }
            override fun transformedToOriginal(offset: Int): Int {
                // Simplified mapping: just return the length to keep it safe
                return originalText.length 
            }
        }
        return TransformedText(AnnotatedString(formatted), offsetMapping)
    }
}

fun calculateFee(amount: Double, network: Network): Double {
    // Current tiered logic is shared by both networks. 
    // You can adjust these values if MTN and Airtel fees differ.
    return when (amount.toInt()) {
        in 500..2500 -> 330.0
        in 2501..5000 -> 440.0
        in 5001..15000 -> 700.0
        in 15001..30000 -> 880.0
        in 30001..45000 -> 1210.0
        in 45001..60000 -> 1500.0
        in 60001..125000 -> 1925.0
        in 125001..250000 -> 3575.0
        in 250001..500000 -> 7000.0
        in 500001..1000000 -> 12500.0
        in 1000001..2000000 -> 15000.0
        in 2000001..4000000 -> 18000.0
        else -> if (amount > 4000000) 20000.0 else 0.0
    }
}

fun calculateMaxCash(totalBalance: Double, network: Network): Double {
    // Finds the largest amount X such that X + Fee(X) <= totalBalance
    var low = 0.0
    var high = totalBalance
    var ans = 0.0
    while (low <= high) {
        val mid = (low + high) / 2
        if (mid + calculateFee(mid, network) <= totalBalance) {
            ans = mid
            low = mid + 1
        } else {
            high = mid - 1
        }
    }
    return ans
}
