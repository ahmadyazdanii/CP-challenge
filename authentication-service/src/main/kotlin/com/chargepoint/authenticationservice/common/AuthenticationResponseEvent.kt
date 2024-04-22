package com.chargepoint.authenticationservice.common

data class AuthenticationResponseEvent(val requestId: String, val authorizationStatus: AuthorizationStatus)
