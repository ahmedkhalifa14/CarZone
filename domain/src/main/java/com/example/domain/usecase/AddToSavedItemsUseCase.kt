package com.example.domain.usecase

import com.example.domain.entity.SavedItem
import com.example.domain.repo.MainRepo

class AddToSavedItemsUseCase (private val mainRepo: MainRepo) {
    suspend operator fun invoke(savedItem: SavedItem) =
        mainRepo.addToSavedItems(savedItem)
}