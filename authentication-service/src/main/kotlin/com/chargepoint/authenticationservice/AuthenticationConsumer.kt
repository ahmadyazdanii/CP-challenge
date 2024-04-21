package com.chargepoint.authenticationservice

import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class AuthenticationConsumer(
    private val authenticationService: AuthenticationService,
) {
    @KafkaListener(topics = ["AuthenticationRequests"], groupId = "authentication", properties = [
        "spring.json.value.default.type=com.chargepoint.authenticationservice.AuthenticationRequestEvent"]
    )
    fun listenToAuthenticationRequestsTopic(payload: AuthenticationRequestEvent) {
        val authorizationStatus = authenticationService.getUserAuthorizationStatus(payload.driverIdentifierId)

        authenticationService.produceAuthenticationResponseEvent(payload.requestId, authorizationStatus)
    }
}