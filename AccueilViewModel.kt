package org.babetech.borastock.ui.screens.screennavigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.babetech.borastock.data.models.RecentMovement
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.data.models.StockStatistics
import org.babetech.borastock.data.models.StockStatus
import org.babetech.borastock.domain.usecase.GetAllStockItemsUseCase
import org.babetech.borastock.domain.usecase.GetRecentMovementsUseCase
import org.babetech.borastock.domain.usecase.GetStockStatisticsUseCase


/**
 * Represents the different states of the Accueil (Home) screen UI.
 * This sealed class helps to manage the UI state in a clear and type-safe manner.
 */
sealed class AccueilUiState {
    object Loading : AccueilUiState()
    data class Success(
        val statistics: StockStatistics,
        val recentMovements: List<RecentMovement>,
        val criticalStockItems: List<StockItem>
    ) : AccueilUiState()
    data class Error(val message: String) : AccueilUiState()
}

/**
 * ViewModel for the AccueilScreen. Manages data fetching and state for the UI.
 */
class AccueilViewModel(
    private val getStockStatisticsUseCase: GetStockStatisticsUseCase,
    private val getRecentMovementsUseCase: GetRecentMovementsUseCase,
    private val getAllStockItemsUseCase: GetAllStockItemsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<AccueilUiState>(AccueilUiState.Loading)
    val uiState: StateFlow<AccueilUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    /**
     * Fetches all the necessary data for the dashboard.
     * Uses a `viewModelScope.launch` to perform the operation on a background thread.
     */
    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = AccueilUiState.Loading
            try {
                // Fetch data from use cases
                val stockItems = getAllStockItemsUseCase().first()
                val statistics = getStockStatisticsUseCase()
                val recentMovements = getRecentMovementsUseCase(10)

                // Filter for critical items
                val criticalItems = stockItems.filter { item ->
                    item.stockStatus == StockStatus.LOW_STOCK || item.stockStatus == StockStatus.OUT_OF_STOCK
                }.take(5)

                _uiState.value = AccueilUiState.Success(
                    statistics = statistics,
                    recentMovements = recentMovements,
                    criticalStockItems = criticalItems
                )
            } catch (e: Exception) {
                // Handle errors and update the state
                _uiState.value = AccueilUiState.Error("Erreur lors du chargement des données: ${e.message}")
            }
        }
    }
}
