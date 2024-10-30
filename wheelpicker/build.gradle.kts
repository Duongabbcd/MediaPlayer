plugins {
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.android.library")
}

android {
    namespace = "com.cheonjaeung.powerwheelpicker.android"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    api(libs.androidx.recylerview)
    implementation(libs.simplecarousel)
}
