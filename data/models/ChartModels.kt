package org.babetech.borastock.data.models

import androidx.compose.ui.graphics.Color

/**
 * Data models specifically for chart representations
 */

data class ChartDataPoint(
    val label: String,
    val value: Double
)

data class PieChartDataPoint(
    val label: String,
    val value: Double,
    val color: Color
)

data class BarChartDataPoint(
    val label: String,
    val value: Double,
    val minValue: Double = 0.0,
    val color: Color
)

data class RadarChartDataPoint(
    val label: String,
    val values: List<Double>
)

/**
 * Chart configuration models
 */
data class ChartConfig(
    val title: String,
    val subtitle: String?,
    val showLegend: Boolean = true,
    val showGrid: Boolean = true,
    val animateChart: Boolean = true
)

/**
 * Time period for chart filtering
 */
enum class ChartTimePeriod(val label: String, val days: Int) {
    WEEK("7 jours", 7),
    MONTH("30 jours", 30),
    QUARTER("3 mois", 90),
    YEAR("1 an", 365)
}

/**
 * Chart type enumeration
 */
enum class ChartTypeEnum(val label: String, val description: String) {
    LINE("Ligne", "Évolution temporelle"),
    BAR("Barres", "Comparaisons"),
    PIE("Secteurs", "Répartitions"),
    DONUT("Anneau", "Proportions"),
    RADAR("Radar", "Multi-critères")
}