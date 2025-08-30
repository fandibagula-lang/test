package org.babetech.borastock.data.models
//
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.graphics.painter.Painter
//
//
//import borastock.composeapp.generated.resources.Res
//import borastock.composeapp.generated.resources.block
//import borastock.composeapp.generated.resources.checkcircle
//import borastock.composeapp.generated.resources.error
//import borastock.composeapp.generated.resources.schedule


import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.DrawableResource

import org.babetech.borastock.data.models.ChartPeriod
import org.babetech.borastock.data.models.StatisticCard
import org.babetech.borastock.data.models.TopProduct

//
//import borastock.composeapp.generated.resources.ic_cancel_filled
//import borastock.composeapp.generated.resources.ic_check_circle
//import borastock.composeapp.generated.resources.ic_close
//import borastock.composeapp.generated.resources.ic_star_filled
//import borastock.composeapp.generated.resources.inventory
//import borastock.composeapp.generated.resources.localshipping
//import borastock.composeapp.generated.resources.pause
//import borastock.composeapp.generated.resources.priorityhigh
//import borastock.composeapp.generated.resources.remove
//import borastock.composeapp.generated.resources.thumbdown
//import borastock.composeapp.generated.resources.thumbup
//import borastock.composeapp.generated.resources.trendingdown
//import borastock.composeapp.generated.resources.trendingup
//import borastock.composeapp.generated.resources.warning
//import org.jetbrains.compose.resources.DrawableResource
//import kotlinx.datetime.LocalDateTime
//
//// Data classes communes pour l'ensemble de l'application
//
///**
// * Modèle de données pour les statistiques générales (utilisé dans Accueil, Entrées, Sorties, Stock).
// * Représente une carte de statistique avec un titre, une valeur, une icône et une couleur.
// */
//data class StockStat(
//    val title: String,
//    val value: String,
//    val iconRes: DrawableResource,
//    val color: Color
//)
//
///**
// * Modèle de données pour les résumés ou indicateurs clés (utilisé dans Entrées, Sorties, Stock).
// * Représente une ligne de résumé avec un libellé, une valeur, une icône et des couleurs de style.
// */
//data class StockSummary(
//    val label: String,
//    val value: String,
//    val iconRes: DrawableResource,
//    val iconTint: Color,
//    val backgroundColor: Color,
//    val valueColor: Color
//)
//
///**
// * Modèle de données pour les métriques affichées sur le tableau de bord (AccueilScreen).
// * Inclut des informations de tendance.
// */
//data class MetricData(
//    val title: String,
//    val value: String,
//    val trend: String,
//    val trendUp: Boolean,
//    val icon: DrawableResource,
//    val color: Color
//)
//
///**
// * Modèle de données pour les mouvements récents de stock (AccueilScreen).
// */
//data class Movement(
//    val description: String,
//    val time: String,
//    val isIncoming: Boolean
//)
//
///**
// * Modèle de données pour un produit en stock (StockScreen).
// */
//data class StockItem(
//    val id: Long,
//    val name: String,
//    val category: String,
//    val currentStock: Int,
//    val minStock: Int,
//    val maxStock: Int,
//    val price: Double,
//    val supplier: Supplier,
//    val lastUpdate: String,
//    val status: StockStatus
//)
//
///**
// * Enum pour le statut d'un produit en stock (StockScreen).
// */
//enum class StockStatus(val label: String, val color: Color, val icon: DrawableResource) {
//    IN_STOCK("En stock", Color(0xFF22c55e), Res.drawable.checkcircle),
//    LOW_STOCK("Stock faible", Color(0xFFf59e0b), Res.drawable.error),
//    OUT_OF_STOCK("Rupture", Color(0xFFef4444), Res.drawable.trendingdown),
//    OVERSTOCKED("Surstock", Color(0xFF3b82f6), Res.drawable.trendingup)
//}
//
///**
// * Modèle de données pour une entrée de stock (EntriesScreen).
// */
//
////data class StockEntry(
////    val id: String,
////    val productName: String,
////    val category: String,
////    val quantity: Int,
////    val unitPrice: Double,
////    val totalValue: Double, // Ajout de totalValue
////    val supplier: String,
////    val entryDate: LocalDateTime,
////    val status: EntryStatus,
////    val batchNumber: String?,
////    val notes: String?
////)
//
//
///**
// * Enum pour le statut d'une entrée de stock (EntriesScreen).
// */
//enum class EntryStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
//    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.schedule),
//    VALIDATED("Validée", Color(0xFF22c55e), Res.drawable.checkcircle),
//    RECEIVED("Reçue", Color(0xFF3b82f6), Res.drawable.inventory),
//    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_close)
//}
//
///**
// * Modèle de données pour une sortie de stock (ExitsScreen).
// */
//data class StockExit(
//    val id: String,
//    val stockItemId: String,
//    val quantity: Int,
//    val unitPrice: Double,
//    val customer: String,
//    val exitDate: LocalDateTime,
//    val orderNumber: String?,
//    val deliveryAddress: String?,
//    val status: ExitStatus,
//    val notes: String?,
//    val urgency: ExitUrgency
//)
//
///**
// * Enum pour le statut d'une sortie de stock (ExitsScreen).
// */
//enum class ExitStatus(val label: String, val color: Color, val iconRes: DrawableResource) {
//    PENDING("En préparation", Color(0xFFf59e0b), Res.drawable.schedule),
//    PREPARED("Préparée", Color(0xFF3b82f6),  Res.drawable.inventory),
//    SHIPPED("Expédiée", Color(0xFF8b5cf6), Res.drawable.localshipping),
//    DELIVERED("Livrée", Color(0xFF22c55e), Res.drawable.ic_check_circle),
//    CANCELLED("Annulée", Color(0xFFef4444), Res.drawable.ic_cancel_filled)
//}
//
///**
// * Enum pour l'urgence d'une sortie de stock (ExitsScreen).
// */
//enum class ExitUrgency(val label: String, val color: Color, val iconRes: DrawableResource) {
//    LOW("Normale", Color(0xFF6b7280), Res.drawable.remove),
//    MEDIUM("Prioritaire", Color(0xFFf59e0b), Res.drawable.priorityhigh),
//    HIGH("Urgente", Color(0xFFef4444),Res.drawable.warning)
//}
//
///**
// * Data class pour les cartes de statistiques dans l'écran StatistiqueScreen.
// */
data class StatisticCard(
    val title: String,
    val value: String,
    val change: String,
    val isPositive: Boolean,
    val icon: DrawableResource,
    val color: Color
)
//
///**
// * Data class pour les périodes des graphiques dans l'écran StatistiqueScreen.
// */
data class ChartPeriod(
    val label: String,
    val value: String
)
//
///**
// * Data class pour les top produits dans l'écran StatistiqueScreen.
// */
data class TopProduct(
    val name: String,
    val category: String,
    val sales: Int,
    val revenue: Double
)
//
///**
// * Data classes pour les paramètres (SettingsScreen).
// */
data class SettingCategory(
    val title: String,
    val items: List<SettingItem>
)
//
data class SettingItem(
    val title: String,
    val subtitle: String? = null,
    val icon: DrawableResource,
    val type: SettingType,
    val switchState: Boolean = false,
    val onSwitchChanged: ((Boolean) -> Unit)? = null,
    val action: (() -> Unit)? = null,
    //   val customContent: @Composable (() -> Unit)? = null // Contenu Composable personnalisé
)


