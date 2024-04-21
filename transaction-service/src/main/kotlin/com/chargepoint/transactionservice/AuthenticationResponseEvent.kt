package com.chargepoint.transactionservice

data class AuthenticationResponseEvent(val requestId: String, val authorizationStatus: AuthorizationStatus)
