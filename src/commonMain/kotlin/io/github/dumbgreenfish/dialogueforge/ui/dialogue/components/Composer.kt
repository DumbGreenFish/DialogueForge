package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_input_hint
import org.jetbrains.compose.resources.stringResource

private val Radius           = 22.dp
private val InnerPaddingT    = 10.dp
private val InnerPaddingB    = 8.dp
private val InnerPaddingH    = 12.dp
private val FieldMinHeight   = 22.dp
private val FieldMaxHeight   = 160.dp
private val FieldPaddingT    = 4.dp
private val BottomRowGap     = 4.dp
private val AttachBtnSize    = 32.dp
private val ActionBtnSize    = 36.dp
private val PresetChipHeight = 30.dp
private val PresetChipPadH   = 10.dp
private val PresetChipGap    = 6.dp

@Composable
internal fun Composer(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(Radius),
        color = cs.surface,
        border = BorderStroke(1.dp, cs.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(
                top = InnerPaddingT,
                bottom = InnerPaddingB,
                start = InnerPaddingH,
                end = InnerPaddingH,
            ),
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = onInputChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = cs.onSurface,
                    lineHeight = 20.sp,
                ),
                cursorBrush = SolidColor(ForgeColors.spark),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = FieldMinHeight, max = FieldMaxHeight)
                    .padding(top = FieldPaddingT),
                decorationBox = { innerTextField ->
                    Box {
                        if (inputText.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.dialogue_input_hint),
                                style = MaterialTheme.typography.bodyLarge,
                                color = ForgeColors.onSurfaceFaint,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(BottomRowGap),
            ) {
                // TODO: not implemented
                IconButton(onClick = {}, enabled = false, modifier = Modifier.size(AttachBtnSize)) {
                    Icon(
                        Icons.Filled.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = cs.onSurfaceVariant,
                    )
                }
                Spacer(Modifier.weight(1f))
                // TODO: preset not implemented — chip rendered empty
                Surface(
                    shape = RoundedCornerShape(PresetChipHeight / 2),
                    color = cs.surface,
                    border = BorderStroke(1.dp, cs.outlineVariant),
                    modifier = Modifier.height(PresetChipHeight),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = PresetChipPadH),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(PresetChipGap),
                    ) {
                        Icon(
                            Icons.Filled.Tune,
                            contentDescription = null,
                            modifier = Modifier.size(13.dp),
                            tint = ForgeColors.spark,
                        )
                    }
                }
                val hasText = inputText.isNotBlank()
                if (hasText) {
                    FilledIconButton(
                        onClick = onSend,
                        modifier = Modifier.size(ActionBtnSize),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = cs.primary,
                            contentColor = cs.onPrimary,
                        ),
                    ) {
                        Icon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                } else {
                    // TODO: mic permission not implemented
                    IconButton(onClick = {}, enabled = false, modifier = Modifier.size(ActionBtnSize)) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = cs.primary,
                        )
                    }
                }
            }
        }
    }
}
