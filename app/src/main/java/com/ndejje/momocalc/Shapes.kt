package com.ndejje.momocalc

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

val MoMoShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),   // chips, tooltips
    small      = RoundedCornerShape(8.dp),   // TextField, buttons
    medium     = RoundedCornerShape(16.dp),  // cards, dialogs
    large      = RoundedCornerShape(24.dp),  // bottom sheets
    extraLarge = RoundedCornerShape(28.dp)   // FABs, hero containers
)

@Composable
fun FeeCard(formattedFee: String, modifier: Modifier = Modifier) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.15f),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.fee_label, formattedFee),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "Fee Card – Light", showBackground = true)
@Preview(
    name = "Fee Card – Dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun PreviewFeeCard() {
    MoMoAppTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            FeeCard(formattedFee = "UGX 1,500")
        }
    }
}
