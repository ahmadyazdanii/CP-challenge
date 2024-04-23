package com.chargepoint.transactionservice

import com.chargepoint.transactionservice.common.AuthenticationRequestEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.BeforeEach
import org.springframework.kafka.core.KafkaTemplate

class TransactionServiceTest {
    private val kafkaTemplate = mockk<KafkaTemplate<String, AuthenticationRequestEvent>>(relaxed = true)

    private lateinit var transactionService: TransactionService

    @BeforeEach
    fun setup() {
        transactionService = TransactionService(kafkaTemplate)
    }
    
    @Nested
    @DisplayName("When produceAuthenticationRequestEvent was called")
    inner class WhenProduceAuthenticationRequestEventWasCalled() {
        @Test
        fun `Should send AuthenticationRequestEvent with correct parameters`() {
            val requestId = "sample-request-id"
            val driverIdentifierId = "driver-1"

            transactionService.produceAuthenticationRequestEvent(requestId, driverIdentifierId)

            verify {
                kafkaTemplate.send(
                    "AuthenticationRequests",
                    AuthenticationRequestEvent(requestId, driverIdentifierId)
                )
            }
        }
    }
}