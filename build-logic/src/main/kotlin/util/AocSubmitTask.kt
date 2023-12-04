package util

import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.*
import org.gradle.kotlin.dsl.property
import java.net.URLEncoder
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse.BodyHandlers
import java.nio.charset.StandardCharsets.UTF_8

enum class Part(val level: Int) {
    A(1), B(2)
}

@CacheableTask
open class AocSubmitTask : AocTask() {

    @get:Input
    val part: Property<Part> = project.objects.property()

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NAME_ONLY)
    @get:SkipWhenEmpty
    val inputFile: RegularFileProperty = project.objects.fileProperty()

    @get:OutputFile
    val successFile: RegularFileProperty = project.objects.fileProperty()

    @TaskAction
    fun execute() {
        val token = sessionToken()
        val url = "answer".toAocUrl()

        val lines = inputFile.get().asFile.readLines()
        val answer = lines[part.get().level - 1].trim()

        val data = mapOf(
            "level" to part.get().level.toString(),
            "answer" to answer,
        )

        successFile.asFile.get().let {
            if (it.exists()) {
                logger.lifecycle("File $it already exists, nothing to do here...")
                didWork = true
                return
            }
        }

        val request = HttpRequest.newBuilder()
            .uri(url.toURI())
            .header("Cookie", "session=$token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .POST(BodyPublishers.ofString(data.toFormData()))
            .build()

        val response = client.get().send(request, BodyHandlers.ofString())
        if (response.statusCode() == 200 && response.body().contains("That's the right answer!")) {
            logger.lifecycle("{}", response)
            successFile.asFile.get().writeText(answer)
        } else {
            logger.lifecycle("{}\n\n{}", response, response.body())
            throw GradleException("$response")
        }
    }

    companion object {
        private fun String.urlEncode() = URLEncoder.encode(this, UTF_8)

        private fun Map<String, String>.toFormData(): String =
            map { (k, v) ->
                "${k.urlEncode()}=${v.urlEncode()}"
            }.joinToString("&")
    }
}
