package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class GetAllAdsByVehicleTypeUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(targetVehicleType: String) =
        mainRepo.getAllAdsByVehicleType(targetVehicleType)

}