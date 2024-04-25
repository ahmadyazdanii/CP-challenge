package com.chargepoint.transactionservice

import com.chargepoint.common.AuthorizationStatus
import com.chargepoint.common.event.AuthenticationResponseEvent
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.Runs
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID

class TransactionConsumerTest {
    val requestStateManager = mockk<RequestStateManager>(relaxed = true)

    lateinit var transactionConsumer: TransactionConsumer

    @BeforeEach
    fun setup() {
        transactionConsumer = TransactionConsumer(requestStateManager)
    }

    @Nested
    @DisplayName("When AuthenticationResponseEvent was processed")
    inner class WhenAuthenticationResponseEventWasProcessed() {
        @Test
        fun `Should complete related request and remove it from state manager`() {
            val requestId = UUID.randomUUID().toString()
            val authorizationStatus = AuthorizationStatus.Accepted
            val authenticationResponseEvent = AuthenticationResponseEvent(requestId, authorizationStatus)
            every { requestStateManager.completeAndRemoveAuthorizationRequestState(requestId, authorizationStatus) } just Runs

            transactionConsumer.listenToAuthenticationResponsesTopic(authenticationResponseEvent)

            verify { requestStateManager.completeAndRemoveAuthorizationRequestState(requestId, authorizationStatus) }
        }
    }
}