package com.chargepoint.transactionservice

import com.chargepoint.transactionservice.common.AuthenticationResponseEvent
import com.chargepoint.transactionservice.dto.AuthorizationRequestDTO
import com.chargepoint.transactionservice.dto.AuthorizationResponseDTO
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap

@RestController
@RequestMapping("/transaction")
class TransactionController(
    private val transactionService: TransactionService
) {
    private val responses = ConcurrentHashMap<String, CompletableFuture<AuthorizationResponseDTO>>()

    @PostMapping("/authorize")
    fun getUserAuthorizationStatus(@RequestBody payload: AuthorizationRequestDTO): AuthorizationResponseDTO {
        val requestId: String = UUID.randomUUID().toString()
        val response = CompletableFuture<AuthorizationResponseDTO>()

        // Produce an AuthenticationRequest event
        transactionService.produceAuthenticationRequestEvent(requestId, payload.driverIdentifier.id)
        // Store current request's response
        responses[requestId] = response

        return response.get()
    }

    @KafkaListener(topics = ["AuthenticationResponses"], groupId = "transaction", properties = [
        "spring.json.value.default.type=com.chargepoint.transactionservice.common.AuthenticationResponseEvent"]
    )
    fun listenAuthorizationStatusResponsesTopic(event: AuthenticationResponseEvent) {
        val response = responses.remove(event.requestId)

        response?.complete(
            AuthorizationResponseDTO(event.authorizationStatus)
        )
    }
}