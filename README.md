# Vesting Event Scheduler #

## Prerequisites ##
- Java 11
- Gradle

## Getting Started ##
To verify the code is in a working state run the tests with `./gradlew clean check`

Building the project will also run tests before creating the jar which can be done with `./gradlew clean build`

## Running the App ##
After building the jar the app can be run with `java -jar build/libs/vesting-event-scheduler.jar [CSV File Location] [Date] [Decimal Precision]`

The decimal precision argument is an optional argument that defaults to 0 when omitted.

The CSV format has no header and follows the following format: [TYPE],[EMPLOYEE ID],[EMPLOYEE NAME],[AWARD ID],[DATE],[QUANTITY]
  
The instructions provided slight variations that might have been in error so I standardized the csv rows as above. A CSV file is included in the zip as an example.

There was also mention that the output should include all employees and awards, even if no shares are vested by the given date. I was unsure what that meant and the provided output didn't help understanding the requirement so I went with a best guess that an output can have an employee with a 0 quantity vested in the event of all vests being canceled by the date provided.
