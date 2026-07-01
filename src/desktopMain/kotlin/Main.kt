import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.dumbgreenfish.dialogueforge.App
import io.github.dumbgreenfish.dialogueforge.ForgeApp
import java.awt.Dimension

fun main() = application {
    val state = rememberWindowState(placement = WindowPlacement.Maximized)
    Window(
        onCloseRequest = ::exitApplication,
        title = "DialogueForge",
        state = state
    ) {
        window.minimumSize = Dimension(480, 640)
        ForgeApp.initKoin()
        App()
    }
}