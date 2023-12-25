package aoc

import org.apache.commons.math3.linear.*
import kotlin.math.*

private val input = readLines("24.txt")

data class PositionAndVelocity(val p: Point3D, val v: Point3D)

data class Intersection(val x: Double, val y: Double, val z: Double)

private fun LongArray.toDoubleArray() = DoubleArray(size) { idx -> this[idx].toDouble() }

fun pathIntersectionXY(pv1: PositionAndVelocity, pv2: PositionAndVelocity): Intersection? {
    if (pv1.p.x == pv2.p.x && pv1.p.y == pv2.p.y) {
        return Intersection(pv1.p.x.toDouble(), pv2.p.y.toDouble(), 0.0)
    }

    if (pv1.v.x * pv2.v.y == pv1.v.y * pv2.v.x) {
        return null
    }

    val t1 = (pv2.v.y * (pv2.p.x - pv1.p.x) - pv2.v.x * (pv2.p.y - pv1.p.y)).toDouble() /
            (pv2.v.y * pv1.v.x - pv2.v.x * pv1.v.y).toDouble()

    val t2 = (pv1.v.y * (pv1.p.x - pv2.p.x) - pv1.v.x * (pv1.p.y - pv2.p.y)).toDouble() /
            (pv1.v.y * pv2.v.x - pv1.v.x * pv2.v.y).toDouble()

    if (t1 < 0.0 || t2 < 0.0) {
        return null
    }

    return Intersection(
        pv1.p.x.toDouble() + t1 * pv1.v.x.toDouble(),
        pv1.p.y.toDouble() + t1 * pv1.v.y.toDouble(),
        0.0
    )
}

fun main() {
    val particles = input.map { line ->
        val parts = line.split(" @ ")
        val p = parts[0].split(", ").map { it.trim().toLong() }
        val v = parts[1].split(", ").map { it.trim().toLong() }

        PositionAndVelocity(
            Point3D(p[0], p[1], p[2]),
            Point3D(v[0], v[1], v[2])
        )
    }

    // part A
    run {
        val range = 200_000_000_000_000 .. 400_000_000_000_000

        val result = particles.indices.sumOf { i1 ->
            ((i1 + 1)..<particles.size).count { i2 ->
                val p = pathIntersectionXY(particles[i1], particles[i2])
                p != null && p.x.toLong() in range && p.y.toLong() in range
            }
        }

        println(result)
    }

    // part B
    run {
        val pairs = particles.drop(2).take(5).zipWithNext()

        // calculate X, Y, VX, VY
        val lhsXY = pairs.map { (pvi, pvj) ->
            longArrayOf(
                (pvj.v.y - pvi.v.y), // X
                (pvi.v.x - pvj.v.x), // Y
                (pvi.p.y - pvj.p.y), // VX
                (pvj.p.x - pvi.p.x), // VY
            )
        }

        val rhsXY = LongArray(pairs.size) { idx ->
            val (pvi, pvj) = pairs[idx]
            (pvj.p.x * pvj.v.y - pvi.p.x * pvi.v.y + pvi.p.y * pvi.v.x - pvj.p.y * pvj.v.x)
        }

        val matrixXY = MatrixUtils.createRealMatrix(
            lhsXY.map { it.toDoubleArray() }.toTypedArray()
        )
        val constantsXY = MatrixUtils.createRealVector(
            rhsXY.toDoubleArray()
        )
        val solverXY = LUDecomposition(matrixXY).solver
        val solutionXY = solverXY.solve(constantsXY)

        val ansX = round(solutionXY.getEntry(0)).toLong()
        val ansY = round(solutionXY.getEntry(1)).toLong()
        val ansVX = round(solutionXY.getEntry(2)).toLong()
        val ansVY = round(solutionXY.getEntry(3)).toLong()

        run {
            // validate
            rhsXY.zip(lhsXY) { r, l ->
                val total = l[0] * ansX + l[1] * ansY + l[2] * ansVX + l[3] * ansVY
                require(total == r) { "Either X=$ansX or Y=$ansY isn't the right answer" }
            }
        }

        // calculate Z and VZ
        val lhsXZ = pairs.map { (pvi, pvj) ->
            longArrayOf(
                (pvj.v.z - pvi.v.z), // X
                (pvi.v.x - pvj.v.x), // Z
                (pvi.p.z - pvj.p.z), // VX
                (pvj.p.x - pvi.p.x), // VZ
            )
        }

        val rhsXZ = LongArray(pairs.size) { idx ->
            val (pvi, pvj) = pairs[idx]
            (pvj.p.x * pvj.v.z - pvi.p.x * pvi.v.z + pvi.p.z * pvi.v.x - pvj.p.z * pvj.v.x)
        }

        val matrixXZ = MatrixUtils.createRealMatrix(
            lhsXZ.map { it.toDoubleArray() }.toTypedArray()
        )
        val constantsXZ = MatrixUtils.createRealVector(
            rhsXZ.toDoubleArray()
        )
        val solverXZ = LUDecomposition(matrixXZ).solver
        val solutionXZ = solverXZ.solve(constantsXZ)

        val ansZ = round(solutionXZ.getEntry(1)).toLong()
        val ansVZ = round(solutionXZ.getEntry(3)).toLong()

        run {
            // validate
            rhsXZ.zip(lhsXZ) { r, l ->
                val total = l[0] * ansX + l[1] * ansZ + l[2] * ansVX + l[3] * ansVZ
                require(total == r) { "Z=$ansZ isn't the right answer" }
            }
        }

        println(ansX + ansY + ansZ)
    }
}
