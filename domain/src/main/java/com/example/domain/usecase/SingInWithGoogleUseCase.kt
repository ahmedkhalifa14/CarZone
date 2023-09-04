package com.example.domain.usecase

import com.example.domain.repo.MainRepo
import com.google.firebase.auth.AuthResult

class SignInWithGoogleUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(idToken: String): AuthResult {
        return mainRepo.signInWithGoogle(idToken)
    }
}