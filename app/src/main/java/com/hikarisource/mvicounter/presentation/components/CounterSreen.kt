package com.hikarisource.mvicounter.presentation.components

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hikarisource.mvicounter.presentation.theme.CountDownTheme
import com.hikarisource.mvicounter.presentation.viewmodel.CounterViewModel
import org.koin.compose.koinInject

@Composable
fun Countdown(
    modifier: Modifier = Modifier,
    viewModel: CounterViewModel = koinInject()
) {
    val state = viewModel.state
    val uiEvent = viewModel.uiEvent.collectAsState(CounterViewModel.CounterUiEvent.Nothing)

    when (uiEvent.value) {
        CounterViewModel.CounterUiEvent.BeginReached -> {
            Toast.makeText(LocalContext.current, "Begin reached!", Toast.LENGTH_SHORT).show()
        }

        CounterViewModel.CounterUiEvent.EndReached -> {
            Toast.makeText(LocalContext.current, "End reached!", Toast.LENGTH_SHORT).show()
        }

        CounterViewModel.CounterUiEvent.Nothing -> {}
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { viewModel.onEvent(CounterViewModel.CounterEvent.IncreaseAutomatically) },
            modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 16.dp),
            enabled = state.isIncreaseEnable
        ) {
            Text(
                text = "Increase Automatically",
                fontSize = 30.sp,
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { viewModel.onEvent(CounterViewModel.CounterEvent.Decrease) },
                modifier = Modifier.padding(0.dp, 0.dp, 16.dp, 0.dp),
                enabled = state.isDecreaseEnable
            ) {
                Text(
                    text = "-",
                    fontSize = 30.sp,
                )
            }

            Text(
                text = String.format("%02d", state.number),
                fontSize = 80.sp,
                modifier = Modifier.width(160.dp),
                textAlign = TextAlign.Center
            )

            Button(
                onClick = { viewModel.onEvent(CounterViewModel.CounterEvent.Increase) },
                modifier = Modifier.padding(16.dp, 0.dp, 0.dp, 0.dp),
                enabled = state.isIncreaseEnable
            ) {
                Text(
                    text = "+",
                    fontSize = 30.sp,
                )
            }
        }

        Button(
            onClick = { viewModel.onEvent(CounterViewModel.CounterEvent.DecreaseAutomatically) },
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 0.dp),
            enabled = state.isDecreaseEnable
        ) {
            Text(
                text = "Decrease Automatically",
                fontSize = 30.sp,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CountdownPreview() {
    CountDownTheme { Countdown(viewModel = CounterViewModel()) }
}