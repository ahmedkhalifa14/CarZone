package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class RegisterUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(email: String, password: String) =
        mainRepo.register(email,password)
}