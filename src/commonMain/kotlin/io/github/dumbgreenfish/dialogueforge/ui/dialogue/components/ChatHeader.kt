package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_no_model
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.CharacterAvatar
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass
import org.jetbrains.compose.resources.stringResource

private val AvatarSizeCompact = 36.dp
private val AvatarSizeWide = 32.dp
private val AvatarFontCompact = 16.sp
private val AvatarFontWide = 14.sp
private val TitleBlockGap = 10.dp
private val TitleBlockPadH = 6.dp
private val TitleBlockPadV = 4.dp
private val SubtitleGap = 5.dp

@Composable
internal fun ChatHeader(
    character: Character?,
    modelName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val compact = windowClass != WindowClass.Wide
    val avatarSize = if (compact) AvatarSizeCompact else AvatarSizeWide
    val avatarFont = if (compact) AvatarFontCompact else AvatarFontWide
    val titleStyle = dialogueHeaderTitleStyle()
    val subtitleStyle = if (compact) MaterialTheme.typography.labelMedium
    else MaterialTheme.typography.labelSmall

    DialogueHeaderRow(modifier = modifier) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = null,
                tint = cs.onSurface
            )
        }
        character?.let { char ->
            Row(
                modifier = Modifier.weight(1f).padding(
                    horizontal = TitleBlockPadH,
                    vertical = TitleBlockPadV,
                ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TitleBlockGap),
            ) {
                CharacterAvatar(
                    letter = char.letter,
                    modifier = Modifier.size(avatarSize),
                    shape = ForgeShape.avatar,
                    fontSize = avatarFont,
                    avatarBytes = char.avatarBytes,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = char.name,
                        style = titleStyle,
                        color = cs.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(SubtitleGap),
                    ) {
                        val model =
                            modelName.ifEmpty { stringResource(Res.string.dialogue_no_model) }
                        Text(
                            text = model,
                            style = subtitleStyle,
                            color = ForgeColors.onSurfaceFaint,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
        // TODO: not implemented
        IconButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.requiredSize(DialogueHeaderDimens.ActionBtnTarget)
        ) {
            Icon(Icons.Filled.History, contentDescription = null, tint = cs.onSurfaceVariant)
        }
        // TODO: not implemented
        IconButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.requiredSize(DialogueHeaderDimens.ActionBtnTarget)
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = cs.onSurfaceVariant)
        }
        // TODO: not implemented
        IconButton(
            onClick = {},
            enabled = false,
            modifier = Modifier.requiredSize(DialogueHeaderDimens.ActionBtnTarget)
        ) {
            Icon(Icons.Filled.MoreVert, contentDescription = null, tint = cs.onSurfaceVariant)
        }
    }
}
