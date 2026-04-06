package com.ndejje.momocalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.ndejje.momocalc.ui.theme.MoMoCalculatorAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MoMoCalculatorAppTheme {

        }
      }
    }
  }


@Composable
fun BrokenInput (){
var amount = "0"

  TextField(
    value = amount,
    onValueChange = { amount = it },
    label = {
      Text(stringResource(R.string.enter_amount))
    },
  )
}

@Composable
fun InternalStateInput (){
  var amount by remember { mutableStateOf("50000") }
  TextField(
    value = amount,
    onValueChange = { amount = it },
    label = {
      Text(stringResource(R.string.enter_amount))
    },
  )
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
      label = { Text(stringResource(R.string.enter_amount)) }
    )
    if (isError) {
      Text(
        text = stringResource(R.string.error_numbers_only),
        color = MaterialTheme.colorScheme.error,
        style = MaterialTheme.typography.bodySmall
      )
    }
  }
}
