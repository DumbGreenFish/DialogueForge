import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ru.greenfish.dialogueforge.App

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "DialogueForge") {
        App()
    }
}