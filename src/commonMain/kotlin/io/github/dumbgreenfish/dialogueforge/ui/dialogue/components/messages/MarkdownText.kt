package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.messages

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownDivider
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.m3.markdownTypography
import com.mikepenz.markdown.model.markdownAnnotator
import com.mikepenz.markdown.model.markdownAnnotatorConfig
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

    val components = remember {
        markdownComponents(
            orderedList = { CustomOrderedListComponent(it) },
            unorderedList = { CustomBulletListComponent(it) },
            horizontalRule = { MarkdownDivider(Modifier) },
        )
    }

    Markdown(
        markdownState = markdownState,
        modifier = modifier,
        components = components,
        annotator = markdownAnnotator(
            config = markdownAnnotatorConfig(eolAsNewLine = true),
        ),
        typography = markdownTypography(
            h1 = baseStyle.copy(fontSize = fontSize * 1.75f, fontWeight = FontWeight.Bold),
            h2 = baseStyle.copy(fontSize = fontSize * 1.5f, fontWeight = FontWeight.Bold),
            h3 = baseStyle.copy(fontSize = fontSize * 1.25f, fontWeight = FontWeight.Bold),
            h4 = baseStyle.copy(fontSize = fontSize * 1.1f, fontWeight = FontWeight.Bold),
            h5 = baseStyle.copy(fontWeight = FontWeight.Bold),
            h6 = baseStyle.copy(fontSize = fontSize * 0.95f, fontWeight = FontWeight.Bold),
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
