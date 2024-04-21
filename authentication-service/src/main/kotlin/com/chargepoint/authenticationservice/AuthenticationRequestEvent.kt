package com.chargepoint.authenticationservice

data class AuthenticationRequestEvent(val requestId: String, val driverIdentifierId: String)
