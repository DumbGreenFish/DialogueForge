package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_edit_cancel
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_edit_save
import io.github.dumbgreenfish.dialogueforge.ui.dialogue.model.MessageRole
import io.github.dumbgreenfish.dialogueforge.ui.settings.model.MessageWidth
import org.jetbrains.compose.resources.stringResource

sealed interface EditFieldEvent {
    data class TextChanged(val value: TextFieldValue) : EditFieldEvent
    data object Save : EditFieldEvent
    data object Cancel : EditFieldEvent
}

private val EditFieldRadius = 16.dp
private val EditFieldPaddingH = 14.dp
private val EditFieldPaddingV = 10.dp
private val EditFieldMinHeight = 40.dp
private val EditFieldFontSize = 14.sp
private val EditButtonsGap = 8.dp

@Composable
internal fun EditField(
    text: TextFieldValue,
    onEditFieldEvent: (EditFieldEvent) -> Unit,
    role: MessageRole,
    messageWidth: MessageWidth,
) {
    val cs = MaterialTheme.colorScheme
    val fieldBgIdle = cs.onSurface.copy(alpha = 0.05f)
    val fieldBorderFocus = cs.onSurface.copy(alpha = 0.12f)

    Column(
        modifier = Modifier.then(bubbleWidthModifier(messageWidth)),
        horizontalAlignment = if (role == MessageRole.User) Alignment.End else Alignment.Start,
    ) {
        BasicTextField(
            value = text,
            onValueChange = { onEditFieldEvent(EditFieldEvent.TextChanged(it)) },
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = cs.onSurface,
                fontSize = EditFieldFontSize,
            ),
            cursorBrush = SolidColor(ForgeColors.spark),
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            singleLine = false,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = EditFieldMinHeight)
                .clip(RoundedCornerShape(EditFieldRadius))
                .background(fieldBgIdle)
                .border(
                    width = 1.dp,
                    color = fieldBorderFocus,
                    shape = RoundedCornerShape(EditFieldRadius),
                )
                .padding(horizontal = EditFieldPaddingH, vertical = EditFieldPaddingV),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(EditButtonsGap),
        ) {
            TextButton(onClick = { onEditFieldEvent(EditFieldEvent.Cancel) }) {
                Text(stringResource(Res.string.dialogue_edit_cancel))
            }
            TextButton(onClick = { onEditFieldEvent(EditFieldEvent.Save) }) {
                Text(stringResource(Res.string.dialogue_edit_save))
            }
        }
    }
}
