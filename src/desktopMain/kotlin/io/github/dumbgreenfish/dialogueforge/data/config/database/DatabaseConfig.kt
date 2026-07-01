package io.github.dumbgreenfish.dialogueforge.data.config.database

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import io.github.dumbgreenfish.dialogueforge.data.config.MAIN_DB_DIR
import io.github.dumbgreenfish.dialogueforge.data.config.MAIN_DB_NAME
import io.github.dumbgreenfish.dialogueforge.data.config.MainDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single

private fun appDataDir(): java.io.File {
    val osName = System.getProperty("os.name").lowercase()
    val appData = when {
        osName.contains("win") -> System.getenv("AppData")?.let { java.io.File(it) }
        osName.contains("mac") -> java.io.File(System.getProperty("user.home"), "Library/Application Support")
        else -> java.io.File(System.getProperty("user.home"), ".local/share")
    } ?: java.io.File(System.getProperty("user.home"))
    return appData.resolve("DialogueForge").resolve(MAIN_DB_DIR).also { it.mkdirs() }
}


@Single
class DesktopDatabaseConfig() : DatabaseConfig {
    override fun mainDatabase() = Room.databaseBuilder<MainDatabase>(
        name = appDataDir().resolve(MAIN_DB_NAME).absolutePath
    ).setDriver(BundledSQLiteDriver()).setQueryCoroutineContext(Dispatchers.IO).fallbackToDestructiveMigration().build()
}