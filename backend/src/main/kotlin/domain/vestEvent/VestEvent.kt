package domain.vestEvent

import java.math.BigDecimal
import java.util.*

data class VestEvent(
    val type: VestEventType,
    val employeeId: String,
    val employeeName: String,
    val awardId: String,
    val date: Date,
    val quantity: BigDecimal
) {
    enum class VestEventType{
        VEST, CANCEL
    }
}
