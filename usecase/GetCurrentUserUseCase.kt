package org.babetech.borastock.domain.usecase

import org.babetech.borastock.data.models.datasource.repository.BoraStockRepository
import org.babetech.borastock.ui.screens.auth.viewmodel.User
import org.babetech.borastock.utils.Result // <-- ici !

class GetCurrentUserUseCase(
    private val repository: BoraStockRepository
) {
    suspend operator fun invoke(): Result<User?> {
        return repository.getCurrentUser()
    }
}
