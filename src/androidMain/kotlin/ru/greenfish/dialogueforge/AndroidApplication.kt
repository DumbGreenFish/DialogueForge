package ru.greenfish.dialogueforge

import android.app.Application

class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
    }
}
