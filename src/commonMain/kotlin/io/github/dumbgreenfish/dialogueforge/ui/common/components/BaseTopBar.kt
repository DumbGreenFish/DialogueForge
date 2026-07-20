package io.github.dumbgreenfish.dialogueforge.ui.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

internal val BaseTopBarHeight = 64.dp
internal val BaseWideTopBarPaddingH = 24.dp
internal val BaseCompactTopBarPaddingH = 4.dp

@Composable
fun BaseTopBar(
    isCompact: Boolean,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    leading: @Composable RowScope.() -> Unit = {},
    title: @Composable () -> Unit = {},
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val rowModifier = if (isCompact) {
        Modifier
            .windowInsetsPadding(WindowInsets.statusBars)
            .height(BaseTopBarHeight)
            .padding(horizontal = BaseCompactTopBarPaddingH)
    } else {
        Modifier
            .height(BaseTopBarHeight)
            .padding(horizontal = BaseWideTopBarPaddingH)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .then(rowModifier),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        Box(Modifier.weight(1f)) {
            title()
        }
        trailing()
    }
}
