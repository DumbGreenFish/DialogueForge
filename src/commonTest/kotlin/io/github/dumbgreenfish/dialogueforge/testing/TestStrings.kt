package io.github.dumbgreenfish.dialogueforge.testing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import kotlinx.coroutines.CompletableDeferred
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

class TestStrings(vararg resources: StringResource) {
    private val resources = resources.toList()
    private val resolvedValues = CompletableDeferred<Map<StringResource, String>>()

    @Composable
    fun provide(content: @Composable () -> Unit) {
        val currentValues = mutableMapOf<StringResource, String>()
        for (resource in resources) {
            currentValues[resource] = stringResource(resource)
        }
        SideEffect {
            if (currentValues.values.all(String::isNotEmpty)) {
                resolvedValues.complete(currentValues)
            }
        }
        content()
    }

    suspend operator fun get(resource: StringResource): String {
        require(resource in resources) {
            "String resource was not registered: $resource"
        }
        return checkNotNull(resolvedValues.await()[resource]) {
            "String resource did not resolve: $resource"
        }
    }
}
