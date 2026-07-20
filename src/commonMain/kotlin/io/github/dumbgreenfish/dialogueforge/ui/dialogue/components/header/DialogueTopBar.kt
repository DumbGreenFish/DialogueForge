package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.header

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.ArrowLeft
import com.composables.icons.lucide.History
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Plus
import com.composables.icons.lucide.Settings
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.components.BaseTopBar
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass

private val ActionIconGap = 4.dp

@Composable
internal fun DialogueTopBar(
    backgroundOpacity: Float,
    onBack: () -> Unit,
    onHistory: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    BaseTopBar(
        isCompact = windowClass == WindowClass.Compact,
        backgroundColor = cs.background.copy(alpha = backgroundOpacity),
        modifier = modifier,
        leading = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Lucide.ArrowLeft,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
        },
        trailing = {
            IconButton(onClick = onHistory) {
                Icon(
                    imageVector = Lucide.History,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(ActionIconGap))
            IconButton(onClick = onAdd) {
                Icon(
                    imageVector = Lucide.Plus,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
            Spacer(Modifier.width(ActionIconGap))
            IconButton(onClick = {}, enabled = false) {
                Icon(
                    imageVector = Lucide.Settings,
                    contentDescription = null,
                    tint = cs.onSurfaceVariant,
                )
            }
        },
    )
}
