package org.babetech.borastock.domain.usecase

import org.babetech.borastock.data.repository.StockRepository
import org.babetech.borastock.data.models.RecentMovement

class GetStockMovementsUseCase(
    private val repository: StockRepository
) {
    suspend operator fun invoke(limit: Int = 20): List<RecentMovement> {
        return repository.getRecentMovements(limit)
    }
}
