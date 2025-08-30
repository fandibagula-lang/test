package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import com.aay.compose.barChart.BarChart
import com.aay.compose.barChart.model.BarParameters
import com.aay.compose.baseComponents.model.GridOrientation
import com.aay.compose.donutChart.DonutChart
import com.aay.compose.donutChart.PieChart
import com.aay.compose.donutChart.model.PieChartData
import com.aay.compose.lineChart.LineChart
import com.aay.compose.lineChart.model.LineParameters
import com.aay.compose.lineChart.model.LineType
import com.aay.compose.radarChart.RadarChart
import com.aay.compose.radarChart.model.NetLinesStyle
import com.aay.compose.radarChart.model.Polygon
import com.aay.compose.radarChart.model.PolygonStyle
import org.babetech.borastock.ui.screens.dashboard.viewmodel.GraphicsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

data class ChartType(
    val key: String,
    val title: String,
    val icon: Painter,
    val description: String
)

@Composable
fun GraphicSwitcherScreen(
    viewModel: GraphicsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedChart by remember { mutableStateOf("Line") }

    val chartTypes = listOf(
        ChartType("Line", "Évolution", painterResource(Res.drawable.analytics), "Tendances temporelles"),
        ChartType("Bar", "Stock Critique", painterResource(Res.drawable.barchart), "Alertes stock"),
        ChartType("Pie", "Répartition", painterResource(Res.drawable.piechart), "Par catégorie"),
        ChartType("Donut", "Distribution", painterResource(Res.drawable.donutlarge), "Vue d'ensemble"),
        ChartType("Radar", "Fournisseurs", painterResource(Res.drawable.analytics), "Performance")
    )

    Column(modifier = Modifier.fillMaxSize()) {
        // Chart type selector
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chartTypes) { chartType ->
                ElevatedCard(
                    onClick = { selectedChart = chartType.key },
                    modifier = Modifier.width(140.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = if (selectedChart == chartType.key) 
                            MaterialTheme.colorScheme.primaryContainer 
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = chartType.icon,
                            contentDescription = chartType.title,
                            tint = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            chartType.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = if (selectedChart == chartType.key) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            chartType.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        // Chart display area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            when (uiState) {
                is GraphicsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is GraphicsUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                "Erreur de chargement",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                uiState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Button(
                                onClick = { viewModel.loadChartData() },
                                modifier = Modifier.padding(top = 8.dp)
                            ) {
                                Text("Réessayer")
                            }
                        }
                    }
                }
                is GraphicsUiState.Success -> {
                    AnimatedVisibility(
                        visible = true,
                        enter = fadeIn() + slideInVertically()
                    ) {
                        when (selectedChart) {
                            "Line" -> DynamicLineChart(uiState.stockEvolutionData)
                            "Bar" -> DynamicBarChart(uiState.lowStockData)
                            "Pie" -> DynamicPieChart(uiState.categoryDistributionData)
                            "Donut" -> DynamicDonutChart(uiState.categoryDistributionData)
                            "Radar" -> DynamicRadarChart(uiState.supplierPerformanceData)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DynamicLineChart(data: List<ChartDataPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée d'évolution disponible")
        return
    }

    val lineParameters = listOf(
        LineParameters(
            label = "Valeur du stock",
            data = data.map { it.value },
            lineColor = Color(0xFF3B82F6),
            lineType = LineType.CURVED_LINE,
            lineShadow = true
        )
    )

    LineChart(
        modifier = Modifier.fillMaxSize(),
        linesParameters = lineParameters,
        isGrid = true,
        gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        xAxisData = data.map { it.label },
        animateChart = true,
        showGridWithSpacer = true,
        yAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        xAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W400
        ),
        yAxisRange = 10,
        oneLineChart = true,
        gridOrientation = GridOrientation.VERTICAL
    )
}

@Composable
private fun DynamicBarChart(data: List<BarChartDataPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucun article en stock critique")
        return
    }

    val barParameters = listOf(
        BarParameters(
            dataName = "Stock actuel",
            data = data.map { it.value },
            barColor = Color(0xFFEF4444)
        ),
        BarParameters(
            dataName = "Stock minimum",
            data = data.map { it.minValue },
            barColor = Color(0xFFF59E0B)
        )
    )

    BarChart(
        chartParameters = barParameters,
        gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
        xAxisData = data.map { it.label },
        isShowGrid = true,
        animateChart = true,
        showGridWithSpacer = true,
        yAxisStyle = TextStyle(
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        xAxisStyle = TextStyle(
            fontSize = 10.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.W400
        ),
        yAxisRange = 10,
        barWidth = 20.dp
    )
}

@Composable
private fun DynamicPieChart(data: List<PieChartDataPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de répartition disponible")
        return
    }

    val pieChartData = data.map { point ->
        PieChartData(
            partName = point.label,
            data = point.value,
            color = point.color
        )
    }

    PieChart(
        modifier = Modifier.fillMaxSize(),
        pieChartData = pieChartData,
        ratioLineColor = MaterialTheme.colorScheme.outline,
        textRatioStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp
        )
    )
}

