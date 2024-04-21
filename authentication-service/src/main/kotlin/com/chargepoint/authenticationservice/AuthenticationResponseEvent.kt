package com.chargepoint.authenticationservice

data class AuthenticationResponseEvent(val requestId: String, val authorizationStatus: AuthorizationStatus)
