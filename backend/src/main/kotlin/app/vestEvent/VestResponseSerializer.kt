package app.vestEvent

import domain.vestEvent.VestEvent
import domain.vestEvent.VestEvent.VestEventType.CANCEL
import domain.vestEvent.VestEvent.VestEventType.VEST

object VestResponseSerializer {
    fun serialize(vestEvents: List<VestEvent>): List<VestResponse> {
        val eventsById = mutableMapOf<String, MutableList<VestResponse>>()

        vestEvents.sortedBy { it.date }.forEach { event ->
            when (event.type) {
                VEST -> {
                    val potentialVestResponse = VestResponse(
                        employeeId = event.employeeId,
                        employeeName = event.employeeName,
                        awardId = event.awardId,
                        quantity = event.quantity
                    )

                    eventsById[event.employeeId]?.let { vestResponseList ->
                        val foundVestResponse = vestResponseList.firstOrNull { vestResponse -> vestResponse.awardId == event.awardId }
                        if (foundVestResponse == null) vestResponseList.add(potentialVestResponse) else foundVestResponse.quantity += event.quantity
                    } ?: run { eventsById[event.employeeId] = mutableListOf(potentialVestResponse) }
                }
                CANCEL -> {
                    eventsById[event.employeeId]?.let { vestResponseList ->
                        val foundVestResponse = vestResponseList.firstOrNull { vestResponse -> vestResponse.awardId == event.awardId }
                        if (foundVestResponse != null && foundVestResponse.quantity >= event.quantity) foundVestResponse.quantity -= event.quantity
                    }
                }
            }
        }

        return eventsById.values.flatten()
    }
}