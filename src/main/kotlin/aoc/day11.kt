package aoc

import kotlin.math.*

private val input = readLines("11.txt")

fun distanceBetweenGalaxies(
    g1: Point2D,
    g2: Point2D,
    expandedRowIdx: IntArray? = null,
    expandedColIdx: IntArray? = null,
    rate: Long = 1
): Long {
    val dx = min(g1.x, g2.x) ..< max(g1.x, g2.x)
    val dy = min(g1.y, g2.y) ..< max(g1.y, g2.y)
    return ((rate - 1) * (expandedRowIdx?.count { it in dx } ?: 0)
            + (rate - 1) * (expandedColIdx?.count { it in dy } ?: 0)
            + dx.last - dx.first + 1
            + dy.last - dy.first + 1)
}

fun main() {
    val emptyRows = input.withIndex()
        .filter { (_, row) -> row.all { it == '.' } }
        .map { it.index }
        .toIntArray()

    val emptyCols = input[0].indices
        .filter { idx -> input.all { it[idx] == '.' } }
        .toIntArray()

    val galaxies = input.flatMapIndexed { x, row ->
        row.mapIndexedNotNull { y, c ->
            if (c == '#') Point2D(x, y) else null
        }
    }

    // part A
    run {
        val result = galaxies.sumOf { g1 ->
            galaxies.sumOf { g2 ->
                distanceBetweenGalaxies(g1, g2, emptyRows, emptyCols, rate = 2)
            }
        }
        println(result / 2)
    }

    // part B
    run {
        val result = galaxies.sumOf { g1 ->
            galaxies.sumOf { g2 ->
                distanceBetweenGalaxies(g1, g2, emptyRows, emptyCols, rate = 1000000)
            }
        }
        println(result / 2)
    }
}
