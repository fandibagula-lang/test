package org.babetech.borastock.data.models

import androidx.compose.ui.graphics.Color
import borastock.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

// Domain models for the application

/**
 * Domain model for Supplier
 */
data class Supplier(
    val id: Long,
    val name: String,
    val category: String?,
    val contactPerson: String?,
    val email: String?,
    val phone: String?,
    val address: String?,
    val city: String?,
    val country: String?,
    val rating: Float,
    val status: SupplierStatus,
    val reliability: SupplierReliability,
    val lastOrderDate: String?,
    val paymentTerms: String?,
    val notes: String?,
    val createdAt: String,
    val updatedAt: String
)




/**
 * Domain model for Stock Item with current stock calculation
 */
data class StockItem(
    val id: Long,
    val name: String,
    val category: String,
    val description: String?,
    val currentStock: Int,
    val minStock: Int,
    val maxStock: Int,
    val unitPrice: Double,
    val supplier: Supplier,
    val status: StockItemStatus,
    val createdAt: String,
    val updatedAt: String,

) {
    val totalValue: Double
        get() = currentStock * unitPrice

    val stockPercentage: Float
        get() = if (maxStock > 0) (currentStock.toFloat() / maxStock.toFloat()).coerceIn(0f, 1f) else 0f

    // Propriété calculée qui correspond à ton enum StockStatus
    val stockStatus: StockStatus
        get() = when {
            currentStock == 0 -> StockStatus.OUT_OF_STOCK
            currentStock <= minStock -> StockStatus.LOW_STOCK
            currentStock > maxStock -> StockStatus.OVERSTOCKED
            else -> StockStatus.IN_STOCK
        }

}


/**
 * Domain model for Stock Entry
 */
data class StockEntry(
    val id: Long?,
    val stockItemId: Long,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val entryDate: String,
    val batchNumber: String?,
    val expiryDate: String?,
    val supplier: String,
    val supplierId: Long,
    val status: EntryStatus,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)

/**
 * Domain model for Stock Exit
 */
data class StockExit(
    val id: Long?,
    val stockItemId: Long,
    val productName: String,
    val category: String,
    val quantity: Int,
    val unitPrice: Double,
    val totalValue: Double,
    val exitDate: String,
    val customer: String,
    val orderNumber: String?,
    val deliveryAddress: String?,
    val status: ExitStatus,
    val urgency: ExitUrgency,
    val notes: String?,
    val createdAt: String?,
    val updatedAt: String?
)

/**
 * Statistics model for dashboard
 */
data class StockStatistics(
    val totalItems: Int,
    val itemsInStock: Int,
    val itemsLowStock: Int,
    val itemsOutOfStock: Int,
    val itemsOverstocked: Int,
    val totalStockValue: Double
)

/**
 * Recent movement model for dashboard
 */
data class RecentMovement(
    val id: Long,
    val productName: String,
    val quantity: Int,
    val date: String,
    val type: MovementType,
    val description: String
)

// Enums


enum class SupplierStatus(val label: String, val color: Color, val icon: DrawableResource) {
    ACTIVE("Actif", Color(0xFF22c55e), Res.drawable.ic_check_circle),
    INACTIVE("Inactif", Color(0xFF6b7280), Res.drawable.pause),
    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.schedule),
    BLOCKED("Bloqué", Color(0xFFef4444), Res.drawable.block)
}

enum class SupplierReliability(val label: String, val color: Color, val icon: DrawableResource) {
    EXCELLENT("Excellent", Color(0xFF22c55e), Res.drawable.ic_star_filled),
    GOOD("Bon", Color(0xFF3b82f6), Res.drawable.thumbup),
    AVERAGE("Moyen", Color(0xFFf59e0b), Res.drawable.remove),
    POOR("Faible", Color(0xFFef4444), Res.drawable.thumbdown)
}

enum class StockItemStatus(val label: String) {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    DISCONTINUED("Discontinué")
}

enum class StockStatus(val label: String, val color: Color, val icon: DrawableResource) {
    IN_STOCK("En stock", Color(0xFF22c55e), Res.drawable.checkcircle),
    LOW_STOCK("Stock faible", Color(0xFFf59e0b), Res.drawable.warning),
    OUT_OF_STOCK("Rupture", Color(0xFFef4444), Res.drawable.error),
    OVERSTOCKED("Surstock", Color(0xFF3b82f6), Res.drawable.trendingup)
}

enum class EntryStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.schedule),
    VALIDATED("Validée", Color(0xFF22c55e), Res.drawable.checkcircle),
    RECEIVED("Reçue", Color(0xFF3b82f6), Res.drawable.inventory),
    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_close)
}

enum class ExitStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
    PENDING("En préparation", Color(0xFFf59e0b), Res.drawable.schedule),
    PREPARED("Préparée", Color(0xFF3b82f6), Res.drawable.inventory),
    SHIPPED("Expédiée", Color(0xFF8b5cf6), Res.drawable.localshipping),
    DELIVERED("Livrée", Color(0xFF22c55e), Res.drawable.ic_check_circle),
    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_cancel_filled)
}

enum class ExitUrgency(val label: String, val color: Color, val iconRes: DrawableResource) {
    LOW("Normale", Color(0xFF6b7280), Res.drawable.remove),
    MEDIUM("Prioritaire", Color(0xFFf59e0b), Res.drawable.priorityhigh),
    HIGH("Urgente", Color(0xFFef4444), Res.drawable.warning)
}

enum class MovementType {
    ENTRY, EXIT
}

// UI Models (keeping existing ones for compatibility)
data class StockStat(
    val title: String,
    val value: String,
    val iconRes: DrawableResource,
    val color: Color
)

data class StockSummary(
    val label: String,
    val value: String,
    val iconRes: DrawableResource,
    val iconTint: Color,
    val backgroundColor: Color,
    val valueColor: Color
)

data class MetricData(
    val title: String,
    val value: String,
    val trend: String,
    val trendUp: Boolean,
    val icon: DrawableResource,
    val color: Color
)

data class Movement(
    val description: String,
    val time: String,
    val isIncoming: Boolean
)


data class StockMovement(
    val date: String,
    val type: String, // "Entry" ou "Exit"
    val itemName: String,
    val quantity: Int,
    val totalValue: Double
)

data class SupplierDistribution(
    val name: String,
    val value: Double
)
