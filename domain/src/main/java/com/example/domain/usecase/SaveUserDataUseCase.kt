package com.example.domain.usecase

import com.example.domain.entity.User
import com.example.domain.repo.MainRepo

class SaveUserDataUseCase (private val mainRepo: MainRepo) {
    suspend  operator fun invoke(user: User) =
        mainRepo.saveUserData(user)
}