package io.github.dumbgreenfish.dialogueforge.ui.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Bell
import com.composables.icons.lucide.Info
import com.composables.icons.lucide.Languages
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Palette
import io.github.dumbgreenfish.dialogueforge.config.BackgroundGenerationSettings
import io.github.dumbgreenfish.dialogueforge.generated.resources.Res
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_category_about
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_category_chat
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_category_language
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_category_notifications
import io.github.dumbgreenfish.dialogueforge.generated.resources.settings_category_ui
import io.github.dumbgreenfish.dialogueforge.ui.navigation.NavController
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.NavTabs
import io.github.dumbgreenfish.dialogueforge.ui.navigation.tabs.SettingsTab
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
@OptIn(KoinExperimentalAPI::class)
fun SettingsView(modifier: Modifier = Modifier) {
    @Composable
    fun CategoryItem(
        title: String,
        icon: ImageVector,
        screen: SettingsTab.Screen
    ) {
        Surface(
            onClick = {
                val tab = NavTabs.Settings.tabObject as SettingsTab
                tab.navigateTo(screen)
            },
            shape = MaterialTheme.shapes.medium,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )
                Text(title, style = MaterialTheme.typography.bodyLarge)
            }
        }
    }

    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .widthIn(max = 600.dp)
        ) {
            item { CategoryItem(stringResource(Res.string.settings_category_ui), icon = Lucide.Palette, SettingsTab.Screen.UiSettingsScreen("Test")) }
            item { CategoryItem(stringResource(Res.string.settings_category_chat), icon = Lucide.MessageCircle, SettingsTab.Screen.UiSettingsScreen("Test")) }
            item { CategoryItem(stringResource(Res.string.settings_category_notifications), icon = Lucide.Bell, SettingsTab.Screen.UiSettingsScreen("Test")) }
            item { CategoryItem(stringResource(Res.string.settings_category_about), icon = Lucide.Info, SettingsTab.Screen.UiSettingsScreen("Test")) }
            item { CategoryItem(stringResource(Res.string.settings_category_language), icon = Lucide.Languages, SettingsTab.Screen.UiSettingsScreen("Test")) }
            item { CategoryItem(stringResource(Res.string.settings_category_about), icon = Lucide.Info, SettingsTab.Screen.UiSettingsScreen("Test")) }
        }
    }
}