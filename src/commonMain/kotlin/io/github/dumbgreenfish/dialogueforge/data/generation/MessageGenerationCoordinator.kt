package io.github.dumbgreenfish.dialogueforge.data.generation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single(binds = [GenerationController::class])
class MessageGenerationCoordinator(
    private val task: GenerationTask,
    private val lifetime: GenerationLifetime,
    private val notifier: GenerationNotifier,
) : GenerationController {
    private var scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val jobs = MutableStateFlow<Map<String, ActiveGeneration>>(emptyMap())
    private val _activeConversationIds = MutableStateFlow<Set<String>>(emptySet())
    override val activeConversationIds: StateFlow<Set<String>> = _activeConversationIds.asStateFlow()
    private val _changedConversationIds = MutableSharedFlow<String>(extraBufferCapacity = 32)
    override val changedConversationIds: SharedFlow<String> = _changedConversationIds.asSharedFlow()

    internal constructor(
        task: GenerationTask,
        lifetime: GenerationLifetime,
        notifier: GenerationNotifier,
        scope: CoroutineScope,
    ) : this(task, lifetime, notifier) {
        this.scope = scope
    }

    override fun start(request: GenerationRequest): Boolean {
        lateinit var job: Job
        job = scope.launch(start = kotlinx.coroutines.CoroutineStart.LAZY) {
            try {
                when (val result = task.run(request)) {
                    is GenerationResult.Success -> notifier.completed(request.conversationId, result)
                    GenerationResult.Failure -> Unit
                }
            } finally {
                remove(request.conversationId)
            }
        }

        while (true) {
            val current = jobs.value
            if (request.conversationId in current) {
                job.cancel()
                return false
            }
            val updated = current + (request.conversationId to ActiveGeneration(job, request))
            if (jobs.compareAndSet(current, updated)) {
                publishActive(updated)
                break
            }
        }

        return try {
            lifetime.activeGenerationsChanged(jobs.value.values.map(ActiveGeneration::request))
            job.start()
        } catch (_: Exception) {
            job.cancel()
            remove(request.conversationId)
            false
        }
    }

    override fun cancel(conversationId: String) {
        jobs.value[conversationId]?.job?.cancel(UserGenerationCancellationException())
    }

    override fun cancelAll() {
        jobs.value.values.forEach { it.job.cancel(UserGenerationCancellationException()) }
    }

    override fun interruptAll() {
        jobs.value.values.forEach { it.job.cancel() }
    }

    private fun remove(conversationId: String) {
        while (true) {
            val current = jobs.value
            if (conversationId !in current) return
            val updated = current - conversationId
            if (jobs.compareAndSet(current, updated)) {
                publishActive(updated)
                _changedConversationIds.tryEmit(conversationId)
                lifetime.activeGenerationsChanged(updated.values.map(ActiveGeneration::request))
                return
            }
        }
    }

    private fun publishActive(activeJobs: Map<String, ActiveGeneration>) {
        _activeConversationIds.value = activeJobs.keys
    }

    private data class ActiveGeneration(
        val job: Job,
        val request: GenerationRequest,
    )
}
