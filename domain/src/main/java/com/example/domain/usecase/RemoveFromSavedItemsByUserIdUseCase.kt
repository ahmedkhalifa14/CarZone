package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class RemoveFromSavedItemsByUserIdUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(userId: String, itemId: String) =
        mainRepo.removeFromSavedItemsByUserId(userId, itemId)
}