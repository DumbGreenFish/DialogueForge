import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import io.github.dumbgreenfish.dialogueforge.App
import io.github.dumbgreenfish.dialogueforge.ForgeApp
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavBar
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavScreen
import java.awt.Dimension
import org.koin.core.context.GlobalContext

fun main() = application {
    val state = rememberWindowState(placement = WindowPlacement.Maximized)
    Window(
        onCloseRequest = ::exitApplication,
        title = "DialogueForge",
        state = state,
        onPreviewKeyEvent = { keyEvent ->
            var handled = false
            if (keyEvent.type == KeyEventType.KeyDown) {
                val context = GlobalContext.getOrNull()
                if (context != null) {
                    val controller = context.get<NavController>()
                    val tab = controller.activeTab.value
                    val bar = controller.getBar(tab) as? NavBar<NavScreen>
                    if (bar != null) {
                        when {
                            keyEvent.key == Key.DirectionLeft && keyEvent.isAltPressed -> {
                                bar.popBack()
                                handled = true
                            }
                            keyEvent.key == Key.DirectionRight && keyEvent.isAltPressed -> {
                                bar.popForward()
                                handled = true
                            }
                        }
                    }
                }
            }
            handled
        },
    ) {
        window.minimumSize = Dimension(480, 640)
        ForgeApp.initKoin()
        App()
    }
}