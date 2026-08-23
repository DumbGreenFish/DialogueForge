package io.github.dumbgreenfish.dialogueforge.config

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.Single


@Single
class AndroidDatabaseConfig(val app: Application) : DatabaseConfig {
    private val db: MainDatabase by lazy {
        val dbFile = app.getDatabasePath(MAIN_DB_NAME)
        dbFile.parentFile?.mkdirs()
        Room.databaseBuilder<MainDatabase>(name = dbFile.absolutePath)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun mainDatabase(): MainDatabase = db
}
