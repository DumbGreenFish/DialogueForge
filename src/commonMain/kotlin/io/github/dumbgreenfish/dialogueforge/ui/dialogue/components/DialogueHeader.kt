package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import io.github.dumbgreenfish.dialogueforge.ui.common.WindowClass
import io.github.dumbgreenfish.dialogueforge.ui.common.windowClass

internal object DialogueHeaderDimens {
    val HeightCompact = 64.dp
    val HeightWide = 56.dp
    val PaddingHCompact = 4.dp
    val PaddingHWide = 12.dp
    val GapCompact = 0.dp
    val GapWide = 4.dp
    val ActionBtnTarget = 36.dp
}

@Composable
internal fun dialogueHeaderTitleStyle(): TextStyle =
    if (windowClass != WindowClass.Wide) MaterialTheme.typography.titleMedium
    else MaterialTheme.typography.titleSmall

@Composable
internal fun DialogueHeaderRow(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val compact = windowClass != WindowClass.Wide
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(if (compact) DialogueHeaderDimens.HeightCompact else DialogueHeaderDimens.HeightWide)
            .padding(horizontal = if (compact) DialogueHeaderDimens.PaddingHCompact else DialogueHeaderDimens.PaddingHWide),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(if (compact) DialogueHeaderDimens.GapCompact else DialogueHeaderDimens.GapWide),
        content = content,
    )
}
