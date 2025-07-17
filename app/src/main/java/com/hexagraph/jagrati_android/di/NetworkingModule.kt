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

private const val BASE_URL = "https://1fc5-2409-40e3-3189-3db5-8d95-a3c8-4a3-703d.ngrok-free.app"
private const val TIMEOUT = 6000L

val networkModule = module {

    factory { AuthProvider(get(), BASE_URL) }

    single {
        val authProvider = get<AuthProvider>()

        HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                })
            }

            install(HttpTimeout) {
                requestTimeoutMillis = TIMEOUT
                connectTimeoutMillis = TIMEOUT
                socketTimeoutMillis = TIMEOUT
            }

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

            install(Auth) {
                with(authProvider) {
                    configureBearerAuth()
                }
            }

            defaultRequest {
                contentType(ContentType.Application.Json)
                url(BASE_URL)
            }

        }
    }

    single { KtorAuthService(get(), BASE_URL) }

    single { KtorPermissionService(get(), BASE_URL) }

    single { KtorRoleService(get(), BASE_URL) }

    single { KtorUserService(get(), BASE_URL) }

    single { KtorVolunteerRequestService(get(), BASE_URL) }
}