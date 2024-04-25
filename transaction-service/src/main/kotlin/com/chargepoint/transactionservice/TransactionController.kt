package com.chargepoint.transactionservice

import com.chargepoint.transactionservice.dto.AuthorizationRequestDTO
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

@RestController
@RequestMapping("/transaction")
class TransactionController(
    private val transactionService: TransactionService,
    private val requestStateManager: RequestStateManager
) {
    @PostMapping("/authorize")
    fun getUserAuthorizationStatus(@Valid @RequestBody payload: AuthorizationRequestDTO): Any {
        val requestId: String = requestStateManager.generateRequestId()

        // Store current request's response
        val currentRequestState = requestStateManager.storeAuthorizationRequestState(requestId)
        // Produce an AuthenticationRequest event
        transactionService.produceAuthenticationRequestEvent(requestId, payload.driverIdentifier.id)

        return try {
            currentRequestState.first.get(10, TimeUnit.SECONDS)
        }catch (err: TimeoutException) {
            ResponseEntity
                .status(HttpStatus.REQUEST_TIMEOUT)
                .body("Request timed out. Please try again later.")
        }
    }
}