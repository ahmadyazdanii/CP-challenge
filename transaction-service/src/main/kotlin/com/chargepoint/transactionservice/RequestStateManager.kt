package com.chargepoint.transactionservice

import com.chargepoint.common.AuthorizationStatus
import com.chargepoint.transactionservice.dto.AuthorizationResponseDTO
import org.springframework.stereotype.Component
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

@Component
class RequestStateManager {
    private val requestsState = ConcurrentHashMap<String, Pair<CompletableFuture<AuthorizationResponseDTO>, ScheduledFuture<*>>>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    private fun scheduleToRemoveSuspendedState(requestId: String): ScheduledFuture<*> {
        return scheduler.schedule({
            requestsState.remove(requestId)
        }, 15, TimeUnit.SECONDS)
    }

    fun storeAuthorizationRequestState(requestId: String): Pair<CompletableFuture<AuthorizationResponseDTO>, ScheduledFuture<*>> {
        val completableResponse = CompletableFuture<AuthorizationResponseDTO>()
        val scheduledTask = scheduleToRemoveSuspendedState(requestId)
        val currentRequestState = Pair(completableResponse, scheduledTask)

        // Store request's state
        requestsState[requestId] = currentRequestState

        return currentRequestState
    }

    fun completeAndRemoveAuthorizationRequestState(requestId: String, authorizationStatus: AuthorizationStatus) {
        val authorizationRequestState = requestsState.remove(requestId)

        authorizationRequestState?.first?.complete(
            AuthorizationResponseDTO(authorizationStatus)
        )
        authorizationRequestState?.second?.cancel(false)
    }

    fun generateRequestId() = UUID.randomUUID().toString()
}