package com.hexagraph.jagrati_android.di

import com.hexagraph.jagrati_android.api.AuthProvider
import com.hexagraph.jagrati_android.service.auth.KtorAuthService
import com.hexagraph.jagrati_android.service.permission.KtorPermissionService
import com.hexagraph.jagrati_android.service.role.KtorRoleService
import com.hexagraph.jagrati_android.service.user.KtorUserService
import com.hexagraph.jagrati_android.service.volunteer.KtorVolunteerRequestService
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.statement.request
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private const val BASE_URL = "https://57f6-2409-40e3-3189-3db5-533f-6e2b-9fa1-57fa.ngrok-free.app"
private const val TIMEOUT = 6000L

val networkModule = module {

    // Create AuthProvider
    single { AuthProvider(get(), BASE_URL) }

    // Create main HttpClient with auth
    single {
        val authProvider = get<AuthProvider>()

        HttpClient(Android) {
            // Install ContentNegotiation to handle JSON serialization
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            // Configure timeout
            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }

            // Enhanced logging with detailed request/response information
            install(Logging) {
                logger = object : Logger {
                    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())

                    override fun log(message: String) {
                        val timestamp = dateFormat.format(Date())
                        android.util.Log.d("KtorClient", "[$timestamp] $message")
                    }
                }
                level = LogLevel.ALL
            }

            // Add response observer for additional logging of response status and timing
            install(ResponseObserver) {
                onResponse { response ->
                    val status = response.status
                    val url = response.request.url
                    val time = response.responseTime.timestamp - response.requestTime.timestamp

                    val logMessage =
                        "Response: $url - ${status.value} (${status.description}) - ${time}ms"
                    val logColor =
                        if (status == HttpStatusCode.OK || status == HttpStatusCode.Created) {
                            "Response ✅"
                        } else {
                            "Response ❌"
                        }

                    android.util.Log.d("KtorResponse", "$logColor: $logMessage")
                }
            }

            // Configure authentication with token refresh
            install(Auth) {
                with(authProvider) {
                    configureBearerAuth()
                }
            }

            // Default request configuration
            defaultRequest {
                contentType(ContentType.Application.Json)
                url(BASE_URL)
            }

        }
    }

    // Provide KtorAuthService
    single { KtorAuthService(get(), BASE_URL) }

    // Provide KtorPermissionService
    single { KtorPermissionService(get(), BASE_URL) }

    // Provide KtorRoleService
    single { KtorRoleService(get(), BASE_URL) }

    // Provide KtorUserService
    single { KtorUserService(get(), BASE_URL) }

    // Provide KtorVolunteerRequestService
    single { KtorVolunteerRequestService(get(), BASE_URL) }
}