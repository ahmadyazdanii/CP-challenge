package com.chargepoint.authenticationservice

import com.chargepoint.authenticationservice.repository.UsersRepository
import com.chargepoint.common.event.AuthenticationResponseEvent
import com.chargepoint.common.AuthorizationStatus
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class AuthenticationService(
    private val kafkaTemplate: KafkaTemplate<String, AuthenticationResponseEvent>,
    private val usersRepository: UsersRepository
) {

    fun getUserAuthorizationStatus(userId: String): AuthorizationStatus {
        // Check Invalid status
        if (userId.length !in 20..80) {
            return AuthorizationStatus.Invalid
        }
        // Find user via user ID
        val userInstance = usersRepository.findOneById(userId) ?: return AuthorizationStatus.Unknown

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