package com.example.mangavision.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mangavision.domain.model.User

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val email: String,
    val password: String,
    val isLoggedIn: Boolean = false
)

fun UserEntity.toDomainUser(): User {
    return User(
        email = this.email,
        isLoggedIn = this.isLoggedIn
    )
}