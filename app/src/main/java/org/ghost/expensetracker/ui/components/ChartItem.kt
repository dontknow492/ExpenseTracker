package org.ghost.expensetracker.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.patrykandpatrick.vico.compose.cartesian.CartesianChartHost
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberAxisLabelComponent
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberBottom
import com.patrykandpatrick.vico.compose.cartesian.axis.rememberStart
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberColumnCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLine
import com.patrykandpatrick.vico.compose.cartesian.layer.rememberLineCartesianLayer
import com.patrykandpatrick.vico.compose.cartesian.rememberCartesianChart
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoScrollState
import com.patrykandpatrick.vico.compose.cartesian.rememberVicoZoomState
import com.patrykandpatrick.vico.compose.common.component.rememberLineComponent
import com.patrykandpatrick.vico.compose.common.fill
import com.patrykandpatrick.vico.compose.common.shader.verticalGradient
import com.patrykandpatrick.vico.core.cartesian.CartesianMeasuringContext
import com.patrykandpatrick.vico.core.cartesian.Scroll
import com.patrykandpatrick.vico.core.cartesian.Zoom
import com.patrykandpatrick.vico.core.cartesian.axis.HorizontalAxis
import com.patrykandpatrick.vico.core.cartesian.axis.VerticalAxis
import com.patrykandpatrick.vico.core.cartesian.data.CartesianChartModelProducer
import com.patrykandpatrick.vico.core.cartesian.data.CartesianLayerRangeProvider
import com.patrykandpatrick.vico.core.cartesian.data.columnSeries
import com.patrykandpatrick.vico.core.cartesian.data.lineSeries
import com.patrykandpatrick.vico.core.cartesian.layer.ColumnCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.layer.LineCartesianLayer
import com.patrykandpatrick.vico.core.cartesian.marker.DefaultCartesianMarker
import com.patrykandpatrick.vico.core.common.shader.ShaderProvider
import org.ghost.expensetracker.data.models.ExpenseChartData
import org.ghost.expensetracker.ui.sample.compose.rememberMarker
import java.text.DecimalFormat


data class GraphItemState(
    val title: String,
    val amountString: String,
    val filter: String,
    val filters: List<String>,
    val isError: Boolean,
    val isLoading: Boolean,
)

@Composable
fun GraphItem(
    modifier: Modifier = Modifier,
    state: GraphItemState,
    onFilterChange: (String) -> Unit,
    graph: @Composable () -> Unit
) {

    val defaultColor = CardDefaults.cardColors().containerColor

    val color = if (state.isError) {
        MaterialTheme.colorScheme.errorContainer
    } else {
        defaultColor
    }

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color),
    ) {
        Row(
            modifier = Modifier
                .padding(start = 16.dp, top = 8.dp, end = 16.dp, bottom = 0.dp)
                .offset(y = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = state.title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = state.amountString,
                    style = MaterialTheme.typography.headlineLarge
                )

            }
            DropDownButton(
                filter = state.filter,
                filters = state.filters,
                onFilterChange = onFilterChange
            )

        }
        graph()

    }

}

@Composable
fun BarChart(
    modifier: Modifier = Modifier,
    expenseData: List<ExpenseChartData>
) {
    val modelProducer = remember { CartesianChartModelProducer() }

    val labels = remember(expenseData) {
        expenseData.map { it.label ?: "??" }
    }

    // FIX 1: Use `expenseData` as the key to update the chart when data changes.
    LaunchedEffect(expenseData) {
        if (expenseData.isNotEmpty()) {
            modelProducer.runTransaction {
                columnSeries {
                    series(
                        expenseData.map { it.totalAmount }
                    )
                }
            }
        }
    }

    // Return early if there's no data to prevent chart from showing empty state.
    if (expenseData.isEmpty()) {
        // You can place a placeholder Composable here, like Text("No data available").
        Text("No data available")
        return
    }

    CartesianChartHost(
        chart =
            rememberCartesianChart(
                rememberColumnCartesianLayer(
                    columnProvider = ColumnCartesianLayer.ColumnProvider.series(
                        rememberLineComponent(
                            fill = fill(MaterialTheme.colorScheme.primary),
                            thickness = 16.dp
                        ),
                    )
                ),
                // FIX 3: Configure axes with value formatters for custom labels.
                startAxis = VerticalAxis.rememberStart(
                    valueFormatter = { value, chart_value, _ -> chart_value.toString() },
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.primary
                    )
                ),
                topAxis = HorizontalAxis.top(

                ),
                bottomAxis = HorizontalAxis.rememberBottom(
                    valueFormatter = { value, double_value, _ ->
                        labels.getOrNull(double_value.toInt()) ?: "??"
                    },
                    label = rememberAxisLabelComponent(
                        color = MaterialTheme.colorScheme.primary
                    )
                ),
            ),
        modelProducer = modelProducer,
        modifier = modifier,
        scrollState = rememberVicoScrollState()
    )
}


@Composable
fun LineChart(modifier: Modifier = Modifier, expense: List<ExpenseChartData>) {
    if (expense.isEmpty()) {
        Text("No data available")
        return
    }
    val labels = remember(expense) {
        Log.d("LineChart", "labels: ${expense.size}")
        expense.map { it.label ?: "" }
    }

    remember(expense) {
        expense.mapIndexed { index, data -> index.toFloat() to (data.label ?: "") }.toMap()
    }


    val modelProducer = remember { CartesianChartModelProducer() }

    val RangeProvider = CartesianLayerRangeProvider.auto()
    val YDecimalFormat = DecimalFormat("#.##'%'")
    val MarkerValueFormatter = DefaultCartesianMarker.ValueFormatter.default(YDecimalFormat)

    LaunchedEffect(expense) {
        modelProducer.runTransaction {
            lineSeries {
                series(
                    expense.map { it.totalAmount }
                )
            }
        }
    }

    val lineColor = MaterialTheme.colorScheme.primary
    CartesianChartHost(
        rememberCartesianChart(
            rememberLineCartesianLayer(
                lineProvider =
                    LineCartesianLayer.LineProvider.series(
                        LineCartesianLayer.rememberLine(
                            fill = LineCartesianLayer.LineFill.single(fill(lineColor)),
                            areaFill =
                                LineCartesianLayer.AreaFill.single(
                                    fill(
                                        ShaderProvider.verticalGradient(
                                            arrayOf(lineColor.copy(alpha = 0.4f), Color.Transparent)
                                        )
                                    )
                                ),
                        )
                    ),
                rangeProvider = RangeProvider,
            ),
            startAxis = VerticalAxis.rememberStart(),
            bottomAxis = HorizontalAxis.rememberBottom(
                valueFormatter = { cartesianValue: CartesianMeasuringContext, doubleValue, _ ->

//                     "fuck off"
                    labels.getOrNull(doubleValue.toInt()) ?: " "
                }
            ),
            marker = rememberMarker(MarkerValueFormatter),
        ),
        modelProducer,
        modifier.height(220.dp),
        rememberVicoScrollState(
            scrollEnabled = true,
            initialScroll = Scroll.Absolute.End,
        ),
        zoomState = rememberVicoZoomState(
            zoomEnabled = true,
            initialZoom = Zoom.Content,
        ),
        animateIn = true
    )
}




