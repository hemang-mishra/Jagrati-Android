import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.gms.google.services)
}

// Load properties from local.properties file
val localPropertiesFile = rootProject.file("local.properties")

android {
    namespace = "com.hexagraph.jagrati_android"
    compileSdk = 36

    defaultConfig {
        // Read web client ID from local.properties
        val localProperties = Properties()
            .takeIf { localPropertiesFile.exists() }
            ?.apply { load(FileInputStream(localPropertiesFile)) }

        fun addStringRes(name: String) =
            resValue("string", name, localProperties?.getProperty(name)?.toString().toString())

        addStringRes("WEB_CLIENT_ID")
        addStringRes("BASE_URL")
        addStringRes("IMAGE_KIT_URL_ENDPOINT")
        addStringRes("IMAGE_KIT_PUBLIC_KEY")
        addStringRes("IMAGE_KIT_PRIVATE_KEY")
        applicationId = "com.hexagraph.jagrati_android"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
//        debug {
//            applicationIdSuffix = ".debug"
//            versionNameSuffix = "-debug"
//        }
        release {


            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    // Ktor client
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.serialization.jvm)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    // Add these missing dependencies for auth features
    implementation(libs.ktor.client.auth)

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


    // The following line is optional, as the core library is included indirectly by camera-camera2
    implementation(libs.androidx.camera.core)
    implementation(libs.androidx.camera.camera2)
    // If you want to additionally use the CameraX Lifecycle library
    implementation(libs.androidx.camera.lifecycle)
    // If you want to additionally use the CameraX VideoCapture library
    implementation(libs.androidx.camera.video)
    // If you want to additionally use the CameraX View class
    implementation(libs.androidx.camera.view)
    // If you want to additionally add CameraX ML Kit Vision Integration
    implementation(libs.androidx.camera.mlkit.vision)
    // If you want to additionally use the CameraX Extensions library
    implementation(libs.androidx.camera.extensions)


    implementation(libs.tensorflow.lite.task.vision)
    implementation(libs.tensorflow.lite.gpu.delegate.plugin)
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

    //Image kit
    implementation("com.github.imagekit-developer.imagekit-android:imagekit-android:3.0.1")
}
