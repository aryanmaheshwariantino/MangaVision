package com.example.mangavision.domain.repository

import com.example.mangavision.domain.model.User
import com.google.android.datatransport.runtime.dagger.Provides


interface AuthRepository {
    suspend fun signIn(email: String, password: String): Boolean
    suspend fun signOut()
    suspend fun isUserLoggedIn(): Boolean
    suspend fun getLoggedInUser(): User?
}