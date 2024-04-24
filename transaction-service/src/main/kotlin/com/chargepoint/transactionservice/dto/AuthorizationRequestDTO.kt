package com.chargepoint.transactionservice.dto

import jakarta.validation.Valid
import java.util.UUID

data class AuthorizationRequestDTO(val stationUuid: UUID, @field:Valid val driverIdentifier: DriverIdentifierDTO)
