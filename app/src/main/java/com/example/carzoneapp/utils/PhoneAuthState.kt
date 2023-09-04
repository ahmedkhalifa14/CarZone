package com.example.carzoneapp.utils

sealed class PhoneAuthState {
    object Initial : PhoneAuthState()
    object Loading : PhoneAuthState()
    object VerificationCodeSent : PhoneAuthState()
    object Verified : PhoneAuthState()
    object Error : PhoneAuthState()
}