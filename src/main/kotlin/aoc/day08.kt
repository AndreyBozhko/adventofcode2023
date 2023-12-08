package aoc

import java.math.BigInteger

private val input get() = readLines("08.txt")

private fun <T> Array<T>.cycle() = object : Iterator<T> {
    private val elements = this@cycle
    private val sz = elements.size
    private var i = 0

    override fun hasNext() = true

    override fun next(): T = elements[i].also {
        i = (i + 1) % sz
    }
}

fun main() {
    val dirs = input[0].toCharArray().toTypedArray()
    val nodes = input
        .drop(2)
        .associate {
            it.substring(0, 3) to (it.substring(7, 10) to it.substring(12, 15))
        }

    // part A
    run {
        val result = dirs.cycle()
            .asSequence()
            .runningFold("AAA") { n, choice ->
                when (choice) {
                    'L' -> nodes[n]!!.first
                    else -> nodes[n]!!.second
                }
            }
                .takeWhile { it != "ZZZ" }
                .count()
        println(result)
    }

    // part B
    run {
        val startNodes = nodes.keys.filter { it[2] == 'A' }

        val steps = startNodes.map { st ->
            dirs.cycle()
                .asSequence()
                .runningFold(st) { n, choice ->
                    when (choice) {
                        'L' -> nodes[n]!!.first
                        else -> nodes[n]!!.second
                    }
                }
                .takeWhile { it[2] != 'Z' }
                .count()
                .toBigInteger()
        }

        val result = steps.fold(BigInteger.ONE) { a, b ->
            a * b / a.gcd(b)
        }
        println(result)
    }
}
