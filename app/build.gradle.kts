plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
    id("kotlin-kapt")
    id ("dagger.hilt.android.plugin")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.pokedex"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pokedex"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core library
    implementation("androidx.core:core-ktx:1.12.0")

    // Compose
    val composeVersion = "2023.03.00"
    implementation(platform("androidx.compose:compose-bom:$composeVersion"))
    implementation("androidx.compose.runtime:runtime")
    implementation("androidx.compose.runtime:runtime-livedata")
    implementation("androidx.compose.runtime:runtime-rxjava2")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.1.2")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Activity Compose
    implementation("androidx.activity:activity-compose:1.8.2")

    // Compose Lifecycles
    val lifecycleVersion = "2.7.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:$composeVersion"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // System UI controller
    implementation ("com.google.accompanist:accompanist-systemuicontroller:0.32.0")

    // Shared Elements
    implementation("com.mxalbert.sharedelements:shared-elements:0.1.0-SNAPSHOT")

    // Dagger Hilt
    implementation("com.google.dagger:hilt-android:2.48.1")
    kapt("com.google.dagger:hilt-android-compiler:2.48.1")
    implementation("androidx.hilt:hilt-work:1.1.0")
    kapt("androidx.hilt:hilt-compiler:1.1.0")
    implementation("androidx.work:work-runtime-ktx:2.9.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

    // GSON
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation ("com.google.code.gson:gson:2.10")

    // Timber
    implementation ("com.jakewharton.timber:timber:4.7.1")

    // Coil
    implementation("io.coil-kt:coil-compose:2.5.0")

    implementation ("androidx.palette:palette-ktx:1.0.0")

    // define any required OkHttp artifacts without version
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")

    // define a BOM and its version
    implementation(platform("com.squareup.okhttp3:okhttp-bom:5.0.0-alpha.7"))
    implementation ("io.karn:khttp-android:0.1.0")

    // Compose Destinations
    implementation ("io.github.raamcosta.compose-destinations:core:1.9.54")
    ksp ("io.github.raamcosta.compose-destinations:ksp:1.9.54")
}