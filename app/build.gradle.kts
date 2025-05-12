plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

val releaseStoreFile = project.findProperty("RELEASE_STORE_FILE") as String
val releaseStorePassword = project.findProperty("RELEASE_STORE_PASSWORD") as String
val releaseKeyAlias = project.findProperty("RELEASE_KEY_ALIAS") as String
val releaseKeyPassword = project.findProperty("RELEASE_KEY_PASSWORD") as String


android {
    namespace = "com.muhammedalikocabey.securecheck"
    compileSdk = 35

    defaultConfig {
        buildConfigField("String", "SAFETYNET_API_KEY", "\"Think_Of_It_As_A_Real_API_Key\"")
        applicationId = "com.muhammedalikocabey.securecheck"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = File(releaseStoreFile)
            storePassword = releaseStorePassword
            keyAlias = releaseKeyAlias
            keyPassword = releaseKeyPassword
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            signingConfig = signingConfigs.getByName("release")
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

    // SafetyNet
    implementation(libs.play.services.safetynet)

    // Integrity API
    implementation(libs.play.integrity.api)

    // KSP + Dagger
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Security + Crypto
    implementation(libs.security.crypto)
    implementation(libs.androidx.security.crypto.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.constraintlayout.compose)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(kotlin("reflect"))
}