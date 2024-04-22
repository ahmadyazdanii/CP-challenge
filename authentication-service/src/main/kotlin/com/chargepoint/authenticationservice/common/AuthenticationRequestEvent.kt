package com.chargepoint.authenticationservice.common

data class AuthenticationRequestEvent(val requestId: String, val driverIdentifierId: String)
