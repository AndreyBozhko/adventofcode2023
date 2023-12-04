package util

import org.gradle.api.GradleException
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

@CacheableTask
open class AocInputTask : AocTask() {

    @get:OutputFile
    val outputFile: RegularFileProperty = project.objects.fileProperty()

    @TaskAction
    fun execute() {
        val token = sessionToken()
        val url = "input".toAocUrl()

        val request = HttpRequest.newBuilder()
            .uri(url.toURI())
            .header("Cookie", "session=$token")
            .GET()
            .build()

        val response = client.get().send(request, BodyHandlers.ofString())
        if (response.statusCode() == 200) {
            val output = outputFile.get().asFile
            output.writeText(response.body())
        } else {
            logger.lifecycle("{}\n\n{}", response, response.body())
            throw GradleException("$response")
        }
    }
}
