package io.github.dumbgreenfish.dialogueforge.config

import androidx.room3.Room
import androidx.sqlite.driver.web.WebWorkerSQLiteDriver
import org.koin.core.annotation.Single
import org.w3c.dom.Worker

@JsFun("(url) => new Worker(url, { type: 'module' })")
private external fun createModuleWorker(url: String): Worker

@Single
class WasmDatabaseConfig : DatabaseConfig {

    private val db: MainDatabase by lazy {
        Room.databaseBuilder<MainDatabase>(name = MAIN_DB_NAME)
            .setDriver(WebWorkerSQLiteDriver(createModuleWorker(SQLITE_WORKER_SCRIPT)))
            .fallbackToDestructiveMigration()
            .build()
    }

    override fun mainDatabase(): MainDatabase = db

    companion object {
        private const val SQLITE_WORKER_SCRIPT = "sqlite-worker.js"
    }
}
