package org.babetech.borastock.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.babetech.borastock.data.models.EntryStatus
import org.babetech.borastock.data.models.ExitStatus
import org.babetech.borastock.data.models.ExitUrgency
import org.babetech.borastock.data.models.RecentMovement
import org.babetech.borastock.data.models.StockEntry
import org.babetech.borastock.data.models.StockExit
import org.babetech.borastock.data.models.StockItem
import org.babetech.borastock.data.models.StockStatistics
import org.babetech.borastock.data.models.Supplier
import org.babetech.borastock.data.repository.StockRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

// Stock Item Use Cases
class GetAllStockItemsUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<StockItem>> = repository.getAllStockItems()
}


class GetStockItemByIdUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(id: Long): StockItem? = repository.getStockItemById(id)
}

class CreateStockItemUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(
        name: String,
        category: String,
        description: String?,
        minStock: Int,
        maxStock: Int,
        unitPrice: Double,
        supplierId: Long
    ): Result<Long> {
        return try {
            if (name.isBlank()) {
                Result.failure(IllegalArgumentException("Le nom du produit ne peut pas être vide"))
            } else if (category.isBlank()) {
                Result.failure(IllegalArgumentException("La catégorie ne peut pas être vide"))
            } else if (minStock < 0) {
                Result.failure(IllegalArgumentException("Le stock minimum ne peut pas être négatif"))
            } else if (maxStock <= minStock) {
                Result.failure(IllegalArgumentException("Le stock maximum doit être supérieur au stock minimum"))
            } else if (unitPrice < 0) {
                Result.failure(IllegalArgumentException("Le prix unitaire ne peut pas être négatif"))
            } else {
                val id = repository.insertStockItem(name, category, description, minStock, maxStock, unitPrice, supplierId)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateStockItemUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(item: StockItem): Result<Unit> {
        return try {
            if (item.name.isBlank()) {
                Result.failure(IllegalArgumentException("Le nom du produit ne peut pas être vide"))
            } else if (item.category.isBlank()) {
                Result.failure(IllegalArgumentException("La catégorie ne peut pas être vide"))
            } else if (item.minStock < 0) {
                Result.failure(IllegalArgumentException("Le stock minimum ne peut pas être négatif"))
            } else if (item.maxStock <= item.minStock) {
                Result.failure(IllegalArgumentException("Le stock maximum doit être supérieur au stock minimum"))
            } else if (item.unitPrice < 0) {
                Result.failure(IllegalArgumentException("Le prix unitaire ne peut pas être négatif"))
            } else {
                repository.updateStockItem(item)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteStockItemUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteStockItem(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Stock Entry Use Cases
class GetAllStockEntriesUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<StockEntry>> = repository.getAllStockEntries()
}

class GetStockEntriesByItemIdUseCase(private val repository: StockRepository) {
    operator fun invoke(itemId: Long): Flow<List<StockEntry>> = repository.getStockEntriesByItemId(itemId)
}

class AddStockEntryUseCase(private val repository: StockRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        stockItemId: Long,
        quantity: Int,
        unitPrice: Double,
        batchNumber: String?,
        expiryDate: String?,
        supplierId: Long,
        notes: String?
    ): Result<Long> {
        return try {
            if (quantity <= 0) {
                Result.failure(IllegalArgumentException("La quantité doit être positive"))
            } else if (unitPrice < 0) {
                Result.failure(IllegalArgumentException("Le prix unitaire ne peut pas être négatif"))
            } else {
                val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                val totalValue = quantity * unitPrice

                val entry = StockEntry(
                    id = null,
                    stockItemId = stockItemId,
                    productName = "", // Will be filled by the database query
                    category = "", // Will be filled by the database query
                    quantity = quantity,
                    unitPrice = unitPrice,
                    totalValue = totalValue,
                    entryDate = now,
                    batchNumber = batchNumber,
                    expiryDate = expiryDate,
                    supplier = "", // Will be filled by the database query
                    supplierId = supplierId,
                    status = EntryStatus.PENDING,
                    notes = notes,
                    createdAt = now,
                    updatedAt = now
                )

                val id = repository.insertStockEntry(entry)
                Result.success(id)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateStockEntryUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(entry: StockEntry): Result<Unit> {
        return try {
            if (entry.quantity <= 0) {
                Result.failure(IllegalArgumentException("La quantité doit être positive"))
            } else if (entry.unitPrice < 0) {
                Result.failure(IllegalArgumentException("Le prix unitaire ne peut pas être négatif"))
            } else {
                repository.updateStockEntry(entry)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteStockEntryUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteStockEntry(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Stock Exit Use Cases
class GetAllStockExitsUseCase(
    private val repository: StockRepository
) {
    operator fun invoke(): Flow<List<StockExit>> = repository.getAllStockExits()
}



class GetStockExitsByItemIdUseCase(private val repository: StockRepository) {
    operator fun invoke(itemId: Long): Flow<List<StockExit>> = repository.getStockExitsByItemId(itemId)
}

class AddStockExitUseCase(private val repository: StockRepository) {
    @OptIn(ExperimentalTime::class)
    suspend operator fun invoke(
        stockItemId: Long,
        quantity: Int,
        unitPrice: Double,
        customer: String,
        orderNumber: String?,
        deliveryAddress: String?,
        urgency: ExitUrgency,
        notes: String?
    ): Result<Long> {
        return try {
            if (quantity <= 0) {
                Result.failure(IllegalArgumentException("La quantité doit être positive"))
            } else if (unitPrice < 0) {
                Result.failure(IllegalArgumentException("Le prix unitaire ne peut pas être négatif"))
            } else if (customer.isBlank()) {
                Result.failure(IllegalArgumentException("Le client ne peut pas être vide"))
            } else {
                // Check if there's enough stock
                val stockItem = repository.getStockItemById(stockItemId)
                if (stockItem == null) {
                    Result.failure(IllegalArgumentException("Produit non trouvé"))
                } else if (stockItem.currentStock < quantity) {
                    Result.failure(IllegalArgumentException("Stock insuffisant. Stock disponible: ${stockItem.currentStock}"))
                } else {
                    val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).toString()
                    val totalValue = quantity * unitPrice

                    val exit = StockExit(
                        id = null,
                        stockItemId = stockItemId,
                        productName = "", // Will be filled by the database query
                        category = "", // Will be filled by the database query
                        quantity = quantity,
                        unitPrice = unitPrice,
                        totalValue = totalValue,
                        exitDate = now,
                        customer = customer,
                        orderNumber = orderNumber,
                        deliveryAddress = deliveryAddress,
                        status = ExitStatus.PENDING,
                        urgency = urgency,
                        notes = notes,
                        createdAt = now,
                        updatedAt = now
                    )

                    val id = repository.insertStockExit(exit)
                    Result.success(id)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class UpdateStockExitUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(exit: StockExit): Result<Unit> {
        return try {
            if (exit.quantity <= 0) {
                Result.failure(IllegalArgumentException("La quantité doit être positive"))
            } else if (exit.unitPrice < 0) {
                Result.failure(IllegalArgumentException("Le prix unitaire ne peut pas être négatif"))
            } else if (exit.customer.isBlank()) {
                Result.failure(IllegalArgumentException("Le client ne peut pas être vide"))
            } else {
                repository.updateStockExit(exit)
                Result.success(Unit)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteStockExitUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(id: Long): Result<Unit> {
        return try {
            repository.deleteStockExit(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Supplier Use Cases
class GetAllSuppliersUseCase(private val repository: StockRepository) {
    operator fun invoke(): Flow<List<Supplier>> = repository.getAllSuppliers()
}

class GetSupplierByIdUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(id: Long): Supplier? = repository.getSupplierById(id)
}

//class CreateSupplierUseCase(private val repository: StockRepository) {
//    suspend operator fun invoke(supplier: Supplier): Result<Long> {
//        return try {
//            if (supplier.name.isBlank()) {
//                Result.failure(IllegalArgumentException("Le nom du fournisseur ne peut pas être vide"))
//            } else {
//                val id = repository.insertSupplier(supplier)
//                Result.success(id)
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}

//class UpdateSupplierUseCase(private val repository: StockRepository) {
//    suspend operator fun invoke(supplier: Supplier): Result<Unit> {
//        return try {
//            if (supplier.name.isBlank()) {
//                Result.failure(IllegalArgumentException("Le nom du fournisseur ne peut pas être vide"))
//            } else {
//                repository.updateSupplier(supplier)
//                Result.success(Unit)
//            }
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}

//class DeleteSupplierUseCase(private val repository: StockRepository) {
//    suspend operator fun invoke(id: Long): Result<Unit> {
//        return try {
//            repository.deleteSupplier(id)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
//}

// Statistics Use Cases
//class GetStockStatisticsUseCase(
//    private val repository: StockRepository
//) {
//    suspend operator fun invoke(): StockStatistics = repository.getStockStatistics()
//}

class GetRecentMovementsUseCase(private val repository: StockRepository) {
    suspend operator fun invoke(limit: Int = 5): List<RecentMovement> = repository.getRecentMovements(limit)
}


