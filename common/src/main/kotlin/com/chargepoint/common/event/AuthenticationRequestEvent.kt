package com.chargepoint.common.event

data class AuthenticationRequestEvent(val requestId: String, val driverIdentifierId: String)
