package com.chargepoint.transactionservice

import com.chargepoint.common.event.AuthenticationRequestEvent
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class TransactionService(
    private val kafkaTemplate: KafkaTemplate<String, AuthenticationRequestEvent>
) {
    fun produceAuthenticationRequestEvent(requestId: String, driverIdentifierId: String) {
        kafkaTemplate.send(
            "AuthenticationRequests",
            AuthenticationRequestEvent(requestId, driverIdentifierId)
        )
    }
}