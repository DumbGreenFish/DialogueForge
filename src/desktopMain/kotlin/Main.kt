import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import ru.greenfish.dialogueforge.App

fun main() = application {
    val state = rememberWindowState(size = DpSize(640.dp, 480.dp))
    Window(
        onCloseRequest = ::exitApplication,
        title = "DialogueForge",
        state = state
    ) {
        App()
    }
}