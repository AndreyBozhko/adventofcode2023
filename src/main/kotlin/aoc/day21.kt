package aoc

import aoc.Point2D.Companion.at
import kotlin.math.*

private val input = readLines("21.txt")

private typealias Grid21 = Array<CharArray>

private operator fun Grid21.get(p: Point2D) = this[p.x][p.y]

private fun Grid21.neighborsOf(position: Point2D, repeat: Boolean = false): List<Point2D> {
    val boundX = this.size
    val boundY = this[0].size
    val (x, y) = position
    return buildList {
        add(x - 1 at y + 0)
        add(x + 0 at y - 1)
        add(x + 0 at y + 1)
        add(x + 1 at y + 0)
    }.filter { (x, y) ->
        repeat || (x in 0..<boundX && y in 0..<boundY)
    }.filter { (x, y) ->
        this[(x % boundX + boundX) % boundX][(y % boundY + boundY) % boundY] != '#'
    }
}

fun main() {
    val grid = input.map { it.toCharArray() }.toTypedArray()

    val startRow = grid.indexOfFirst { 'S' in it }
    val start = Point2D(startRow, grid[startRow].indexOf('S'))

    // part A
    run {
        val current = mutableSetOf(start)
        val next = mutableSetOf<Point2D>()

        for (step in 1..64) {
            for (cur in current) {
                next += grid.neighborsOf(cur, repeat = false)
            }

            current.clear()
            current += next
            next.clear()
        }

        val result = current.size
        println(result)
    }

    // part B
    run {
        require(grid.size == grid[0].size)
        require(grid.size % 2 == 1)

        val n = grid.size
        val maxStep = 26_501_365

        val preCompute = 10 * n
        val progress = mutableMapOf(
            start to LongArray(preCompute + 1) { if (it == 0) 1 else 0 }
        )

        run {
            var current = mutableSetOf(start)
            var next = mutableSetOf<Point2D>()
            var prev = mutableSetOf<Point2D>()

            for (step in 1..preCompute) {
                for (cur in current) {
                    next += grid.neighborsOf(cur, repeat = true).filter { it !in prev }
                }

                next.forEach { p ->
                    val adjusted = Point2D((p.x % n + n) % n, (p.y % n + n) % n)
                    val l = progress[adjusted] ?: LongArray(preCompute + 1)
                    l[step] += 1L
                    progress[adjusted] = l
                }

                prev = current
                current = next
                next = mutableSetOf()
            }
        }

        // progress is periodic
        val increments = progress.mapValues { (_, pointProgress) ->
            val pos = pointProgress.size - 4 * n
            LongArray(2 * n) { pointProgress[it + pos + 2 * n] - pointProgress[it + pos] }
        }

        val offsets = progress.mapValues { (_, v) ->
            val pos = v.size - 2 * n
            LongArray(2 * n) { v[it + pos] }
        }

        // compute answer
        if (maxStep <= preCompute) {

            val result = ((maxStep and 1)..maxStep step 2).sumOf { st ->
                progress.values.sumOf { it[st] }
            }
            println(result)

        } else {

            val resultInitial = ((maxStep and 1)..preCompute step 2).sumOf { st ->
                progress.values.sumOf { it[st] }
            }

            val resultRemaining = offsets.keys.sumOf { p ->
                (0..<2 * n).sumOf { idx ->
                    val currentStep = preCompute + idx + 1
                    val offset = offsets[p]!![idx]
                    val increment = increments[p]!![idx]

                    if (currentStep and 1 != maxStep and 1) {
                        0L
                    } else {
                        val repeats = floor((maxStep - currentStep).toDouble() / (2 * n)).toLong() + 1
                        repeats * offset + repeats * (repeats + 1) / 2 * increment
                    }
                }
            }

            val result = resultInitial + resultRemaining
            println(result)
        }
    }
}
