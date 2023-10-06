package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class GetSavedItemsByUserIdUseCase (private val mainRepo: MainRepo) {
    suspend operator fun invoke(userId: String) =
        mainRepo.getSavedItemsByUserId(userId)
}
