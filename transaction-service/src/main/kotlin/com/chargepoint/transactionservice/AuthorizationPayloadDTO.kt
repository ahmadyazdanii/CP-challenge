package com.chargepoint.transactionservice

import java.util.UUID

data class AuthorizationPayloadDTO(val stationUuid: UUID, val driverIdentifier: DriverIdentifierDTO)
