package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class GetUserInfoUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(userId:String)=
        mainRepo.getUserByUserId(userId)

}