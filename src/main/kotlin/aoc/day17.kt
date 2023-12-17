package aoc

import java.util.*
import kotlin.math.*

private val input = readLines("17.txt")

private typealias HeatMap = Array<IntArray>

operator fun HeatMap.get(v: Vertex) = this[v.x][v.y]

data class Vertex(val x: Int, val y: Int, val d: Direction, val steps: Int) {
    fun advance() = copy(x = x + d.dx, y = y + d.dy, steps = steps + 1)
    fun turnClockwise() = copy(d = d.turnClockwise(), steps = 0)
    fun turnCounterClockwise() = copy(d = d.turnCounterClockwise(), steps = 0)
}

fun HeatMap.calculateHeatLoss(maxSteps: Int, turnAllowed: Vertex.() -> Boolean): Map<Vertex, Int> {
    val queue: Queue<Pair<Vertex, Int>> = PriorityQueue { (_, cost1), (_, cost2) ->
        cost1.compareTo(cost2)
    }

    queue.offer(Vertex(0, 0, Direction.R, 0) to 0)
    queue.offer(Vertex(0, 0, Direction.D, 0) to 0)

    val costMap = mutableMapOf<Vertex, Int>()

    while (queue.isNotEmpty()) {
        val (vertex, cost) = queue.poll()

        if (vertex in costMap) continue
        costMap[vertex] = cost

        val candidates = buildList {
            add(vertex)
            if (turnAllowed(vertex)) {
                add(vertex.turnClockwise())
                add(vertex.turnCounterClockwise())
            }
        }

        candidates.forEach { v ->
            val v3 = v.advance()
            if (v3.steps <= maxSteps) {
                if (v3.x in this.indices && v3.y in this[0].indices) {
                    val newCost = cost + this[v3]
                    queue.offer(v3 to newCost)
                }
            }
        }
    }

    return costMap
}

fun main() {
    val heatMap = input.map { it.map(Char::digitToInt).toIntArray() }.toTypedArray()

    // part A
    run {
        val result = heatMap.calculateHeatLoss(maxSteps = 3, turnAllowed = { true })
        val (_, best) = result.minBy { (v, cost) ->
            if (v.x == heatMap.indices.last && v.y == heatMap[0].indices.last)
                cost else Int.MAX_VALUE
        }
        println(best)
    }

    // part B
    run {
        val result = heatMap.calculateHeatLoss(maxSteps = 10, turnAllowed = { steps > 3 })

        val rightEdgeCost = result.firstNotNullOf { (v, cost) ->
            if (v.x == heatMap.indices.last - 3 && v.y == heatMap[0].indices.last && v.d == Direction.D)
                cost else null
        }
        val bottomEdgeCost = result.firstNotNullOf { (v, cost) ->
            if (v.x == heatMap.indices.last && v.y == heatMap[0].indices.last - 3 && v.d == Direction.R)
                cost else null
        }

        val best = min(
            bottomEdgeCost + (1..3).sumOf { idx -> heatMap.last().run { this[this.size - idx] } },
            rightEdgeCost + (1..3).sumOf { idx -> heatMap[heatMap.size - idx].last() },
        )
        println(best)
    }
}
