package com.chargepoint.common.event

import com.chargepoint.common.AuthorizationStatus

data class AuthenticationResponseEvent(val requestId: String, val authorizationStatus: AuthorizationStatus)
