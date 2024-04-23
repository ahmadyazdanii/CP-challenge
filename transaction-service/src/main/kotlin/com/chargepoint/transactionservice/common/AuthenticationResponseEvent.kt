package com.chargepoint.transactionservice.common

data class AuthenticationResponseEvent(val requestId: String, val authorizationStatus: AuthorizationStatus)
