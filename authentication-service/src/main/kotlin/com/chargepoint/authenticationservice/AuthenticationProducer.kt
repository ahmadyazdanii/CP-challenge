package com.chargepoint.authenticationservice

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuthenticationProducer(private val kafkaTemplate: KafkaTemplate<String, AuthorizationStatusResponse>) {
    fun produceAuthorizationStatusResponse(requestId: String, authorizationStatus: AuthorizationStatus) {
        kafkaTemplate.send(
            "AuthorizationStatusResponses",
            AuthorizationStatusResponse(requestId, authorizationStatus)
        )
    }
}