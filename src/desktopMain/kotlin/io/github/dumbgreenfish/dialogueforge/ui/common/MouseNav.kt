package io.github.dumbgreenfish.dialogueforge.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent

@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun Modifier.mouseNav(
    onBack: () -> Unit,
    onForward: () -> Unit,
): Modifier {
    val isLinux = remember { System.getProperty("os.name").lowercase().contains("linux") }

    return if (isLinux) {
        this.onPointerEvent(PointerEventType.Press) { event ->
            when (event.button?.index) {
                4, 6 -> onForward()
                5, 7 -> onBack()
            }
        }
    } else {
        this.onPointerEvent(PointerEventType.Press) { event ->
            when (event.button?.index) {
                8 -> onBack()
                9 -> onForward()
            }
        }
    }
}
