plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.chaquo.python")
}

android {
    namespace = "com.hackfire.hftube"
    compileSdk = 34

    // The release keystore is committed as base64 text (keystore/release-
    // keystore.jks.base64) rather than a binary .jks so it survives normal
    // git operations cleanly. Decode it once, on first build, into
    // app/keystore.jks — that decoded file IS gitignored (see .gitignore),
    // it's just a local build artifact regenerated from the committed text.
    //
    // This is a self-signed key committed in the open on purpose: HF-Tube
    // isn't distributed through Play Store, so there's no store-identity
    // concern — the only thing a consistent signing key buys here is that
    // future versions install as *updates* instead of needing an uninstall
    // first. If you want a private key instead, generate your own keystore
    // and point keystoreFile/passwords below at it (ideally via
    // gradle.properties, which is gitignored, rather than editing this file).
    val decodedKeystore = file("keystore.jks")
    if (!decodedKeystore.exists()) {
        val encoded = rootProject.file("keystore/release-keystore.jks.base64")
        if (encoded.exists()) {
            decodedKeystore.writeBytes(java.util.Base64.getDecoder().decode(encoded.readText().trim()))
        }
    }

    signingConfigs {
        create("release") {
            storeFile = decodedKeystore
            storePassword = "hftube_release_2026"
            keyAlias = "hftube"
            keyPassword = "hftube_release_2026"
        }
    }

    defaultConfig {
        applicationId = "com.hackfire.hftube"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"
        ndk { abiFilters += listOf("arm64-v8a", "armeabi-v7a") }
        python {
            pip {
                // yt-dlp is the whole extraction/download engine; no other
                // Python deps needed.
                install("yt-dlp")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
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
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.fragment:fragment-ktx:1.8.1")
    implementation("androidx.activity:activity-ktx:1.9.1")
    implementation("androidx.webkit:webkit:1.11.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
}
