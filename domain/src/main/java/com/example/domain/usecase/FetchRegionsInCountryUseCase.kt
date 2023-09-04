package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class FetchRegionsInCountryUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(
        username: String,
        country: String,
        featureCode: String
    ) = mainRepo.fetchRegionsInCountry(username, country, featureCode)
}