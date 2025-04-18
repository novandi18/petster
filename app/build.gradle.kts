import org.gradle.kotlin.dsl.release
import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.room)
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists() && localPropertiesFile.isFile) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.novandiramadhan.petster"
    compileSdk = 35

    room {
        schemaDirectory("$projectDir/schemas")
    }

    defaultConfig {
        applicationId = "com.novandiramadhan.petster"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val geminiApiKey = localProperties.getProperty("GEMINI_API_KEY", "")
        val geminiModel = localProperties.getProperty("GEMINI_MODEL", "")
        val imgbbApiKey = localProperties.getProperty("IMGBB_API_KEY", "")

        buildConfigField("String", "GEMINI_API_KEY", geminiApiKey)
        buildConfigField("String", "GEMINI_MODEL", geminiModel)
        buildConfigField("String", "IMGBB_API_KEY", imgbbApiKey)
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(libs.activity.ktx)

    implementation(libs.dagger.hilt)
    implementation(libs.navigation.hilt)
    implementation(libs.firebase.firestore)
    ksp(libs.hilt.compiler)
    ksp(libs.hilt.android.compiler)

    implementation(libs.kotlinx.serialization)
    implementation(libs.navigation.compose)

    implementation(libs.viewModel.ktx)
    implementation(libs.viewModel.compose)
    implementation(libs.livedata.ktx)
    implementation(libs.runtime.compose)
    ksp(libs.lifecycle.compiler)

    implementation(libs.datastore)
    implementation(libs.material.icons)

    implementation(libs.room.ktx)
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    annotationProcessor(libs.room.compiler)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.converter)
    implementation(libs.gson)
    implementation(libs.okHttp)
    implementation(libs.okHttp.logging)

    implementation(libs.firebase.auth.ktx)

    implementation(libs.paging)
    implementation(libs.paging.compose)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.easy.crop)

    implementation(libs.compose.markdown)
    implementation(libs.gen.ai)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("reflect"))
}