package aoc

import aoc.Point2D.Companion.at

private val input get() = readLines("03.txt")

private typealias Grid03 = List<String>

private operator fun Grid03.get(p: Point2D): Char = this[p.x][p.y]

private fun Grid03.neighborsOf(position: Point2D): List<Point2D> {
    val boundX = this.size
    val boundY = this[0].length
    val (x, y) = position
    return buildList {
        add(x - 1 at y - 1)
        add(x - 1 at y + 0)
        add(x - 1 at y + 1)
        add(x + 0 at y - 1)
        add(x + 0 at y + 1)
        add(x + 1 at y - 1)
        add(x + 1 at y + 0)
        add(x + 1 at y + 1)
    }.filter { (x, y) ->
        x in 0..<boundX && y in 0..<boundY
    }
}

fun main() {
    val grid = input

    val numberRegex = "\\d+".toRegex()
    val numbers = grid.map {
        numberRegex.findAll(it).toList()
    }

    val symbols = mutableListOf<Point2D>()
    val gears = mutableListOf<Point2D>()
    grid.forEachIndexed { x, row ->
        row.forEachIndexed { y, c ->
            if (!c.isDigit() && c != '.') {
                symbols += x at y
            }
            if (c == '*') {
                gears += x at y
            }
        }
    }

    // part A
    run {
        val adjacentDigits = symbols
            .flatMap { position -> grid.neighborsOf(position) }
            .filter { p -> grid[p].isDigit() }
            .toSet()

        val adjacentNumbers = adjacentDigits
            .fold(mutableSetOf<MatchResult>()) { coll, pos ->
                numbers[pos.x].filterTo(coll) { pos.y in it.range }
            }

        val result = adjacentNumbers.sumOf {
            it.value.toInt()
        }
        println(result)
    }

    // part B
    run {
        val adjacentDigits = gears
            .flatMap { position -> grid.neighborsOf(position).map { position to it } }
            .filter { (_, c) -> grid[c].isDigit() }
            .groupBy({ it.first }) { it.second }

        val adjacentNumbers = adjacentDigits.values.map { digits ->
            digits.fold(mutableSetOf<MatchResult>()) { coll, pos ->
                numbers[pos.x].filterTo(coll) { pos.y in it.range }
            }
        }

        val result: Int = adjacentNumbers.sumOf { matches ->
            when (matches.size) {
                2 -> matches.productOf { it.value.toInt() }
                else -> 0
            }
        }

        println(result)
    }
}
