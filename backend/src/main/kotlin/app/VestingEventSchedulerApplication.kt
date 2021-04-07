package app

import app.vestEvent.CSVTranslator
import app.vestEvent.VestResponse
import app.vestEvent.VestResponseSerializer
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import domain.vestEvent.VestEvent
import java.io.File
import java.io.FileNotFoundException
import java.lang.IllegalArgumentException
import java.lang.IndexOutOfBoundsException
import java.sql.Date

class VestingEventSchedulerApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val rows = verifyInputs {
                csvReader()
                    .readAll(File(args[0]))
                    .map(CSVTranslator::translate)
                    .filter { it.date <= Date.valueOf(args[1]) }
            }

            VestResponseSerializer
                .serialize(rows)
                .sortedWith(compareBy({ it.employeeId }, { it.awardId }))
                .forEach { printSchedule(it, args.getOrNull(2)) }
        }

        private fun verifyInputs(block: () -> List<VestEvent>): List<VestEvent> {
            return try {
                block()
            } catch (ex: IndexOutOfBoundsException) {
                println("Please add required command line arguments")
                emptyList()
            } catch (ex: FileNotFoundException) {
                println("Unable to open file")
                emptyList()
            } catch (ex: IllegalArgumentException) {
                println("Invalid date provided")
                emptyList()
            }
        }

        private fun printSchedule(vestResponse: VestResponse, precision: String?) {
            val scale = precision?.toIntOrNull() ?: 0
            println("${vestResponse.employeeId},${vestResponse.employeeName},${vestResponse.awardId},${vestResponse.quantity.setScale(scale)}")
        }
    }
}