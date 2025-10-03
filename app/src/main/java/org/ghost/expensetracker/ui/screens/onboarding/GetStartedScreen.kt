package org.ghost.expensetracker.ui.screens.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.ghost.expensetracker.R
import org.ghost.expensetracker.ui.theme.Seed

//@OptIn(ExperimentalMaterial3Api::class)
@Preview(showSystemUi = !true)
@Composable
fun GetStartedScreen(
    modifier: Modifier = Modifier,
    onOnboardingFinished: () -> Unit = {},
) {
    val contentColor = Color.Black
    Scaffold(
        modifier = modifier,
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Seed)
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.Bottom,
        ) {
            Icon(
                painter = painterResource(R.drawable.rounded_background_dot_large_24),
                contentDescription = "Back",
                modifier = modifier
                    .size(48.dp),
                tint = contentColor
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(R.string.display),
                style = MaterialTheme.typography.displayLarge,
                color = contentColor
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(R.string.get_started_body),
                style = MaterialTheme.typography.bodyLarge,
                color = contentColor
            )
            Spacer(Modifier.height(60.dp))
            Button(
                onClick = onOnboardingFinished,
                modifier = Modifier.fillMaxWidth()
            ){
                Text(
                    text = stringResource(R.string.get_started),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }
        }
    }
}