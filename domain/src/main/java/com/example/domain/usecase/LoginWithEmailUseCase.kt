package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class LoginWithEmailUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(email: String, password: String)
    = mainRepo.login(email, password)
}