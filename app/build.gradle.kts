import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.firebase.crashlytics)
}

// Load properties from local.properties file
val localPropertiesFile = rootProject.file("local.properties")
// Read web client ID from local.properties

val localProperties = Properties()
    .takeIf { localPropertiesFile.exists() }
    ?.apply { load(FileInputStream(localPropertiesFile)) }


fun propOrEnv(name: String, default: String = ""): String =
    localProperties?.getProperty(name) ?: System.getenv(name) ?: default

android {
    namespace = "com.hexagraph.jagrati_android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hexagraph.jagrati_android"
        minSdk = 26
        targetSdk = 36
        versionCode = 6
        versionName = "1.1.2"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            // For local builds, read from local.properties
            val keystorePropertiesFile = rootProject.file("local.properties")

            if (keystorePropertiesFile.exists()) {
                val keystoreProperties = Properties()
                keystoreProperties.load(FileInputStream(keystorePropertiesFile))

                storeFile = file(keystoreProperties["RELEASE_STORE_FILE"] as String)
                storePassword = keystoreProperties["RELEASE_STORE_PASSWORD"] as String
                keyAlias = keystoreProperties["RELEASE_KEY_ALIAS"] as String
                keyPassword = keystoreProperties["RELEASE_KEY_PASSWORD"] as String
            } else {
                // For CI/CD, read from environment variables
                val keystorePath = System.getenv("KEYSTORE_PATH") ?: "release.jks"
                storeFile = file(keystorePath)
                storePassword = System.getenv("RELEASE_STORE_PASSWORD")
                keyAlias = System.getenv("RELEASE_KEY_ALIAS")
                keyPassword = System.getenv("RELEASE_KEY_PASSWORD")
            }

        }
    }


    buildTypes {
        debug {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            resValue("string", "app_name", "Jagrati Debug")

            resValue("string", "WEB_CLIENT_ID", propOrEnv("WEB_CLIENT_ID"))
            resValue("string", "BASE_URL", propOrEnv("BASE_URL"))
            resValue("string", "IMAGE_KIT_URL_ENDPOINT", propOrEnv("IMAGE_KIT_URL_ENDPOINT"))
            resValue("string", "IMAGE_KIT_PUBLIC_KEY", propOrEnv("IMAGE_KIT_PUBLIC_KEY"))
        }
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            resValue("string", "app_name", "Jagrati")
            signingConfig = signingConfigs.getByName("release")

            resValue("string", "WEB_CLIENT_ID", propOrEnv("PROD_WEB_CLIENT_ID"))
            resValue("string", "BASE_URL", propOrEnv("PROD_BASE_URL"))
            resValue("string", "IMAGE_KIT_URL_ENDPOINT", propOrEnv("PROD_IMAGE_KIT_URL_ENDPOINT"))
            resValue("string", "IMAGE_KIT_PUBLIC_KEY", propOrEnv("PROD_IMAGE_KIT_PUBLIC_KEY"))
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.crashlytics)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Room dependencies
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    annotationProcessor(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    testImplementation(libs.androidx.room.testing)


    //Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    // Ktor client
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.serialization.jvm)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    // Add these missing dependencies for auth features
    implementation(libs.ktor.client.auth)
    implementation(libs.ktor.client.okhttp)

    // Koin core for DI
    implementation(libs.koin.core)
    // Koin for Android
    implementation(libs.koin.androidx.compose)
    // Koin for Android (includes all Android-related features)
    implementation(libs.koin.android)

    //Navigation
    implementation(libs.androidx.navigation.compose)

    //Serialization
    implementation(libs.kotlinx.serialization.json)

    //GSON
    implementation(libs.gson)


    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.video)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.mlkit.vision)
    implementation(libs.androidx.camera.extensions)


    implementation(libs.tensorflow.lite.gpu)
    implementation(libs.tensorflow.lite)

    implementation(libs.face.detection)
    implementation(libs.play.services.mlkit.face.detection)

    implementation(libs.guava)

    implementation(libs.androidx.datastore.preferences)

    //Navigation 3 API
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    // Chucker
    debugImplementation(libs.chucker)
    releaseImplementation(libs.chucker.noop)

    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.koin.androidx.workmanager)

    // Play Core App Update
    implementation(libs.app.update)

    // Lottie animations
    implementation(libs.lottie.compose)
    implementation(libs.app.update.ktx)
}
