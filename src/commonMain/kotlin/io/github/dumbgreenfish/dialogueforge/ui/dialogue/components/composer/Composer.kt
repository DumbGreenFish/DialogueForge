package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.composer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.data.repository.settings.ForgeSettings
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_input_hint
import io.github.dumbgreenfish.dialogueforge.ui.common.isMobilePlatform
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.scaffold.DialogueLayout
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private val InputRadius = 16.dp
private val OuterPaddingH = 16.dp
private val OuterPaddingT = 12.dp
private val OuterPaddingB = 8.dp
private val FieldPaddingV = 10.dp
private val RowGap = 8.dp
private val FieldMinHeight = 40.dp
private val IconBtnSize = 32.dp
private val IconBtnRadius = 8.dp
private val IconBtnPaddingB = 8.dp
private val SendIconSize = 16.dp
private val FieldFontSize = 14.sp

@Composable
internal fun Composer(
    textFieldValue: TextFieldValue,
    onInputChange: (TextFieldValue) -> Unit,
    onSend: () -> Unit,
    isGenerating: Boolean = false,
    onStop: () -> Unit = {},
    onAttach: () -> Unit = {},
    hasLastUserMessage: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val forgeSettings = koinInject<ForgeSettings>()
    val composerMaxHeightDp by forgeSettings.composerMaxHeightDp.collectAsState()
    val isFocused = remember { mutableStateOf(false) }

    val fieldBgIdle = cs.onSurface.copy(alpha = 0.05f)
    val fieldBorderIdle = cs.onSurface.copy(alpha = 0.07f)
    val fieldBorderFocus = cs.onSurface.copy(alpha = 0.12f)
    val borderColor = if (isFocused.value) fieldBorderFocus else fieldBorderIdle

    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = DialogueLayout.ContentMaxWidth)
                .fillMaxWidth()
                .padding(
                    top = OuterPaddingT,
                    bottom = OuterPaddingB,
                    start = OuterPaddingH,
                    end = OuterPaddingH,
                ),
        ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(InputRadius))
                .background(fieldBgIdle)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(InputRadius))
                .padding(horizontal = 4.dp),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(RowGap),
        ) {
            PlusButton(onClick = onAttach)

            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    isFocused.value = true
                    onInputChange(it)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = cs.onSurface,
                    fontSize = FieldFontSize,
                ),
                cursorBrush = SolidColor(ForgeColors.spark),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                singleLine = false,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = FieldMinHeight, max = composerMaxHeightDp.dp)
                    .padding(vertical = FieldPaddingV)
                    .onPreviewKeyEvent { keyEvent ->
                        if (keyEvent.type == KeyEventType.KeyDown && !isMobilePlatform) {
                            val shouldNewline = (keyEvent.key == Key.J && keyEvent.isCtrlPressed)
                                || (keyEvent.key == Key.Enter && keyEvent.isShiftPressed)
                            when {
                                shouldNewline -> {
                                    val v = textFieldValue
                                    val pos = v.selection.start
                                    val newText = v.text.substring(0, pos) + "\n" + v.text.substring(pos)
                                    onInputChange(
                                        v.copy(
                                            text = newText,
                                            selection = TextRange(pos + 1, pos + 1),
                                        )
                                    )
                                    true
                                }
                                keyEvent.key == Key.Enter && (textFieldValue.text.isNotBlank() || hasLastUserMessage) -> {
                                    onSend()
                                    true
                                }
                                else -> false
                            }
                        } else {
                            false
                        }
                    },
                decorationBox = { innerTextField ->
                    Box {
                        if (textFieldValue.text.isEmpty()) {
                            Text(
                                text = stringResource(Res.string.dialogue_input_hint),
                                fontSize = FieldFontSize,
                                color = cs.onSurfaceVariant,
                            )
                        }
                        innerTextField()
                    }
                },
            )

            if (isGenerating) {
                StopButton(onStop)
            } else if (textFieldValue.text.isNotBlank() || hasLastUserMessage) {
                SendButton(onSend)
            }
        }
        }
    }
}

@Composable
private fun PlusButton(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(IconBtnSize)
            .padding(bottom = IconBtnPaddingB)
            .clip(RoundedCornerShape(IconBtnRadius))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Add,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = cs.onSurfaceVariant,
        )
    }
}

@Composable
private fun SendButton(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(IconBtnSize)
            .padding(bottom = IconBtnPaddingB)
            .clip(CircleShape)
            .background(cs.secondary.copy(alpha = 0.2f))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = null,
            modifier = Modifier.size(SendIconSize),
            tint = cs.secondary,
        )
    }
}

@Composable
private fun StopButton(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(IconBtnSize)
            .padding(bottom = IconBtnPaddingB)
            .clip(CircleShape)
            .background(cs.errorContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Close,
            contentDescription = null,
            modifier = Modifier.size(SendIconSize),
            tint = cs.onErrorContainer,
        )
    }
}
