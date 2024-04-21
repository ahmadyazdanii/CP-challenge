package com.chargepoint.authenticationservice

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AuthenticationConsumer(
    private val authenticationService: AuthenticationService,
    private val authenticationProducer: AuthenticationProducer
) {
    @KafkaListener(topics = ["AuthorizationStatusRequests"], groupId = "authentication")
    fun listenAuthorizationStatusRequestsTopic(payload: AuthorizationStatusRequest) {
        val authorizationStatus = authenticationService.getUserAuthorizationStatus(payload.driverIdentifierId)

        authenticationProducer.produceAuthorizationStatusResponse(payload.requestId, authorizationStatus)
    }
}