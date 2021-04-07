import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}

apply(plugin = "org.jetbrains.kotlin.jvm")

repositories {
    jcenter()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.15.1")
    implementation("org.slf4j:slf4j-nop:1.7.30")

    // Tests
    testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
    testImplementation("org.assertj:assertj-core:3.17.2")
}

application {
    mainClass.set("app.VestingEventSchedulerApplication")
    val run: JavaExec by tasks
    run.standardInput = System.`in`
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "11"
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "app.VestingEventSchedulerApplication"
    }

    from(sourceSets.main.get().output)
    dependsOn(configurations.runtimeClasspath)
    from({
        configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
    })
}

tasks.withType<Test> {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    reports {
        html.isEnabled = true
    }

    outputs.upToDateWhen { false }
}