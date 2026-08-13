package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CompletionNotificationPolicyTest {
    @Test
    fun completion_is_suppressed_only_when_the_same_character_is_visible() {
        assertFalse(shouldPostCompletionNotification(true, "character-a", "character-a"))
        assertTrue(shouldPostCompletionNotification(true, "character-b", "character-a"))
        assertTrue(shouldPostCompletionNotification(true, null, "character-a"))
        assertTrue(shouldPostCompletionNotification(false, "character-a", "character-a"))
    }
}
