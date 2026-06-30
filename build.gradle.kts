import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

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

val keystoreFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties().apply {
    if (keystoreFile.exists()) {
        load(keystoreFile.inputStream())
    }
}

val generateBuildConfig by tasks.registering {
    val outputDir = layout.buildDirectory.dir("generated/buildConfig/kotlin")
    val version = projectVersion
    outputs.dir(outputDir)
    doLast {
        val file = outputDir.get().asFile
            .resolve("${packageNamespace.replace('.', '/')}/BuildConfig.kt")
        file.parentFile.mkdirs()
        file.writeText(
            """
                package $packageNamespace

                internal object BuildConfig {
                    const val VERSION = "$version"
                }
                """.trimIndent()
        )
    }
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
                outputFileName = "$appName.js"
            }
        }
        binaries.executable()
    }


    sourceSets {
        val commonMain by getting {
            kotlin.srcDir(generateBuildConfig)
            dependencies {
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.material.icons.extended)
                implementation(project.dependencies.platform(libs.koin.bom))
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.annotations)
                implementation(libs.koin.viewmodel)
                implementation(libs.adaptive)
                implementation(libs.nav3.ui)
                implementation(libs.lifecycle.viewmodel.nav3)
                implementation(libs.nav3.adaptive)
                implementation(libs.koin.navigation3)
                implementation(libs.kotlinx.serialization.core)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.room3.runtime)
            }
        }

        val desktopMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(libs.sqlite.bundled)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
                implementation(libs.sqlite.bundled)
            }
        }

        val wasmJsMain by getting {
            dependencies {
                implementation(libs.sqlite.web)
            }
        }
    }
}

dependencies {
    add("kspAndroid", libs.room3.compiler)
    add("kspDesktop", libs.room3.compiler)
    add("kspWasmJs", libs.room3.compiler)
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

    val releaseSigning = signingConfigs.create("release") {
        storeFile = file(keystoreProperties["storeFile"] as String)
        storePassword = keystoreProperties["storePassword"] as String
        keyAlias = keystoreProperties["keyAlias"] as String
        keyPassword = keystoreProperties["keyPassword"] as String
    }

    buildTypes {
        getByName("release") {
            if (keystoreProperties.isNotEmpty()) {
                val releaseSigning = signingConfigs.create("release") {
                    storeFile = file(keystoreProperties["storeFile"] as String)
                    storePassword = keystoreProperties["storePassword"] as String
                    keyAlias = keystoreProperties["keyAlias"] as String
                    keyPassword = keystoreProperties["keyPassword"] as String
                }
                signingConfig = releaseSigning
            }
        }
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
