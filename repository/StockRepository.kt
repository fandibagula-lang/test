package org.babetech.borastock.data.repository

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.babe.sqldelight.data.db.AppDatabase
// <-- adapte si nécessaire
import org.babetech.borastock.data.db.provideDriver
import org.babetech.borastock.data.dispatchers.IODispatcher
import org.babetech.borastock.data.mappers.*
import org.babetech.borastock.data.models.*

interface StockRepository {
    // Stock Items
    fun getAllStockItems(): Flow<List<StockItem>>
    suspend fun getStockItemById(id: Long): StockItem?
    suspend fun insertStockItem(
        name: String,
        category: String,
        description: String?,
        minStock: Int,
        maxStock: Int,
        unitPrice: Double,
        supplierId: Long
    ): Long
    suspend fun updateStockItem(item: StockItem)
    suspend fun deleteStockItem(id: Long)

    // Stock Entries
    fun getAllStockEntries(): Flow<List<StockEntry>>
    fun getStockEntriesByItemId(itemId: Long): Flow<List<StockEntry>>
    suspend fun insertStockEntry(entry: StockEntry): Long
    suspend fun updateStockEntry(entry: StockEntry)
    suspend fun deleteStockEntry(id: Long)

    // Stock Exits
    fun getAllStockExits(): Flow<List<StockExit>>
    fun getStockExitsByItemId(itemId: Long): Flow<List<StockExit>>
    suspend fun insertStockExit(exit: StockExit): Long
    suspend fun updateStockExit(exit: StockExit)
    suspend fun deleteStockExit(id: Long)

    // Suppliers
    fun getAllSuppliers(): Flow<List<Supplier>>
    suspend fun getSupplierById(id: Long): Supplier?
    suspend fun insertSupplier(supplier: Supplier): Long
    suspend fun updateSupplier(supplier: Supplier)
    suspend fun deleteSupplier(id: Long)

    // Statistics
    suspend fun getStockStatistics(): StockStatistics
    suspend fun getRecentMovements(limit: Int = 5): List<RecentMovement>
}

/**
 * Implémentation "à la TaskRepository" : crée sa propre DB via provideDriver().
 * Toutes les signatures `override` correspondent exactement à l'interface.
 */
