package com.chargepoint.transactionservice

import com.chargepoint.common.event.AuthenticationResponseEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class TransactionConsumer(
    private val requestStateManager: RequestStateManager
) {

    @KafkaListener(topics = ["AuthenticationResponses"], groupId = "transaction")
    fun listenToAuthenticationResponsesTopic(event: AuthenticationResponseEvent) {
        requestStateManager.completeAndRemoveAuthorizationRequestState(event.requestId, event.authorizationStatus)
    }
}