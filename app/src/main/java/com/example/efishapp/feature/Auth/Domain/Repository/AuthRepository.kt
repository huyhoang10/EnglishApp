package com.example.efishapp.feature.Auth.Domain.Repository

interface AuthRepository {
    suspend fun loginWithEmail(email: String, password: String): Result<Unit>
    suspend fun register(email: String, password: String): Result<Unit>
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun clearSession(): Result<Unit>
    fun isUserLoggedIn(): Boolean
}