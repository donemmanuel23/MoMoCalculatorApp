package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview

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
        modifier = Modifier.padding(start = dimensionResource(R.dimen.spacing_small), top = dimensionResource(R.dimen.spacing_small))
      )
    }
  }
}

@Composable
fun MoMoCalcScreen(modifier: Modifier = Modifier) {
  var amountInput by remember { mutableStateOf("") }

  val numericAmount = amountInput.toDoubleOrNull()
  val isError = amountInput.isNotEmpty() && numericAmount == null
  val fee = (numericAmount ?: 0.0) * 0.03
  val formattedFee = "UGX %,.0f".format(fee)

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(dimensionResource(R.dimen.screen_padding)),
    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_large)),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Brand Heading
    Text(
      text = stringResource(R.string.app_title),
      style = MaterialTheme.typography.headlineMedium,
      color = MaterialTheme.colorScheme.primary,
      textAlign = TextAlign.Center
    )

    // Input Section
    HoistedAmountInput(
      amount = amountInput,
      onAmountChange = { amountInput = it },
      modifier = Modifier.fillMaxWidth(),
      isError = isError
    )

    // Result Section
    if (amountInput.isNotEmpty() && !isError) {
        FeeCard(formattedFee = formattedFee)
    } else {
        // Placeholder to keep layout stable or a hint
        Text(
            text = "Enter an amount to see the fee",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )
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
