import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.dumbgreenfish.dialogueforge.App
import io.github.dumbgreenfish.dialogueforge.ForgeApp
import java.awt.Dimension

fun main() = application {
    val state = rememberWindowState(size = DpSize(800.dp, 600.dp))
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