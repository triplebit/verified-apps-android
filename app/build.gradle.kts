plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.10"
    id("io.gitlab.arturbosch.detekt")
}

import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

detekt {
    config.setFrom("$rootDir/config/detekt/detekt.yml")
    buildUponDefaultConfig = true
    // Report to SARIF instead of failing the build.
    ignoreFailures = true
}

tasks.withType<Detekt>().configureEach {
    reports {
        sarif.required.set(true)
    }
}

android {
    namespace = "org.privacyguides.verifiedapps"
    compileSdk = 37
    buildToolsVersion = "37.0.0"
    ndkVersion = "28.1.13356709"

    // -PplayBuild=true builds the Play variant (distinct applicationId, Play
    // upload key). Without it, the build matches the canonical GitHub build.
    val playBuild = (project.findProperty("playBuild") as? String)?.toBoolean() == true

    defaultConfig {
        applicationId = "org.privacyguides.verifiedapps"
        minSdk = 34
        targetSdk = 37
        // CalVer YY.MM.PATCH (e.g. 26.6.0). versionCode = YY*1_000_000 + MM*10_000 + PATCH.
        val releaseVersionYear = 26
        val releaseVersionMonth = 6
        val releaseVersionPatch = 9
        versionCode = releaseVersionYear * 1_000_000 + releaseVersionMonth * 10_000 + releaseVersionPatch
        versionName = "${releaseVersionYear}.${releaseVersionMonth}.${releaseVersionPatch}"

        vectorDrawables {
            useSupportLibrary = true
        }

        ndk {
            abiFilters.clear()
            abiFilters.addAll(listOf("arm64-v8a", "x86_64", "armeabi-v7a", "x86"))
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    androidResources {
        generateLocaleConfig = true
        localeFilters += listOf("en")
    }
    signingConfigs {
        create("release") {
            val keystorePath = System.getenv("ANDROID_KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
            }
            enableV3Signing = true
            enableV4Signing = true
        }
        // Play upload key, separate from the GitHub release key. Google
        // re-signs on its end, so no v4/.idsig here.
        create("playUpload") {
            val keystorePath = System.getenv("PLAY_UPLOAD_KEYSTORE_PATH")
            if (!keystorePath.isNullOrBlank()) {
                storeFile = file(keystorePath)
                storePassword = System.getenv("PLAY_UPLOAD_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("PLAY_UPLOAD_KEY_ALIAS")
                keyPassword = System.getenv("PLAY_UPLOAD_KEY_PASSWORD")
            }
            enableV3Signing = true
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            // Bundle native debug symbols for the one .so we ship
            // (libdatastore_shared_counter.so, pulled in by androidx.datastore)
            ndk {
                debugSymbolLevel = "FULL"
            }
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            val activeSigning =
                signingConfigs.getByName(if (playBuild) "playUpload" else "release")
            activeSigning.takeIf { it.storeFile?.exists() == true }?.let {
                signingConfig = it
            }
            if (playBuild) {
                // -> org.privacyguides.verifiedapps.play
                applicationIdSuffix = ".play"
            }
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
        }
        create("staging") {
            initWith(getByName("release"))
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    // Omit the Google-only dependency metadata block from both APK and bundle.
    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.10.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.navigation:navigation-compose:2.9.8")
    implementation("androidx.datastore:datastore-preferences:1.2.1")
    implementation("com.google.accompanist:accompanist-drawablepainter:0.37.3")

    implementation(platform("androidx.compose:compose-bom:2026.05.01"))
    implementation("androidx.compose.material3:material3:1.5.0-alpha21")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")

    testImplementation("junit:junit:4.13.2")
}
