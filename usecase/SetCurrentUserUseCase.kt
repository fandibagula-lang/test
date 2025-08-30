package org.babetech.borastock.domain.usecase

import org.babetech.borastock.data.models.datasource.repository.BoraStockRepository
import org.babetech.borastock.ui.screens.auth.viewmodel.User
import org.babetech.borastock.utils.Result

class SetCurrentUserUseCase (private val repository: BoraStockRepository) {

    suspend operator fun invoke(user: User?) : Result<User?>{

        return repository.setCurrentUser(user)
    }
}