package aoc

import kotlin.math.*

private val input = readLines("13.txt")

fun reflectionCol(pattern: Array<CharArray>, exclude: Int = -1): Int {
    val idx = (1..< pattern[0].size).indexOfFirst { idx ->
        val rangeRight = idx ..< min(2*idx, pattern[0].size)
        idx != exclude && rangeRight.all { y ->
            pattern.indices.all { x ->
                pattern[x][y] == pattern[x][2*idx - y - 1]
            }
        }
    }
    return idx + 1
}

fun reflectionRow(pattern: Array<CharArray>, exclude: Int = -1): Int {
    val idx = (1 ..< pattern.size).indexOfFirst { idx ->
        val rangeDown = idx ..< min(2*idx, pattern.size)
        idx != exclude && rangeDown.all { pattern[it].contentEquals(pattern[2*idx - it - 1]) }
    }
    return idx + 1
}

fun Char.invert() = ('.'.code + '#'.code - this.code).toChar()

fun main() {
    val patterns = input.fold(mutableListOf(mutableListOf<CharArray>())) { acc, s ->
        acc.apply {
            if (s.isEmpty()) {
                add(mutableListOf())
            } else {
                last() += s.toCharArray()
            }
        }
    }
        .map { it.toTypedArray() }

    // part A
    run {
        val result = patterns.sumOf {
            reflectionCol(it) + 100 * reflectionRow(it)
        }
        println(result)
    }

    // part B
    run {
        val result = patterns.sumOf { pattern ->
            val excludeCol = reflectionCol(pattern)
            val excludeRow = reflectionRow(pattern)

            val seq = sequence {
                for (row in pattern.indices) {
                    for (col in pattern[0].indices) {
                        pattern[row][col] = pattern[row][col].invert()
                        yield(pattern)
                        pattern[row][col] = pattern[row][col].invert()
                    }
                }
            }

            seq.map {
                reflectionCol(it, exclude = excludeCol) + 100 * reflectionRow(it, exclude = excludeRow)
            }.first { it != 0 }
        }
        println(result)
    }
}
