package app

import app.vestEvent.CSVTranslator
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
            val rows: List<VestEvent>

            try {
                rows = csvReader()
                    .readAll(File(args[0]))
                    .map(CSVTranslator::translate)
                    .filter { it.date <= Date.valueOf(args[1]) }
            } catch (ex: IndexOutOfBoundsException) {
                println("Please add required command line arguments")
                return
            } catch (ex: FileNotFoundException) {
                println("Unable to open file ${args[0]}")
                return
            } catch (ex: IllegalArgumentException) {
                println("Invalid date provided: ${args[1]}")
                return
            }

            val vestResponses = VestResponseSerializer
                .serialize(rows)
                .sortedWith(compareBy({ it.employeeId }, { it.awardId }))

            vestResponses.forEach {
                var scale = 0
                if (args.size == 3) scale = args[2].toInt()
                println("${it.employeeId},${it.employeeName},${it.awardId},${it.quantity.setScale(scale)}")
            }
        }
    }
}