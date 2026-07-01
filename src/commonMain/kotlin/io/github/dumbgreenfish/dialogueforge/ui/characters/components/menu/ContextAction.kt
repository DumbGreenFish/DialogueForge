package io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.ui.graphics.vector.ImageVector
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_menu_delete
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_menu_edit
import org.jetbrains.compose.resources.StringResource

internal data class ContextAction(
    val labelRes: StringResource,
    val icon: ImageVector,
    val enabled: Boolean = true,
    val destructive: Boolean = false,
    val onClick: () -> Unit,
)

internal fun characterContextActions(onDelete: () -> Unit): List<ContextAction> = listOf(
    ContextAction(
        labelRes = Res.string.character_menu_edit,
        icon     = Icons.Outlined.Edit,
        enabled  = false,            // TODO: enable when character editing is implemented
        onClick  = {},
    ),
    ContextAction(
        labelRes    = Res.string.character_menu_delete,
        icon        = Icons.Outlined.Delete,
        destructive = true,
        onClick     = onDelete,
    ),
)