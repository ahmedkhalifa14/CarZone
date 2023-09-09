package com.example.domain.usecase

import com.example.domain.entity.Ad
import com.example.domain.entity.VehicleData
import com.example.domain.repo.MainRepo

class AddVehicleAdUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(ad: Ad) =
        mainRepo.addVehicleAds(ad)
}