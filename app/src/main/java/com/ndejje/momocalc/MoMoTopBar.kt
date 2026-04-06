package com.ndejje.momocalc

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoMoTopBar() {
  CenterAlignedTopAppBar(
    title = {
      Text(
        text = stringResource(R.string.app_title),
        style = MaterialTheme.typography.headlineMedium
      )
    },
    navigationIcon = {
      Image(
        painter = painterResource(id = R.drawable.ic_momo_logo),
        contentDescription = "MoMo Logo",
        modifier = Modifier
          .padding(start = dimensionResource(R.dimen.spacing_medium))
          .height(32.dp)
          .wrapContentWidth(),
        contentScale = ContentScale.Fit
      )
    },
    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
      containerColor = MaterialTheme.colorScheme.primary,
      titleContentColor = MaterialTheme.colorScheme.onPrimary,
      navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
    )
  )
}
