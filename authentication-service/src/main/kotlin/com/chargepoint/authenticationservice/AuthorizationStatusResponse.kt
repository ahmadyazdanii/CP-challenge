package com.chargepoint.authenticationservice

data class AuthorizationStatusResponse(val requestId: String, val authorizationStatus: AuthorizationStatus)
