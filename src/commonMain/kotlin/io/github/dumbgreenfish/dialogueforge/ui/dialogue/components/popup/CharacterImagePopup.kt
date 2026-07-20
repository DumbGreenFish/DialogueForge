package io.github.dumbgreenfish.dialogueforge.ui.dialogue.components.popup

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.X
import io.github.dumbgreenfish.dialogueforge.data.cache.ImageCache
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.dialogue_character_image_close
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.stringResource

private val PopupPadding = 32.dp
private val PopupBorderWidth = 1.dp
private const val PopupBorderAlpha = 0.06f
private val PopupCornerRadius = 12.dp
private const val PopupBackdropAlpha = 0.6f
private val PopupCloseButtonPadding = 8.dp

@Composable
internal fun CharacterImagePopup(
    characterId: String,
    characterName: String,
    imageCache: ImageCache,
    onDismiss: () -> Unit,
) {
    var windowSize by remember { mutableStateOf(IntSize.Zero) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val density = LocalDensity.current.density

        Box(
            modifier = Modifier
                .fillMaxSize()
                .onSizeChanged { windowSize = it },
        ) {
            val maxDim = maxOf(windowSize.width, windowSize.height).coerceAtLeast(1)
            val popupFlow = imageCache.observePopup(characterId, maxDim)
            val bitmap by popupFlow.collectAsState()

            val frameDimens = calculateFrameDimens(bitmap, windowSize, density)

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = PopupBackdropAlpha))
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onDismiss,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (frameDimens != null) {
                    Box {
                        Box(
                            modifier = Modifier
                                .size(frameDimens.first, frameDimens.second)
                                .clip(RoundedCornerShape(PopupCornerRadius))
                                .border(
                                    width = PopupBorderWidth,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = PopupBorderAlpha),
                                    shape = RoundedCornerShape(PopupCornerRadius),
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = {},
                                ),
                        ) {
                            Image(
                                bitmap = bitmap!!,
                                contentDescription = characterName,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit,
                                filterQuality = FilterQuality.High,
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(PopupCloseButtonPadding),
                        ) {
                            Icon(
                                imageVector = Lucide.X,
                                contentDescription = stringResource(Res.string.dialogue_character_image_close),
                                tint = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun calculateFrameDimens(
    bitmap: androidx.compose.ui.graphics.ImageBitmap?,
    windowSize: IntSize,
    density: Float,
): Pair<Dp, Dp>? {
    if (bitmap == null || windowSize.width <= 0 || windowSize.height <= 0) return null

    val paddingPx = (PopupPadding.value * density * 2f).roundToInt()
    val availW = (windowSize.width - paddingPx).coerceAtLeast(1)
    val availH = (windowSize.height - paddingPx).coerceAtLeast(1)

    val imageAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
    val spaceAspect = availW.toFloat() / availH.toFloat()

    val fitW: Float
    val fitH: Float
    if (spaceAspect > imageAspect) {
        fitH = availH.toFloat()
        fitW = availH * imageAspect
    } else {
        fitW = availW.toFloat()
        fitH = availW / imageAspect
    }

    return (fitW / density).dp to (fitH / density).dp
}
