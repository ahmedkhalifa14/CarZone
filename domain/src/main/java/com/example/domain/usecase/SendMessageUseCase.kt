package com.example.domain.usecase

import com.example.domain.entity.ChatMessage
import com.example.domain.repo.MainRepo

class SendMessageUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(message: ChatMessage)=
        mainRepo.sendMessage(message)
}