@Composable
private fun DynamicDonutChart(data: List<PieChartDataPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de distribution disponible")
        return
    }

    val pieChartData = data.map { point ->
        PieChartData(
            partName = point.label,
            data = point.value,
            color = point.color
        )
    }

    val totalValue = data.sumOf { it.value }

    DonutChart(
        modifier = Modifier.fillMaxSize(),
        pieChartData = pieChartData,
        centerTitle = "€${String.format("%.0f", totalValue)}",
        centerTitleStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        outerCircularColor = MaterialTheme.colorScheme.outline,
        innerCircularColor = MaterialTheme.colorScheme.surfaceVariant,
        ratioLineColor = MaterialTheme.colorScheme.outline
    )
}

@Composable
private fun DynamicRadarChart(data: List<RadarChartDataPoint>) {
    if (data.isEmpty()) {
        EmptyChartPlaceholder("Aucune donnée de performance disponible")
        return
    }

    val radarLabels = listOf("Valeur", "Délai", "Qualité", "Fiabilité", "Volume")
    val polygons = data.mapIndexed { index, supplier ->
        Polygon(
            values = supplier.values,
            unit = "",
            style = PolygonStyle(
                fillColor = getSupplierColor(index),
                fillColorAlpha = 0.3f,
                borderColor = getSupplierColor(index),
                borderColorAlpha = 0.8f,
                borderStrokeWidth = 2f,
                borderStrokeCap = StrokeCap.Round
            )
        )
    }

    RadarChart(
        modifier = Modifier.fillMaxSize(),
        radarLabels = radarLabels,
        labelsStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        ),
        netLinesStyle = NetLinesStyle(
            netLineColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
            netLinesStrokeWidth = 1f,
            netLinesStrokeCap = StrokeCap.Round
        ),
        scalarSteps = 3,
        scalarValue = 5.0,
        scalarValuesStyle = TextStyle(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 10.sp
        ),
        polygons = polygons
    )
}

@Composable
private fun EmptyChartPlaceholder(message: String) {
    Card(
        modifier = Modifier.fillMaxSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.analytics),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun getSupplierColor(index: Int): Color {
    val colors = listOf(
        Color(0xFF3B82F6),
        Color(0xFF10B981),
        Color(0xFFF59E0B),
        Color(0xFFEF4444),
        Color(0xFF8B5CF6)
    )
    return colors[index % colors.size]
}

// Compact chart for dashboard preview
@Composable
fun CompactStockChart(
    viewModel: GraphicsViewModel = koinViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Évolution du stock",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    painter = painterResource(Res.drawable.analytics),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }

            when (uiState) {
                is GraphicsUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    }
                }
                is GraphicsUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Erreur de chargement",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is GraphicsUiState.Success -> {
                    if (uiState.stockEvolutionData.isNotEmpty()) {
                        val lineParameters = listOf(
                            LineParameters(
                                label = "Stock",
                                data = uiState.stockEvolutionData.takeLast(7).map { it.value },
                                lineColor = MaterialTheme.colorScheme.primary,
                                lineType = LineType.CURVED_LINE,
                                lineShadow = false
                            )
                        )

                        LineChart(
                            modifier = Modifier.fillMaxSize(),
                            linesParameters = lineParameters,
                            isGrid = false,
                            xAxisData = uiState.stockEvolutionData.takeLast(7).map { 
                                it.label.substring(5) // Show MM-DD
                            },
                            animateChart = true,
                            showGridWithSpacer = false,
                            yAxisStyle = TextStyle(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            xAxisStyle = TextStyle(
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            yAxisRange = 5,
                            oneLineChart = true,
                            gridOrientation = GridOrientation.HORIZONTAL
                        )
                    } else {
                        EmptyChartPlaceholder("Aucune donnée disponible")
                    }
                }
            }
        }
    }
}