package com.chargepoint.transactionservice.dto

import com.chargepoint.common.AuthorizationStatus

data class AuthorizationResponseDTO(val authorizationStatus: AuthorizationStatus)
