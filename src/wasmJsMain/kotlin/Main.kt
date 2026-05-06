import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import ru.greenfish.dialogueforge.App
import ru.greenfish.dialogueforge.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(content = {
        initKoin()
        App()
    })
}