package io.github.dumbgreenfish.dialogueforge

import android.app.Application

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
