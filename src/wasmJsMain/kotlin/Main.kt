import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import androidx.compose.ui.window.ComposeViewport
import ru.greenfish.dialogueforge.App

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(content = {
        App()
    })
}