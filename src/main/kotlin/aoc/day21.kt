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
        var current = mutableSetOf(start)
        var next = mutableSetOf<Point2D>()

        for (step in 1..64) {
            for (cur in current) {
                next += grid.neighborsOf(cur, repeat = false)
            }

            current = next
            next = mutableSetOf()
        }

        val result = current.size
        println(result)
    }

    // part B
    run {
        require(grid.size == grid[0].size)
        require(grid.size % 2 == 1)
        require(start == Point2D(grid.size / 2, grid.size / 2))

        val n = grid.size
        val maxStep = 26_501_365

        val preCompute = min(maxStep, 10 * n)
        val progress = LongArray(preCompute + 1).apply { this[0] = 1 }

        run {
            var current = mutableSetOf(start)
            var next = mutableSetOf<Point2D>()
            var prev = mutableSetOf<Point2D>()

            for (step in 1..preCompute) {
                for (cur in current) {
                    next += grid.neighborsOf(cur, repeat = true).filter { it !in prev }
                }

                progress[step] = next.size.toLong()

                prev = current
                current = next
                next = mutableSetOf()
            }
        }

        // progress is periodic
        val increments = LongArray(2 * n) {
            val pos = progress.size - 4 * n
            progress[it + pos + 2 * n] - progress[it + pos]
        }

        val offsets = LongArray(2 * n) {
            val pos = progress.size - 2 * n
            progress[it + pos]
        }

        // compute answer
        if (maxStep <= preCompute) {

            val result = ((maxStep and 1)..maxStep step 2)
                .sumOf { progress[it] }
            println(result)

        } else {

            val resultInitial = ((maxStep and 1)..preCompute step 2)
                .sumOf { progress[it] }

            val resultRemaining =
                (0..<2 * n).sumOf { idx ->
                    val currentStep = preCompute + idx + 1
                    val offset = offsets[idx]
                    val increment = increments[idx]

                    if (currentStep and 1 != maxStep and 1) {
                        0L
                    } else {
                        val repeats = floor((maxStep - currentStep).toDouble() / (2 * n)).toLong() + 1
                        repeats * offset + repeats * (repeats + 1) / 2 * increment
                    }
                }


            val result = resultInitial + resultRemaining
            println(result)
        }
    }
}
