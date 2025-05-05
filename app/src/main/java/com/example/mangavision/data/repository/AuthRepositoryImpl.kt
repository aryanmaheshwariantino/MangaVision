package com.example.mangavision.data.repository

import android.util.Log
import com.example.mangavision.data.local.dao.UserDao
import com.example.mangavision.data.local.entity.UserEntity
import com.example.mangavision.data.local.entity.toDomainUser
import com.example.mangavision.di.IoDispatcher
import com.example.mangavision.domain.model.User
import com.example.mangavision.domain.repository.AuthRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Boolean {
        return withContext(dispatcher) {
            try {
                val existingUser = userDao.getUserByEmail(email)
                if (existingUser == null) {
                    val newUser = UserEntity(
                        email = email,
                        password = hashPassword(password),
                        isLoggedIn = true
                    )
                    userDao.insertUser(newUser)
                    true
                } else {
                    if (verifyPassword(password, existingUser.password)) {
                        userDao.updateLoginStatus(email, true)
                        true
                    } else {
                        false
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", "Sign-in failed for $email", e)
                false
            }
        }
    }

    override suspend fun signOut() {
        return withContext(dispatcher) {
            try {
                userDao.getLoggedInUser()?.email?.let { email ->
                    userDao.updateLoginStatus(email, false)
                    true
                } ?: false
            } catch (e: Exception) {
                Log.e("AuthRepository", "Sign-out failed", e)
                false
            }
        }
    }

    override suspend fun isUserLoggedIn(): Boolean {
        return withContext(dispatcher) {
            try {
                userDao.getLoggedInUser() != null
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to check login status", e)
                false
            }
        }
    }

    override suspend fun getLoggedInUser(): User? {
        return withContext(dispatcher) {
            try {
                userDao.getLoggedInUser()?.toDomainUser()
            } catch (e: Exception) {
                Log.e("AuthRepository", "Failed to get logged-in user", e)
                null
            }
        }
    }

    private fun hashPassword(password: String): String {
        return password.hashCode().toString()
    }

    private fun verifyPassword(inputPassword: String, hashedPassword: String): Boolean {
        return hashPassword(inputPassword) == hashedPassword
    }
}