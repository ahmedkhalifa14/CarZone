package com.example.carzoneapp.utils

import android.content.Intent
import com.example.domain.entity.GoogleAccountInfo

sealed class AuthState {
    object Initial : AuthState()
    object Loading : AuthState()
    class GoogleSignIn(val signInIntent: Intent) : AuthState()
    class Success(val user: GoogleAccountInfo) : AuthState()
    class Error(val error: String) : AuthState()
}