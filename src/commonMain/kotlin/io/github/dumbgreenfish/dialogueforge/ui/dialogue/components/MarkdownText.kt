package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography

private val AssistantTextSize = 14.5.sp
private val AssistantLineHeight = 25.35.sp

@Composable
internal fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
) {
    val cs = MaterialTheme.colorScheme
    val baseStyle = TextStyle(
        color = cs.onSurface,
        fontSize = AssistantTextSize,
        lineHeight = AssistantLineHeight,
    )

    Markdown(
        content = text,
        modifier = modifier,
        typography = markdownTypography(
            text = baseStyle,
            paragraph = baseStyle,
            ordered = baseStyle,
            bullet = baseStyle,
            list = baseStyle,
            code = baseStyle.copy(
                color = cs.onSurfaceVariant,
                fontSize = AssistantTextSize,
            ),
            inlineCode = baseStyle.copy(
                color = cs.onSurfaceVariant,
                fontSize = AssistantTextSize,
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
