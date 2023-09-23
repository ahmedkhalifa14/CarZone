package com.example.domain.usecase

import com.example.domain.entity.UserChat
import com.example.domain.repo.MainRepo

class SaveUserChatListUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(userChat: UserChat) {
        mainRepo.saveUserChats(userChat)
    }
}