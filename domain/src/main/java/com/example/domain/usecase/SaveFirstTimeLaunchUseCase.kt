package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class SaveFirstTimeLaunchUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(isFirstTimeLaunch: Boolean) =
        mainRepo.saveAsFirstTimeLaunch(isFirstTimeLaunch)

}