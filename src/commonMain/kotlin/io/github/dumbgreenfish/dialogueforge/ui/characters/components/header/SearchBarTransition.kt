package io.github.dumbgreenfish.dialogueforge.ui.characters.components.header

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith

internal fun AnimatedContentTransitionScope<Boolean>.searchBarTransition(): ContentTransform =
    if (targetState) {
        (fadeIn() + slideInHorizontally { it / 4 }).togetherWith(
            fadeOut() + slideOutHorizontally { -it / 4 }
        ).using(SizeTransform(clip = false))
    } else {
        (fadeIn() + slideInHorizontally { -it / 4 }).togetherWith(
            fadeOut() + slideOutHorizontally { it / 4 }
        ).using(SizeTransform(clip = false))
    }