class StockRepositoryImpl(
    private val dispatcher: CoroutineDispatcher = IODispatcher
) : StockRepository {

    private val db: AppDatabase = AppDatabase(provideDriver())
    private val queries = db.borastockQueries

    // ---------------- STOCK ITEMS ----------------
    override fun getAllStockItems(): Flow<List<StockItem>> =
        queries.selectAllStockItems()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomainModel() } }

    override suspend fun getStockItemById(id: Long): StockItem? = withContext(dispatcher) {
        queries.selectStockItemById(id)
            .executeAsOneOrNull()
            ?.toDomainModel()
    }

    override suspend fun insertStockItem(
        name: String,
        category: String,
        description: String?,
        minStock: Int,
        maxStock: Int,
        unitPrice: Double,
        supplierId: Long
    ): Long = withContext(dispatcher) {
        queries.insertStockItem(
            name = name,
            category = category,
            description = description,
            min_stock = minStock.toLong(),
            max_stock = maxStock.toLong(),
            unit_price = unitPrice,
            supplier_id = supplierId,
            status = StockItemStatus.ACTIVE.name
        )
        queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateStockItem(item: StockItem): Unit = withContext(dispatcher) {
        queries.updateStockItem(
            name = item.name,
            category = item.category,
            description = item.description,
            min_stock = item.minStock.toLong(),
            max_stock = item.maxStock.toLong(),
            unit_price = item.unitPrice,
            supplier_id = item.supplier.id,
            status = item.status.name,
            id = item.id
        )
    }

    override suspend fun deleteStockItem(id: Long): Unit = withContext(dispatcher) {
        queries.deleteStockItem(id)
    }

    // ---------------- STOCK ENTRIES ----------------
    override fun getAllStockEntries(): Flow<List<StockEntry>> =
        queries.selectAllStockEntries()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomainModel() } }

    override fun getStockEntriesByItemId(itemId: Long): Flow<List<StockEntry>> =
        queries.selectStockEntriesByItemId(itemId)
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomainModel() } }

    override suspend fun insertStockEntry(entry: StockEntry): Long = withContext(dispatcher) {
        queries.insertStockEntry(
            stock_item_id = entry.stockItemId,
            quantity = entry.quantity.toLong(),
            unit_price = entry.unitPrice,
            total_value = entry.totalValue,
            entry_date = entry.entryDate,
            batch_number = entry.batchNumber,
            expiry_date = entry.expiryDate,
            supplier_id = entry.supplierId,
            status = entry.status.name,
            notes = entry.notes
        )
        queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateStockEntry(entry: StockEntry): Unit = withContext(dispatcher) {
        entry.id?.let { id ->
            queries.updateStockEntry(
                quantity = entry.quantity.toLong(),
                unit_price = entry.unitPrice,
                total_value = entry.totalValue,
                entry_date = entry.entryDate,
                batch_number = entry.batchNumber,
                expiry_date = entry.expiryDate,
                supplier_id = entry.supplierId,
                status = entry.status.name,
                notes = entry.notes,
                id = id
            )
        }
    }

    override suspend fun deleteStockEntry(id: Long): Unit = withContext(dispatcher) {
        queries.deleteStockEntry(id)
    }

    // ---------------- STOCK EXITS ----------------
    override fun getAllStockExits(): Flow<List<StockExit>> =
        queries.selectAllStockExits()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomainModel() } }

    override fun getStockExitsByItemId(itemId: Long): Flow<List<StockExit>> =
        queries.selectStockExitsByItemId(itemId)
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomainModel() } }

    override suspend fun insertStockExit(exit: StockExit): Long = withContext(dispatcher) {
        queries.insertStockExit(
            stock_item_id = exit.stockItemId,
            quantity = exit.quantity.toLong(),
            unit_price = exit.unitPrice,
            total_value = exit.totalValue,
            exit_date = exit.exitDate,
            customer = exit.customer,
            order_number = exit.orderNumber,
            delivery_address = exit.deliveryAddress,
            status = exit.status.name,
            urgency = exit.urgency.name,
            notes = exit.notes
        )
        queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateStockExit(exit: StockExit): Unit = withContext(dispatcher) {
        exit.id?.let { id ->
            queries.updateStockExit(
                quantity = exit.quantity.toLong(),
                unit_price = exit.unitPrice,
                total_value = exit.totalValue,
                exit_date = exit.exitDate,
                customer = exit.customer,
                order_number = exit.orderNumber,
                delivery_address = exit.deliveryAddress,
                status = exit.status.name,
                urgency = exit.urgency.name,
                notes = exit.notes,
                id = id
            )
        }
    }

    override suspend fun deleteStockExit(id: Long): Unit = withContext(dispatcher) {
        queries.deleteStockExit(id)
    }

    // ---------------- SUPPLIERS ----------------
    override fun getAllSuppliers(): Flow<List<Supplier>> =
        queries.selectAllSuppliers()
            .asFlow()
            .mapToList(dispatcher)
            .map { rows -> rows.map { it.toDomainModel() } }

    override suspend fun getSupplierById(id: Long): Supplier? = withContext(dispatcher) {
        queries.selectSupplierById(id)
            .executeAsOneOrNull()
            ?.toDomainModel()
    }

    override suspend fun insertSupplier(supplier: Supplier): Long = withContext(dispatcher) {
        queries.insertSupplier(
            name = supplier.name,
            category = supplier.category,
            contact_person = supplier.contactPerson,
            email = supplier.email,
            phone = supplier.phone,
            address = supplier.address,
            city = supplier.city,
            country = supplier.country,
            rating = supplier.rating.toDouble(),
            status = supplier.status.name,
            reliability = supplier.reliability.name,
            last_order_date = supplier.lastOrderDate,
            payment_terms = supplier.paymentTerms,
            notes = supplier.notes
        )
        queries.lastInsertRowId().executeAsOne()
    }

    override suspend fun updateSupplier(supplier: Supplier): Unit = withContext(dispatcher) {
        queries.updateSupplier(
            name = supplier.name,
            category = supplier.category,
            contact_person = supplier.contactPerson,
            email = supplier.email,
            phone = supplier.phone,
            address = supplier.address,
            city = supplier.city,
            country = supplier.country,
            rating = supplier.rating.toDouble(),
            status = supplier.status.name,
            reliability = supplier.reliability.name,
            last_order_date = supplier.lastOrderDate,
            payment_terms = supplier.paymentTerms,
            notes = supplier.notes,
            id = supplier.id
        )
    }

    override suspend fun deleteSupplier(id: Long): Unit = withContext(dispatcher) {
        queries.deleteSupplier(id)
    }

    // ---------------- STATISTICS ----------------
    override suspend fun getStockStatistics(): StockStatistics = withContext(dispatcher) {
        queries.selectStockStatistics()
            .executeAsOne()
            .toDomainModel()
    }

    override suspend fun getRecentMovements(limit: Int): List<RecentMovement> = withContext(dispatcher) {
        val recentEntries = queries.selectRecentEntries(limit.toLong()).executeAsList().map { it.toRecentMovement() }
        val recentExits = queries.selectRecentExits(limit.toLong()).executeAsList().map { it.toRecentMovement() }
        (recentEntries + recentExits).sortedByDescending { it.date }.take(limit)
    }
}
