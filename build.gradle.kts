import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinComposeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.ksp)
    alias(libs.plugins.koin)
}

val groupName: String by project
val appName: String by project
val projectVersion: String by project
val projectUuid: String by project

val packageNamespace = "$groupName.$appName"

group = groupName
version = projectVersion

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
                outputFileName = "$appName.js"
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
                implementation(compose.materialIconsExtended)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.annotations)
                implementation(libs.koin.viewmodel)
                implementation(libs.nav3.ui)
                implementation(libs.lifecycle.viewmodel.nav3)
                implementation(libs.nav3.adaptive)
                implementation(libs.koin.navigation3)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
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
    namespace = packageNamespace
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    
    androidResources {
        localeFilters += arrayOf("en", "ru")
    }

    defaultConfig {
        applicationId = packageNamespace
        minSdk = libs.versions.android.minSdk.get().toInt()
        // Removed inspection 'cause compile api is upper just for compitability
        //noinspection OldTargetApi
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = projectVersion.split(".")
            .take(3)
            //noinspection WrongGradleMethod
            .map { it.toInt() }
            .let { (major, minor, patch) -> major * 10000 + minor * 100 + patch }
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
            packageName = rootProject.name
            packageVersion = version.toString()

            windows {
                iconFile.set(project.file("src/desktopMain/resources/DialogueForge.ico"))
                upgradeUuid = projectUuid
            }
            linux {
                iconFile.set(project.file("src/desktopMain/resources/icon-512.png"))
            }
            macOS {
                iconFile.set(project.file("src/desktopMain/resources/DialogueForge.icns"))
            }
        }
    }
}

koinCompiler {
    userLogs = true
}