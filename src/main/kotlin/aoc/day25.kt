package aoc

import kotlin.math.*

private val input = readLines("25.txt")

private typealias ResidualGraph = Array<IntArray>

fun bfs(residualGraph: ResidualGraph, start: Int, end: Int, backtrack: MutableMap<Int, Int>): Boolean {
    val visited = mutableSetOf<Int>()
    val queue = ArrayDeque<Int>()

    backtrack.clear()
    queue += start
    visited += start

    while (queue.isNotEmpty()) {
        val cur = queue.removeFirst()
        for (i in residualGraph.indices) {
            if (residualGraph[cur][i] > 0 && i !in visited) {
                queue += i
                visited += i
                backtrack[i] = cur
            }
        }
    }

    return end in visited
}

fun findVisited(residualGraph: ResidualGraph, start: Int): BooleanArray {
    fun dfs(residualGraph: ResidualGraph, start: Int, visited: BooleanArray) {
        visited[start] = true
        for (i in residualGraph.indices) {
            if (residualGraph[start][i] > 0 && !visited[i]) {
                dfs(residualGraph, i, visited)
            }
        }
    }

    return BooleanArray(residualGraph.size).apply {
        dfs(residualGraph, start, this)
    }
}

fun minCut(
    graph: Map<String, List<String>>,
    start: String,
    end: String,
): List<Pair<String, String>> {
    val vertices = graph.keys.toList()
    val residualGraph = Array(vertices.size) { i ->
        IntArray(vertices.size) { j ->
            val vi = vertices[i]
            val vj = vertices[j]
            if (graph[vi]?.let { vj in it } == true) 1 else 0
        }
    }

    val startIdx = vertices.indexOf(start)
    val endIdx = vertices.indexOf(end)

    val backtrack = mutableMapOf<Int, Int>()
    while (bfs(residualGraph, startIdx, endIdx, backtrack)) {
        var pathFlow = Int.MAX_VALUE
        var v = endIdx
        while (v != startIdx) {
            val u = backtrack[v]!!
            pathFlow = min(pathFlow, residualGraph[u][v])
            v = u
        }

        v = endIdx
        while (v != startIdx) {
            val u = backtrack[v]!!
            residualGraph[u][v] = residualGraph[u][v] - pathFlow
            residualGraph[v][u] = residualGraph[v][u] + pathFlow
            v = u
        }
    }

    val visited = findVisited(residualGraph, startIdx)

    return buildList {
        vertices.forEachIndexed { i, vi ->
            vertices.forEachIndexed { j, vj ->
                if (graph[vi]?.let { vj in it } == true && visited[i] && !visited[j]) {
                    add(vi to vj)
                }
            }
        }
    }
}

fun summarizeComponents(
    graph: Map<String, List<String>>,
    excludedEdges: Set<Pair<String, String>>
): List<List<String>> = buildList {
    val seen = mutableSetOf<String>()

    for (vertex in graph.keys) {
        if (vertex in seen) continue

        val currentGroup = buildList {
            val queue = ArrayDeque<String>()
            queue += vertex

            while (queue.isNotEmpty()) {
                val v = queue.removeFirst()
                if (v in seen) continue

                seen += v
                this += v

                val nxt = graph[v]?.filter {
                    it !in seen
                            && (v to it) !in excludedEdges
                            && (it to v) !in excludedEdges
                }
                nxt?.let { queue += it }
            }
        }

        this += currentGroup
    }
}

fun main() {
    val graph = input.associate { line ->
        val parts = line.split(": ")
        parts[0] to parts[1].split(' ')
    }

    val pairs = graph.entries.flatMap { (k, v) -> v.map { k to it } }
    val vertices = pairs.flatMap { listOf(it.first, it.second) }.toSet().toList()

    val fullGraph = pairs
        .flatMap { p -> listOf(p, p.second to p.first) }
        .groupBy { it.first }
        .mapValues { (_, v) -> v.map { it.second } }

    val cuts = sequence {
        for (i in vertices.indices) {
            for (j in (i+1) .. vertices.indices.last) {
                val res = minCut(fullGraph, start = vertices[i], end = vertices[j])
                yield(res)
            }
        }
    }

    val cut = cuts.first { it.size == 3 }.toSet()

    val result = summarizeComponents(fullGraph, excludedEdges = cut)
    println(result.productOf { it.size })
}
