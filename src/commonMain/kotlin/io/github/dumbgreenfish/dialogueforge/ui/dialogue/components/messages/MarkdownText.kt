package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.rememberMarkdownState

@Composable
internal fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color,
    fontSize: TextUnit,
    lineHeight: TextUnit,
) {
    val cs = MaterialTheme.colorScheme
    val baseStyle = TextStyle(
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
    )
    val markdownState = rememberMarkdownState(
        content = text,
        immediate = true,
    )

    Markdown(
        markdownState = markdownState,
        modifier = modifier,
        typography = markdownTypography(
            text = baseStyle,
            paragraph = baseStyle,
            ordered = baseStyle,
            bullet = baseStyle,
            list = baseStyle,
            code = baseStyle.copy(
                color = cs.onSurfaceVariant,
                fontSize = fontSize,
            ),
            inlineCode = baseStyle.copy(
                color = cs.onSurfaceVariant,
                fontSize = fontSize,
            ),
            quote = baseStyle.copy(
                color = cs.onSurfaceVariant,
            ),
            textLink = TextLinkStyles(
                style = SpanStyle(color = cs.primary),
            ),
        ),
    )
}
