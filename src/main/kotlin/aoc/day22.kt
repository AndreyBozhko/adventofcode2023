package aoc

import kotlin.math.*

private val input = readLines("22.txt")

data class Brick(val first: Point3D, val second: Point3D) {
    private val isAlongX = first.y == second.y && first.z == second.z
    private val isAlongY = first.x == second.x && first.z == second.z
    private val isAlongZ = first.y == second.y && first.x == second.x

    private val minX = min(first.x, second.x)
    private val maxX = max(first.x, second.x)
    private val minY = min(first.y, second.y)
    private val maxY = max(first.y, second.y)
    val minZ = min(first.z, second.z)
    val maxZ = max(first.z, second.z)

    override fun toString() = "Brick(${first.x},${first.y},${first.z}~${second.x},${second.y},${second.z})"

    fun fall(dist: Int) = copy(first = first.copy(z = first.z - dist), second = second.copy(z = second.z - dist))

    fun supports(other: Brick): Boolean {
        return if (this == other) {
            false
        } else if (isAlongX && other.isAlongX) {
            maxZ < other.minZ
                    && minY == other.minY
                    && maxX >= other.minX
                    && minX <= other.maxX
        } else if (isAlongX && other.isAlongY) {
            maxZ < other.minZ
                    && other.minX in minX..maxX
                    && minY in other.minY..other.maxY
        } else if (isAlongX && other.isAlongZ) {
            maxZ < other.minZ
                    && minY == other.minY
                    && other.minX in minX..maxX
        } else if (isAlongY && other.isAlongX) {
            maxZ < other.minZ
                    && other.minY in minY..maxY
                    && minX in other.minX..other.maxX
        } else if (isAlongY && other.isAlongY) {
            maxZ < other.minZ
                    && minX == other.minX
                    && maxY >= other.minY
                    && minY <= other.maxY
        } else if (isAlongY && other.isAlongZ) {
            maxZ < other.minZ
                    && minX == other.minX
                    && other.minY in minY..maxY
        } else if (isAlongZ && other.isAlongX) {
            maxZ < other.minZ
                    && minY == other.minY
                    && minX in other.minX..other.maxX
        } else if (isAlongZ && other.isAlongY) {
            maxZ < other.minZ
                    && minX == other.minX
                    && minY in other.minY..other.maxY
        } else {
            minX == other.minX
                    && minY == other.minY
                    && maxZ < other.minZ
        }
    }
}

fun main() {
    val bricks = input.map { line ->
        val parts = line.split('~')
        val pos1 = parts[0].split(',').map { it.toInt() }
        val pos2 = parts[1].split(',').map { it.toInt() }

        Brick(
            Point3D(pos1[0], pos1[1], pos1[2]),
            Point3D(pos2[0], pos2[1], pos2[2])
        )
    }
        .sortedBy { it.minZ }

    val ground = bricks.minOf { it.minZ } - 1
    val updatedBricks = buildList<Brick> {
        for (b in bricks) {
            val maxZSoFar = if (isEmpty()) ground else maxOf {
                if (it.supports(b) && it.maxZ < b.minZ) it.maxZ else ground
            }
            val b2 = b.fall(b.minZ - maxZSoFar - 1)
            this += b2
        }
    }
        .sortedBy { it.minZ }

    val supportedUpperToLower = updatedBricks.associateWith { b1 ->
        updatedBricks.filter { b2 -> b2.supports(b1) && b2.maxZ + 1 == b1.minZ }
    }

    val supportedLowerToUpper = updatedBricks.associateWith { b1 ->
        updatedBricks.filter { b2 -> b1.supports(b2) && b1.maxZ + 1 == b2.minZ }
    }

    // part A
    run {
        val result = updatedBricks.count { b ->
            supportedLowerToUpper[b]?.all { supportedUpperToLower[it]!!.size >= 2 } ?: true
        }
        println(result)
    }

    // part B
    run {
        val result = updatedBricks.sumOf { start ->
            val seen = mutableSetOf<Brick>()
            val queue = ArrayDeque<Brick>()
            queue += start

            while (queue.isNotEmpty()) {
                val brick = queue.removeFirst()
                if (brick in seen) continue
                seen += brick

                supportedLowerToUpper[brick]?.forEach { b ->
                    if (supportedUpperToLower[b]!!.all { it in seen }) {
                        queue += b
                    }
                }

            }

            seen.size - 1
        }
        println(result)
    }
}
