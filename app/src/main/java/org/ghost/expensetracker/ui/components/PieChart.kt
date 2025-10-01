package org.ghost.expensetracker.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

data class ChartItem(
    val label: String,
    val amount: Double,
    val color: Color,
)

data class PieChartUiState(
    val data: List<ChartItem>,
    val isLoading: Boolean,
    val isAnimating: Boolean,
)

@Composable
fun PieChart(
    modifier: Modifier = Modifier,
    data: List<ChartItem>,
    backgroundColor: Color = MaterialTheme.colorScheme.background,
) {
    val isRotating = remember(data) { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isRotating.value) 360f else 0f,
        animationSpec = tween(
            durationMillis = 1080,
            easing = LinearOutSlowInEasing
        )
    )

    val sweepAngleAnimatables = remember(data) {
        data.map { Animatable(0f) }
    }

    val totalAmount: Double = data.sumOf { it.amount }

    var startAngle = 0f

    LaunchedEffect(data) {
        // This ensures the animation re-runs every time the data changes.
        isRotating.value = false
        isRotating.value = true

        sweepAngleAnimatables.forEachIndexed { index, animatable ->
            launch {
                animatable.animateTo(
                    targetValue = 1f, // Animate progress from 0 to 1
                    animationSpec = tween(
                        durationMillis = 400, // Duration for each individual slice
                        delayMillis = index * 250 // Stagger the start of each animation
                    )
                )
            }
        }
    }


    Box(
        modifier = modifier.rotate(rotation)
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize(0.8f)
                .align(Alignment.Center)
        ) {
            data.forEachIndexed { index, item ->
                val angle = (item.amount / totalAmount).toFloat() * 360f
                // 3. The sweep angle for each arc is now its own animated value.
                val animatedSweepAngle = angle * sweepAngleAnimatables[index].value

                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = animatedSweepAngle,
                    useCenter = false,
                    style = Stroke(width = 80f)
                )
                startAngle += angle
            }

        }

    }


}

@Preview
@Composable
fun PieChartPreview() {
    val sampleData = listOf(
        ChartItem(label = "Groceries", amount = 150.0, color = Color.Red),
        ChartItem(label = "Entertainment", amount = 100.0, color = Color.Blue),
        ChartItem(label = "Utilities", amount = 75.0, color = Color.Green),
        ChartItem(label = "Transportation", amount = 50.0, color = Color.Yellow)
    )
    val uiState = PieChartUiState(
        data = sampleData,
        isLoading = false,
        isAnimating = true
    )
    PieChart(
        modifier = Modifier.size(300.dp),
        data = uiState.data
    )
}




