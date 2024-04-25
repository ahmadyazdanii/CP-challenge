package com.chargepoint.authenticationservice

import com.chargepoint.common.event.AuthenticationResponseEvent
import com.chargepoint.common.AuthorizationStatus
import com.chargepoint.authenticationservice.model.User
import com.chargepoint.authenticationservice.repository.UsersRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

import org.springframework.kafka.core.KafkaTemplate

class AuthenticationServiceTest {
    val kafkaTemplate = mockk<KafkaTemplate<String, AuthenticationResponseEvent>>(relaxed = true)
    val usersRepository = mockk<UsersRepository>()

    lateinit var authenticationService: AuthenticationService

    @BeforeEach
    fun setup() {
        authenticationService = AuthenticationService(kafkaTemplate, usersRepository)
    }

    @Nested
    @DisplayName("When getUserAuthorizationStatus was called")
    inner class WhenGetUserAuthorizationStatusWasCalled {
        @Test
        fun `Should return Invalid status when userId not within valid length`() {
            val shortUserId = "1234123412341234123" // 19 chars
            val longUserId = "123412341234123412341234123412341234123412341234123412341234123412341234123412341" // 81 chars

            assertAll(
                {
                    assertEquals(
                        AuthorizationStatus.Invalid,
                        authenticationService.getUserAuthorizationStatus(shortUserId)
                    )
                }, {
                    assertEquals(
                        AuthorizationStatus.Invalid,
                        authenticationService.getUserAuthorizationStatus(longUserId)
                    )
                }
            )
        }
        
        @Test
        fun `Should return Unknown status when userId does not exist`() {
            val validUserId = "12345123451234512345" // 20 chars
            every { usersRepository.findOneById(validUserId) } returns null

            assertEquals(AuthorizationStatus.Unknown, authenticationService.getUserAuthorizationStatus(validUserId))
        }

        @Test
        fun `Should return Rejected status when userId is not allowed to charge`() {
            val validUserId = "12345123451234512345" // 20 chars
            val user = User(validUserId, false)
            every { usersRepository.findOneById(validUserId) } returns user

            assertEquals(AuthorizationStatus.Rejected, authenticationService.getUserAuthorizationStatus(validUserId))
        }
        
        @Test
        fun `Should return Accepted status when all conditions are met`() {
            val validUserId = "12345123451234512345" // 20 chars
            val user = User(validUserId, true)
            every { usersRepository.findOneById(validUserId) } returns user

            assertEquals(AuthorizationStatus.Accepted, authenticationService.getUserAuthorizationStatus(validUserId))
        }
    }
    
    @Nested
    @DisplayName("When produceAuthenticationResponseEvent was called")
    inner class WhenProduceAuthenticationResponseEventWasCalled() {
        @Test
        fun `should send a message with correct parameters`() {
            val requestId = "sample-request-id"
            val status = AuthorizationStatus.Accepted
            val event = AuthenticationResponseEvent(requestId, status)

            authenticationService.produceAuthenticationResponseEvent(requestId, status)

            verify { kafkaTemplate.send("AuthenticationResponses", event) }
        }
    }
}