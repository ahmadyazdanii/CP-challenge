package com.chargepoint.authenticationservice

import com.chargepoint.common.event.AuthenticationRequestEvent
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AuthenticationConsumer(
    private val authenticationService: AuthenticationService,
) {
    @KafkaListener(topics = ["AuthenticationRequests"], groupId = "authentication")
    fun listenToAuthenticationRequestsTopic(payload: AuthenticationRequestEvent) {
        val authorizationStatus = authenticationService.getUserAuthorizationStatus(payload.driverIdentifierId)

        authenticationService.produceAuthenticationResponseEvent(payload.requestId, authorizationStatus)
    }
}