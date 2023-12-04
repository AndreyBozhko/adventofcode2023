import org.gradle.kotlin.dsl.*
import util.*
import java.io.ByteArrayOutputStream

val sourceSets = the<SourceSetContainer>()
val main by sourceSets.existing

tasks.addRule("Pattern: input<Day>") {
    if (matches("input\\d\\d".toRegex())) {
        val dayStr = substring(5, 7)
        val d = dayStr.toInt()

        task<AocInputTask>(this) {
            description = "Get input for Day $d"

            day = d
            outputFile = main.map {
                it.resources
                    .sourceDirectories
                    .singleFile
                    .resolve("$dayStr.txt")
            }
        }
    }

    tasks.withType<ProcessResources>().configureEach {
        dependsOn(tasks.withType<AocInputTask>())
    }
}

tasks.addRule("Pattern: solve<Day>") {
    if (matches("solve\\d\\d".toRegex())) {
        val dayStr = substring(5, 7)

        task<JavaExec>(this) {
            description = "Solve Day ${dayStr.toInt()}"
            dependsOn(tasks.named("input$dayStr"))

            mainClass = "aoc.Day${dayStr}Kt"
            classpath = main.get().runtimeClasspath

            standardOutput = file(".output").outputStream()
            outputs.file(".output")

            // handle errors
            val errors = ByteArrayOutputStream()
            isIgnoreExitValue = true
            errorOutput = errors

            doLast {
                val errorMsg = errors.toByteArray().decodeToString()
                if (errorMsg.isNotBlank()) {
                    throw GradleException(errorMsg)
                }
            }
        }
    }
}

tasks.addRule("Pattern: submit<Day>") {
    if (matches("submit\\d\\d[ABab]".toRegex())) {
        val dayStr = substring(6, 8)
        val p = substring(8, 9).uppercase()
        val d = dayStr.toInt()

        task<AocSubmitTask>(this) {
            description = "Submit part $part for Day $d"
            dependsOn(tasks.named("solve$dayStr"))

            day = d
            part = Part.valueOf(p)
            inputFile = file(".output")
            successFile = file("answers/$dayStr$p.txt")
        }
    }
}
