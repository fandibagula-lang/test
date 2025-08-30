package org.babetech.borastock.domain.usecase

import org.babetech.borastock.data.repository.StockRepository
import org.babetech.borastock.data.models.StockStatistics

class GetStockStatisticsUseCase(
    private val repository: StockRepository
) {
    suspend operator fun invoke(): StockStatistics {
        return repository.getStockStatistics()
    }
}
