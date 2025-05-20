package com.hexagraph.jagrati_android.service.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.hexagraph.jagrati_android.model.ResponseError
import com.hexagraph.jagrati_android.util.Resource
import kotlinx.coroutines.tasks.await

class FirebaseAuthService(private val firebaseAuth: FirebaseAuth) {

    suspend fun signInWithEmailPassword(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                Resource.success(result.user!!)
            } else {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = "User not found"
                })
            }
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }
    }

    suspend fun createAccountWithEmailAndPassword(email: String, password: String): Resource<FirebaseUser>{
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if(result.user != null){
                Resource.success(result.user!!)
            } else {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = "User not found"
                })
            }
        }catch (e: Exception){
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            })
        }
    }

    suspend fun sendEmailVerificationLink(): Resource<Unit> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.sendEmailVerification().await()
                Resource.success(Unit)
            } else {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = "No user logged in."
                })
            }
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }
    }

    suspend fun changeUserProfile(name: String): Resource<Unit>{
        return try {
            val profileUpdates = userProfileChangeRequest {
                displayName = name
            }
            val user = firebaseAuth.currentUser
            if (user != null) {
                user.updateProfile(profileUpdates).await()
                Resource.success(Unit)
            } else {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = "No user logged in."
                })
            }
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }
    }

    suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Resource.success(Unit)
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }
    }

    suspend fun reAuthenticateAfterEmailVerification(email: String, password: String): Resource<FirebaseUser> {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val credential = EmailAuthProvider.getCredential(email, password)
                user.reauthenticate(credential).await()
                Resource.success(user)
            } else {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = "No user logged in."
                })
            }
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }

    }

    suspend fun signInWithCredentials(authCredential: AuthCredential): Resource<FirebaseUser>{
        return try {
            val result = firebaseAuth.signInWithCredential(authCredential).await()
            if (result.user != null) {
                Resource.success(result.user!!)
            } else {
                Resource.failure(error = ResponseError.UNKNOWN.apply {
                    actualResponse = "User not found"
                })
            }
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }
    }

    fun signOut(): Resource<Unit> {
        return try {
            firebaseAuth.signOut()
            Resource.success(Unit)
        } catch (e: Exception) {
            return Resource.failure(error = ResponseError.UNKNOWN.apply {
                actualResponse = e.message
            }
            )
        }
    }
}