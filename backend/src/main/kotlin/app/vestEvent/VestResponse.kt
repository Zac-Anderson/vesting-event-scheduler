package app.vestEvent

import java.math.BigDecimal

data class VestResponse(
    val employeeId: String,
    val employeeName: String,
    val awardId: String,
    var quantity: BigDecimal
)