package aoc

import aoc.CubeTriple.Companion.parseTriples
import kotlin.math.max

private val input get() = readLines("02.txt")

private data class CubeTriple(val red: Int, val green: Int, val blue: Int) {

    operator fun contains(other: CubeTriple): Boolean =
        red >= other.red && green >= other.green && blue >= other.blue

    companion object {
        val hasRed = "(\\d+) red".toRegex()
        val hasGreen = "(\\d+) green".toRegex()
        val hasBlue = "(\\d+) blue".toRegex()

        fun String.asTriple() =
            CubeTriple(
                red = hasRed.find(this)?.let { it.groups[1]!!.value.toInt() } ?: 0,
                green = hasGreen.find(this)?.let { it.groups[1]!!.value.toInt() } ?: 0,
                blue = hasBlue.find(this)?.let { it.groups[1]!!.value.toInt() } ?: 0,
            )

        fun String.parseTriples(): List<CubeTriple> =
            split(';').map { it.asTriple() }

        fun combine(left: CubeTriple, right: CubeTriple) =
            CubeTriple(
                red = max(left.red, right.red),
                green = max(left.green, right.green),
                blue = max(left.blue, right.blue),
            )
    }
}

fun main() {
    // part A
    run {
        val bag = CubeTriple(red = 12, green = 13, blue = 14)
        val result = input
            .filter { line ->
                line.parseTriples().all { it in bag }
            }
            .sumOf { line ->
                line.substring(5, line.indexOf(':')).toInt()
            }
        println(result)
    }

    // part B
    run {
        val result = input
            .sumOf { line ->
                val (r, g, b) = line.parseTriples().reduce(CubeTriple::combine)
                r * g * b
            }
        println(result)
    }
}
