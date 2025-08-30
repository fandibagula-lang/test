package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import org.babetech.borastock.ui.screens.dashboard.viewmodel.GraphicsViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphiquesDetailsScreen(
    onBackClick: () -> Unit,
    viewModel: GraphicsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedIndicator by remember { mutableStateOf("evolution") }

    val indicators = listOf(
        ChartIndicator("evolution", "Évolution Stock", "Tendances temporelles", Res.drawable.analytics),
        ChartIndicator("distribution", "Répartition", "Par catégorie", Res.drawable.piechart),
        ChartIndicator("alerts", "Alertes Stock", "Stock critique", Res.drawable.warning),
        ChartIndicator("suppliers", "Fournisseurs", "Performance", Res.drawable.person),
        ChartIndicator("revenue", "Revenus", "Tendance mensuelle", Res.drawable.euro)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Analyses Détaillées",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                        )
                    )
                )
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Indicator selector
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Indicateurs",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(indicators) { indicator ->
                                FilterChip(
                                    onClick = { selectedIndicator = indicator.key },
                                    label = { Text(indicator.title) },
                                    selected = selectedIndicator == indicator.key,
                                    leadingIcon = {
                                        Icon(
                                            painter = painterResource(indicator.icon),
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Chart display
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val selectedIndicatorData = indicators.find { it.key == selectedIndicator }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(selectedIndicatorData?.icon ?: Res.drawable.analytics),
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    selectedIndicatorData?.title ?: "Graphique",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                                Text(
                                    selectedIndicatorData?.description ?: "",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Box(
                            modifier = Modifier.fillMaxSize()
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
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            Text(
                                                "Erreur de chargement",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.error
                                            )
                                            TextButton(onClick = { viewModel.loadChartData() }) {
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
                                        when (selectedIndicator) {
                                            "evolution" -> DynamicLineChart(uiState.stockEvolutionData)
                                            "distribution" -> DynamicPieChart(uiState.categoryDistributionData)
                                            "alerts" -> DynamicBarChart(uiState.lowStockData)
                                            "suppliers" -> DynamicRadarChart(uiState.supplierPerformanceData)
                                            "revenue" -> DynamicLineChart(uiState.monthlyRevenueData)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Statistics summary
            item {
                when (uiState) {
                    is GraphicsUiState.Success -> {
                        StatisticsSummaryCard(
                            selectedIndicator = selectedIndicator,
                            uiState = uiState
                        )
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
private fun StatisticsSummaryCard(
    selectedIndicator: String,
    uiState: GraphicsUiState.Success
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Résumé",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            when (selectedIndicator) {
                "evolution" -> {
                    val totalValue = uiState.stockEvolutionData.lastOrNull()?.value ?: 0.0
                    val trend = if (uiState.stockEvolutionData.size >= 2) {
                        val previous = uiState.stockEvolutionData[uiState.stockEvolutionData.size - 2].value
                        val current = uiState.stockEvolutionData.last().value
                        ((current - previous) / previous * 100).toInt()
                    } else 0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatSummaryItem("Valeur actuelle", "€${String.format("%.0f", totalValue)}")
                        StatSummaryItem("Tendance", "${if (trend >= 0) "+" else ""}$trend%")
                        StatSummaryItem("Points de données", "${uiState.stockEvolutionData.size}")
                    }
                }
                "distribution" -> {
                    val totalCategories = uiState.categoryDistributionData.size
                    val topCategory = uiState.categoryDistributionData.maxByOrNull { it.value }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatSummaryItem("Catégories", "$totalCategories")
                        StatSummaryItem("Top catégorie", topCategory?.label ?: "N/A")
                        StatSummaryItem("Valeur max", "€${String.format("%.0f", topCategory?.value ?: 0.0)}")
                    }
                }
                "alerts" -> {
                    val criticalItems = uiState.lowStockData.size
                    val totalShortage = uiState.lowStockData.sumOf { it.minValue - it.value }
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatSummaryItem("Articles critiques", "$criticalItems")
                        StatSummaryItem("Manque total", "${totalShortage.toInt()} unités")
                        StatSummaryItem("Priorité", "Élevée")
                    }
                }
                "suppliers" -> {
                    val totalSuppliers = uiState.supplierPerformanceData.size
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatSummaryItem("Fournisseurs", "$totalSuppliers")
                        StatSummaryItem("Critères", "5")
                        StatSummaryItem("Évaluation", "Multi-dimensionnelle")
                    }
                }
                "revenue" -> {
                    val totalRevenue = uiState.monthlyRevenueData.sumOf { it.value }
                    val avgMonthly = if (uiState.monthlyRevenueData.isNotEmpty()) {
                        totalRevenue / uiState.monthlyRevenueData.size
                    } else 0.0
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatSummaryItem("Revenus totaux", "€${String.format("%.0f", totalRevenue)}")
                        StatSummaryItem("Moyenne mensuelle", "€${String.format("%.0f", avgMonthly)}")
                        StatSummaryItem("Période", "${uiState.monthlyRevenueData.size} mois")
                    }
                }
            }
        }
    }
}

@Composable
private fun StatSummaryItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

data class ChartIndicator(
    val key: String,
    val title: String,
    val description: String,
    val icon: org.jetbrains.compose.resources.DrawableResource
)