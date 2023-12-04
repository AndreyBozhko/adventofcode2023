package aoc

import aoc.Point2D.Companion.at

private val input get() = readLines("03.txt")

private fun Point2D.neighbors(bound: Point2D): List<Point2D> =
    buildList {
        add(x-1 at y-1)
        add(x-1 at y+0)
        add(x-1 at y+1)
        add(x+0 at y-1)
        add(x+0 at y+1)
        add(x+1 at y-1)
        add(x+1 at y+0)
        add(x+1 at y+1)
    }.filter { (x, y) ->
        x >= 0 && x < bound.x && y >= 0 && y < bound.y
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

    val bounds = grid.size at grid[0].length

    // part A
    run {
        val adjacentDigits = symbols
            .flatMap { position -> position.neighbors(bounds) }
            .filter { (x, y) -> grid[x][y].isDigit() }
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
            .flatMap { position -> position.neighbors(bounds).map { position to it } }
            .filter { (_, c) -> grid[c.x][c.y].isDigit() }
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
