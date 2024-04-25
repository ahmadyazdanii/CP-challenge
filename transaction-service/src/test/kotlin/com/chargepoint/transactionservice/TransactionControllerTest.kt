package com.chargepoint.transactionservice

import com.chargepoint.common.AuthorizationStatus
import com.chargepoint.transactionservice.dto.AuthorizationRequestDTO
import com.chargepoint.transactionservice.dto.AuthorizationResponseDTO
import com.chargepoint.transactionservice.dto.DriverIdentifierDTO
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.http.MediaType
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.web.servlet.post
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledFuture

@SpringBootTest
@AutoConfigureMockMvc
@EmbeddedKafka
class TransactionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    private lateinit var requestStateManager: RequestStateManager


    @Nested
    @DisplayName("When POST /transaction/authorize was called")
    inner class WhenPostTransactionAuthorizeWasCalled() {
        @Test
        fun `Should return a bad-request response when stationUuid is not valid uuid v4`() {
            val performPost = mockMvc.post("/transaction/authorize") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(object {
                    val stationUuid = "invalid-uuid"
                    val driverIdentifier = object {
                        val id= "valid-id"
                    }
                })
            }

            performPost
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `Should return a bad-request response when props were missed`() {
            val performPost = mockMvc.post("/transaction/authorize") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(object {})
            }

            performPost
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `Should return a bad-request response when driverIdentifier id is an empty string`() {
            val performPost = mockMvc.post("/transaction/authorize") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(object {
                    val stationUuid = UUID.randomUUID().toString()
                    val driverIdentifier = object {
                        val id = ""
                    }
                })
            }

            performPost
                .andDo { print() }
                .andExpect {
                    status { isBadRequest() }
                }
        }

        @Test
        fun `Should return Ok when everything works well`() {
            val requestId = UUID.randomUUID().toString()
            val response = CompletableFuture<AuthorizationResponseDTO>()
            val currentRequestState = Pair<CompletableFuture<AuthorizationResponseDTO>, ScheduledFuture<*>>(response, mock())
            val payload = AuthorizationRequestDTO(UUID.randomUUID(), DriverIdentifierDTO("valid-id"))
            val expectedResponse = AuthorizationResponseDTO(AuthorizationStatus.Accepted)

            response.complete(expectedResponse)  // Simulate a successful authorization

            `when`(requestStateManager.generateRequestId()).thenReturn(requestId)
            `when`(requestStateManager.storeAuthorizationRequestState(requestId)).thenReturn(currentRequestState)

            val performPost = mockMvc.post("/transaction/authorize") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(payload)
            }

            performPost
                .andDo { print() }
                .andExpect {
                    status { isOk() }
                    content {
                        json(
                            objectMapper.writeValueAsString(expectedResponse)
                        )
                    }
                }
        }
    }
}