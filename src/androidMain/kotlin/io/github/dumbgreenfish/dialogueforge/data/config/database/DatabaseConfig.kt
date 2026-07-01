package io.github.dumbgreenfish.dialogueforge.data.repository.config.database

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import io.github.dumbgreenfish.dialogueforge.data.config.DatabaseConfig
import io.github.dumbgreenfish.dialogueforge.data.config.MAIN_DB_NAME
import io.github.dumbgreenfish.dialogueforge.data.config.MainDatabase
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single


@Single
class AndroidDatabaseConfig(val app: Application) : DatabaseConfig {
    override fun mainDatabase() : MainDatabase {
        val dbFile = app.getDatabasePath(MAIN_DB_NAME)
        dbFile.parentFile?.mkdirs()
        return Room.databaseBuilder<MainDatabase>(name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}