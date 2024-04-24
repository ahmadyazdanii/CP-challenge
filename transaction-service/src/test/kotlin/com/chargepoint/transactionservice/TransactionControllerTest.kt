package com.chargepoint.transactionservice

import com.chargepoint.transactionservice.dto.AuthorizationRequestDTO
import com.chargepoint.transactionservice.dto.DriverIdentifierDTO
import com.fasterxml.jackson.databind.ObjectMapper
import io.mockk.mockk
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.junit.jupiter.api.*
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import java.util.UUID

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {
    @Autowired
    lateinit var mockMvc: MockMvc
    @Autowired
    lateinit var objectMapper: ObjectMapper

    val transactionService = mockk<TransactionService>(relaxed = true)
    lateinit var transactionController: TransactionController

    @BeforeEach
    fun setup() {
        transactionController = TransactionController(transactionService)
    }

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
        fun `Should return a request-timeout when the response wont be prepared after 10 seconds`() {
            val validPayload = AuthorizationRequestDTO(
                UUID.randomUUID(),
                DriverIdentifierDTO("valid-id")
            )

            val performPost = mockMvc.post("/transaction/authorize") {
                contentType = MediaType.APPLICATION_JSON
                content = objectMapper.writeValueAsString(validPayload)
            }


            performPost
                .andDo { print() }
                .andExpect {
                    status { isRequestTimeout() }
                }
        }
    }
}