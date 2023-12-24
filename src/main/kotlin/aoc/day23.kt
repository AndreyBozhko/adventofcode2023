package aoc

import aoc.Point2D.Companion.at

private val input = readLines("23.txt")

private typealias Grid23 = Array<CharArray>
private typealias Graph23 = Map<Point2D, Map<Point2D, Int>>

private operator fun Grid23.get(p: Point2D) = this[p.x][p.y]

private fun Grid23.neighborsOf(position: Point2D): List<Point2D> {
    val boundX = this.size
    val boundY = this[0].size
    val (x, y) = position
    return buildList {
        add(x - 1 at y + 0)
        add(x + 0 at y - 1)
        add(x + 0 at y + 1)
        add(x + 1 at y + 0)
    }.filter { p ->
        p.x in 0..<boundX && p.y in 0..<boundY && this[p] != '#'
    }
}

fun Grid23.distToNearestVertices(
    cur: Point2D,
    vertices: Set<Point2D>,
    pathToNextPointValid: (Point2D, Point2D) -> Boolean
): Map<Point2D, Int> = buildMap {
    val queue = mutableSetOf(cur)
    val seen = mutableSetOf<Point2D>()
    val tmp = mutableSetOf<Point2D>()
    var step = 0
    while (queue.isNotEmpty()) {
        for (p in queue) {
            if (p in seen) continue

            seen += p

            if (p in vertices && p != cur) {
                this[p] = step
            } else {
                tmp += neighborsOf(p).filter {
                    it !in seen && pathToNextPointValid(p, it)
                }
            }
        }

        step += 1

        queue.clear()
        queue += tmp
        tmp.clear()
    }
}

private fun Graph23.dfs(cur: Point2D, end: Point2D, path: MutableMap<Point2D, Int>, results: MutableSet<Int>) {
    if (cur == end) {
        val weight = path.values.sum()
        results += weight
        return
    }

    for ((n, w) in this[cur]!!) {
        if (n in path) continue

        path[n] = w
        dfs(n, end, path, results)
        path -= n
    }
}

fun Graph23.longestPath(start: Point2D, end: Point2D): Int {
    val distances = buildSet {
        dfs(start, end, mutableMapOf(start to 0), this)
    }
    return distances.max()
}

fun main() {
    val grid = input.map { it.toCharArray() }.toTypedArray()

    val start = Point2D(0, grid.first().indexOf('.'))
    val end = Point2D(grid.size - 1, grid.last().indexOf('.'))

    val vertices = grid.flatMapIndexed { x, row ->
        row.mapIndexed { y, ch ->
            val p = Point2D(x, y)
            if (ch == '#' || grid.neighborsOf(p).size == 2) {
                null
            } else {
                p
            }
        }
    }
        .filterNotNull()
        .toSet()

    // part A
    run {
        val graph: Graph23 = vertices.associateWith { v ->
            grid.distToNearestVertices(v, vertices) { cur, nxt ->
                when (grid[nxt]) {
                    '<' -> (nxt.y < cur.y)
                    '>' -> (nxt.y > cur.y)
                    'v' -> (nxt.x > cur.x)
                    '^' -> (nxt.x < cur.x)
                    else -> true
                }
            }
        }

        val result = graph.longestPath(start, end)
        println(result)
    }

    // part B
    run {
        val graph: Graph23 = vertices.associateWith { v ->
            grid.distToNearestVertices(v, vertices) { _, _ -> true }
        }

        val result = graph.longestPath(start, end)
        println(result)
    }
}
