package io.github.dumbgreenfish.dialogueforge.ui.common

import java.io.File
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

class ForgeAlertDialogArchitectureTest {
    @Test
    fun production_code_uses_only_forge_alert_dialog() {
        val projectDirectory = File(System.getProperty("user.dir"))
        val allowedFile = projectDirectory.resolve(
            "src/commonMain/kotlin/io/github/dumbgreenfish/dialogueforge/ui/common/ForgeAlertDialog.kt",
        )
        val violations = projectDirectory.resolve("src")
            .walkTopDown()
            .filter { file ->
                file.isFile &&
                    file.extension == "kt" &&
                    "Main" in file.invariantSeparatorsPath &&
                    file != allowedFile
            }
            .filter { file ->
                "androidx.compose.material3.AlertDialog" in file.readText()
            }
            .map { file -> file.relativeTo(projectDirectory).invariantSeparatorsPath }
            .sorted()
            .toList()

        assertEquals(emptyList(), violations, "Use ForgeAlertDialog instead of Material AlertDialog")
    }

    @Test
    fun forge_alert_dialog_owns_the_project_container_color() {
        val projectDirectory = File(System.getProperty("user.dir"))
        val source = projectDirectory.resolve(
            "src/commonMain/kotlin/io/github/dumbgreenfish/dialogueforge/ui/common/ForgeAlertDialog.kt",
        ).readText()

        assertContains(source, "containerColor = ForgeColors.surfaceContainerHigh")
    }
}
