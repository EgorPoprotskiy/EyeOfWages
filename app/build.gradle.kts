plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    //Room
    alias(libs.plugins.ksp)

    //3.1 Плагин для сериализации(преобразует JSON-строку в объект Kotlin)
    kotlin("plugin.serialization") version "2.1.20"

}

android {
    namespace = "com.egorpoprotskiy.eyeofwages"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.egorpoprotskiy.eyeofwages"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        //Для использование векторных изображений в Compose
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    //Navigation
    implementation(libs.androidx.navigation.compose)
    //ViewModel
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.compose.runtime.livedata)
    //Для свайпа
    implementation(libs.androidx.compose.material)
    //Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Если ты используешь бандл:
    implementation(libs.bundles.network.deps)

    // Или если ты добавляешь по одной:
    implementation(libs.retrofit.core)
//     implementation(libs.retrofit.scalars)
    implementation(platform(libs.okhttp.bom)) // Важно для BOM
    implementation(libs.okhttp.logging.interceptor)
//     implementation(libs.jsoup)

    // Если ты добавил okhttp-bom, то тебе также понадобится обычный okhttp, но его версия будет управляться BOM
    implementation("com.squareup.okhttp3:okhttp") // Добавь, если Retrofit не тянет его транзитивно, или если нужен базовый OkHttp

}