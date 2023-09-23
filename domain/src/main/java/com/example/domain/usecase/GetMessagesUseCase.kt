package com.example.domain.usecase

import androidx.lifecycle.LiveData
import com.example.domain.entity.ChatMessage
import com.example.domain.repo.MainRepo

class GetMessagesUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(receiverID: String, senderID: String): LiveData<List<ChatMessage>> =
        mainRepo.getMessages(receiverID, senderID)
}