package com.chargepoint.transactionservice.dto

import jakarta.validation.constraints.NotBlank

data class DriverIdentifierDTO(@field:NotBlank val id: String)
