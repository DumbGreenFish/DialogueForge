package io.github.dumbgreenfish.dialogueforge

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import io.github.dumbgreenfish.dialogueforge.data.generation.ConversationVisibility
import io.github.dumbgreenfish.dialogueforge.notification.AndroidNotificationPermission
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NotificationNavigationRequests
import org.koin.core.context.GlobalContext

class MainActivity : ComponentActivity() {
    private val conversationVisibility: ConversationVisibility by lazy { GlobalContext.get().get() }
    private val notificationPermission: AndroidNotificationPermission by lazy { GlobalContext.get().get() }
    private val navigationRequests: NotificationNavigationRequests by lazy { GlobalContext.get().get() }

    // Orientation change on small screens is very annoying
    // TODO: Move it to Application Settings to make people be able to change it if they don't like it
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (resources.configuration.smallestScreenWidthDp < 600) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
        setContent {
            App()
        }
        handleNotificationIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    override fun onStart() {
        super.onStart()
        conversationVisibility.setAppVisible(true)
        notificationPermission.attach(this)
    }

    override fun onStop() {
        notificationPermission.detach(this)
        conversationVisibility.setAppVisible(false)
        super.onStop()
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val characterId = intent?.getStringExtra(EXTRA_CHARACTER_ID) ?: return
        intent.removeExtra(EXTRA_CHARACTER_ID)
        navigationRequests.openCharacter(characterId)
    }

    companion object {
        const val EXTRA_CHARACTER_ID = "notification_character_id"
    }
}
