package com.chargepoint.authenticationservice

import com.chargepoint.authenticationservice.common.AuthenticationRequestEvent
import com.chargepoint.authenticationservice.common.AuthorizationStatus
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class AuthenticationConsumerTest {
    @MockK
    private lateinit var authenticationService: AuthenticationService

    @InjectMockKs
    private lateinit var authenticationConsumer: AuthenticationConsumer

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
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