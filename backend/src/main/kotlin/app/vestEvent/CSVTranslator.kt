package app.vestEvent

import domain.vestEvent.VestEvent
import domain.vestEvent.VestEvent.VestEventType
import java.math.BigDecimal
import java.sql.Date

object CSVTranslator {
    fun translate(row: List<String>) =
        VestEvent(
            type = VestEventType.valueOf(row[0]),
            employeeId = row[1],
            employeeName = row[2],
            awardId = row[3],
            date = Date.valueOf(row[4]),
            quantity = BigDecimal.valueOf(row[5].toDouble())
        )
}