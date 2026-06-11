package io.github.dumbgreenfish.dialogueforge

import android.app.Application
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ForgeApp.initKoin()
        loadKoinModules(module { single<Application> { this@AndroidApplication } })
    }
}
