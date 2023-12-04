package aoc

import java.io.*

fun readLines(resource: String): List<String> =
    Thread.currentThread()
        .contextClassLoader
        .getResourceAsStream(resource)
        ?.bufferedReader()
        ?.readLines()
        ?: throw IOException("Resource $resource not found!")
