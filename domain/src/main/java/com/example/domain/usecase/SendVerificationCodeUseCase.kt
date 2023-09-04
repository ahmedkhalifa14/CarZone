package com.example.domain.usecase

import com.example.domain.repo.MainRepo

class SendVerificationCodeUseCase(private val mainRepo: MainRepo) {
    suspend operator fun invoke(phoneNumber:String):String{
      return  mainRepo.sendVerificationCode(phoneNumber)
    }
}