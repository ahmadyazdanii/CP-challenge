package com.chargepoint.transactionservice.common

data class AuthenticationRequestEvent(val requestId: String, val driverIdentifierId: String)
