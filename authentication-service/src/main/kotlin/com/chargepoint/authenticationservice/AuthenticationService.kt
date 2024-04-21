package com.chargepoint.authenticationservice

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val kafkaTemplate: KafkaTemplate<String, AuthenticationResponseEvent>
) {
    private val users = arrayOf(
        User("af22009d-efc8-4cc4-8802-fa7b2bb98330", true),
        User("bb72649d-9246-423c-a56e-c8b92117dcdd", false),
        User("af22009d-efc8-4cc4-8", true),
        User("68276308-0731-4961-b", false),
        User("018f0127-8ee2-77a5-9f85-f4ad4f40a068-018f0127-8ee2-77a5-9f85-f4ad4f40a068-018f01", true),
        User("018f0128-d256-7041-b35f-851d55182364-018f0128-d256-7041-b35f-851d55182364-018f01", false),
    )

    fun getUserAuthorizationStatus(userId: String): AuthorizationStatus {
        // Check Invalid status
        if (userId.length !in 20..80) {
            return AuthorizationStatus.Invalid
        }
        // Find user via user ID
        val userInstance = users.find { it.id == userId }

        // Check Unknown status
        if (userInstance == null) {
            return AuthorizationStatus.Unknown
        }
        // Check Rejected status
        if(!userInstance.isAllowedToCharge) {
            return AuthorizationStatus.Rejected
        }

        // If a valid user is allowed to charge, Accepted status will be returned
        return AuthorizationStatus.Accepted
    }

    fun produceAuthenticationResponseEvent(requestId: String, authorizationStatus: AuthorizationStatus) {
        kafkaTemplate.send(
            "AuthenticationResponses",
            AuthenticationResponseEvent(requestId, authorizationStatus)
        )
    }
}