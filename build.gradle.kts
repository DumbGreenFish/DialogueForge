import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.kotlinComposeCompiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.ksp)
}

group = "ru.greenfish"
version = "0.0.1"

val jvmTargetVersion = JvmTarget.fromTarget(libs.versions.jvm.get())
val javaVersion = JavaVersion.toVersion(libs.versions.jvm.get())

dependencies {
    add("kspCommonMainMetadata", libs.cafe.adriel.lyricist.processor)
}

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
                implementation(libs.cafe.adriel.lyricist)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)  // оставляем пока, см. выше
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
            }
        }

        val wasmJsMain by getting
    }
}

android {
    namespace = "ru.greenfish.dialogueforge"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "ru.greenfish.dialogueforge"
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

android {
    androidResources {
        localeFilters += arrayOf("en", "ru")
    }
}

ksp {
    arg("lyricist.internalVisibility", "true")
    arg("lyricist.defaultLanguageTag", "en")
    arg("lyricist.generateStringsKey", "true")
    arg("lyricist.generateStringsProperty", "true")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask<*>>().configureEach {
    if (name != "kspCommonMainKotlinMetadata") {
        dependsOn("kspCommonMainKotlinMetadata")
    }
}

kotlin.sourceSets.commonMain {
    kotlin.srcDir("build/generated/ksp/metadata/commonMain/kotlin")
}