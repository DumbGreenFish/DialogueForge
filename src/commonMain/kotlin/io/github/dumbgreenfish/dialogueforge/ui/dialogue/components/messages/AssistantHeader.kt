package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.common.CharacterAvatar
import io.github.dumbgreenfish.dialogueforge.ui.common.ImageProvider

private val AvatarBorderWidth = 1.dp
internal val AssistantHeaderGap = 10.dp

@Composable
internal fun AssistantHeader(
    name: String,
    imageProvider: ImageProvider,
    avatarSize: Dp,
    nameSize: TextUnit,
    modifier: Modifier = Modifier,
    targetSizeDp: Dp = avatarSize,
    onAvatarClick: () -> Unit = {},
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AssistantHeaderGap),
    ) {
        AssistantAvatar(imageProvider = imageProvider, avatarSize = avatarSize, targetSizeDp = targetSizeDp, onClick = onAvatarClick)
        AssistantName(name = name, nameSize = nameSize)
    }
}

@Composable
internal fun AssistantAvatar(
    imageProvider: ImageProvider,
    avatarSize: Dp,
    modifier: Modifier = Modifier,
    targetSizeDp: Dp = avatarSize,
    onClick: () -> Unit = {},
) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .size(avatarSize)
            .clip(CircleShape)
            .border(
                width = AvatarBorderWidth,
                color = cs.secondary.copy(alpha = 0.2f),
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    ) {
        CharacterAvatar(
            imageProvider = imageProvider,
            targetSizeDp = targetSizeDp,
            modifier = Modifier.fillMaxSize(),
            shape = CircleShape,
        )
    }
}

@Composable
internal fun AssistantName(
    name: String,
    nameSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    Text(
        modifier = modifier,
        text = name,
        fontSize = nameSize,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.secondary,
    )
}
