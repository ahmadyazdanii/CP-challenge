package com.chargepoint.transactionservice.dto

import java.util.UUID

data class AuthorizationRequestDTO(val stationUuid: UUID, val driverIdentifier: DriverIdentifierDTO)
