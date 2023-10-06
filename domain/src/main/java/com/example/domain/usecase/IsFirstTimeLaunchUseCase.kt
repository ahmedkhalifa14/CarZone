package com.example.domain.usecase

import com.example.domain.entity.LaunchInfo
import com.example.domain.repo.MainRepo
import kotlinx.coroutines.flow.Flow

class IsFirstTimeLaunchUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(): Flow<LaunchInfo> =
        mainRepo.isFirstTimeLaunch()
}