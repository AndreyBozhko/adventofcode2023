package util

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.kotlin.dsl.property
import java.net.URL
import java.net.http.HttpClient

open class AocTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    val token: RegularFileProperty = project.objects.fileProperty()
        .convention { project.file(".token") }

    @get:Input
    val url: Property<String> = project.objects.property<String>()
        .value("https://adventofcode.com")

    @get:Input
    val year: Property<Int> = project.objects.property<Int>()
        .value(2023)

    @get:Input
    val day: Property<Int> = project.objects.property()

    @get:Internal
    val client: Property<HttpClient> = project.objects.property<HttpClient>()
        .value(defaultClient)

    protected fun sessionToken(): String =
        token.get().asFile.readText().trim()
            .ifBlank { null } ?: throw GradleException("token not configured")

    protected fun String.toAocUrl() =
        URL("${url.get()}/${year.get()}/day/${day.get()}/$this")

    companion object {
        private val defaultClient = HttpClient.newHttpClient()
    }
}
