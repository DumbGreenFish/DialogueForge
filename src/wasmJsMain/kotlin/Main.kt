import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.github.dumbgreenfish.dialogueforge.App
import io.github.dumbgreenfish.dialogueforge.initKoin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(content = {
        initKoin()
        App()
    })
}