package com.chargepoint.transactionservice.dto

import com.chargepoint.transactionservice.common.AuthorizationStatus

data class AuthorizationResponseDTO(val authorizationStatus: AuthorizationStatus)
