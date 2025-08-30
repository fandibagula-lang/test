package org.babetech.borastock.domain.usecase

import org.babetech.borastock.data.repository.StockRepository
import org.babetech.borastock.data.models.Supplier

class GetSuppliersDistributionUseCase(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): Map<String, Int> {
        val suppliers = repository.getAllSuppliers()
        var result = emptyMap<String, Int>()
        suppliers.collect { list ->
            result = list.groupingBy { it.country ?: "Inconnu" }.eachCount()
        }
        return result
    }
}
