package com.chargepoint.transactionservice

data class AuthenticationRequestEvent(val requestId: String, val driverIdentifierId: String)
