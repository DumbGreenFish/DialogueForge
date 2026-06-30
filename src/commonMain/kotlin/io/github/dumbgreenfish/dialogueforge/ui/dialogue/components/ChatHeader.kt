package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_no_model
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_no_preset
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.CharacterAvatar
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.common.isCompact
import org.jetbrains.compose.resources.stringResource

private val HeightCompact      = 64.dp
private val HeightWide         = 56.dp
private val PaddingHCompact    = 4.dp
private val PaddingHWide       = 12.dp
private val GapCompact         = 0.dp
private val GapWide            = 4.dp
private val ActionBtnTarget     = 36.dp
private val AvatarSizeCompact  = 36.dp
private val AvatarSizeWide     = 32.dp
private val AvatarFontCompact  = 16.sp
private val AvatarFontWide     = 14.sp
private val TitleBlockGap      = 10.dp
private val TitleBlockPadH     = 6.dp
private val TitleBlockPadV     = 4.dp
private val SubtitleGap        = 5.dp
private val SparkDotSize       = 6.dp

@Composable
internal fun ChatHeader(
    character: Character?,
    presetName: String,
    modelName: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val compact = isCompact

    val height        = if (compact) HeightCompact      else HeightWide
    val paddingH      = if (compact) PaddingHCompact    else PaddingHWide
    val gap           = if (compact) GapCompact         else GapWide
    val avatarSize    = if (compact) AvatarSizeCompact  else AvatarSizeWide
    val avatarFont    = if (compact) AvatarFontCompact  else AvatarFontWide
    val titleStyle    = if (compact) MaterialTheme.typography.titleMedium
                       else MaterialTheme.typography.titleSmall
    val subtitleStyle = if (compact) MaterialTheme.typography.labelMedium
                       else MaterialTheme.typography.labelSmall

    Row(
        modifier = modifier.fillMaxWidth().height(height).padding(horizontal = paddingH),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(gap),
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, tint = cs.onSurface)
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
                        val model = modelName.ifEmpty { stringResource(Res.string.dialogue_no_model) }
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
        IconButton(onClick = {}, enabled = false, modifier = Modifier.requiredSize(ActionBtnTarget)) {
            Icon(Icons.Filled.History, contentDescription = null, tint = cs.onSurfaceVariant)
        }
        // TODO: not implemented
        IconButton(onClick = {}, enabled = false, modifier = Modifier.requiredSize(ActionBtnTarget)) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = cs.onSurfaceVariant)
        }
        // TODO: not implemented
        IconButton(onClick = {}, enabled = false, modifier = Modifier.requiredSize(ActionBtnTarget)) {
            Icon(Icons.Filled.MoreVert, contentDescription = null, tint = cs.onSurfaceVariant)
        }
    }
}
