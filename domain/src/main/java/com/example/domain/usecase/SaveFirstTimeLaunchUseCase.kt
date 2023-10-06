package com.example.domain.usecase

import com.example.domain.entity.LaunchInfo
import com.example.domain.repo.MainRepo

class SaveFirstTimeLaunchUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(launchInfo: LaunchInfo) =
        mainRepo.saveAsFirstTimeLaunch(launchInfo)

}