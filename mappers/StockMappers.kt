package org.babetech.borastock.data.mappers

import org.babe.sqldelight.data.db.*
import org.babetech.borastock.data.models.*

/**
 * Extension functions to map database entities to domain models
 */

// Supplier mappers
fun Suppliers.toDomainModel(): Supplier {
    return Supplier(
        id = id,
        name = name,
        category = category,
        contactPerson = contact_person,
        email = email,
        phone = phone,
        address = address,
        city = city,
        country = country,
        rating = rating.toFloat(),
        status = SupplierStatus.valueOf(status),
        reliability = SupplierReliability.valueOf(reliability),
        lastOrderDate = last_order_date,
        paymentTerms = payment_terms,
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Stock Item mappers
fun SelectAllStockItems.toDomainModel(): StockItem {
    val supplier = Supplier(
        id = supplier_id,
        name = supplier_name,
        category = supplier_category,
        contactPerson = contact_person,
        email = supplier_email,
        phone = supplier_phone,
        address = null,
        city = null,
        country = null,
        rating = supplier_rating.toFloat(),
        status = SupplierStatus.valueOf(supplier_status),
        reliability = SupplierReliability.valueOf(supplier_reliability),
        lastOrderDate = null,
        paymentTerms = null,
        notes = null,
        createdAt = created_at,
        updatedAt = updated_at
    )

    val currentStockInt = current_stock?.toInt() ?: 0

    return StockItem(
        id = id,
        name = name,
        category = category,
        description = description,
        currentStock = currentStockInt,
        minStock = min_stock.toInt(),
        maxStock = max_stock.toInt(),
        unitPrice = unit_price,
        supplier = supplier,
        status = StockItemStatus.valueOf(status),
        createdAt = created_at,
        updatedAt = updated_at
        // ❌ PAS DE stockStatus ici
    )
}

fun SelectStockItemById.toDomainModel(): StockItem {
    val supplier = Supplier(
        id = supplier_id,
        name = supplier_name,
        category = supplier_category,
        contactPerson = contact_person,
        email = supplier_email,
        phone = supplier_phone,
        address = null,
        city = null,
        country = null,
        rating = supplier_rating.toFloat(),
        status = SupplierStatus.valueOf(supplier_status),
        reliability = SupplierReliability.valueOf(supplier_reliability),
        lastOrderDate = null,
        paymentTerms = null,
        notes = null,
        createdAt = created_at,
        updatedAt = updated_at
    )

    val currentStockInt = current_stock?.toInt() ?: 0

    return StockItem(
        id = id,
        name = name,
        category = category,
        description = description,
        currentStock = currentStockInt,
        minStock = min_stock.toInt(),
        maxStock = max_stock.toInt(),
        unitPrice = unit_price,
        supplier = supplier,
        status = StockItemStatus.valueOf(status),
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Stock Entry mappers
fun SelectAllStockEntries.toDomainModel(): StockEntry {
    return StockEntry(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        entryDate = entry_date,
        batchNumber = batch_number,
        expiryDate = expiry_date,
        supplier = supplier_name,
        supplierId = supplier_id,
        status = EntryStatus.valueOf(status),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockEntriesByItemId.toDomainModel(): StockEntry {
    return StockEntry(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        entryDate = entry_date,
        batchNumber = batch_number,
        expiryDate = expiry_date,
        supplier = supplier_name,
        supplierId = supplier_id,
        status = EntryStatus.valueOf(status),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Stock Exit mappers
fun SelectAllStockExits.toDomainModel(): StockExit {
    return StockExit(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        exitDate = exit_date,
        customer = customer,
        orderNumber = order_number,
        deliveryAddress = delivery_address,
        status = ExitStatus.valueOf(status),
        urgency = ExitUrgency.valueOf(urgency),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

fun SelectStockExitsByItemId.toDomainModel(): StockExit {
    return StockExit(
        id = id,
        stockItemId = stock_item_id,
        productName = product_name,
        category = product_category,
        quantity = quantity.toInt(),
        unitPrice = unit_price,
        totalValue = total_value,
        exitDate = exit_date,
        customer = customer,
        orderNumber = order_number,
        deliveryAddress = delivery_address,
        status = ExitStatus.valueOf(status),
        urgency = ExitUrgency.valueOf(urgency),
        notes = notes,
        createdAt = created_at,
        updatedAt = updated_at
    )
}

// Statistics mapper
fun SelectStockStatistics.toDomainModel(): StockStatistics {
    return StockStatistics(
        totalItems = total_items?.toInt() ?: 0,
        itemsInStock = items_in_stock?.toInt() ?: 0,
        itemsLowStock = items_low_stock?.toInt() ?: 0,
        itemsOutOfStock = items_out_of_stock?.toInt() ?: 0,
        itemsOverstocked = items_overstocked?.toInt() ?: 0,
        totalStockValue = total_stock_value ?: 0.0
    )
}

// Recent movements mappers
fun SelectRecentEntries.toRecentMovement(): RecentMovement {
    return RecentMovement(
        id = id,
        productName = product_name,
        quantity = quantity.toInt(),
        date = entry_date,
        type = MovementType.ENTRY,
        description = "Entrée de ${quantity} unités de ${product_name} par ${supplier_name}"
    )
}

fun SelectRecentExits.toRecentMovement(): RecentMovement {
    return RecentMovement(
        id = id,
        productName = product_name,
        quantity = quantity.toInt(),
        date = exit_date,
        type = MovementType.EXIT,
        description = "Sortie de ${quantity} unités de ${product_name} pour ${customer}"
    )
}

// Domain to database mappers (inserts/updates)
fun StockItem.toInsertParams(): Triple<String, String, String?> {
    return Triple(name, category, description)
}

fun StockEntry.toInsertParams() = listOf(
    stockItemId,
    quantity.toLong(),
    unitPrice,
    totalValue,
    entryDate,
    batchNumber,
    expiryDate,
    supplierId,
    status.name,
    notes
)

fun StockExit.toInsertParams() = listOf(
    stockItemId,
    quantity.toLong(),
    unitPrice,
    totalValue,
    exitDate,
    customer,
    orderNumber,
    deliveryAddress,
    status.name,
    urgency.name,
    notes
)




