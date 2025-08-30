package org.babetech.borastock.ui.screens.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.*
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import borastock.composeapp.generated.resources.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.MovementType
import org.babetech.borastock.data.models.RecentMovement
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.data.models.StockStatistics
import org.babetech.borastock.ui.screens.screennavigation.AccueilUiState
import org.babetech.borastock.ui.screens.screennavigation.AccueilViewModel
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

/**
 * A simple data class to represent a dashboard metric.
 * This helps map the ViewModel's data to the UI components.
 */
data class DashboardMetric(
    val title: String,
    val value: String,
    val trend: String,
    val trendUp: Boolean,
    val icon: Painter,
    val color: Color,
    val delay: Long = 0L
)

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AccueilScreen(viewModel: AccueilViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
    val paneState = navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting]
    val showSupporting = paneState != PaneAdaptedValue.Hidden

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        SupportingPaneScaffold(
            value = navigator.scaffoldValue,
            directive = navigator.scaffoldDirective,
            mainPane = {
                AnimatedPane(modifier = Modifier
                    .width(if (showSupporting) maxWidth * 0.2f else maxWidth)
                    .fillMaxHeight()) {
                    when (val state = uiState) {
                        is AccueilUiState.Loading -> {
                            LoadingState()
                        }
                        is AccueilUiState.Error -> {
                            ErrorState(state.message) { viewModel.loadDashboardData() }
                        }
                        is AccueilUiState.Success -> {
                            MainDashboardPane(
                                statistics = state.statistics,
                                recentMovements = state.recentMovements,
                                criticalStockItems = state.criticalStockItems,
                                showChartButton = !showSupporting,
                                onToggleChart = {
                                    scope.launch {
                                        navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                                    }
                                }
                            )
                        }
                    }
                }
            },
            supportingPane = {
                if (showSupporting) {
                    AnimatedPane(modifier = Modifier
                        .width(maxWidth * 0.8f)
                        .fillMaxHeight()) {
                        SupportingChartPane(
                            onBack = {
                                scope.launch { navigator.navigateBack() }
                            }
                        )
                    }
                }
            }
        )
    }
}

