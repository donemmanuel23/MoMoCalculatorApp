package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MoMoAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
          Scaffold(topBar = { MoMoTopBar() }) { innerPadding ->
            MoMoCalcScreen(modifier = Modifier.padding(innerPadding))
          }
        }
      }
    }
  }
}

@Composable
fun HoistedAmountInput(
  amount: String,
  onAmountChange: (String) -> Unit,
  onClear: () -> Unit,
  modifier: Modifier = Modifier,
  isError: Boolean = false
) {
  Column(modifier = modifier) {
    TextField(
      value = amount,
      onValueChange = onAmountChange,
      isError = isError,
      modifier = Modifier.fillMaxWidth(),
      shape = MaterialTheme.shapes.small,
      label = { Text(stringResource(R.string.enter_amount)) },
      keyboardOptions = KeyboardOptions(
          keyboardType = KeyboardType.Number,
          imeAction = ImeAction.Done
      ),
      trailingIcon = {
          if (amount.isNotEmpty()) {
              IconButton(onClick = onClear) {
                  Icon(
                      imageVector = Icons.Default.Clear,
                      contentDescription = stringResource(R.string.clear_input)
                  )
              }
          }
      },
      colors = TextFieldDefaults.colors(
          focusedContainerColor = MaterialTheme.colorScheme.surface,
          unfocusedContainerColor = MaterialTheme.colorScheme.surface,
          errorContainerColor = MaterialTheme.colorScheme.surface
      )
    )
    if (isError) {
      Text(
        text = stringResource(R.string.error_numbers_only),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall,
        modifier = Modifier.padding(
            start = dimensionResource(R.dimen.spacing_small),
            top = dimensionResource(R.dimen.spacing_small)
        )
      )
    }
  }
}

@Composable
fun MoMoCalcScreen(modifier: Modifier = Modifier) {
  var amountInput by remember { mutableStateOf("") }

  val numericAmount = amountInput.toDoubleOrNull()
  val isError = amountInput.isNotEmpty() && numericAmount == null
  
  val amountVal = numericAmount ?: 0.0
  val feeVal = amountVal * 0.03
  val totalVal = amountVal + feeVal

  val formattedAmount = "UGX %,.0f".format(amountVal)
  val formattedFee = "UGX %,.0f".format(feeVal)
  val formattedTotal = "UGX %,.0f".format(totalVal)

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(dimensionResource(R.dimen.screen_padding)),
    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large)),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Money Hero Visual using Emoji (No dependency required)
    Text(
        text = "💸",
        fontSize = 64.sp,
        modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
    )

    // Specific Screen Title
    Text(
      text = stringResource(R.string.screen_title),
      style = MaterialTheme.typography.headlineSmall,
      fontWeight = FontWeight.Bold,
      color = MaterialTheme.colorScheme.primary,
      textAlign = TextAlign.Center
    )

    // Input Section
    HoistedAmountInput(
      amount = amountInput,
      onAmountChange = { amountInput = it },
      onClear = { amountInput = "" },
      modifier = Modifier.fillMaxWidth(),
      isError = isError
    )

    // Result Section with Animation
    AnimatedVisibility(
        visible = amountInput.isNotEmpty() && !isError,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 2 })
    ) {
        FeeCard(
            amount = formattedAmount,
            fee = formattedFee,
            total = formattedTotal
        )
    }

    if (amountInput.isEmpty() || isError) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = if (isError) "" else "Enter an amount to see the fee",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
        }
    }
  }
}

@Preview(showBackground = true)
@Composable
fun MoMoCalcPreview() {
  MoMoAppTheme {
    Surface {
      MoMoCalcScreen()
    }
  }
}
