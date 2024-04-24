package com.chargepoint.transactionservice

import com.chargepoint.common.event.AuthenticationResponseEvent
import com.chargepoint.transactionservice.dto.AuthorizationRequestDTO
import com.chargepoint.transactionservice.dto.AuthorizationResponseDTO
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.web.bind.annotation.*
import java.util.UUID
import java.util.concurrent.*

@RestController
@RequestMapping("/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {
    private val requestsState = ConcurrentHashMap<String, Pair<CompletableFuture<AuthorizationResponseDTO>, ScheduledFuture<*>>>()
    private val scheduler = Executors.newScheduledThreadPool(1)

    private fun scheduleToRemoveSuspendedState(requestId: String): ScheduledFuture<*> {
        return scheduler.schedule({
            requestsState.remove(requestId)
        }, 15, TimeUnit.SECONDS)
    }
    private fun storeCurrentRequestState(requestId: String): Pair<CompletableFuture<AuthorizationResponseDTO>, ScheduledFuture<*>> {
        val completableResponse = CompletableFuture<AuthorizationResponseDTO>()
        val scheduledTask = scheduleToRemoveSuspendedState(requestId)
        val currentRequestState = Pair(completableResponse, scheduledTask)

        // Store current request's state
        requestsState[requestId] = currentRequestState

        return currentRequestState
    }

    @PostMapping("/authorize")
    fun getUserAuthorizationStatus(@RequestBody payload: AuthorizationRequestDTO): Any {
        val requestId: String = UUID.randomUUID().toString()

        // Store current request's response
        val currentRequestState = storeCurrentRequestState(requestId)
        // Produce an AuthenticationRequest event
        transactionService.produceAuthenticationRequestEvent(requestId, payload.driverIdentifier.id)

        try {
            return currentRequestState.first.get(10, TimeUnit.SECONDS)
        }catch (err: TimeoutException) {
            return ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body("Request timed out. Please try again later.")
        }
    }

    @KafkaListener(topics = ["AuthenticationResponses"], groupId = "transaction")
    fun listenToAuthenticationResponsesTopic(event: AuthenticationResponseEvent) {
        val currentRequestState = requestsState.remove(event.requestId)

        currentRequestState?.first?.complete(
            AuthorizationResponseDTO(event.authorizationStatus)
        )
        currentRequestState?.second?.cancel(false)
    }
}