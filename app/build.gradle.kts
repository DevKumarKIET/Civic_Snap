plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    kotlin("plugin.serialization") version "2.2.20"
}

android {
    namespace = "com.example.civicsnap"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.civicsnap"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.firebase:firebase-auth:24.0.1")
    implementation("com.google.android.gms:play-services-auth:21.4.0") // for Google Sign-In
    implementation("com.google.firebase:firebase-auth-ktx:23.2.1")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.7")
    implementation("androidx.credentials:credentials:1.5.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.5.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")
    implementation("com.google.firebase:firebase-database:22.0.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-ktx:1.8.0")
    // Fragment/Activity KTX (if needed)
    implementation("androidx.fragment:fragment-ktx:1.7.0")
    implementation("androidx.activity:activity-ktx:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
    // For lifecycleScope
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    // For coroutine support
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation ("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")




    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))  // latest stable
    implementation ("com.google.firebase:firebase-auth")
    implementation ("com.google.firebase:firebase-database")
    implementation ("com.google.firebase:firebase-analytics")
    implementation ("io.supabase:supabase-kt-android:1.2.4")
    implementation ("io.ktor:ktor-client-okhttp:2.0.0")
    implementation ("io.ktor:ktor-client-auth:2.0.0")
    implementation ("io.ktor:ktor-client-serialization:2.0.0")


    //Supabase
    implementation(platform("io.github.jan-tennert.supabase:bom:2.0.0-rc-1"))
    implementation("io.github.jan-tennert.supabase:postgrest-kt")
    implementation("io.ktor:ktor-client-android:3.2.3")
    implementation("io.github.jan-tennert.supabase-kt:supabase-android:2.5.0")
    implementation("io.github.jan-tennert.supabase-kt:postgrest-kt:2.5.0")
    implementation("io.github.jan-tennert.supabase-kt:storage-kt:2.5.0")

}