enum class SettingType {
    NAVIGATION,
    SWITCH,
    ACTION,
    INFO,

}
//
//data class Supplier(
//    val id: Int,
//    val name: String,
//    val category: String,
//    val contactPerson: String,
//    val email: String,
//    val phone: String,
//    val address: String,
//    val city: String,
//    val country: String,
//    val productsCount: Int,
//    val totalOrders: Int,
//    val totalValue: Double,
//    val rating: Float,
//    val status: SupplierStatus,
//    val reliability: SupplierReliability,
//    val lastOrderDate: LocalDateTime,
//    val paymentTerms: String,
//    val notes: String?
//)
//
//enum class SupplierStatus(val label: String, val color: Color, val icon: DrawableResource) {
//    ACTIVE("Actif", Color(0xFF22c55e), Res.drawable.ic_check_circle),
//    INACTIVE("Inactif", Color(0xFF6b7280), Res.drawable.pause),
//    PENDING("En attente", Color(0xFFf59e0b), Res.drawable.schedule),
//    BLOCKED("Bloqué", Color(0xFFef4444), Res.drawable.block)
//}
//
//enum class SupplierReliability(val label: String, val color: Color, val icon: DrawableResource) {
//    EXCELLENT("Excellent", Color(0xFF22c55e), Res.drawable.ic_star_filled),
//    GOOD("Bon", Color(0xFF3b82f6), Res.drawable.thumbup),
//    AVERAGE("Moyen", Color(0xFFf59e0b), Res.drawable.remove),
//    POOR("Faible", Color(0xFFef4444), Res.drawable.thumbdown)
//}
//
//
//data class ChartType(
//    val key: String,
//    val title: String,
//    val icon: Painter,
//    val description: String
//)
