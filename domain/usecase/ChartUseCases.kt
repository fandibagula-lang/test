package org.babetech.borastock.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import org.babetech.borastock.data.models.*
import org.babetech.borastock.data.repository.StockRepository

/**
 * Use case for getting chart data for stock evolution over time
 */
class GetStockEvolutionChartDataUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<ChartDataPoint>> {
        return flow {
            emit(repository.getStockEvolutionData())
        }
    }
}

/**
 * Use case for getting stock distribution by category
 */
class GetStockDistributionChartDataUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<PieChartDataPoint>> {
        return flow {
            emit(repository.getCategoryDistributionData())
        }
    }
}

/**
 * Use case for getting low stock alerts data
 */
class GetLowStockAlertsChartDataUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<BarChartDataPoint>> {
        return flow {
            emit(repository.getLowStockItemsData())
        }
    }
}

/**
 * Use case for getting supplier performance data
 */
class GetSupplierPerformanceChartDataUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<RadarChartDataPoint>> {
        return combine(
            repository.getAllSuppliers(),
            repository.getAllStockEntries()
        ) { suppliers, entries ->
            suppliers.take(5).map { supplier ->
                val supplierEntries = entries.filter { it.supplierId == supplier.id }
                val totalValue = supplierEntries.sumOf { it.totalValue }
                val avgDeliveryTime = 5.0 // Placeholder - would need delivery tracking
                val qualityScore = supplier.rating.toDouble()
                val reliability = when (supplier.reliability) {
                    SupplierReliability.EXCELLENT -> 5.0
                    SupplierReliability.GOOD -> 4.0
                    SupplierReliability.AVERAGE -> 3.0
                    SupplierReliability.POOR -> 2.0
                }

                RadarChartDataPoint(
                    label = supplier.name,
                    values = listOf(
                        totalValue / 1000, // Scale down for visualization
                        avgDeliveryTime,
                        qualityScore,
                        reliability,
                        supplierEntries.size.toDouble()
                    )
                )
            }
        }
    }
}

/**
 * Use case for getting monthly revenue trend
 */
class GetMonthlyRevenueTrendUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<ChartDataPoint>> {
        return flow {
            emit(repository.getMonthlyRevenueData())
        }
    }
}

// Helper function for category colors
private fun getCategoryColor(category: String): androidx.compose.ui.graphics.Color {
    return when (category.lowercase()) {
        "électronique" -> androidx.compose.ui.graphics.Color(0xFF3B82F6)
        "informatique" -> androidx.compose.ui.graphics.Color(0xFF8B5CF6)
        "accessoires" -> androidx.compose.ui.graphics.Color(0xFF10B981)
        "mobilier" -> androidx.compose.ui.graphics.Color(0xFFF59E0B)
        "vêtements" -> androidx.compose.ui.graphics.Color(0xFFEF4444)
        else -> androidx.compose.ui.graphics.Color(0xFF6B7280)
    }
}

// Data classes for chart data
data class ChartDataPoint(
    val label: String,
    val value: Double
)

data class PieChartDataPoint(
    val label: String,
    val value: Double,
    val color: androidx.compose.ui.graphics.Color
)

data class BarChartDataPoint(
    val label: String,
    val value: Double,
    val minValue: Double = 0.0,
    val color: androidx.compose.ui.graphics.Color
)

data class RadarChartDataPoint(
    val label: String,
    val values: List<Double>
)