package aoc

import kotlin.math.*

private val input get() = readLines("06.txt")

fun waysToBeatRecord(time: Double, dist: Double): Int {
    val betterDist = dist + 1
    val low = ceil((time - (time * time - 4 * betterDist).pow(0.5)) / 2).toInt()
    val high = floor((time + (time * time - 4 * betterDist).pow(0.5)) / 2).toInt()

    return high - low + 1
}

fun main() {
    // part A
    run {
        val times = input[0].split(whitespaceRegex).drop(1).map { it.toDouble() }
        val distances = input[1].split(whitespaceRegex).drop(1).map { it.toDouble() }

        val pairs = times.zip(distances)

        val result = pairs.productOf { (t, d) ->
            waysToBeatRecord(time = t, dist = d)
        }
        println(result)
    }

    // part B
    run {
        val t = input[0].substring(9).replace(" ", "").toDouble()
        val d = input[1].substring(9).replace(" ", "").toDouble()

        val result = waysToBeatRecord(time = t, dist = d)
        println(result)
    }
}
