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
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_input_hint
import org.jetbrains.compose.resources.stringResource

private val Radius = 20.dp
private val OuterPaddingT = 8.dp
private val OuterPaddingB = 12.dp
private val OuterPaddingH = 12.dp
private val InnerPaddingT = 12.dp
private val InnerPaddingB = 8.dp
private val InnerPaddingH = 16.dp
private val FieldMinHeight = 20.dp
private val FieldMaxHeight = 160.dp
private val FieldPaddingT = 4.dp
private val FieldPaddingH = 8.dp
private val FieldBottomGap = 8.dp
private val BottomRowGap = 4.dp
private val AttachBtnSize = 32.dp
private val PresetChipHeight = 32.dp
private val PresetChipPadH = 12.dp
private val PresetChipGap = 8.dp

@Composable
internal fun Composer(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSend: () -> Unit,
    isGenerating: Boolean = false,
    onStop: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = OuterPaddingT,
                bottom = OuterPaddingB,
                start = OuterPaddingH,
                end = OuterPaddingH,
            ),
        shape = RoundedCornerShape(Radius),
        color = cs.surfaceVariant,
        border = BorderStroke(1.dp, cs.outlineVariant),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(
                top = InnerPaddingT,
                bottom = InnerPaddingB,
                start = InnerPaddingH,
                end = InnerPaddingH,
            ),
            verticalArrangement = Arrangement.spacedBy(FieldBottomGap),
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = onInputChange,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = cs.onSurface,
                ),
                cursorBrush = SolidColor(ForgeColors.spark),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                enabled = !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = FieldMinHeight, max = FieldMaxHeight)
                    .padding(top = FieldPaddingT, start = FieldPaddingH, end = FieldPaddingH),
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
                Surface(
                    modifier = Modifier.requiredSize(AttachBtnSize),
                    shape = CircleShape,
                    color = cs.surface,
                    border = BorderStroke(1.dp, cs.outline),
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            Icons.Filled.Add,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                            tint = cs.onSurfaceVariant,
                        )
                    }
                }
                Spacer(Modifier.weight(1f))
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
                if (isGenerating) {
                    FilledIconButton(
                        onClick = onStop,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = cs.error,
                            contentColor = cs.onError,
                        ),
                    ) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                } else if (inputText.isNotBlank()) {
                    FilledIconButton(
                        onClick = onSend,
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = cs.primary,
                            contentColor = cs.onPrimary,
                        ),
                    ) {
                        Icon(
                            Icons.Filled.ArrowUpward,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                        )
                    }
                } else {
                    IconButton(onClick = {}, enabled = false) {
                        Icon(
                            Icons.Filled.Mic,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            tint = cs.primary,
                        )
                    }
                }
            }
        }
    }
}
