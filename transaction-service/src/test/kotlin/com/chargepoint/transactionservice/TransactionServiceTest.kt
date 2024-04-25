package com.chargepoint.transactionservice

import com.chargepoint.common.event.AuthenticationRequestEvent
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.kafka.core.KafkaTemplate

class TransactionServiceTest {
    val kafkaTemplate = mockk<KafkaTemplate<String, AuthenticationRequestEvent>>(relaxed = true)

    lateinit var transactionService: TransactionService

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