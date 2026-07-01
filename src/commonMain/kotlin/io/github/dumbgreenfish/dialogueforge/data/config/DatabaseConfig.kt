package io.github.dumbgreenfish.dialogueforge.data.config

import org.koin.core.annotation.Single

@Single
interface DatabaseConfig {
    fun mainDatabase() : MainDatabase
}