package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class GetUserChatListUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(userId: String) =
        mainRepo.getChatMessages(userId).sortedByDescending { it.timestamp }

}