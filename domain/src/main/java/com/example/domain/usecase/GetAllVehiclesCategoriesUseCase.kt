package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class GetAllVehiclesCategoriesUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke()=
        mainRepo.getAllVehiclesCategories()
}