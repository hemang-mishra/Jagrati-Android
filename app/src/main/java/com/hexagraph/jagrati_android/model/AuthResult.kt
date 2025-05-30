package com.hexagraph.jagrati_android.model

/**
 * Sealed class representing the result of authentication operations.
 * This allows for type-safe handling of different authentication outcomes.
 */
sealed class AuthResult {
    /**
     * Represents a successful authentication operation.
     * @property user The authenticated user
     */
    data class Success(val user: User) : AuthResult()

    /**
     * Represents an authentication operation that requires email verification.
     * @property email The email address that needs verification
     */
    data class VerificationNeeded(val email: String) : AuthResult()

    /**
     * Represents a failed authentication operation.
     * @property message Error message describing the failure
     */
    data class Error(val message: String) : AuthResult()

    /**
     * Represents an authentication operation that is in progress.
     */
    data object Loading : AuthResult()

    /**
     * Represents an initial state before any authentication operation has been attempted.
     */
    data object Initial : AuthResult()
}