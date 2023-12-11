package aoc

import aoc.Point2D.Companion.at

private val input = readLines("10.txt")

private typealias Grid10 = Array<CharArray>

private operator fun Grid10.get(p: Point2D): Char = this[p.x][p.y]

private fun Grid10.neighborsOf(p: Point2D): Set<Point2D> {
    val boundX = this.size
    val boundY = this[0].size
    val c = this[p]
    val (x, y) = p
    return buildMap {
        if (c == '-' || c == 'S') {
            put(x + 0 at y - 1, "-FL")
            put(x + 0 at y + 1, "-7J")
        }
        if (c == '|' || c == 'S') {
            put(x - 1 at y + 0, "|7F")
            put(x + 1 at y + 0, "|JL")
        }
        if (c == 'F') {
            put(x + 0 at y + 1, "-7J")
            put(x + 1 at y + 0, "|JL")
        }
        if (c == 'L') {
            put(x - 1 at y + 0, "|7F")
            put(x + 0 at y + 1, "-7J")
        }
        if (c == '7') {
            put(x + 0 at y - 1, "-FL")
            put(x + 1 at y + 0, "|JL")
        }
        if (c == 'J') {
            put(x - 1 at y + 0, "|7F")
            put(x + 0 at y - 1, "-FL")
        }
    }
        .filter { (pp, allowed) ->
            val (xx, yy) = pp
            xx in 0..<boundX && yy in 0..<boundY && this@neighborsOf[pp] in allowed
        }
        .keys
}

fun main() {
    val grid: Grid10 = input.map { it.toCharArray() }.toTypedArray()

    val start = input.withIndex()
        .first { 'S' in it.value }
        .let { (idx, line) ->
            idx at line.indexOf('S')
        }

    val seen: MutableSet<Point2D> = mutableSetOf(start)
    val next: MutableList<Point2D> = mutableListOf(start)
    val cand: MutableList<Point2D> = mutableListOf()

    while (next.isNotEmpty()) {
        for (cell in next) {
            for (n in grid.neighborsOf(cell)) {
                if (n !in seen) {
                    seen += n
                    cand += n
                }
            }
        }

        next.clear()
        next += cand
        cand.clear()
    }

    // part A
    println(seen.size / 2)

    // part B
    var total = 0
    for (i in grid.indices) {
        for (j in grid[0].indices) {
            if ((i at j) in seen) {
                continue
            }
            val intersections = (j..<grid[0].size)
                .joinToString("") {
                    val p = i at it
                    if (p in seen) grid[p].toString() else ""
                }
                    .replace("-", "")
                    .replace("FJ", "|")
                    .replace("L7", "|")
                    .count { it == '|' }

            total += intersections and 1
        }
    }
    println(total)
}
