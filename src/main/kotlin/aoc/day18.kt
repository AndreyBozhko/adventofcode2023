package aoc

import kotlin.math.*

private val input = readLines("18.txt")

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

        val border = instructions.fold(mutableListOf(Point2D(0, 0))) { coll, (dir, st) ->
            coll.apply {
                val prev = last()
                val points = (1..st).map {
                    prev.copy(x = prev.x + dir.dx * it, y = prev.y + dir.dy * it)
                }
                this += points
            }
        }

        val minX = border.minOf { it.x }
        val maxX = border.maxOf { it.x }
        val minY = border.minOf { it.y }
        val maxY = border.maxOf { it.y }

        val borderSet = border.toSet()

        val outside = mutableSetOf<Point2D>()
        val queue = ArrayDeque<Point2D>().apply {
            this += (minX..maxX).map { Point2D(it, minY) }
            this += (minX..maxX).map { Point2D(it, maxY) }
            this += (minY..maxY).map { Point2D(minX, it) }
            this += (minY..maxY).map { Point2D(maxX, it) }
        }


        while (queue.isNotEmpty()) {
            val pt = queue.removeFirst()
            if (pt in outside || pt in borderSet) continue
            outside += pt
            for (d in Direction.entries) {
                val n = pt.copy(x = pt.x + d.dx, y = pt.y + d.dy)
                if (n.x in minX..maxX && n.y in minY..maxY && n !in borderSet) {
                    queue.addLast(n)
                }
            }
        }

        val result = (maxX - minX + 1) * (maxY - minY + 1) - outside.size
        println(result)
    }

    // part B
    run {
//        val instructions = input
//            .map { line ->
//                val parts = line.split(' ')
//                val direction = Direction.valueOf(parts[0])
//                val steps = parts[1].toInt()
//                Pair(direction, steps)
//            }

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

        val border = instructions.runningFold(Point2D(0, 0)) { prev, (dir, st) ->
            prev.copy(x = prev.x + dir.dx * st, y = prev.y + dir.dy * st)
        }
        val borderSegments = border.zipWithNext()

        val segmentsX = border.asSequence()
            .map { it.x }
            .distinct()
            .sorted()
            .zipWithNext()
            .flatMap { (l, r) -> listOf(l..l, l+1..<r, r..r) }
            .distinct()
            .toList()
        val segmentsY = border.asSequence()
            .map { it.y }
            .distinct()
            .sorted()
            .zipWithNext()
            .flatMap { (l, r) -> listOf(l..l, l+1..<r, r..r) }
            .distinct()
            .toList()


        // TODO FIXME

        val interiorBoxes = mutableListOf<Pair<IntRange, IntRange>>()
//        for (ys in segmentsY) {
//            var inside = false
//            for (xs in segmentsX) {
//                if (xs.first == xs.last && ys.first == ys.last) {
//                    // corner is always inside
//                    interiorBoxes += xs to ys
//                } else if (xs.first == xs.last) {
//                    // horizontal line
//                    val flag = borderSegments.any { (p1, p2) ->
//                        val r = min(p1.y, p2.y)..max(p1.y, p2.y)
//                        ys.first in r && ys.last in r
//                    }
//                    if (flag || inside) {
//                        interiorBoxes += xs to ys
//                    }
//                    if (flag) {
//                        inside = !inside
//                    }
//                } else if (ys.first == ys.last) {
//                    // vertical line
//                    val flag = borderSegments.any { (p1, p2) ->
//                        val r = min(p1.x, p2.x)..max(p1.x, p2.x)
//                        xs.first in r && xs.last in r
//                    }
//                    if (flag || inside) {
//                        interiorBoxes += xs to ys
//                    }
//                } else {
//                    // box
//                    if (inside) interiorBoxes += xs to ys
//                }
//            }
//        }

        val totalArea = interiorBoxes.sumOf { (xs, ys) ->
            (xs.last - xs.first + 1).toLong() * (ys.last - ys.first + 1).toLong()
        }
        println(totalArea)
    }
}
