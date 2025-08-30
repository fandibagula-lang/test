package org.babetech.borastock.domain.usecase

import kotlinx.coroutines.flow.Flow
import org.babetech.borastock.data.models.*
import org.babetech.borastock.data.repository.StockRepository

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Supplier Use Cases
//class GetAllSuppliersUseCase(private val repository: StockRepository) {
//    operator fun invoke(): Flow<List<Supplier>> = repository.getAllSuppliers()
//}


class CreateSupplierUseCase(private val repository: StockRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        name: String,
        category: String?,
        contactPerson: String?,
        email: String?,
        phone: String?,
        address: String?,
        city: String?,
        country: String?,
        rating: Float,
        status: SupplierStatus,
        reliability: SupplierReliability,
        lastOrderDate: String?,
        paymentTerms: String?,
        notes: String?
    ): Result<Long> {
        return try {
            if (name.isBlank()) {
                Result.failure(IllegalArgumentException("Le nom du fournisseur ne peut pas être vide"))
            } else if (rating < 0 || rating > 5) {
                Result.failure(IllegalArgumentException("La note doit être comprise entre 0 et 5"))
            } else {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()

                val supplier = Supplier(
                    id = 0L, // Will be set by database
                    name = name,
                    category = category,
                    contactPerson = contactPerson,
                    email = email,
                    phone = phone,
                    address = address,
                    city = city,
                    country = country,
                    rating = rating,
                    status = status,
                    reliability = reliability,
                    lastOrderDate = lastOrderDate,
                    paymentTerms = paymentTerms,
                    notes = notes,
                    createdAt = now,
                    updatedAt = now
                )

                val id = repository.insertSupplier(supplier)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateSupplierUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(supplier: Supplier): Result<Unit> {
        return try {
            if (supplier.name.isBlank()) {
                Result.failure(IllegalArgumentException("Le nom du fournisseur ne peut pas être vide"))
            } else if (supplier.rating < 0 || supplier.rating > 5) {
                Result.failure(IllegalArgumentException("La note doit être comprise entre 0 et 5"))
            } else {
                repository.updateSupplier(supplier)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteSupplierUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteSupplier(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class GetSupplierStatisticsUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(): SupplierStatistics {
        return try {
            val suppliers = repository.getAllSuppliers()
            var totalSuppliers = 0
            var activeSuppliers = 0
            var pendingSuppliers = 0
            var blockedSuppliers = 0
            var totalRating = 0.0
            var totalValue = 0.0

            suppliers.collect { supplierList ->
                totalSuppliers = supplierList.size
                activeSuppliers = supplierList.count { it.status == SupplierStatus.ACTIVE }
                pendingSuppliers = supplierList.count { it.status == SupplierStatus.PENDING }
                blockedSuppliers = supplierList.count { it.status == SupplierStatus.BLOCKED }
                totalRating = if (supplierList.isNotEmpty()) {
                    supplierList.map { it.rating.toDouble() }.average()
                } else 0.0
                // Note: totalValue would need to be calculated from orders/entries
                // For now, we'll use a placeholder calculation
                totalValue = supplierList.sumOf {
                    // This would ideally come from actual order data
                    0.0 // Placeholder
                }
            }

            SupplierStatistics(
                totalSuppliers = totalSuppliers,
                activeSuppliers = activeSuppliers,
                pendingSuppliers = pendingSuppliers,
                blockedSuppliers = blockedSuppliers,
                averageRating = totalRating,
                totalValue = totalValue
            )
        } catch (e: Exception) {
            SupplierStatistics(
                totalSuppliers = 0,
                activeSuppliers = 0,
                pendingSuppliers = 0,
                blockedSuppliers = 0,
                averageRating = 0.0,
                totalValue = 0.0
            )
        }
    }
}

// Data class for supplier statistics
data class SupplierStatistics(
    val totalSuppliers: Int,
    val activeSuppliers: Int,
    val pendingSuppliers: Int,
    val blockedSuppliers: Int,
    val averageRating: Double,
    val totalValue: Double
)