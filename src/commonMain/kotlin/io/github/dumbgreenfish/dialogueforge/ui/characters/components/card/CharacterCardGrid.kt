package io.github.dumbgreenfish.dialogueforge.ui.characters.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.dumbgreenfish.dialogueforge.design.ForgeColors
import io.github.dumbgreenfish.dialogueforge.design.ForgeShape
import io.github.dumbgreenfish.dialogueforge.data.cache.ImageCache
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.character_menu_more
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.CharacterContextMenu
import io.github.dumbgreenfish.dialogueforge.ui.characters.components.menu.ContextAction
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Character
import io.github.dumbgreenfish.dialogueforge.ui.characters.model.Tag
import io.github.dumbgreenfish.dialogueforge.ui.common.CharacterAvatar
import io.github.dumbgreenfish.dialogueforge.ui.common.rememberImageProvider
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

private const val NAME_MAX_LINES    = 1
private const val TAGLINE_MAX_LINES = 2
private const val GRADIENT_ALPHA    = 0.85f
private const val BADGE_ALPHA       = 0.70f
private val PinBadgeSize        = 24.dp
private val MoreBadgeSize       = 30.dp
private val MoreIconSize        = 18.dp
private val BadgeClusterPadding = 8.dp
private val BadgeClusterGap     = 6.dp
private val GradientOverlayPadH      = 8.dp
private val GradientOverlayPadTop    = 16.dp
private val GradientOverlayPadBottom = 8.dp
private val TagRowGap                = 4.dp
private val PreloadDialogueSizes     = listOf(28.dp, 48.dp, 64.dp)

@Composable
internal fun CharacterCardGrid(
    char: Character,
    onClick: () -> Unit,
    onDeleteRequest: (Character) -> Unit,
    isCompact: Boolean,
) {
    CharacterCardShell(char, onClick, onDeleteRequest, isCompact) { menuExpanded, onMoreClick, onMenuDismiss, actions ->
        Column {
            GridSquare(
                char          = char,
                isCompact     = isCompact,
                menuExpanded  = menuExpanded,
                onMoreClick   = onMoreClick,
                onMenuDismiss = onMenuDismiss,
                actions       = actions,
            )
            GridInfo(char)
        }
    }
}

@Composable
private fun GridSquare(
    char: Character,
    isCompact: Boolean,
    menuExpanded: Boolean,
    onMoreClick: () -> Unit,
    onMenuDismiss: () -> Unit,
    actions: List<ContextAction>,
) {
    val bg = MaterialTheme.colorScheme.background
    BoxWithConstraints(modifier = Modifier.fillMaxWidth().aspectRatio(1f)) {
        val flowWidth = maxWidth - GradientOverlayPadH * 2
        val maxVisible = calculateVisibleTags(
            tags = char.tags,
            containerWidth = flowWidth,
            chipPadH = TagHorzPadding,
            gap = TagRowGap,
        )
        val hasOverflow = maxVisible < char.tags.size
        val visibleCount = if (hasOverflow) maxOf(1, maxVisible - 1) else maxVisible
        val visibleTags = char.tags.take(visibleCount)
        val extraCount = char.tags.size - visibleCount

        val imageCache = koinInject<ImageCache>()
        val density = LocalDensity.current.density
        val gridDim = remember(maxWidth, density) { (maxWidth.value * density * 1.5f).toInt() }
        LaunchedEffect(char.id) {
            val dialogueDims = PreloadDialogueSizes.map { (it.value * density * 1.5f).toInt() }
            imageCache.preload(char.id, dialogueDims + gridDim)
        }

        CharacterAvatar(imageProvider = rememberImageProvider(char.id), targetSizeDp = maxWidth, modifier = Modifier.fillMaxSize(), shape = ForgeShape.avatar)
        GradientOverlay(bg.copy(alpha = 0f), bg.copy(alpha = GRADIENT_ALPHA)) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(TagRowGap), verticalArrangement = Arrangement.spacedBy(TagRowGap)) {
                CardTagChips(visibleTags, extraCount)
            }
        }
        if (char.pinned || !isCompact) {
            Row(
                modifier = Modifier.align(Alignment.TopEnd).padding(BadgeClusterPadding),
                horizontalArrangement = Arrangement.spacedBy(BadgeClusterGap),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (char.pinned) PinBadge()
                if (!isCompact) {
                    Box {
                        MoreBadge(onClick = onMoreClick)
                        CharacterContextMenu(
                            expanded = menuExpanded,
                            onDismiss = onMenuDismiss,
                            actions = actions
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BoxScope.GradientOverlay(
    colorTop: Color,
    colorBottom: Color,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomStart)
            .background(Brush.verticalGradient(0f to colorTop, 1f to colorBottom))
            .padding(start = GradientOverlayPadH, end = GradientOverlayPadH, top = GradientOverlayPadTop, bottom = GradientOverlayPadBottom),
    ) { content() }
}

@Composable
private fun calculateVisibleTags(
    tags: List<Tag>,
    containerWidth: Dp,
    chipPadH: Dp,
    gap: Dp,
    maxLines: Int = 1,
): Int {
    if (containerWidth <= 0.dp || tags.isEmpty()) return 0

    val measurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val style = MaterialTheme.typography.labelMedium
    val padTotalPx = with(density) { (chipPadH * 2).toPx() }
    val gapPx = with(density) { gap.toPx() }
    val maxPx = with(density) { containerWidth.toPx() }

    val widths = tags.map { tag ->
        measurer.measure(tag.value, style, maxLines = 1).size.width + padTotalPx
    }

    var lines = 1
    var linePx = 0f
    var count = 0
    for (w in widths) {
        val needed = if (linePx == 0f) w else w + gapPx
        if (linePx + needed > maxPx) {
            lines++
            if (lines > maxLines) break
            linePx = w
        } else {
            linePx += needed
        }
        count++
    }
    return count.coerceAtLeast(1)
}

@Composable
private fun PinBadge() {
    val bg = MaterialTheme.colorScheme.background
    Box(
        modifier = Modifier.size(PinBadgeSize).background(bg.copy(alpha = BADGE_ALPHA), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.PushPin,
            contentDescription = null,
            tint = ForgeColors.spark,
            modifier = Modifier.size(13.dp).graphicsLayer { rotationZ = ForgeShape.pinRotationDeg },
        )
    }
}

@Composable
private fun MoreBadge(onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.size(MoreBadgeSize).background(cs.background.copy(alpha = BADGE_ALPHA), CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(MoreBadgeSize)) {
            Icon(
                imageVector = Icons.Filled.MoreVert,
                contentDescription = stringResource(Res.string.character_menu_more),
                tint = cs.onSurface,
                modifier = Modifier.size(MoreIconSize),
            )
        }
    }
}

@Composable
private fun GridInfo(char: Character) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = char.name,
                style = MaterialTheme.typography.titleSmall,
                color = cs.onSurface,
                maxLines = NAME_MAX_LINES,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.size(6.dp))
            Text(text = char.lastUsed, style = MaterialTheme.typography.labelSmall, color = ForgeColors.onSurfaceFaint)
        }
        Text(
            text = char.tagline,
            style = MaterialTheme.typography.bodySmall,
            color = cs.onSurfaceVariant,
            maxLines = TAGLINE_MAX_LINES,
            overflow = TextOverflow.Ellipsis,
        )
    }
}