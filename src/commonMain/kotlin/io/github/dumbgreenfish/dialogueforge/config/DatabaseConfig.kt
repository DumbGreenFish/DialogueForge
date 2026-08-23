package io.github.dumbgreenfish.dialogueforge.config

import org.koin.core.annotation.Single

@Single
interface DatabaseConfig {
    fun mainDatabase() : MainDatabase
}