package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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

private val AvatarBorderWidth = 1.dp
internal val AssistantHeaderGap = 10.dp

@Composable
internal fun AssistantHeader(
    name: String,
    letter: String,
    avatarBytes: ByteArray?,
    avatarSize: Dp,
    nameSize: TextUnit,
    avatarFontSize: TextUnit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AssistantHeaderGap),
    ) {
        Box(
            modifier = Modifier
                .size(avatarSize)
                .clip(CircleShape)
                .border(
                    width = AvatarBorderWidth,
                    color = cs.secondary.copy(alpha = 0.2f),
                    shape = CircleShape,
                ),
        ) {
            CharacterAvatar(
                letter = letter,
                modifier = Modifier.size(avatarSize),
                shape = CircleShape,
                fontSize = avatarFontSize,
                avatarBytes = avatarBytes,
            )
        }
        Text(
            text = name,
            fontSize = nameSize,
            fontWeight = FontWeight.Medium,
            color = cs.secondary,
        )
    }
}
