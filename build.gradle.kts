import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinComposeCompiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.ksp)
    alias(libs.plugins.koin)
}

group = "io.github.dumbgreenfish"
version = "0.0.1"

val jvmTargetVersion = JvmTarget.fromTarget(libs.versions.jvm.get())
val javaVersion = JavaVersion.toVersion(libs.versions.jvm.get())

kotlin {
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(jvmTargetVersion)
        }
    }

    androidTarget {
        compilerOptions {
            jvmTarget.set(jvmTargetVersion)
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "dialogueforge.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.annotations)
                implementation(libs.koin.viewmodel)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
            }
        }

        val wasmJsMain by getting
    }
}

android {
    namespace = "io.github.dumbgreenfish.dialogueforge"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    androidResources {
        localeFilters += arrayOf("en", "ru")
    }

    defaultConfig {
        applicationId = "io.github.dumbgreenfish.dialogueforge"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = version.toString()
    }

    compileOptions {
        sourceCompatibility = javaVersion
        targetCompatibility = javaVersion
    }

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Msi, TargetFormat.Exe, TargetFormat.Deb)
            packageName = "DialogueForge"
            packageVersion = version.toString()
        }
    }
}

koinCompiler {
    userLogs = true
}