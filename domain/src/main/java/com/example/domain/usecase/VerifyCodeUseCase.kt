package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class VerifyCodeUseCase (private val mainRepo: MainRepo) {
    suspend operator fun invoke(verificationId: String,code: String) {
        return mainRepo.verifyCode(verificationId,code)
    }
}