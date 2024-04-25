package com.chargepoint.authenticationservice

import com.chargepoint.common.event.AuthenticationRequestEvent
import com.chargepoint.common.AuthorizationStatus
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AuthenticationConsumerTest {
    val authenticationService = mockk<AuthenticationService>(relaxed = true)

    lateinit var authenticationConsumer: AuthenticationConsumer

    @BeforeEach
    fun setup() {
        authenticationConsumer = AuthenticationConsumer(authenticationService)
    }

    @Nested
    @DisplayName("When a AuthenticationRequests event was process")
    inner class WhenAAuthenticationRequestsEventWasProcess() {
        @Test
        fun `Should process AuthenticationRequestEvent and produce response`() {
            val requestId = "sample-request-id"
            val driverIdentifierId = "validUserId"
            val authenticationRequestEvent = AuthenticationRequestEvent(requestId, driverIdentifierId)
            every { authenticationService.getUserAuthorizationStatus(driverIdentifierId) } returns AuthorizationStatus.Accepted
            every { authenticationService.produceAuthenticationResponseEvent(any(), any()) } just Runs

            authenticationConsumer.listenToAuthenticationRequestsTopic(authenticationRequestEvent)

            verify { authenticationService.getUserAuthorizationStatus(driverIdentifierId) }
            verify { authenticationService.produceAuthenticationResponseEvent(requestId, AuthorizationStatus.Accepted) }
        }
    }
}