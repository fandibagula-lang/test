package org.babetech.borastock.ui.screens.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.babetech.borastock.domain.usecase.*

sealed class GraphicsUiState {
    object Loading : GraphicsUiState()
    data class Success(
        val stockEvolutionData: List<ChartDataPoint>,
        val categoryDistributionData: List<PieChartDataPoint>,
        val lowStockData: List<BarChartDataPoint>,
        val supplierPerformanceData: List<RadarChartDataPoint>,
        val monthlyRevenueData: List<ChartDataPoint>
    ) : GraphicsUiState()
    data class Error(val message: String) : GraphicsUiState()
}

class GraphicsViewModel(
    private val getStockEvolutionChartDataUseCase: GetStockEvolutionChartDataUseCase,
    private val getStockDistributionChartDataUseCase: GetStockDistributionChartDataUseCase,
    private val getLowStockAlertsChartDataUseCase: GetLowStockAlertsChartDataUseCase,
    private val getSupplierPerformanceChartDataUseCase: GetSupplierPerformanceChartDataUseCase,
    private val getMonthlyRevenueTrendUseCase: GetMonthlyRevenueTrendUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<GraphicsUiState>(GraphicsUiState.Loading)
    val uiState: StateFlow<GraphicsUiState> = _uiState.asStateFlow()

    init {
        loadChartData()
    }

    fun loadChartData() {
        viewModelScope.launch {
            _uiState.value = GraphicsUiState.Loading
            try {
                combine(
                    getStockEvolutionChartDataUseCase(),
                    getStockDistributionChartDataUseCase(),
                    getLowStockAlertsChartDataUseCase(),
                    getSupplierPerformanceChartDataUseCase(),
                    getMonthlyRevenueTrendUseCase()
                ) { stockEvolution, categoryDistribution, lowStock, supplierPerformance, monthlyRevenue ->
                    GraphicsUiState.Success(
                        stockEvolutionData = stockEvolution,
                        categoryDistributionData = categoryDistribution,
                        lowStockData = lowStock,
                        supplierPerformanceData = supplierPerformance,
                        monthlyRevenueData = monthlyRevenue
                    )
                }.collect { state ->
                    _uiState.value = state
                }
            } catch (e: Exception) {
                _uiState.value = GraphicsUiState.Error("Erreur lors du chargement des graphiques: ${e.message}")
            }
        }
    }

    fun refreshData() {
        loadChartData()
    }
}