// --- Main Dashboard Pane Composable ---
@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ThreePaneScaffoldScope.MainDashboardPane(
    statistics: StockStatistics,
    recentMovements: List<RecentMovement>,
    criticalStockItems: List<StockItem>,
    showChartButton: Boolean,
    onToggleChart: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.3f)
                    )
                )
            )
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    Column {
                        Text(
                            "Tableau de bord",
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            "Aperçu de votre activité",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    scrolledContainerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item { DashboardMetricsGrid(statistics) }
            item { RecentMovementsList(recentMovements) }
            if (criticalStockItems.isNotEmpty()) {
                item { CriticalStockSection(criticalStockItems) }
            }
            item { QuickActionsSection() }
            if (showChartButton) {
                item {
                    Button(
                        onClick = onToggleChart,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(Res.drawable.analytics),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                "Afficher l'analyse",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Supporting Chart Pane Composable ---
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldScope.SupportingChartPane(
    onBack: () -> Unit
) {
    val navigator = rememberSupportingPaneScaffoldNavigator()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] != PaneAdaptedValue.Expanded) {
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart as Alignment.Horizontal)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Retour")
            }
        }

        Text(
            text = "Analyse des performances",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        // Add your chart composable here
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("Graphiques en construction...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

// --- Dashboard Metrics Grid Composable ---
@Composable
fun DashboardMetricsGrid(statistics: StockStatistics) {
    val metrics = listOf(
        DashboardMetric(
            title = "Produits",
            value = statistics.totalItems.toString(),
            trend = "+0%", // Trend data is not in ViewModel, using placeholder
            trendUp = true,
            icon = painterResource(Res.drawable.inventory),
            color = Color(0xFF3B82F6)
        ),
        DashboardMetric(
            title = "En Stock",
            value = statistics.itemsInStock.toString(),
            trend = "+0%",
            trendUp = true,
            icon = painterResource(Res.drawable.checkcircle),
            color = Color(0xFF10B981)
        ),
        DashboardMetric(
            title = "Faible Stock",
            value = statistics.itemsLowStock.toString(),
            trend = "-0%",
            trendUp = false,
            icon = painterResource(Res.drawable.warning),
            color = Color(0xFFF59E0B)
        ),
        DashboardMetric(
            title = "Ruptures",
            value = statistics.itemsOutOfStock.toString(),
            trend = "+0%",
            trendUp = true,
            icon = painterResource(Res.drawable.error),
            color = Color(0xFFEF4444)
        ),
        DashboardMetric(
            title = "Valeur Stock",
            value = "€" + String.format("%.2f", statistics.totalStockValue),
            trend = "+0%",
            trendUp = true,
            icon = painterResource(Res.drawable.euro),
            color = Color(0xFF8B5CF6)
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(180.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.heightIn(min = 200.dp, max = 500.dp)
    ) {
        items(
            items = metrics,
            key = { it.title }
        ) { metric ->
            StatCard(
                title = metric.title,
                value = metric.value,
                icon = metric.icon,
                trend = metric.trend,
                trendUp = metric.trendUp,
                color = metric.color,
                delay = metrics.indexOf(metric) * 100L
            )
        }
    }
}


@Composable
fun StatCard(
    title: String,
    value: String,
    icon: Painter,
    trend: String,
    trendUp: Boolean,
    color: Color,
    delay: Long
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        ) + fadeIn()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(16.dp),
                    spotColor = color.copy(alpha = 0.1f)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = icon,
                            contentDescription = null,
                            tint = color
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            painter = if (trendUp) painterResource(Res.drawable.trendingup) else painterResource(Res.drawable.trendingdown),
                            contentDescription = null,
                            tint = if (trendUp) Color(0xFF22c55e) else Color(0xFFef4444),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = trend,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (trendUp) Color(0xFF22c55e) else Color(0xFFef4444)
                        )
                    }
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = value,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

// --- Recent Movements List Composable ---
@Composable
fun RecentMovementsList(movements: List<RecentMovement>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Mouvements récents",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            if (movements.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Aucun mouvement récent", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                movements.forEach { movement ->
                    MovementItem(
                        movement = movement.description,
                        time = movement.date,
                        isIncoming = movement.type == MovementType.ENTRY
                    )
                }
            }
        }
    }
}

// --- Movement Item Composable ---
@Composable
private fun MovementItem(
    movement: String,
    time: String,
    isIncoming: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (isIncoming) Color(0xFF22c55e) else Color(0xFFef4444)
                    )
            )
            Column {
                Text(
                    text = movement,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// --- Critical Stock Section Composable ---
@Composable
fun CriticalStockSection(criticalItems: List<StockItem>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Articles en stock critique",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(criticalItems) { item ->
                    CriticalItemCard(item)
                }
            }
        }
    }
}

// --- Critical Item Card Composable ---
@Composable
fun CriticalItemCard(item: StockItem) {
    Card(
        modifier = Modifier
            .width(180.dp)
            .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.warning),
                    contentDescription = "Critical Icon",
                    tint = MaterialTheme.colorScheme.error
                )
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
            Text(
                text = "Stock: ${item.currentStock}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = "Min: ${item.minStock}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
        }
    }
}

// --- Quick Actions Section Composable ---
@Composable
private fun QuickActionsSection() {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Actions rapides",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionButton(
                title = "Ajouter produit",
                icon = painterResource(Res.drawable.ic_add),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            ) { /* Action */ }

            QuickActionButton(
                title = "Scanner code",
                icon = painterResource(Res.drawable.qr_code),
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.weight(1f)
            ) { /* Action */ }
        }

        QuickActionButton(
            title = "Voir l'analyse détaillée",
            icon = painterResource(Res.drawable.analytics),
            color = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.fillMaxWidth()
        ) { /* Action */ }
    }
}

// --- Quick Action Button Composable ---
@Composable
private fun QuickActionButton(
    title: String,
    icon: Painter,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.1f),
            contentColor = color
        )
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                title,
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

// --- State Composable Functions (reused from previous refactoring) ---
@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Erreur",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onErrorContainer
            )
            Button(onClick = onRetry) {
                Text("Réessayer")
            }
        }
    }
}