package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.bubble

import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors

@Composable
internal fun MessageBubbleContent(
    text: String,
    isEditing: Boolean,
    isSelected: Boolean,
    fg: Color,
    bg: Color,
    editTextValue: TextFieldValue,
    onEditTextChange: ((TextFieldValue) -> Unit)?,
) {
    val cs = MaterialTheme.colorScheme

    if (isEditing) {
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        TextField(
            value = editTextValue,
            onValueChange = { onEditTextChange?.invoke(it) },
            modifier = Modifier.focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = fg),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = bg,
                unfocusedContainerColor = bg,
                focusedIndicatorColor = ForgeColors.spark,
                unfocusedIndicatorColor = cs.outline,
                cursorColor = ForgeColors.spark,
            ),
        )
    } else if (isSelected) {
        SelectionContainer {
            Text(
                text = text,
                color = fg,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
    } else {
        Text(
            text = text,
            color = fg,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}
