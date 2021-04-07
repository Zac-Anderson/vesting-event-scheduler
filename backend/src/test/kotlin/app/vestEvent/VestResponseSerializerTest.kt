package app.vestEvent

import domain.vestEvent.VestEvent
import domain.vestEvent.VestEvent.VestEventType.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.sql.Date

class VestResponseSerializerTest {
    @Test
    fun `serialize when handling VEST types returns all vest events as vest responses`() {
        val alice = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-01"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val bobby = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(100.00)
        )

        val result = VestResponseSerializer.serialize(listOf(alice,bobby))

        assertThat(result).containsExactlyInAnyOrder(
            VestResponse(
                employeeId = "E001",
                employeeName = "Alice Smith",
                awardId = "ISO-001",
                quantity = BigDecimal.valueOf(1000.00)
            ),
            VestResponse(
                employeeId = "E002",
                employeeName = "Bobby Jones",
                awardId = "NSO-001",
                quantity = BigDecimal.valueOf(100.00)
            )
        )
    }

    @Test
    fun `serialize when handling VEST types adds the quantity of matching vest awards`() {
        val alice1000 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-01"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val alice500 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(500.00)
        )

        val bobby100 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(100.00)
        )

        val bobby200 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-03"),
            quantity = BigDecimal.valueOf(200.00)
        )

        val result = VestResponseSerializer.serialize(listOf(alice1000,bobby100,alice500,bobby200))

        assertThat(result).containsExactlyInAnyOrder(
            VestResponse(
                employeeId = "E001",
                employeeName = "Alice Smith",
                awardId = "ISO-001",
                quantity = BigDecimal.valueOf(1500.00)
            ),
            VestResponse(
                employeeId = "E002",
                employeeName = "Bobby Jones",
                awardId = "NSO-001",
                quantity = BigDecimal.valueOf(300.00)
            )
        )
    }

    @Test
    fun `serialize when handling CANCEL types subtracts the quantity of matching vest awards`() {
        val alice1000 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-01"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val alice500 = VestEvent(
            type = CANCEL,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(500.00)
        )

        val bobby100 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(100.00)
        )

        val bobby200 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-03"),
            quantity = BigDecimal.valueOf(200.00)
        )

        val result = VestResponseSerializer.serialize(listOf(alice1000,bobby100,alice500,bobby200))

        assertThat(result).containsExactlyInAnyOrder(
            VestResponse(
                employeeId = "E001",
                employeeName = "Alice Smith",
                awardId = "ISO-001",
                quantity = BigDecimal.valueOf(500.00)
            ),
            VestResponse(
                employeeId = "E002",
                employeeName = "Bobby Jones",
                awardId = "NSO-001",
                quantity = BigDecimal.valueOf(300.00)
            )
        )
    }

    @Test
    fun `serialize does not subtract the quantity when dealing with an invalid CANCEL event`() {
        val alice1500Invalid = VestEvent(
            type = CANCEL,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(1500.00)
        )

        val alice1000 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-01"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val bobby100 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(100.00)
        )

        val bobby200 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-03"),
            quantity = BigDecimal.valueOf(200.00)
        )

        val result = VestResponseSerializer.serialize(listOf(alice1500Invalid,alice1000,bobby100,bobby200))

        assertThat(result).containsExactlyInAnyOrder(
            VestResponse(
                employeeId = "E001",
                employeeName = "Alice Smith",
                awardId = "ISO-001",
                quantity = BigDecimal.valueOf(1000.00)
            ),
            VestResponse(
                employeeId = "E002",
                employeeName = "Bobby Jones",
                awardId = "NSO-001",
                quantity = BigDecimal.valueOf(300.00)
            )
        )
    }

    @Test
    fun `serialize handles out of date order events`() {
        val alice1500 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-03"),
            quantity = BigDecimal.valueOf(1500.00)
        )

        val alice1500Invalid = VestEvent(
            type = CANCEL,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(1500.00)
        )

        val alice1000 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-01"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val bobby100 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(100.00)
        )

        val bobby200 = VestEvent(
            type = VEST,
            employeeId = "E002",
            employeeName = "Bobby Jones",
            awardId = "NSO-001",
            date = Date.valueOf("2020-01-03"),
            quantity = BigDecimal.valueOf(200.00)
        )

        val result = VestResponseSerializer.serialize(listOf(alice1500,alice1500Invalid,alice1000,bobby100,bobby200))

        assertThat(result).containsExactlyInAnyOrder(
            VestResponse(
                employeeId = "E001",
                employeeName = "Alice Smith",
                awardId = "ISO-001",
                quantity = BigDecimal.valueOf(2500.00)
            ),
            VestResponse(
                employeeId = "E002",
                employeeName = "Bobby Jones",
                awardId = "NSO-001",
                quantity = BigDecimal.valueOf(300.00)
            )
        )
    }

    @Test
    fun `serialize returns quantity 0 when applicable`() {
        val aliceCancel1000 = VestEvent(
            type = CANCEL,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-02"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val aliceVest1000 = VestEvent(
            type = VEST,
            employeeId = "E001",
            employeeName = "Alice Smith",
            awardId = "ISO-001",
            date = Date.valueOf("2020-01-01"),
            quantity = BigDecimal.valueOf(1000.00)
        )

        val result = VestResponseSerializer.serialize(listOf(aliceCancel1000,aliceVest1000))

        assertThat(result).containsExactly(
            VestResponse(
                employeeId = "E001",
                employeeName = "Alice Smith",
                awardId = "ISO-001",
                quantity = BigDecimal.valueOf(0.00)
            )
        )
    }
}