package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Copy
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.Pencil
import com.composables.icons.lucide.SquareCheck
import com.composables.icons.lucide.Trash2
import io.github.dumbgreenfish.dialogueforge.design.ForgeAnimation
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole

sealed interface ActionRowEvent {
    data object Copy : ActionRowEvent
    data object Edit : ActionRowEvent
    data object Delete : ActionRowEvent
    data object Select : ActionRowEvent
}

private val ActionRowHeight = 28.dp
private val ActionIconSize = 16.dp
private val ActionTouchTargetSize = 16.dp
private val DistanceBetweenActionItems = 8.dp

@Composable
internal fun ActionRow(
    role: MessageRole,
    visible: Boolean,
    onActionRowEvent: (ActionRowEvent) -> Unit,
    modifier: Modifier = Modifier,
    startPadding: Dp = 0.dp,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
    val isHovered by interactionSource.collectIsHoveredAsState()
    val alpha by animateFloatAsState(
        targetValue = if (visible || isHovered) 1f else 0f,
        animationSpec = tween(ForgeAnimation.DurationHover),
    )

    val actionRowItems: List<@Composable () -> Unit> = listOfNotNull(
        @Composable {
            ActionIcon(
                imageVector = Lucide.SquareCheck,
                contentDescription = null,
                onClick = { onActionRowEvent(ActionRowEvent.Select) },
            )
        },
        @Composable {
            ActionIcon(
                imageVector = Lucide.Copy,
                contentDescription = null,
                onClick = { onActionRowEvent(ActionRowEvent.Copy) },
            )
        },
        @Composable {
            ActionIcon(
                imageVector = Lucide.Pencil,
                contentDescription = null,
                onClick = { onActionRowEvent(ActionRowEvent.Edit) },
            )
        },
        @Composable {
            ActionIcon(
                imageVector = Lucide.Trash2,
                contentDescription = null,
                onClick = { onActionRowEvent(ActionRowEvent.Delete) },
            )
        },
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(ActionRowHeight),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = startPadding)
                .graphicsLayer(alpha = alpha),
            horizontalArrangement = if (role == MessageRole.User) Arrangement.End else Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actionRowItems.forEach { item ->
                item()
                Spacer(Modifier.width(DistanceBetweenActionItems))
            }
        }
    }
}

@Composable
private fun ActionIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
) {
    val cs = MaterialTheme.colorScheme
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val tint = if (isHovered) cs.onSurface else cs.onSurfaceVariant

    Box(
        modifier = Modifier
            .size(ActionTouchTargetSize)
            .hoverable(interactionSource)
            .clickable(
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(ActionIconSize),
            tint = tint,
        )
    }
}
