package aoc

import kotlin.math.*

private val input = readLines("18.txt")

private fun IntRange.max(): Int = max(first, last)
private fun IntRange.min(): Int = min(first, last)

fun calculateVolume(instructions: Iterable<Pair<Direction, Int>>): Long {
    val border = instructions.runningFold(Point2D(0, 0)) { prev, (dir, st) ->
        prev.copy(x = prev.x + dir.dx * st, y = prev.y + dir.dy * st)
    }
    val borderSegments = border.zipWithNext()

    val segmentsX = border.asSequence()
        .map { it.x }
        .distinct()
        .sorted()
        .zipWithNext()
        .map { (l, r) -> l..r }
        .distinct()
        .toList()
    val segmentsY = border.asSequence()
        .map { it.y }
        .distinct()
        .sorted()
        .zipWithNext()
        .map { (l, r) -> l..r }
        .distinct()
        .toList()

    fun hasLeftBorder(xs: IntRange, y: Int): Boolean {
        return borderSegments.any { (p1, p2) ->
            val px = min(p1.x, p2.x)..max(p1.x, p2.x)
            p1.y == y && p2.y == y && xs.first in px && xs.last in px
        }
    }

    val inside = mutableSetOf<Pair<IntRange, IntRange>>()
    for (xs in segmentsX) {
        var currentBoxInside = false
        for (ys in segmentsY) {
            if (hasLeftBorder(xs = xs, y = ys.first)) {
                currentBoxInside = !currentBoxInside
            }
            if (currentBoxInside) {
                inside += (xs to ys)
            }
        }
    }

    val interiorBoxes = inside.flatMap { (xs, ys) ->
        val minX = xs.min()
        val maxX = xs.max()
        val minY = ys.min()
        val maxY = ys.max()
        listOf(
            minX..minX to minY..minY,
            minX..minX to minY+1..<maxY,
            minX..minX to maxY..maxY,

            minX+1..<maxX to minY..minY,
            minX+1..<maxX to minY+1..<maxY,
            minX+1..<maxX to maxY..maxY,

            maxX..maxX to minY..minY,
            maxX..maxX to minY+1..<maxY,
            maxX..maxX to maxY..maxY,
        )
    }.toSet()

    return interiorBoxes.sumOf { (xs, ys) ->
        (xs.last - xs.first + 1).toLong() * (ys.last - ys.first + 1).toLong()
    }
}

fun main() {
    // part A
    run {
        val instructions = input
            .map { line ->
                val parts = line.split(' ')
                val direction = Direction.valueOf(parts[0])
                val steps = parts[1].toInt()
                Pair(direction, steps)
            }

        val result = calculateVolume(instructions)
        println(result)
    }

    // part B
    run {
        val instructions = input
            .map { line ->
                val part = line.substring(line.length - 7, line.length - 1)
                val direction = when (part[5]) {
                    '0' -> Direction.R
                    '1' -> Direction.D
                    '2' -> Direction.L
                    else -> Direction.U
                }
                val steps = part.substring(0, 5).toInt(16)
                Pair(direction, steps)
            }

        val result = calculateVolume(instructions)
        println(result)
    }
}
