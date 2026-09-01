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
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MessageCircle
import com.composables.icons.lucide.Palette
import io.github.dumbgreenfish.dialogueforge.config.BackgroundGenerationSettings
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.annotation.KoinExperimentalAPI

@Composable
fun CategoryItem(title: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
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


@Composable
@OptIn(KoinExperimentalAPI::class)
fun SettingsView(modifier: Modifier = Modifier) {
    val viewModel = koinViewModel<SettingsViewModel>()
    val backgroundGenerationSettings = koinInject<BackgroundGenerationSettings>()
    val state by viewModel.state.collectAsState()
    var expandedId by remember { mutableStateOf<String?>(null) }

    val listState = rememberLazyListState()

    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .widthIn(max = 600.dp),
            state = listState
        ) {
            item { CategoryItem("UI", icon = Lucide.Palette, { }) }
            item { CategoryItem("Chat", icon = Lucide.MessageCircle, { }) }
            item { CategoryItem("Notification", icon = Lucide.Bell, { }) }
            item { CategoryItem("About", icon = Lucide.Info, { }) }
        }
    }
}